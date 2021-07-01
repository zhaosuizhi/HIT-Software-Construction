package application;

import adt.DutyIntervalSet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 排班管理系统
 * <p>每个public方法都是void类型，对应了菜单中的一个操作
 */
public class DutyRosterApp {
    DutyIntervalSet<Employee> dutySet; // 排班表
    Set<Employee> employeeSet; // 员工表
    LocalDate startDate; // 开始时间
    LocalDate endDate; // 结束时间
    Scanner scanner; // 输入流

    /**
     * @param start 开始日期
     * @param end   结束日期
     */
    public DutyRosterApp(LocalDate start, LocalDate end) {
        dutySet = new DutyIntervalSet<>(start, end);
        employeeSet = new HashSet<>();
        startDate = start;
        endDate = end;
        scanner = new Scanner(System.in);
    }

    /**
     * 根据名字查找员工
     *
     * @param name 名字
     * @return 员工；不存在返回null
     */
    private Employee getEmployeeByName(String name) {
        for (Employee e : employeeSet)
            if (e.getName().equals(name))
                return e;
        return null;
    }

    /**
     * 返回指定时间段内的排班信息组成的字符串
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 排班表
     */
    private String table2str(LocalDate start, LocalDate end) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%-10s ", "日期"))
                .append(String.format("%-7s ", "值班人员名字"))
                .append(String.format("%-9s ", "职位"))
                .append(String.format("%-10s ", "手机号码"))
                .append("\n");
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            sb.append(date);
            Employee e = dutySet.getEmployeeByDate(date);
            if (e != null) {
                sb.append("   ")
                        .append(String.format("%-10s ", e.getName())).append(" ")
                        .append(String.format("%-10s ", e.getJob())).append(" ")
                        .append(e.getPhone());
            }
            sb.append("\n");
        }

        sb.deleteCharAt(sb.length() - 1); // 删除最后的换行符
        return sb.toString();
    }

    /**
     * 添加员工
     */
    public void addEmployee() {
        String name = scanner.nextLine();
        Employee employee = getEmployeeByName(name);

        if (employee != null) {
            System.out.printf("员工“%s”已存在！%n", name);
            return;
        }

        System.out.print("请输入员工职位：");
        String job = scanner.nextLine();
        System.out.print("请输入员工电话：");
        String phone = scanner.nextLine();

        employeeSet.add(new Employee(name, job, phone));
        System.out.printf("员工“%s”添加成功。%n", name);
    }

    /**
     * 移除员工
     */
    public void removeEmployee() {
        String name = scanner.nextLine();
        Employee employee = getEmployeeByName(name);

        System.out.printf("员工“%s”", name);
        if (employee == null)
            System.out.println("未找到！");

        if (dutySet.labels().contains(employee)) // 已排班，删除失败
            System.out.println("已排班，请先移出排班！");
        else { // 未排班，正常删除
            employeeSet.remove(employee);
            System.out.println("已删除。");
        }
    }

    /**
     * 添加排班
     */
    public void addDuty() {
        LocalDate start, end;
        String input;

        System.out.print("请输入开始时间：");
        input = scanner.nextLine();
        start = LocalDate.parse(input);

        System.out.print("请输入结束时间：");
        input = scanner.nextLine();
        end = LocalDate.parse(input);

        if (start.isAfter(end)) {
            System.out.println("结束时间不能晚于开始时间！");
            return;
        }

        System.out.print("请输入员工姓名：");
        input = scanner.nextLine();
        Employee employee = getEmployeeByName(input);
        System.out.printf("员工“%s”", input);

        if (employee == null) {
            System.out.println("不存在！");
            return;
        }

        if (dutySet.add(start, end, employee)) {
            System.out.println("排班成功。");
        } else {
            System.out.println("已被安排过值班，排班失败！");
        }
    }

    /**
     * 移除排班
     */
    public void removeDuty() {
        String input;

        input = scanner.nextLine();
        Employee employee = getEmployeeByName(input);
        System.out.printf("员工“%s”", input);

        if (employee == null) {
            System.out.println("不存在！");
            return;
        }

        if (dutySet.remove(employee))
            System.out.println("排班已取消。");
        else
            System.out.println("尚未安排值班！");
    }

    /**
     * 查看当前信息
     */
    public void currentTable() {
        String input;
        LocalDate start, end;

        System.out.print("请输入开始日期（默认为整个表的开始日期）：");
        input = scanner.nextLine().trim();
        if (input.length() == 0)
            start = startDate;
        else {
            start = LocalDate.parse(input);
            if (start.isBefore(startDate))
                start = startDate;
        }

        System.out.print("请输入结束日期（默认为整个表的结束日期）：");
        input = scanner.nextLine().trim();
        if (input.length() == 0)
            end = endDate;
        else {
            end = LocalDate.parse(input);
            if (end.isAfter(endDate))
                end = endDate;
        }

        System.out.println(table2str(start, end));
    }

    /**
     * 在控制台输出排班的完成率
     */
    public void finishRate() {
        long blankCNT = dutySet.countUnscheduledDate();
        long days = startDate.until(endDate, ChronoUnit.DAYS) + 1;
        double rate = (double) (days - blankCNT) / days * 100;
        System.out.printf("完成率%.2f%%，", rate);
        if (blankCNT == 0)
            System.out.println("已排班完成！");
        else
            System.out.println("排班未完成。");
    }

    /**
     * 随机生成排班表
     */
    public void randomGenerate() {
        // 清空原值班表
        for (Employee e : dutySet.labels()) {
            dutySet.remove(e);
        }

        List<Employee> eList = new ArrayList<>(employeeSet);
        Collections.shuffle(eList);
        final int emCNT = eList.size(); // 员工数

        Random random = new Random(); // 随机数生成器

        LocalDate currentStart = startDate; // 当前员工的开始日
        long maxDays = startDate.until(endDate, ChronoUnit.DAYS) - emCNT + 2; // 当前员工最多工作的天数
        for (int i = 0; i < emCNT; i++) {
            Employee e = eList.get(i); // 当前员工

            long days; // 这位员工的值班天数
            if (i == emCNT - 1) // 最后一个了，全给他
                days = maxDays;
            else // 至少给后面每人留一个
                days = Math.abs((random.nextLong() % maxDays) + 1);

            LocalDate currentEnd = currentStart.plusDays(days - 1);
            dutySet.add(currentStart, currentEnd, e);

            maxDays = maxDays - days + 1; // 下一员工的最多值班数
            currentStart = currentEnd.plusDays(1); // 下一员工的开始日
        }
        System.out.println("分配完成。");
    }

    /**
     * 首先打印要求用户输入姓名的信息，然后调用相应方法
     *
     * @param method 方法
     * @throws InvocationTargetException 反射异常
     * @throws IllegalAccessException    当没有权限访问方法时
     */
    public void askNameAndCall(Method method) throws InvocationTargetException, IllegalAccessException {
        System.out.print("请输入员工姓名：");
        method.invoke(this);
    }

    @Override
    public String toString() {
        return table2str(startDate, endDate);
    }

    /**
     * 在控制台打印目录，并从输入流读取用户输入
     *
     * @param scanner 输入流
     * @return 用户输入的编号；若不合法返回-1
     */
    public static int menu(Scanner scanner) {
        System.out.println("1. 查看当前排班信息");
        System.out.println("2. 添加排班");
        System.out.println("3. 删除排班");
        System.out.println("4. 添加人员");
        System.out.println("5. 删除人员");
        System.out.println("6. 随机生成排班表");
        System.out.println("0. 退出");

        int choice;
        try {
            choice = scanner.nextInt();
        } catch (InputMismatchException e) {
            choice = -1;
        }
        scanner.nextLine();
        return choice;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LocalDate startDate;
        LocalDate endDate;

        // 设定排班日期
        System.out.print("排班开始日期：");
        startDate = LocalDate.parse(scanner.nextLine());
        System.out.print("排班结束日期：");
        endDate = LocalDate.parse(scanner.nextLine());

        // 初始化APP
        DutyRosterApp app = new DutyRosterApp(startDate, endDate);
        System.out.println();

        int num; // 操作编号
        while ((num = menu(scanner)) != 0) {
            try {
                switch (num) {
                    case 1:
                        app.currentTable();
                        app.finishRate();
                        System.out.println();
                        break;
                    case 2:
                        app.addDuty();
                        System.out.println();
                        break;
                    case 3:
                        app.askNameAndCall(DutyRosterApp.class.getMethod("removeDuty"));
                        System.out.println();
                        break;
                    case 4:
                        app.askNameAndCall(DutyRosterApp.class.getMethod("addEmployee"));
                        System.out.println();
                        break;
                    case 5:
                        app.askNameAndCall(DutyRosterApp.class.getMethod("removeEmployee"));
                        System.out.println();
                        break;
                    case 6:
                        app.randomGenerate();
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
 * 一个员工的信息，包括姓名、职位、电话号码
 * <p>Immutable
 */
class Employee {
    private final String name;
    private final String job;
    private final String phone;

    /**
     * @param name  姓名
     * @param job   职位
     * @param phone 电话号码
     */
    Employee(String name, String job, String phone) {
        this.name = name;
        this.job = job;
        this.phone = phone;
    }

    /**
     * 获取员工的姓名
     *
     * @return 姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 获取员工的职位
     *
     * @return 职位
     */
    public String getJob() {
        return job;
    }

    /**
     * 获取员工的电话号码
     *
     * @return 电话号码
     */
    public String getPhone() {
        return phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(name, employee.name) && Objects.equals(job, employee.job) && Objects.equals(phone, employee.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, job, phone);
    }
}
