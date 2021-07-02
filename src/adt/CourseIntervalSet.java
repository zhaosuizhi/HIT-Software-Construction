package adt;

import adt.utl.IntervalSet;
import adt.utl.MultiIntervalSet;
import adt.utl.NoBlankMultiIntervalSet;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 课程安排集合
 *
 * @param <L> 课程
 */
public class CourseIntervalSet<L> {

    private final NoBlankMultiIntervalSet<L> set;
    private final LocalDate startDate;
    private final LocalDate endDate;

    // 抽象函数:
    //   AF(set, startDate, endDate) = 一个学期的课程安排，学期日期范围[startDate, endDate]
    //       set的时间范围为[0, 34]，代表一周的每天5个、一周7天、共35个上课时间段
    //       星期一的5个上课时间段分别对应时间段[0,0]，[1,1]，[2,2]，[3,3]，[4,4]，以此类推，直到一周的最后一节课[34,34]
    // 表示不变量:
    //   set != null
    //   startDate与endDate相差的天数 mod 7 == 6
    // 防止表示暴露:
    //   LocalDate是Immutable类型，不会发生表示暴露
    //   各方法返回值或者是新申请的对象、或者委托给set

    private void checkRep() {
        assert set != null;
        assert startDate.until(endDate, ChronoUnit.DAYS) % 7 == 6;
    }

    /**
     * 判断给定日期是否在当前学期中
     *
     * @param date 日期
     * @return 是否属于本学期
     */
    public boolean dateInSemester(LocalDate date) {
        return !(date.isBefore(startDate) || date.isAfter(endDate));
    }

    /**
     * @param startDate 开始日期
     * @param weekCNT   总周数
     * @throws IllegalArgumentException 当 weekCNT <= 0 时
     */
    public CourseIntervalSet(LocalDate startDate, long weekCNT) throws IllegalArgumentException {
        if (weekCNT <= 0)
            throw new IllegalArgumentException("总周数必须大于0！");

        // 每天最多5节课，每周有7天
        set = new NoBlankMultiIntervalSet<>(MultiIntervalSet.empty(), 5 * 7 - 1);

        this.startDate = startDate;
        this.endDate = startDate.plusDays(weekCNT * 7).minusDays(1); // 持续到最后一周周日

        checkRep();
    }

    /**
     * 将日期转换为距开始日期的偏移量
     *
     * @param date 日期
     * @return 对应的偏移量
     */
    private long date2offset(LocalDate date, int num) {
        return (startDate.until(date, ChronoUnit.DAYS) % 7) * 5 + num;
    }


    /**
     * 安排一次课
     *
     * @param date  上课日期
     * @param num   第几节课，可取值：0,1,2,3,4
     * @param label 课程
     * @return 是否添加成功
     * @throws NullPointerException     当label == null时
     * @throws IllegalArgumentException 当num不在0~4之间时
     */
    public boolean add(LocalDate date, int num, L label) throws NullPointerException, IllegalArgumentException {
        if (num < 0 || num > 4)
            throw new IllegalArgumentException("num必须在0~4之间！");

        long offset = date2offset(date, num);
        return set.insert(offset, offset, label);
    }

    /**
     * 获取一天的课程安排
     *
     * @param date 日期
     * @return 一个长度为5的列表，分别对应这天的5个上课时间段；
     * <p>每个元素是一个{@link Set}，其中存放了这一时间段的所有课程；元素为空说明该时间段无课程。
     * <p>如果日期不在本学期，返回null
     */
    public List<Set<L>> getDateSchedule(LocalDate date) {
        if (!dateInSemester(date))
            return null;

        List<Set<L>> list = new ArrayList<>();

        long offset = date2offset(date, 0);
        for (int i = 0; i < 5; i++) {
            Set<L> today = new HashSet<>();
            for (L label : set.labels()) {
                IntervalSet<Integer> intervals = set.intervals(label); // 该课程对应的所有时间
                if (intervals.getLabelByTime(offset + i) != null) // 有课
                    today.add(label);
            }
            list.add(today);
        }

        return list;
    }

    /**
     * 获取所有课程
     *
     * @return 所有课程构成的集合
     */
    public Set<L> labels() {
        return set.labels();
    }

    /**
     * 统计未排班课时数占总课时数的比例
     *
     * @return 未排课比例
     */
    public double unscheduledRate() {
        return set.blankRate();
    }

    /**
     * 统计课程间冲突的比例
     *
     * @return 重复比例
     */
    public double overlapRate() {
        return set.overlapRate();
    }
}
