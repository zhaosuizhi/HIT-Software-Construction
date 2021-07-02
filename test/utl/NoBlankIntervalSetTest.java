package utl;

import adt.utl.IntervalSet;
import adt.utl.NoBlankIntervalSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

public class NoBlankIntervalSetTest {

    // 测试策略
    //   测试其中重载的方法

    @Test
    public void insertTest() {
        NoBlankIntervalSet<String> intervalSet = new NoBlankIntervalSet<>(IntervalSet.empty(), 10);

        // 正常插入
        Assert.assertTrue(intervalSet.insert(0, 2, "a"));
        Assert.assertTrue(intervalSet.insert(2, 4, "b"));
        Assert.assertEquals(2, intervalSet.end("a"));
        Assert.assertEquals(2, intervalSet.start("b"));

        // 插入相同标签
        Assert.assertFalse(intervalSet.insert(5, 6, "a"));

        // 插入非法时间段
        Assert.assertThrows(IllegalArgumentException.class, () -> intervalSet.insert(1, 0, "c"));
        Assert.assertThrows(IllegalArgumentException.class, () -> intervalSet.insert(1, 11, "c"));
        Assert.assertThrows(IllegalArgumentException.class, () -> intervalSet.insert(13, 14, "c"));

        // 最后观察，经过各种非法操作后，是否未改变其中信息
        Assert.assertEquals(new HashSet<>(Arrays.asList("a", "b")), intervalSet.labels());
        Assert.assertEquals(0, intervalSet.start("a"));
        Assert.assertEquals(4, intervalSet.end("b"));
    }

    @Test
    public void blankRateTest() {
        NoBlankIntervalSet<String> set = new NoBlankIntervalSet<>(IntervalSet.empty(), 9);
        set.insert(0, 1, "a");
        Assert.assertEquals(80.0, set.blankRate(), 0.001);
        set.insert(0, 2, "b");
        Assert.assertEquals(70.0, set.blankRate(), 0.001);
        set.insert(1, 4, "c");
        Assert.assertEquals(50.0, set.blankRate(), 0.001);
        set.insert(5, 9, "d");
        Assert.assertEquals(0.0, set.blankRate(), 0.001);
    }

}
