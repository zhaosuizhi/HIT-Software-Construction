package application;

import adt.ProcessIntervalSet;
import adt.utl.MultiIntervalSet;
import io.RobustScanner;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class ProcessScheduleApp {

    private final ProcessIntervalSet<Process> pis = new ProcessIntervalSet<>(MultiIntervalSet.empty());
    private final Map<Process, Long> durationMap = new HashMap<>(); // 进程->运行时间的映射
    private final RobustScanner scanner; // 健壮的输入流
    private int pc; // 程序计数器
    private Process currentProcess = null; // 当前运行进程

    /**
     * @param scanner 输入流
     */
    public ProcessScheduleApp(RobustScanner scanner) {
        this.scanner = scanner;
    }

    /**
     * 通过进程ID查找进程
     *
     * @return 进程对象；不存在返回null
     */
    private Process getProcessById(int id) {
        for (Process p : durationMap.keySet()) {
            if (p.getPid() == id)
                return p;
        }
        return null;
    }

    /**
     * 新增一个进程
     */
    public void addProcess() {
        int id;
        String name;
        long minLast, maxLast;

        System.out.print("请输入进程ID：");
        id = scanner.nextInt();
        if (getProcessById(id) != null) {
            System.out.println("ID重复！");
            return;
        }

        System.out.print("请输入进程名称：");
        name = scanner.nextNotEmptyString("进程名称");

        System.out.print("请输入最小运行时间：");
        minLast = scanner.nextLong();
        System.out.print("请输入最大运行时间：");
        maxLast = scanner.nextLong();

        Process p = new Process(id, name, minLast, maxLast); // 创建进程
        durationMap.put(p, 0L);
    }

    /**
     * 进行一次模拟运行
     */
    public void start() {
        System.out.print("请输入运行的时长：");
        long duration = scanner.nextLong();
        if (duration <= 0) {
            System.out.println("运行时长必须>0！");
            return;
        }

        do {
            /* 首先选出最早结束的进程 */
            Process earliestFinishProcess = null;
            long leftTime = -1; // 最早结束的进程的剩余执行时间
            for (Process p : durationMap.keySet()) {
                long currentLeft = p.getMinDuration() - durationMap.get(p);
                if (leftTime < 0 || currentLeft < leftTime) {
                    earliestFinishProcess = p;
                    leftTime = currentLeft;
                }
            }

            /* 执行该进程 */
            if (earliestFinishProcess == null) { // 无进程可执行，剩余时间空闲
                currentProcess = null;

                pc += duration;
                duration = 0;
            } else {
                currentProcess = earliestFinishProcess;

                long runTime = Math.min(duration, leftTime); // 此次运行时间

                pis.insert(pc, pc + runTime - 1, earliestFinishProcess);
                if (duration >= leftTime) // 该进程执行完毕，移出
                    durationMap.remove(earliestFinishProcess);

                pc += runTime; // 增加PC
                duration -= runTime; // 减少剩余时间
            }
        } while (duration > 0); // 当剩余运行时间>0时
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%-6s ", "时刻"))
                .append(String.format("%-7s ", "进程"))
                .append('\n');

        for (long i = 0; i < pc; i++) {
            sb.append(String.format("%-7d ", i));

            Process p = pis.getLabelByTime(i);
            if (p == null) // 空闲
                sb.append("—");
            else
                sb.append(p);
            sb.append('\n');
        }

        sb.append("当前正在执行的进程：");
        if (currentProcess == null)
            sb.append("无");
        else
            sb.append(currentProcess);

        return sb.toString();
    }

    /**
     * 在控制台打印目录，并从输入流读取用户输入
     *
     * @return 用户输入的编号；若不合法返回-1
     */
    public int menu() {
        System.out.println("1. 查看进程调度结果");
        if (pc == 0)
            System.out.println("2. 开始运行");
        else
            System.out.println("2. 继续运行");
        System.out.println("3. 添加进程");
        System.out.println("0. 退出");

        return scanner.nextInt();
    }

    public static void main(String[] args) {
        RobustScanner scanner = new RobustScanner(new Scanner(System.in));

        ProcessScheduleApp app = new ProcessScheduleApp(scanner);

        int num; // 操作编号
        while ((num = app.menu()) != 0) {
            try {
                switch (num) {
                    case 1:
                        System.out.println(app);
                        System.out.println();
                        break;
                    case 2:
                        app.start();
                        System.out.println();
                        break;
                    case 3:
                        app.addProcess();
                        System.out.println();
                        break;
                    default:
                        System.out.println("输入错误！");
                        System.out.println();
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("发生错误，程序退出！");
                System.exit(1);
            }
        }
    }
}

/**
 * 表示一个进程
 * <p>Immutable
 */
class Process {
    private final int pid; // 进程ID
    private final String name; // 进程名称
    private final long minDuration; // 最短执行时间
    private final long maxDuration; // 最长执行时间

    Process(int pid, String name, long minDuration, long maxDuration) {
        this.pid = pid;
        this.name = name;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
    }

    /**
     * 获取进程ID
     *
     * @return 进程ID
     */
    public int getPid() {
        return pid;
    }

    /**
     * 获取进程名称
     *
     * @return 进程名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取进程最短持续时间
     *
     * @return 最短持续时间
     */
    public long getMinDuration() {
        return minDuration;
    }

    /**
     * 获取进程最长持续时间
     *
     * @return 最长持续时间
     */
    public long getMaxDuration() {
        return maxDuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Process process = (Process) o;
        return pid == process.pid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pid);
    }

    @Override
    public String toString() {
        return String.format("Process{ID=%d, name=%s}", pid, name);
    }
}
