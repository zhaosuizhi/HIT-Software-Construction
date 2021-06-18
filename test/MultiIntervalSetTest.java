import adt.CommonMultiIntervalSet;
import adt.IntervalSet;
import adt.MultiIntervalSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class MultiIntervalSetTest {

    @Test
    public void constructorTest() {
        // 测试工厂方法
        MultiIntervalSet.empty();

        // 测试默认构造函数
        new CommonMultiIntervalSet<>();

        // 测试initial构造函数
        IntervalSet<String> expected = IntervalSet.empty();
        expected.insert(0, 1, "a");
        expected.insert(2, 3, "b");
        expected.insert(3, 5, "c");

        MultiIntervalSet<String> set = new CommonMultiIntervalSet<>(expected);

        Assert.assertEquals(new HashSet<>(Arrays.asList("a", "b", "c")), set.labels());

        IntervalSet<Integer> aInterval = IntervalSet.empty();
        aInterval.insert(0, 1, 0);
        Assert.assertEquals(aInterval, set.intervals("a"));

        IntervalSet<Integer> cInterval = IntervalSet.empty();
        cInterval.insert(3, 5, 0);
        Assert.assertEquals(cInterval, set.intervals("c"));
    }

    @Test
    public void insertTest() {
        MultiIntervalSet<String> set = MultiIntervalSet.empty();

        // 正常插入
        Assert.assertTrue(set.insert(0, 1, "a"));
        Assert.assertTrue(set.insert(2, 3, "a"));
        Assert.assertTrue(set.insert(1, 3, "b"));

        // 插入冲突时间段
        Assert.assertFalse(set.insert(2, 5, "b"));

        // 插入非法时间段
        Assert.assertThrows(IllegalArgumentException.class, () -> set.insert(1, 0, "c"));
        Assert.assertThrows(IllegalArgumentException.class, () -> set.insert(1, 0, "c"));

        // 最后观察，经过各种非法操作后，是否未改变其中信息
        Assert.assertEquals(new HashSet<>(Arrays.asList("a", "b")), set.labels());
    }

    @Test
    public void labelsTest() {
        MultiIntervalSet<String> set = MultiIntervalSet.empty();

        // 进行多种mutate操作，调用labels()方法查看返回值是否符合预期

        set.insert(0, 1, "a");
        set.insert(2, 3, "a");
        set.insert(1, 3, "b");
        Assert.assertEquals(new HashSet<>(Arrays.asList("a", "b")), set.labels());

        set.remove("a");
        Assert.assertEquals(new HashSet<>(Collections.singletonList("b")), set.labels());

        set.remove("b");
        Assert.assertEquals(new HashSet<>(), set.labels());
    }

    @Test
    public void removeTest() {
        MultiIntervalSet<String> set = MultiIntervalSet.empty();

        set.insert(0, 1, "a");
        set.insert(2, 3, "a");
        set.insert(1, 3, "b");

        Assert.assertTrue(set.remove("b"));
        Assert.assertEquals(new HashSet<>(Collections.singletonList("a")), set.labels());
    }

    @Test
    public void intervalsTest() {
        MultiIntervalSet<String> set = MultiIntervalSet.empty();

        set.insert(7, 8, "a");
        set.insert(0, 1, "a");
        set.insert(5, 7, "a");
        set.insert(2, 3, "a");

        IntervalSet<Integer> expected = IntervalSet.empty();
        expected.insert(0, 1, 0);
        expected.insert(2, 3, 1);
        expected.insert(5, 7, 2);
        expected.insert(7, 8, 3);
        Assert.assertEquals(expected, set.intervals("a"));
    }
}
