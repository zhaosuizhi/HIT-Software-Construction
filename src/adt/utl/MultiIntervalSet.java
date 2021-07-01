package adt.utl;

import java.util.*;

/**
 * 一组在时间轴上分布的时间段（interval）构成成的集合，
 * 其中一个标签可以对应多个时间段
 * <p>Mutable
 *
 * @param <L> 时间段标签的类型
 */
public interface MultiIntervalSet<L> {
    static <L> MultiIntervalSet<L> empty() {
        return new CommonMultiIntervalSet<>();
    }

    /**
     * 计算两个{@link MultiIntervalSet}对象的相似度
     *
     * @param s1  一个时间段集合
     * @param s2  另一个时间段集合
     * @param <L> 标签类型
     * @return 两集合的相似度
     */
    static <L> double similarity(MultiIntervalSet<L> s1, MultiIntervalSet<L> s2) {
        return Api.similarity(s1, s2);
    }

    /**
     * 插入新的时间段和标签
     *
     * @param start 开始时间
     * @param end   结束时间
     * @param label 标签
     * @return 是否添加成功，若失败说明存在冲突
     * @throws NullPointerException     当label == null时
     * @throws IllegalArgumentException 当start, end不满足0 <= start <= end时
     */
    boolean insert(long start, long end, L label) throws NullPointerException, IllegalArgumentException;

    /**
     * 获得当前的标签集合
     *
     * @return 标签集合
     */
    Set<L> labels();

    /**
     * 移除某个标签所关联的时间段
     *
     * @param label 被移除时间段的标签
     * @return 移除返回true，时间段不存在返回null
     */
    boolean remove(L label);

    /**
     * 获取标签所关联的所有时间段
     * <p>返回结果表达为{@code IntervalSet<Integer>}的形式，其中的时间段按开始时间从小到大的
     * 次序排列。
     * <p>例如：当前对象为{ "A"=[[0,10],[20,30]], "B"=[[10,20]] }，
     * 那么{@code intervals("A")}返回的结果是{ 0=[0,10],1=[20,30] }。
     *
     * @param label 标签
     * @return 标签关联的所有时间段；不存在返回null
     */
    IntervalSet<Integer> intervals(L label);
}

/**
 * 与{@link MultiIntervalSet}计算相关的委托类
 */
class Api {

    /**
     * 计算两个{@link MultiIntervalSet}对象的相似度
     *
     * @param s1  一个时间段集合
     * @param s2  另一个时间段集合
     * @param <L> 标签类型
     * @return 两集合的相似度
     */
    static <L> double similarity(MultiIntervalSet<L> s1, MultiIntervalSet<L> s2) {

        Set<L> lSet1 = s1.labels();
        Set<L> lSet2 = s2.labels();

        Set<L> unionSet = new HashSet<>(lSet1); // s1和s2共有的标签构成的集合
        unionSet.retainAll(lSet2);

        long maxTime = -1; // 最晚时间
        long simCNT = 0; // 相似计数

        /* 统计最晚的时间（计算maxTime） */
        maxTime = Math.max(maxTime, getMaxTime(s1));
        maxTime = Math.max(maxTime, getMaxTime(s2));

        /* 统计相似时间段个数（计算simCNT） */
        for (L label : unionSet) {
            IntervalSet<Integer> intervals1 = s1.intervals(label);
            final int size1 = intervals1.labels().size();
            IntervalSet<Integer> intervals2 = s2.intervals(label);
            final int size2 = intervals2.labels().size();

            int i = 0, j = 0;
            while (i < size1 && j < size2) {
                Interval i1 = intervals1.interval(i);
                Interval i2 = intervals2.interval(j);

                int compRes = i1.compareTo(i2);
                if (compRes < 0) // i1在i2前
                    i++;
                else if (compRes > 0) // i1在i2后
                    j++;
                else { // 时间段存在重叠，进行累计
                    Interval overlap = i1.overlap(i2); // 由于存在重合，overlap != null
                    long length = overlap.getLength() - 1; // 重叠部分长度
                    simCNT += length;
                    i++;
                    j++;
                }
            }
        }

        return (double) simCNT / maxTime;
    }

    /**
     * 获取一个{@link MultiIntervalSet}中的最大时间段
     *
     * @param set 时间段集合
     * @param <L> 标签类型
     * @return 所有时间段中的最大时刻
     */
    private static <L> long getMaxTime(MultiIntervalSet<L> set) {
        long maxTime = -1;
        Set<L> labelSet = set.labels();
        for (L label : labelSet) {
            IntervalSet<Integer> orderedSet = set.intervals(label);
            final int size = orderedSet.labels().size();

            for (int i = 0; i < size; i++) {
                Interval interval = orderedSet.interval(i);
                long end = interval.getEnd();
                maxTime = Math.max(end, maxTime);
            }
        }
        return maxTime;
    }
}
