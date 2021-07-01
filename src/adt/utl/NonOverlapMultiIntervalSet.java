package adt.utl;

import java.util.HashSet;
import java.util.Set;

/**
 * 任意标签之间也不能发生冲突的时间段集
 *
 * @param <L> 标签类型
 */
public class NonOverlapMultiIntervalSet<L> extends MultiIntervalSetDecorator<L> {

    public NonOverlapMultiIntervalSet(MultiIntervalSet<L> set) {
        super(set);
    }

    /**
     * 获取所有时间段的集合（所有标签）
     *
     * @return 所有时间段的集合
     */
    private Set<Interval> allInterval() {
        Set<Interval> allInterval = new HashSet<>();

        Set<L> labels = set.labels();
        for (L label : labels) {
            IntervalSet<Integer> intervals = set.intervals(label);
            int size = intervals.labels().size(); // interval的个数
            for (int i = 0; i < size; i++)
                allInterval.add(intervals.interval(i));
        }

        return allInterval;
    }

    @Override
    public boolean insert(long start, long end, L label) {
        Set<Interval> allI = allInterval();
        if (allI.contains(new Interval(start, end)))
            return false;
        return super.insert(start, end, label);
    }

    /**
     * 获取指定时刻的标签
     *
     * @param time 时刻
     * @return 对应的标签；不存在返回null
     */
    public L getLabelByTime(long time) {
        Set<L> labels = set.labels();
        for (L label : labels) {
            IntervalSet<Integer> intervals = set.intervals(label); // 该标签对应的全部时间段
            int size = intervals.labels().size(); // 时间段个数

            for (int i = 0; i < size; i++)
                if (intervals.interval(i).contains(time)) // 若时间段包含该时刻，则说明查找成功，返回标签
                    return label;
        }
        return null;
    }
}
