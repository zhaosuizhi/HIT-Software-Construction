package adt.utl;

import java.util.Set;

/**
 * {@link IntervalSet}的装饰器
 *
 * @param <L> 时间段标签的类型
 */
public abstract class IntervalSetDecorator<L> extends IntervalSet<L> {
    protected final IntervalSet<L> set;

    // 抽象函数:
    //   AF(set) = set是一个“标签->时间段”的满射
    // 表示不变量:
    //   set != null
    // 防止表示暴露:
    //   本类中的所有方法均委托给set中的相应方法，因此防止表示暴露由set负责

    private void checkRep() {
        assert set != null;
    }

    protected IntervalSetDecorator(IntervalSet<L> set) {
        this.set = set;
        checkRep();
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
    public L getLabelByTime(long time) {
        return set.getLabelByTime(time);
    }

    @Override
    protected Interval interval(L label) {
        return set.interval(label);
    }
}
