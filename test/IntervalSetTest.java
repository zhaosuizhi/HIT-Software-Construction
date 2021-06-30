import adt.utl.IntervalSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class IntervalSetTest {
    @Test
    public void insertTest() {
        IntervalSet<String> intervalSet = IntervalSet.empty();

        // 正常插入
        Assert.assertTrue(intervalSet.insert(0, 2, "a"));
        Assert.assertTrue(intervalSet.insert(2, 4, "b"));
        Assert.assertEquals(2, intervalSet.end("a"));
        Assert.assertEquals(2, intervalSet.start("b"));

        // 插入相同标签
        Assert.assertFalse(intervalSet.insert(5, 6, "a"));

        // 插入冲突时间段
        Assert.assertFalse(intervalSet.insert(1, 3, "c"));

        // 插入非法时间段
        Assert.assertThrows(IllegalArgumentException.class, () -> intervalSet.insert(1, 0, "c"));
        Assert.assertThrows(IllegalArgumentException.class, () -> intervalSet.insert(1, 0, "c"));

        // 最后观察，经过各种非法操作后，是否未改变其中信息
        Assert.assertEquals(new HashSet<>(Arrays.asList("a", "b")), intervalSet.labels());
        Assert.assertEquals(0, intervalSet.start("a"));
        Assert.assertEquals(4, intervalSet.end("b"));
    }

    @Test
    public void labelsTest() {
        IntervalSet<String> intervalSet = IntervalSet.empty();

        // 对时间段集合进行多次修改，判断labels方法的返回值是否符合预期
        Assert.assertEquals(new HashSet<>(), intervalSet.labels());

        intervalSet.insert(0, 2, "a");
        intervalSet.insert(2, 4, "b");
        Assert.assertEquals(new HashSet<>(Arrays.asList("a", "b")), intervalSet.labels());

        intervalSet.remove("a");
        Assert.assertEquals(new HashSet<>(Collections.singletonList("b")), intervalSet.labels());

        intervalSet.insert(5, 7, "c");
        Assert.assertEquals(new HashSet<>(Arrays.asList("b", "c")), intervalSet.labels());
    }

    @Test
    public void removeTest() {
        IntervalSet<String> intervalSet = IntervalSet.empty();

        intervalSet.insert(0, 2, "a");
        intervalSet.insert(6, 7, "b");
        intervalSet.insert(2, 5, "c");

        // 以防万一，先验证labels符合预期
        Assert.assertEquals(new HashSet<>(Arrays.asList("a", "b", "c")), intervalSet.labels());

        // 正常删除
        Assert.assertTrue(intervalSet.remove("b"));
        Assert.assertEquals(new HashSet<>(Arrays.asList("a", "c")), intervalSet.labels());
        Assert.assertTrue(intervalSet.remove("c"));
        Assert.assertEquals(new HashSet<>(Collections.singletonList("a")), intervalSet.labels());

        // 删除不存在的标签
        Assert.assertFalse(intervalSet.remove("c"));
        Assert.assertEquals(new HashSet<>(Collections.singletonList("a")), intervalSet.labels());
        Assert.assertEquals(2, intervalSet.end("a"));
        Assert.assertEquals(0, intervalSet.start("a"));
    }

    @Test
    public void startTest() {
        IntervalSet<String> intervalSet = IntervalSet.empty();

        // 正常插入
        intervalSet.insert(0, 2, "a");
        Assert.assertEquals(0, intervalSet.start("a"));

        // 多次插入后有没有改变原值
        intervalSet.insert(6, 7, "b");
        intervalSet.insert(2, 5, "c");
        Assert.assertEquals(0, intervalSet.start("a"));
        Assert.assertEquals(6, intervalSet.start("b"));

        // 删除有没有改变其它标签的值
        intervalSet.remove("b");
        Assert.assertEquals(2, intervalSet.start("c"));

        // 删除不存在的标签能否正确返回-1
        Assert.assertEquals(-1, intervalSet.start("b"));
    }

    @Test
    public void endTest() {
        IntervalSet<String> intervalSet = IntervalSet.empty();

        // 正常插入
        intervalSet.insert(0, 2, "a");
        Assert.assertEquals(2, intervalSet.end("a"));

        // 多次插入后有没有改变原值
        intervalSet.insert(6, 7, "b");
        intervalSet.insert(2, 5, "c");
        Assert.assertEquals(2, intervalSet.end("a"));
        Assert.assertEquals(7, intervalSet.end("b"));

        // 删除有没有改变其它标签的值
        intervalSet.remove("b");
        Assert.assertEquals(5, intervalSet.end("c"));

        // 删除不存在的标签能否正确返回-1
        Assert.assertEquals(-1, intervalSet.end("b"));
    }
}
