package adt.utl;

/**
 * 无空白的时间轴
 * <p>调用{@link #blankRate()}方法可以获得时间轴上的空白比例
 *
 * @param <L>时间段标签的类型
 */
public class NoBlankIntervalSet<L> extends IntervalSetDecorator<L> implements NoBlankSet {

    private final long maxTime; // 最大时刻

    // 抽象函数:
    //   AF(maxTime) = 整个时间段中的最大有效时间为maxTime
    // 表示不变量:
    //   maxTime >= 0
    // 防止表示暴露:
    //   maxTime为private final，无法修改引用

    private void checkRep() {
        assert maxTime >= 0;
    }

    /**
     * @param set     时间段集合
     * @param maxTime 最大时刻
     */
    public NoBlankIntervalSet(IntervalSet<L> set, long maxTime) {
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
    public boolean insert(long start, long end, L label) throws NullPointerException, IllegalArgumentException {
        if (maxTime < end)
            throw new IllegalArgumentException("end <= maxTime is needed");

        return super.insert(start, end, label);
    }

    @Override
    public double blankRate() {
        long blankCNT = 0;
        for (long i = 0; i <= maxTime; i++)
            if (set.getLabelByTime(i) == null)
                blankCNT++;

        return (double) blankCNT / (maxTime + 1) * 100;
    }
}
