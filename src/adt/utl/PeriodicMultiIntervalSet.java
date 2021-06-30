package adt.utl;

import java.util.Set;

/**
 * 可添加周期性时间段且标签可重复的时间轴
 * <p>通过{@link #insertPeriod}方法添加
 *
 * @param <L>时间段标签的类型
 */
public class PeriodicMultiIntervalSet<L> extends MultiIntervalSetDecorator<L> {

    private final long delta; // 一个周期的长度

    /**
     * @param set   时间段集合
     * @param delta 周期的长度
     */
    protected PeriodicMultiIntervalSet(MultiIntervalSet<L> set, long delta) {
        super(set);
        this.delta = delta;
    }

    /**
     * 插入周期性的时间段和标签
     *
     * @param start 开始时间
     * @param end   结束时间
     * @param label 标签
     * @param times 重复次数
     * @return 是否添加成功，若失败说明存在冲突
     * @throws NullPointerException     当label == null时
     * @throws IllegalArgumentException 当start, end不满足0 <= start <= end时
     */
    public boolean insertPeriod(long start, long end, L label, int times) throws NullPointerException, IllegalArgumentException {
        Interval.validInterval(start, end);  // 非法输入时抛出IllegalArgumentException

        IntervalSet<Integer> prevSet = set.intervals(label); // 备份之前的时间段，方便回滚

        for (int i = 0; i < times; i++) {
            boolean res = set.insert(start + delta * times, end + delta * times, label);
            if (!res) { // 存在冲突，回滚并返回false
                set.remove(label);

                Set<Integer> ints = prevSet.labels();
                for (int index : ints) {
                    set.insert(prevSet.start(index), prevSet.end(index), label);
                }

                return false;
            }
        }
        return true;
    }
}
