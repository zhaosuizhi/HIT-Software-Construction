package utl;

import adt.utl.MultiIntervalSet;
import adt.utl.NonOverlapMultiIntervalSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class NonOverlapMultiIntervalSetTest {

    // 测试策略
    //    测试NonOverlapMultiIntervalSetTest中重载和新增的方法
    //    对于每个接口，划分等价类，每个等价类至少构造一个测试样例，对于频繁发生的情况，测试多组数据

    @Test
    public void insertTest() {
        NonOverlapMultiIntervalSet<String> set = new NonOverlapMultiIntervalSet<>(MultiIntervalSet.empty());

        // 正常插入
        Assert.assertTrue(set.insert(0, 1, "a"));
        Assert.assertTrue(set.insert(2, 3, "a"));

        // 插入冲突时间段
        Assert.assertFalse(set.insert(1, 3, "b"));
        Assert.assertFalse(set.insert(3, 5, "b"));

        // 插入非法时间段
        Assert.assertThrows(IllegalArgumentException.class, () -> set.insert(1, 0, "c"));
        Assert.assertThrows(IllegalArgumentException.class, () -> set.insert(1, 0, "c"));

        // 最后观察，经过各种非法操作后，是否未改变其中信息
        Assert.assertEquals(new HashSet<>(Collections.singletonList("a")), set.labels());
    }

    @Test
    public void getLabelByTimeTest() {
        NonOverlapMultiIntervalSet<String> set = new NonOverlapMultiIntervalSet<>(MultiIntervalSet.empty());

        set.insert(0, 1, "a");
        set.insert(3, 4, "b");

        List<String> expected = Arrays.asList("a", "a", null, "b", "b", null);
        for (int i = 0; i < 6; i++) {
            Assert.assertEquals(expected.get(i), set.getLabelByTime(i));
        }
    }
}
