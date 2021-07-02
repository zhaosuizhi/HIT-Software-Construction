package adt;

import adt.utl.IntervalSet;
import adt.utl.MultiIntervalSet;
import adt.utl.NoBlankMultiIntervalSet;
import adt.utl.NonOverlapMultiIntervalSet;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;

/**
 * 排班时间段集合
 * <p>Mutable
 *
 * @param <L> 表示员工的类型
 */
public class DutyIntervalSet<L> {

    private final NoBlankMultiIntervalSet<L> set; // 非空、无冲突的时间段集合
    private final LocalDate startDate;

    // 抽象函数:
    //   AF(set, startDate) = 一张员工值班表，开始日期为startDate，结束日期endDate委托给set
    //       set的时间单位表示startDate之后的天数，例如set中0时间表示startDate当天，1表示startDate的下一天
    // 表示不变量:
    //   set != null
    //   startDate != null
    // 防止表示暴露:
    //   LocalDate是Immutable类型，不会发生表示暴露
    //   各方法返回值或者是新申请的对象、或者委托给set

    private void checkRep() {
        assert set != null;
        assert startDate != null;
    }

    /**
     * @param startDate 开始日期
     * @param endDate   结束日期
     */
    public DutyIntervalSet(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        set = new NoBlankMultiIntervalSet<>(new NonOverlapMultiIntervalSet<>(MultiIntervalSet.empty()), date2offset(endDate));
        checkRep();
    }

    /**
     * 将日期转换为距开始日期的偏移量
     *
     * @param date 日期
     * @return 对应的偏移量
     */
    private long date2offset(LocalDate date) {
        return startDate.until(date, ChronoUnit.DAYS);
    }

    /**
     * 将距开始日期的偏移量转换为日期
     *
     * @param offset 偏移量
     * @return 对应的日期
     */
    private LocalDate offset2date(long offset) {
        return startDate.plusDays(offset);
    }

    /**
     * 添加一个值班信息
     *
     * @param start 开始日期
     * @param end   结束日期
     * @param label 员工
     * @return 是否添加成功
     * @throws NullPointerException     当label == null时
     * @throws IllegalArgumentException 当start, end不满足0 <= start <= end时
     */
    public boolean add(LocalDate start, LocalDate end, L label) throws NullPointerException, IllegalArgumentException {
        return set.insert(date2offset(start), date2offset(end), label);
    }

    /**
     * 获取所有员工
     *
     * @return 所有员工构成的集合
     */
    public Set<L> labels() {
        return set.labels();
    }

    /**
     * 移出一个员工对应的值班信息
     *
     * @param label 员工
     * @return 是否成功移出
     */
    public boolean remove(L label) {
        return set.remove(label);
    }

    /**
     * 获取指定日期的员工
     *
     * @param date 日期
     * @return 对应日期的员工；不存在返回null
     */
    public L getEmployeeByDate(LocalDate date) {
        long offset = date2offset(date);

        for (L label : set.labels()) {
            IntervalSet<Integer> intervals = set.intervals(label);
            if (intervals.getLabelByTime(offset) != null)
                return label;
        }
        return null;
    }

    /**
     * 统计未排班天数占总天数的比例
     *
     * @return 未排班比例
     */
    public double unscheduledRate() {
        return set.blankRate();
    }
}
