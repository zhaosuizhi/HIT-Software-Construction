import adt.DutyIntervalSet;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

public class DutyIntervalSetTest {

    // 测试策略
    //    测试DutyIntervalSetSet中的所有public方法
    //    对于每个方法，划分等价类，每个等价类至少构造一个测试样例，对于频繁发生的情况，测试多组数据

    @Test
    public void addTest() {
        LocalDate startDate = LocalDate.parse("2021-01-01");
        LocalDate endDate = LocalDate.parse("2021-01-10");
        DutyIntervalSet<String> set = new DutyIntervalSet<>(startDate, endDate);

        Assert.assertTrue(set.add(
                LocalDate.parse("2021-01-01"),
                LocalDate.parse("2021-01-01"),
                "a"
        ));
        Assert.assertTrue(set.add(
                LocalDate.parse("2021-01-02"),
                LocalDate.parse("2021-01-02"),
                "b"
        ));
        Assert.assertTrue(set.add(
                LocalDate.parse("2021-01-03"),
                LocalDate.parse("2021-01-10"),
                "a"
        ));

        Assert.assertEquals(0, set.unscheduledRate(), 0.001);
    }

    @Test
    public void labelsTest() {
        LocalDate startDate = LocalDate.parse("2021-01-01");
        LocalDate endDate = LocalDate.parse("2021-01-10");
        DutyIntervalSet<String> set = new DutyIntervalSet<>(startDate, endDate);

        set.add(LocalDate.parse("2021-01-01"), LocalDate.parse("2021-01-01"), "a");
        set.add(LocalDate.parse("2021-01-02"), LocalDate.parse("2021-01-02"), "b");
        set.add(LocalDate.parse("2021-01-03"), LocalDate.parse("2021-01-04"), "c");

        Assert.assertEquals(
                new HashSet<>(Arrays.asList("a", "b", "c")),
                set.labels()
        );
    }

    @Test
    public void removeTest() {
        LocalDate startDate = LocalDate.parse("2021-01-01");
        LocalDate endDate = LocalDate.parse("2021-01-10");
        DutyIntervalSet<String> set = new DutyIntervalSet<>(startDate, endDate);

        set.add(LocalDate.parse("2021-01-01"), LocalDate.parse("2021-01-01"), "a");
        set.add(LocalDate.parse("2021-01-02"), LocalDate.parse("2021-01-02"), "b");
        set.add(LocalDate.parse("2021-01-03"), LocalDate.parse("2021-01-04"), "c");

        Assert.assertTrue(set.remove("a"));
        Assert.assertEquals(
                new HashSet<>(Arrays.asList("b", "c")),
                set.labels()
        );

        Assert.assertFalse(set.remove("a"));
    }
}
