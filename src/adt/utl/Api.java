package adt.utl;

import java.util.HashSet;
import java.util.Set;

/**
 * 与时间段计算相关的委托类
 */
public class Api {

    /**
     * 计算两个{@link MultiIntervalSet}对象的相似度
     *
     * @param s1  一个时间段集合
     * @param s2  另一个时间段集合
     * @param <L> 标签类型
     * @return 两集合的相似度
     */
    public static <L> double similarity(MultiIntervalSet<L> s1, MultiIntervalSet<L> s2) {

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
     * 计算一个{@link IntervalSet}对象中的时间冲突比例
     *
     * @param set 时间段集合
     * @param <L> 标签类型
     * @return 冲突比例
     */
    public static <L> double calcConflictRatio(IntervalSet<L> set) {
        final long maxTime = getMaxTime(set); // 总长度
        long conflictCNT = 0; // 冲突天数

        Set<L> labelSet = set.labels();

        for (long i = 0; i <= maxTime; i++) { // 查看每一天
            boolean finished = false;

            for (L l1 : labelSet) {
                for (L l2 : labelSet) {
                    if (!l1.equals(l2)) {
                        Interval i1 = set.interval(l1);
                        Interval i2 = set.interval(l2);

                        Interval overlap = i1.overlap(i2);
                        if (overlap != null && overlap.getLength() > 1 && overlap.contains(i)) {
                            conflictCNT++;
                            finished = true;
                            break;
                        }
                    }
                }
                if (finished)
                    break;
            }
        }

        return (double) conflictCNT / maxTime;
    }

    /**
     * 计算一个{@link MultiIntervalSet}对象中的时间冲突比例
     *
     * @param set 时间段集合
     * @param <L> 标签类型
     * @return 冲突比例
     */
    public static <L> double calcConflictRatio(MultiIntervalSet<L> set) {
        final long maxTime = getMaxTime(set); // 总长度
        long conflictCNT = 0; // 冲突天数

        Set<L> labelSet = set.labels();

        for (long i = 0; i <= maxTime; i++) { // 查看每一天
            boolean finished = false;

            for (L l1 : labelSet) {
                for (L l2 : labelSet)
                    if (!l1.equals(l2)) {
                        IntervalSet<Integer> intervals1 = set.intervals(l1);
                        IntervalSet<Integer> intervals2 = set.intervals(l2);

                        Integer index1 = intervals1.getLabelByTime(i);
                        Integer index2 = intervals2.getLabelByTime(i);

                        if (index1 != null && index2 != null) { // 可能发生冲突
                            Interval i1 = intervals1.interval(index1);
                            Interval i2 = intervals2.interval(index2);

                            Interval overlap = i1.overlap(i2);
                            if (overlap != null && overlap.getLength() > 1) {
                                conflictCNT++;
                                finished = true;
                                break;
                            }
                        }
                    }
                if (finished)
                    break;
            }
        }

        return (double) conflictCNT / maxTime;
    }


    /**
     * 计算一个{@link IntervalSet}对象中的空闲比例
     * <p>“空闲”是指某时间段内没有安排任何interval对象
     *
     * @param set 时间段集合
     * @param <L> 标签类型
     * @return 空闲比例
     */
    public static <L> double calcFreeTimeRatio(IntervalSet<L> set) {
        final long maxTime = getMaxTime(set) + 1; // 总长度
        long blankCNT = maxTime; // 空闲天数

        Set<L> labelSet = set.labels();

        for (long i = 0; i <= maxTime; i++) { // 查看每一天
            for (L label : labelSet) {
                Interval interval = set.interval(label);
                if (interval.contains(i)) { // 不空闲，排除这一天
                    blankCNT--;
                    break;
                }
            }
        }

        return (double) blankCNT / maxTime;
    }

    /**
     * 计算一个{@link IntervalSet}对象中的空闲比例
     * <p>“空闲”是指某时间段内没有安排任何interval对象
     *
     * @param set 时间段集合
     * @param <L> 标签类型
     * @return 空闲比例
     */
    public static <L> double calcFreeTimeRatio(MultiIntervalSet<L> set) {
        final long maxTime = getMaxTime(set) + 1; // 总长度
        long blankCNT = maxTime; // 空闲天数

        Set<L> labelSet = set.labels();

        for (long i = 0; i <= maxTime; i++) { // 查看每一天
            boolean finished = false;

            for (L label : labelSet) {
                IntervalSet<Integer> intervals = set.intervals(label);
                final int size = intervals.labels().size();
                for (int j = 0; j < size; j++) {
                    if (intervals.interval(j).contains(i)) { // 不空闲，排除这一天
                        blankCNT--;
                        finished = true;
                        break;
                    }
                }
                if (finished)
                    break;
            }
        }

        return (double) blankCNT / maxTime;
    }


    /**
     * 获取一个{@link IntervalSet}中的最大时间段
     *
     * @param set 时间段集合
     * @param <L> 标签类型
     * @return 所有时间段中的最大时刻
     */
    private static <L> long getMaxTime(IntervalSet<L> set) {
        long maxTime = -1;

        for (L label : set.labels()) {
            long end = set.end(label);
            maxTime = Math.max(maxTime, end);
        }

        return maxTime;
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
        for (L label : labelSet)
            maxTime = Math.max(maxTime, getMaxTime(set.intervals(label)));

        return maxTime;
    }
}
