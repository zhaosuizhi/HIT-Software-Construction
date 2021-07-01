package adt;

import adt.utl.IntervalSet;
import adt.utl.NoBlankIntervalSet;
import adt.utl.NonOverlapIntervalSet;

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

    private final NoBlankIntervalSet<L> set; // 非空、无冲突的时间段集合
    private final LocalDate startDate;

    /**
     * @param startDate 开始日期
     * @param endDate   结束日期
     */
    public DutyIntervalSet(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        set = new NoBlankIntervalSet<>(new NonOverlapIntervalSet<>(IntervalSet.empty()), date2offset(endDate));
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
     * 获取一个员工对应的值班开始日期
     *
     * @param label 员工
     * @return 开始日期
     */
    public LocalDate start(L label) {
        long offset = set.start(label);
        if (offset == -1)
            return null;
        else
            return offset2date(offset);
    }

    /**
     * 获取一个员工对应的值班结束日期
     *
     * @param label 员工
     * @return 开始日期
     */
    public LocalDate end(L label) {
        long offset = set.end(label);
        if (offset == -1)
            return null;
        else
            return offset2date(offset);
    }

    /**
     * 获取指定日期的员工
     *
     * @param date 日期
     * @return 对应日期的员工；不存在返回null
     */
    public L getEmployeeByDate(LocalDate date) {
        return set.getLabelByTime(date2offset(date));
    }

    /**
     * 查看未排班的天数
     *
     * @return 未排班的天数
     */
    public long countUnscheduledDate() {
        return set.countBlank();
    }

    /**
     * 检查排班是否完成
     *
     * @return 是否完成
     */
    public boolean checkFinished() {
        return set.checkNoBlank();
    }
}
