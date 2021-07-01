package adt.utl;

import java.util.*;

/**
 * IntervalSet的一个实现
 */
public class CommonIntervalSet<L> extends IntervalSet<L> {
    private final Map<L, Interval> intervalMap = new HashMap<>(); // 标签->时间段 映射

    // 抽象函数:
    //   AF(intervalMap) = intervalMap是一个“标签->时间段”的满射
    // 表示不变量:
    //   intervalMap中的每个Interval两两不相等
    // 防止表示暴露:
    //   intervalMap为private final，无法修改引用
    //   在labels()方法中返回的值是可变类型，在返回时进行防御性复制
    //   其余方法中返回的均是不可变类型，不会发生表示暴露

    private void checkRep() {
        Collection<Interval> intervals = intervalMap.values();
        Set<Interval> set = new HashSet<>(intervals);

        assert intervals.size() == set.size();
    }

    @Override
    public boolean insert(long start, long end, L label) {
        if (label == null)
            throw new NullPointerException("label不能为null");

        Interval interval = new Interval(start, end);

        if (intervalMap.containsKey(label)  // 标签已经被添加过
                || intervalMap.containsValue(interval)) // 或时间段冲突
            return false; // 不予添加

        intervalMap.put(label, interval);

        checkRep();
        return true;
    }

    @Override
    public Set<L> labels() {
        return new HashSet<>(intervalMap.keySet());
    }

    @Override
    public boolean remove(L label) {
        Interval removed = intervalMap.remove(label);

        checkRep();
        return removed != null;
    }

    @Override
    protected Interval interval(L label) {
        return intervalMap.get(label);
    }

    @Override
    public long start(L label) {
        Interval interval = intervalMap.get(label);
        if (interval == null)
            return -1;
        else
            return interval.getStart();
    }

    @Override
    public long end(L label) {
        Interval interval = intervalMap.get(label);
        if (interval == null)
            return -1;
        else
            return interval.getEnd();
    }

    @Override
    public L getLabelByTime(long time) {
        for (L label : intervalMap.keySet()) {
            if (intervalMap.get(label).contains(time))
                return label;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommonIntervalSet<?> that = (CommonIntervalSet<?>) o;
        return intervalMap.equals(that.intervalMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(intervalMap);
    }
}
