package adt;

import java.util.*;

/**
 * MultiIntervalSet的一个实现
 */
public class CommonMultiIntervalSet<L> implements MultiIntervalSet<L> {
    private final Map<L, IntervalSet<Integer>> intervalSetMap = new HashMap<>();
    private final LabelIdGenerator<L> idGenerator = new LabelIdGenerator<>();

    // 抽象函数:
    //   AF(intervalSetMap) = intervalSetMap是一个“标签->时间段集合”的满射
    //   每个时间段集合中包含了这个标签对应的全部时间段
    // 表示不变量:
    //   对于intervalSetMap中的每个时间段集，其中的时间段无冲突
    //   该不变量已经由IntervalSet在归约中保证，无需额外的checkRep()方法
    // 防止表示暴露:
    //   intervalSetMap为private final，无法修改引用
    //   在labels()方法中返回的值是可变类型，在返回时进行防御性复制
    //   其余方法中返回的均是不可变类型，不会发生表示暴露

    public CommonMultiIntervalSet() {
    }

    /**
     * 利用initial中包含的数据创建非空对象
     */
    public CommonMultiIntervalSet(final IntervalSet<L> initial) {
        Set<L> labelSet = initial.labels();
        for (L label : labelSet) {
            // 初始化该标签对应的时间段集合
            IntervalSet<Integer> set = new CommonIntervalSet<>();
            intervalSetMap.put(label, set);

            // 插入当前时间段
            set.insert(initial.start(label), initial.end(label), idGenerator.nextId(label));
        }
    }

    @Override
    public boolean insert(long start, long end, L label) {
        Interval.validInterval(start, end); // 非法输入时抛出IllegalArgumentException

        IntervalSet<Integer> set = intervalSetMap.get(label);
        if (set == null) { // 标签不存在，新建
            set = IntervalSet.empty();
            intervalSetMap.put(label, set);
        }

        return set.insert(start, end, idGenerator.nextId(label));
    }

    @Override
    public Set<L> labels() {
        return new HashSet<>(intervalSetMap.keySet());
    }

    @Override
    public boolean remove(L label) {
        return intervalSetMap.remove(label) != null;
    }

    @Override
    public IntervalSet<Integer> intervals(L label) {
        IntervalSet<Integer> set = intervalSetMap.get(label);
        if (set == null)
            return null;

        SortedSet<Interval> sortedInterval = getSortedInterval(set); // 获取有序的Interval序列

        IntervalSet<Integer> orderedIntervals = IntervalSet.empty(); // 待返回的整数集合
        int i = 0;
        for (Interval interval : sortedInterval) {
            /* 以i做标签插入时间段，而后i自增 */
            orderedIntervals.insert(interval.getStart(), interval.getEnd(), i++);
        }

        return orderedIntervals;
    }

    /**
     * 从intervalSet中导出所有时间段并按照升序排列
     *
     * @param intervalSet 时间段集
     * @param <L>         标签类型
     * @return 按照升序排列的时间段
     */
    static private <L> SortedSet<Interval> getSortedInterval(IntervalSet<L> intervalSet) {
        SortedSet<Interval> set = new TreeSet<>();

        Set<L> labelSet = intervalSet.labels();
        for (L label : labelSet) { // 对于每个标签，取出其时间段
            set.add(intervalSet.interval(label));
        }

        return set;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommonMultiIntervalSet<?> that = (CommonMultiIntervalSet<?>) o;
        return intervalSetMap.equals(that.intervalSetMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(intervalSetMap);
    }
}

/**
 * 根据标签生成不重复的自增序列
 * <p>起始值为1
 *
 * @param <L>标签类型
 */
final class LabelIdGenerator<L> {
    private final Map<L, Integer> labelIdMap = new HashMap<>();

    /**
     * 获取标签的下一个id
     *
     * @param label 标签
     * @return 下一个id
     */
    public int nextId(L label) {
        int nextId = labelIdMap.getOrDefault(label, 1); // 当标签不存在时，初始化id为1

        labelIdMap.put(label, nextId + 1);
        return nextId;
    }
}
