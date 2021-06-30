package adt.utl;

import java.util.Set;

/**
 * 一组在时间轴上分布的时间段（interval）构成成的集合
 * <p>Mutable
 * <p>每个时间段附着一个特定的标签，且标签不重复
 *
 * @param <L> 时间段标签的类型
 */
public abstract class IntervalSet<L> {

    /**
     * 创建一个新的时间段集
     *
     * @param <L> 时间段标签的类型
     * @return 标签类型为L的时间段集
     */
    public static <L> IntervalSet<L> empty() {
        return new CommonIntervalSet<>();
    }

    /**
     * 插入新的时间段和标签
     *
     * @param start 开始时间
     * @param end   结束时间
     * @param label 标签
     * @return 是否添加成功，若失败说明存在冲突
     * @throws NullPointerException     当label == null时
     * @throws IllegalArgumentException 当start, end不满足0 <= start <= end时
     */
    abstract public boolean insert(long start, long end, L label) throws NullPointerException, IllegalArgumentException;

    /**
     * 获得当前的标签集合
     *
     * @return 标签集合
     */
    abstract public Set<L> labels();

    /**
     * 移除某个标签所关联的时间段
     *
     * @param label 被移除时间段的标签
     * @return 移除返回true，时间段不存在返回null
     */
    abstract public boolean remove(L label);

    /**
     * 获得标签对应时间段的开始时间
     *
     * @param label 标签
     * @return 对应时间段的开始时间；若不存在返回-1
     */
    abstract public long start(L label);

    /**
     * 获得标签对应时间段的结束时间
     *
     * @param label 标签
     * @return 对应时间段的结束时间；若不存在返回-1
     */
    abstract public long end(L label);

    /**
     * 获得标签对应的时间段
     * <p>包内其它类可使用，方便利用{@link Interval}获取值
     *
     * @param label 标签
     * @return 对应的时间段；若不存在返回null
     */
    abstract protected Interval interval(L label);
}
