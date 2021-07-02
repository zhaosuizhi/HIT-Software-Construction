import adt.CourseIntervalSet;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.*;

public class CourseIntervalSetTest {

    // 测试策略
    //    测试CourseIntervalSet中的所有public方法
    //    对于每个方法，划分等价类，每个等价类至少构造一个测试样例，对于频繁发生的情况，测试多组数据

    @Test
    public void dateInSemesterTest() {
        CourseIntervalSet<String> set = new CourseIntervalSet<>(LocalDate.parse("2021-01-01"), 2);

        // 属于这学期的日期
        Assert.assertTrue(set.dateInSemester(LocalDate.parse("2021-01-01")));
        Assert.assertTrue(set.dateInSemester(LocalDate.parse("2021-01-05")));
        Assert.assertTrue(set.dateInSemester(LocalDate.parse("2021-01-12")));
        Assert.assertTrue(set.dateInSemester(LocalDate.parse("2021-01-14")));

        // 不属于这学期的日期
        Assert.assertFalse(set.dateInSemester(LocalDate.parse("2020-12-31")));
        Assert.assertFalse(set.dateInSemester(LocalDate.parse("2021-01-15")));
    }

    @Test
    public void addTest() {
        CourseIntervalSet<String> set = new CourseIntervalSet<>(LocalDate.parse("2021-01-01"), 2);

        // 正确插入课程
        Assert.assertTrue(set.add(LocalDate.parse("2021-01-01"), 1, "a"));
        Assert.assertTrue(set.add(LocalDate.parse("2021-01-02"), 2, "a"));

        // 插入非法课程
        Assert.assertThrows(IllegalArgumentException.class, () -> set.add(LocalDate.parse("2021-01-10"), 5, "a"));
        Assert.assertThrows(IllegalArgumentException.class, () -> set.add(LocalDate.parse("2021-01-10"), -1, "a"));
    }

    @Test
    public void getDateScheduleTest() {
        CourseIntervalSet<String> set = new CourseIntervalSet<>(LocalDate.parse("2021-01-01"), 10);

        set.add(LocalDate.parse("2021-01-03"), 0, "math");
        set.add(LocalDate.parse("2021-01-03"), 0, "pe");
        set.add(LocalDate.parse("2021-01-03"), 2, "computer");
        List<Set<String>> res;

        res = set.getDateSchedule(LocalDate.parse("2021-01-01")); // 应当无课程
        Assert.assertEquals(5, res.size());
        for (Set<String> daily : res) {
            Assert.assertTrue(daily.isEmpty());
        }

        res = set.getDateSchedule(LocalDate.parse("2021-01-03"));
        Assert.assertEquals(5, res.size());
        Assert.assertEquals(new HashSet<>(Arrays.asList("math", "pe")), res.get(0));
        Assert.assertTrue(res.get(1).isEmpty());
        Assert.assertEquals(new HashSet<>(Collections.singletonList("computer")), res.get(2));
        Assert.assertTrue(res.get(3).isEmpty());
        Assert.assertTrue(res.get(4).isEmpty());
    }

    @Test
    public void blankRateTest() {
        CourseIntervalSet<String> set = new CourseIntervalSet<>(LocalDate.parse("2021-01-01"), 1);
        set.add(LocalDate.parse("2021-01-01"), 0, "a");
        set.add(LocalDate.parse("2021-01-01"), 1, "a");
        set.add(LocalDate.parse("2021-01-01"), 2, "a");
        set.add(LocalDate.parse("2021-01-01"), 3, "a");
        set.add(LocalDate.parse("2021-01-01"), 4, "a");
        Assert.assertEquals(6.0 / 7 * 100, set.unscheduledRate(), 0.001);

        set.add(LocalDate.parse("2021-01-01"), 0, "b");
        set.add(LocalDate.parse("2021-01-01"), 1, "c");
        set.add(LocalDate.parse("2021-01-01"), 4, "d");
        Assert.assertEquals(6.0 / 7 * 100, set.unscheduledRate(), 0.001);
    }

    @Test
    public void overlapRateTest() {
        CourseIntervalSet<String> set = new CourseIntervalSet<>(LocalDate.parse("2021-01-01"), 1);
        set.add(LocalDate.parse("2021-01-01"), 1, "a");
        Assert.assertEquals(0.0, set.overlapRate(), 0.001);
        set.add(LocalDate.parse("2021-01-01"), 2, "b");
        Assert.assertEquals(0.0, set.overlapRate(), 0.001);
        set.add(LocalDate.parse("2021-01-01"), 1, "b");
        Assert.assertEquals(2.857, set.overlapRate(), 0.001);
    }
}
