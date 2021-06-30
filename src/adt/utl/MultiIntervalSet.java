package adt.utl;

import java.util.Set;

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
