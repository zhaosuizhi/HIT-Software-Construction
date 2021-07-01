package adt.utl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 无冲突的时间轴
 *
 * @param <L>时间段标签的类型
 */
public class NonOverlapIntervalSet<L> extends IntervalSetDecorator<L> {

    // 抽象函数:
    //   AF(intervalMap) = intervalMap是一个“标签->时间段”的满射
    // 表示不变量:
    //   intervalMap中的每个Interval两两不相等
    // 防止表示暴露:
    //   intervalMap为private final，无法修改引用
    //   在labels()方法中返回的值是可变类型，在返回时进行防御性复制
    //   其余方法中返回的均是不可变类型，不会发生表示暴露

    private void checkRep() {
        Set<Interval> intervals = new HashSet<>();

        for (L label : set.labels()) {
            Interval i = set.interval(label);
            boolean ret = intervals.add(i);
            assert ret;
        }
    }

    /**
     * @param set 时间段集合
     */
    public NonOverlapIntervalSet(IntervalSet<L> set) {
        super(set);
    }

    /**
     * 插入新的时间段和标签
     *
     * @param start 开始时间
     * @param end   结束时间
     * @param label 标签
     * @return 是否添加成功，若失败说明标签已存在或时间段存在冲突
     * @throws NullPointerException     当label == null时
     * @throws IllegalArgumentException 当start, end不满足0 <= start <= end时
     */
    @Override
    public boolean insert(long start, long end, L label) throws NullPointerException, IllegalArgumentException {
        Interval interval = new Interval(start, end);

        for (L l : set.labels())
            if (set.interval(l).equals(interval)) // 冲突
                return false;

        boolean res = super.insert(start, end, label);
        checkRep();
        return res;
    }
}
