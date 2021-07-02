package adt.utl;

import java.util.Set;

public class NoBlankMultiIntervalSet<L> extends MultiIntervalSetDecorator<L> implements NoBlankSet {

    private final long maxTime;

    /**
     * 最大时间
     *
     * @param set     时间段集
     * @param maxTime 最大时间
     */
    public NoBlankMultiIntervalSet(MultiIntervalSet<L> set, long maxTime) {
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
    public boolean insert(long start, long end, L label) {
        if (maxTime < end)
            throw new IllegalArgumentException("end <= maxTime is needed");

        return super.insert(start, end, label);
    }

    @Override
    public double blankRate() {
        long blankCNT = 0;
        for (long i = 0; i <= maxTime; i++) {
            boolean found = false;
            for (L label : set.labels()) {
                IntervalSet<Integer> intervalSet = set.intervals(label);
                if (intervalSet.getLabelByTime(i) != null) {
                    found = true;
                    break;
                }
            }
            if (!found)
                blankCNT++;
        }
        return (double) blankCNT / maxTime;
    }
}
