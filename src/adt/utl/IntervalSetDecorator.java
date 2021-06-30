package adt.utl;

import java.util.Set;

/**
 * {@link IntervalSet}的装饰器
 *
 * @param <L> 时间段标签的类型
 */
public abstract class IntervalSetDecorator<L> extends IntervalSet<L> {
    protected final IntervalSet<L> set;

    protected IntervalSetDecorator(IntervalSet<L> set) {
        this.set = set;
    }

    @Override
    public boolean insert(long start, long end, L label) throws NullPointerException, IllegalArgumentException {
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
    public long start(L label) {
        return set.start(label);
    }

    @Override
    public long end(L label) {
        return set.end(label);
    }

    @Override
    protected Interval interval(L label) {
        return set.interval(label);
    }
}
