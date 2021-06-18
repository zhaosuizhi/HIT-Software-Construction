package adt;


import java.util.Objects;

/**
 * 一个时间段
 * <p>Immutable
 * <p>要求时间点均为非负整数且长度大于0，否则将在构造函数抛出{@link IllegalArgumentException}
 * <p>时间段可比较。定义时间段有重合为相等，否则靠前的时间段小于靠后的时间段。
 */
class Interval implements Comparable<Interval> {
    private final long start;
    private final long end;

    // 抽象函数:
    //   AF(start, end) = 从start到end的时间段
    // 表示不变量:
    //   0 <= start < end
    // 防止表示暴露:
    //   start和end均为private final，Long是不可变类型，故无法在初始化后被直接访问和修改

    /**
     * @param start 开始时间
     * @param end   结束时间
     * @throws IllegalArgumentException 当start, end不满足0 <= start < end时
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
     * @throws IllegalArgumentException 当start, end不满足0 <= start < end时
     */
    public static void validInterval(long start, long end) {
        if (start < 0 || start >= end)
            throw new IllegalArgumentException();
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    /**
     * 时间段有重叠则为相等，否则靠前的为小
     */
    @Override
    public int compareTo(Interval other) {
        /* 首先根据end比较大小 */
        Interval bigger, smaller;
        if (this.end > other.end) {
            bigger = this;
            smaller = other;
        } else {
            bigger = other;
            smaller = this;
        }

        if (bigger.start < smaller.end) // 时间段有重叠
            return 0;
        else // 时间段无重叠，简单判断即可
            return this.start < other.start ? -1 : 1;
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
