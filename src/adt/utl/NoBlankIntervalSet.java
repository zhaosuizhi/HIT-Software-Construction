package adt.utl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 无空白的时间轴
 * <p>调用{@link #blankRate()}方法可以获得时间轴上的空白比例
 *
 * @param <L>时间段标签的类型
 */
public class NoBlankIntervalSet<L> extends IntervalSetDecorator<L> implements NoBlankSet {

    private final long maxTime; // 最大时刻

    /**
     * @param set     时间段集合
     * @param maxTime 最大时刻
     */
    public NoBlankIntervalSet(IntervalSet<L> set, long maxTime) {
        super(set);
        this.maxTime = maxTime;
    }


    /**
     * 插入新的时间段和标签
     *
     * @param start 开始时间
     * @param end   结束时间
     * @param label 标签
     * @return 是否添加成功，若失败说明标签已存在
     * @throws NullPointerException     当label == null时
     * @throws IllegalArgumentException 当start, end不满足 0 <= start <= end <= maxTime 时
     */
    @Override
    public boolean insert(long start, long end, L label) throws NullPointerException, IllegalArgumentException {
        if (maxTime < end)
            throw new IllegalArgumentException("end <= maxTime is needed");

        return super.insert(start, end, label);
    }

    @Override
    public double blankRate() {
        Set<L> labelSet = set.labels();
        List<Interval> intervalList = new ArrayList<>();

        // 提取出所有时间段，并排序
        for (L label : labelSet) {
            intervalList.add(set.interval(label));
        }

        long blankCNT = maxTime + 1;
        // 遍历时间段，将每个时间段的长度从空白时间数中扣除
        for (Interval interval : intervalList) {
            blankCNT -= interval.getLength();
        }

        return (double) blankCNT / (maxTime + 1) * 100;
    }
}
