import adt.utl.Api;
import adt.utl.IntervalSet;
import adt.utl.MultiIntervalSet;
import org.junit.Assert;
import org.junit.Test;

public class ApiTest {

    @Test
    public void similarityTest() {
        MultiIntervalSet<String> set1 = MultiIntervalSet.empty();
        MultiIntervalSet<String> set2 = MultiIntervalSet.empty();

        set1.insert(0, 5, "A");
        set1.insert(20, 25, "A");
        set1.insert(10, 20, "B");
        set1.insert(25, 30, "B");

        set2.insert(20, 35, "A");
        set2.insert(10, 20, "B");
        set2.insert(0, 5, "C");

        Assert.assertEquals(0.42857, Api.similarity(set1, set2), 0.001);
    }

    @Test
    public void calcConflictRatioTest() {
        IntervalSet<String> set = IntervalSet.empty();

        Assert.assertTrue(set.insert(0, 5, "A"));
        Assert.assertTrue(set.insert(10, 20, "B"));
        Assert.assertTrue(set.insert(4, 14, "C"));

        Assert.assertEquals(0.35, Api.calcConflictRatio(set), 0.001);
    }

    @Test
    public void calcConflictRatioMultiTest() {
        MultiIntervalSet<String> set = MultiIntervalSet.empty();

        set.insert(0, 5, "A");
        set.insert(20, 25, "A");
        set.insert(10, 20, "B");
        set.insert(25, 30, "B");

        Assert.assertEquals(0, Api.calcConflictRatio(set), 0.001);
    }

    @Test
    public void calcFreeTimeRatioTest() {
        IntervalSet<String> set = IntervalSet.empty();

        Assert.assertTrue(set.insert(0, 2, "A"));
        Assert.assertTrue(set.insert(10, 20, "B"));
        Assert.assertTrue(set.insert(4, 14, "C"));

        Assert.assertEquals((double) 1 / 21, Api.calcFreeTimeRatio(set), 0.001);
    }

    @Test
    public void calcFreeTimeRatioMultiTest() {
        MultiIntervalSet<String> set = MultiIntervalSet.empty();

        Assert.assertTrue(set.insert(0, 2, "A"));
        Assert.assertTrue(set.insert(12, 14, "A"));
        Assert.assertTrue(set.insert(10, 20, "B"));
        Assert.assertTrue(set.insert(4, 14, "C"));

        Assert.assertEquals((double) 1 / 21, Api.calcFreeTimeRatio(set), 0.001);
    }
}
