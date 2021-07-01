package adt.utl;

import java.util.Set;

/**
 * {@link MultiIntervalSet}的装饰器
 *
 * @param <L> 时间段标签的类型
 */
public class MultiIntervalSetDecorator<L> implements MultiIntervalSet<L> {
    protected final MultiIntervalSet<L> set;

    public MultiIntervalSetDecorator(MultiIntervalSet<L> set) {
        this.set = set;
    }

    @Override
    public boolean insert(long start, long end, L label) {
        return set.insert(start, end, label);
    }

    @Override
    public Set<L> labels() {
        return set.labels();
    }

    @Override
    public boolean remove(L label) {
        return set.remove(label);
    }

    @Override
    public IntervalSet<Integer> intervals(L label) {
        return set.intervals(label);
    }
}
