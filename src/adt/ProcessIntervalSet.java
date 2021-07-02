package adt;

import adt.utl.IntervalSet;
import adt.utl.MultiIntervalSet;
import adt.utl.NonOverlapMultiIntervalSet;

import java.util.Set;

/**
 * 进程时间段集合
 *
 * @param <L> 表示进程的类型
 */
public class ProcessIntervalSet<L> extends NonOverlapMultiIntervalSet<L> {

    // 无额外成员变量，因此AF与RI同父类
    // 防止表示暴露:
    //   均委托给NonOverlapMultiIntervalSet类

    public ProcessIntervalSet(MultiIntervalSet<L> set) {
        super(set);
    }

    /**
     * 设置进程在[start, end]时间段上运行
     *
     * @param start 开始时间
     * @param end   结束时间
     * @param label 标签
     * @return 是否添加成功，若失败说明存在冲突
     * @throws NullPointerException     当label == null时
     * @throws IllegalArgumentException 当start, end不满足0 <= start <= end时
     */
    @Override
    public boolean insert(long start, long end, L label) {
        return super.insert(start, end, label);
    }

    /**
     * 获得所有进程构成的集合
     *
     * @return 进程集合
     */
    @Override
    public Set<L> labels() {
        return super.labels();
    }

    /**
     * 移除某个进程的所有运行时间
     *
     * @param label 被移除的进程
     * @return 移除返回true，进程不存在返回null
     */
    @Override
    public boolean remove(L label) {
        return super.remove(label);
    }

    /**
     * 获取进程运行的所有时间段
     * <p>返回结果表达为{@code IntervalSet<Integer>}的形式，其中的时间段按开始时间从小到大的次序排列。
     * <p>例如：当前对象为{ "A"=[[0,10],[20,30]], "B"=[[10,20]] }，
     * 那么{@code intervals("A")}返回的结果是{ 0=[0,10],1=[20,30] }。
     *
     * @param label 待查看的进程
     * @return 进程运行的所有时间段；不存在返回null
     */
    @Override
    public IntervalSet<Integer> intervals(L label) {
        return super.intervals(label);
    }

    /**
     * 获取指定时刻正在运行的进程
     *
     * @param time 时刻
     * @return 对应正在运行的进程；空闲返回null
     */
    @Override
    public L getLabelByTime(long time) {
        return super.getLabelByTime(time);
    }
}
