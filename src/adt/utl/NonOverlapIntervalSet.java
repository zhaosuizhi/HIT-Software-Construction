package adt.utl;

import java.util.HashSet;
import java.util.Set;

/**
 * 无冲突的时间轴
 *
 * @param <L>时间段标签的类型
 */
public class NonOverlapIntervalSet<L> extends IntervalSetDecorator<L> {

    // 无额外成员变量，因此AF与RI同父类
    // 防止表示暴露:
    //   insert方法没有返回内部变量的地方，因此不会发生表示暴露
    //   其余方法由父类负责

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
