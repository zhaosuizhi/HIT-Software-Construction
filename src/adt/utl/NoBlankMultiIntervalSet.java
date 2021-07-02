package adt.utl;

public class NoBlankMultiIntervalSet<L> extends MultiIntervalSetDecorator<L> implements NoBlankSet {

    // 抽象函数:
    //   AF(maxTime) = 整个时间段中的最大有效时间为maxTime
    // 表示不变量:
    //   maxTime >= 0
    // 防止表示暴露:
    //   maxTime为private final，无法修改引用

    private final long maxTime;

    private void checkRep() {
        assert maxTime >= 0;
    }

    /**
     * 最大时间
     *
     * @param set     时间段集
     * @param maxTime 最大时间
     */
    public NoBlankMultiIntervalSet(MultiIntervalSet<L> set, long maxTime) {
        super(set);
        this.maxTime = maxTime;
        checkRep();
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
            for (L label : super.labels()) {
                IntervalSet<Integer> intervalSet = super.intervals(label);
                if (intervalSet.getLabelByTime(i) != null) {
                    found = true;
                    break;
                }
            }
            if (!found)
                blankCNT++;
        }
        return (double) blankCNT / (maxTime + 1) * 100;
    }

    /**
     * 统计冲突的比例
     *
     * @return 冲突比例
     */
    public double overlapRate() {
        long overlapCNT = 0;
        for (long i = 0; i <= maxTime; i++) {
            boolean found = false;
            for (L label : super.labels()) {
                IntervalSet<Integer> intervalSet = super.intervals(label);
                if (intervalSet.getLabelByTime(i) != null) {
                    if (found) { // 发生重复
                        overlapCNT++;
                        break;
                    }
                    found = true;
                }
            }
        }
        return (double) overlapCNT / (maxTime + 1) * 100;
    }
}
