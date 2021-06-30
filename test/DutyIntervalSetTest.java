import adt.DutyIntervalSet;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

public class DutyIntervalSetTest {
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
                "c"
        ));

        Assert.assertTrue(set.checkFinished());
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
