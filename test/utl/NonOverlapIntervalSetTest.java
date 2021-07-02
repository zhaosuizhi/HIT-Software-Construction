package utl;

import adt.utl.IntervalSet;
import adt.utl.NonOverlapIntervalSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

public class NonOverlapIntervalSetTest {

    // 测试策略
    //    测试NonOverlapIntervalSet中重载的方法
    //    对于每个接口，划分等价类，每个等价类至少构造一个测试样例，对于频繁发生的情况，测试多组数据

    @Test
    public void insertTest() {
        NonOverlapIntervalSet<String> set = new NonOverlapIntervalSet<>(IntervalSet.empty());

        // 正常插入
        Assert.assertTrue(set.insert(0, 1, "a"));
        Assert.assertTrue(set.insert(2, 3, "b"));

        // 重复插入
        Assert.assertFalse(set.insert(3, 3, "a"));

        // 插入冲突时间段
        Assert.assertFalse(set.insert(2, 5, "c"));

        // 插入非法时间段
        Assert.assertThrows(IllegalArgumentException.class, () -> set.insert(1, 0, "c"));
        Assert.assertThrows(IllegalArgumentException.class, () -> set.insert(1, 0, "c"));

        // 最后观察，经过各种非法操作后，是否未改变其中信息
        Assert.assertEquals(new HashSet<>(Arrays.asList("a", "b")), set.labels());
    }

}
