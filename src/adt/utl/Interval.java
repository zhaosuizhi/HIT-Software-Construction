package adt.utl;


import java.util.Objects;

/**
 * 一个时间段
 * <p>Immutable
 * <p>要求时间点均为非负整数且长度>=0，否则将在构造函数抛出{@link IllegalArgumentException}
 * <p>时间段可比较。定义时间段有重合为相等，否则靠前的时间段小于靠后的时间段。
 */
class Interval implements Comparable<Interval> {
    private final long start;
    private final long end;

    // 抽象函数:
    //   AF(start, end) = 从start到end的时间段
    // 表示不变量:
    //   0 <= start <= end
    // 防止表示暴露:
    //   start和end均为private final，long是基础类型，故无法在初始化后被直接访问和修改

    /**
     * @param start 开始时间
     * @param end   结束时间
     * @throws IllegalArgumentException 当start, end不满足0 <= start <= end时
     */
    public Interval(long start, long end) {
        this.start = start;
        this.end = end;

        checkRep();
    }

    private void checkRep() {
        validInterval(start, end);
    }

    /**
     * 检查时间段是否合法
     *
     * @param start 开始时间
     * @param end   结束时间
     * @throws IllegalArgumentException 当start, end不满足0 <= start <= end时
     */
    public static void validInterval(long start, long end) {
        if (start < 0)
            throw new IllegalArgumentException("start >= 0 is needed");
        else if (start > end)
            throw new IllegalArgumentException("start <= end is needed");
    }

    /**
     * 获取时间段的开始时间
     *
     * @return 开始时间
     */
    public long getStart() {
        return start;
    }

    /**
     * 获取时间段的结束时间
     *
     * @return 结束时间
     */
    public long getEnd() {
        return end;
    }

    /**
     * 获取时间段占据的单位时间段数
     *
     * @return 时间段长度(> = 1)
     */
    public long getLength() {
        return end - start + 1;
    }

    /**
     * 求与另一时间段重合部分的时间段
     *
     * @param other 另一时间段
     * @return 重合部分的时间段；若无重合，返回null
     */
    public Interval overlap(Interval other) {
        /* 首先根据end比较大小 */
        Interval bigger, smaller;
        if (this.end > other.end) {
            bigger = this;
            smaller = other;
        } else {
            bigger = other;
            smaller = this;
        }

        if (bigger.start <= smaller.end) // 时间段有重叠
            return new Interval(bigger.start, smaller.end);
        else // 时间段无重叠
            return null;
    }

    /**
     * 判断给定时刻是否在当前时间段中
     *
     * @param time 时刻
     * @return 当前时间段是否包含该时刻
     */
    public boolean contains(long time) {
        return start <= time && time <= end;
    }

    /**
     * 时间段有重叠则为相等，否则靠前的为小
     */
    @Override
    public int compareTo(Interval other) {
        if (overlap(other) == null)
            return this.start < other.start ? -1 : 1;
        else
            return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Interval interval = (Interval) o;
        return compareTo(interval) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return String.format("[%d,%d]", start, end);
    }
}
