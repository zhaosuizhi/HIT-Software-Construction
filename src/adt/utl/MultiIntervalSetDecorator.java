package adt.utl;

import java.util.Set;

/**
 * {@link MultiIntervalSet}的装饰器
 *
 * @param <L> 时间段标签的类型
 */
public class MultiIntervalSetDecorator<L> implements MultiIntervalSet<L> {
    private final MultiIntervalSet<L> set;

    // 抽象函数:
    //   AF(set) = set是一个时间段集合，一个标签可对应多个时间段
    // 表示不变量:
    //   set != null
    // 防止表示暴露:
    //   本类中的所有方法均委托给set中的相应方法，因此防止表示暴露由set负责

    private void checkRep() {
        assert set != null;
    }

    public MultiIntervalSetDecorator(MultiIntervalSet<L> set) {
        this.set = set;
        checkRep();
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
