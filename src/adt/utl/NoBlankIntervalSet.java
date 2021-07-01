package adt.utl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 无空白的时间轴
 * <p>需要显示调用{@link #checkNoBlank()}方法进行检查
 *
 * @param <L>时间段标签的类型
 */
public class NoBlankIntervalSet<L> extends IntervalSetDecorator<L> {

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
     * @throws IllegalArgumentException 当start, end不满足 0 <= start <= end <= maxTime 时
     */
    @Override
    public boolean insert(long start, long end, L label) throws NullPointerException, IllegalArgumentException {
        if (maxTime < end)
            throw new IllegalArgumentException("end <= maxTime is needed");

        return super.insert(start, end, label);
    }

    /**
     * 查看时间轴上的空白单位时间数
     *
     * @return 空白时间数
     */
    public long countBlank() {
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

        return blankCNT;
    }

    /**
     * 检查时间轴是否合法
     *
     * @return 时间轴上是否不存在空白
     */
    public boolean checkNoBlank() {
        return countBlank() == 0;
    }
}
