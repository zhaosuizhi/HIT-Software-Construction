import adt.CourseIntervalSet;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.*;

public class CourseIntervalSetTest {

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
}
