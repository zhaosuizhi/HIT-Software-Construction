package utl;

import adt.utl.MultiIntervalSet;
import adt.utl.NoBlankMultiIntervalSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

public class NoBlankMultiIntervalSetTest {

    // 测试策略
    //    测试NoBlankMultiIntervalSet中的构造函数和各个方法
    //    对于每个接口，划分等价类，每个等价类至少构造一个测试样例，对于频繁发生的情况，测试多组数据

    @Test
    public void constructorTest() {
        new NoBlankMultiIntervalSet<String>(MultiIntervalSet.empty(), 10);
    }


    @Test
    public void insertTest() {
        MultiIntervalSet<String> set = new NoBlankMultiIntervalSet<>(MultiIntervalSet.empty(), 9);

        // 正常插入
        Assert.assertTrue(set.insert(0, 1, "a"));
        Assert.assertTrue(set.insert(2, 3, "a"));
        Assert.assertTrue(set.insert(1, 3, "b"));

        // 插入冲突时间段
        Assert.assertTrue(set.insert(2, 5, "b"));

        // 插入非法时间段
        Assert.assertThrows(IllegalArgumentException.class, () -> set.insert(1, 0, "c"));
        Assert.assertThrows(IllegalArgumentException.class, () -> set.insert(10, 11, "c"));

        // 最后观察，经过各种非法操作后，是否未改变其中信息
        Assert.assertEquals(new HashSet<>(Arrays.asList("a", "b")), set.labels());
    }

    @Test
    public void blankRateTest() {
        NoBlankMultiIntervalSet<String> set = new NoBlankMultiIntervalSet<>(MultiIntervalSet.empty(), 9);
        set.insert(0, 1, "a");
        Assert.assertEquals(80.0, set.blankRate(), 0.001);
        set.insert(0, 2, "b");
        Assert.assertEquals(70.0, set.blankRate(), 0.001);
        set.insert(1, 4, "c");
        Assert.assertEquals(50.0, set.blankRate(), 0.001);
        set.insert(3, 9, "a");
        Assert.assertEquals(0.0, set.blankRate(), 0.001);
    }

    @Test
    public void overlapRateTest() {
        NoBlankMultiIntervalSet<String> set = new NoBlankMultiIntervalSet<>(MultiIntervalSet.empty(), 9);
        set.insert(0, 1, "a");
        Assert.assertEquals(0.0, set.overlapRate(), 0.001);
        set.insert(0, 2, "b");
        Assert.assertEquals(20.0, set.overlapRate(), 0.001);
        set.insert(1, 4, "c");
        Assert.assertEquals(30.0, set.overlapRate(), 0.001);
        set.insert(3, 9, "a");
        Assert.assertEquals(50.0, set.overlapRate(), 0.001);
    }
}
