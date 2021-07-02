package application;

import adt.DutyIntervalSet;
import io.RobustScanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 排班管理系统
 * <p>每个public方法都是void类型，对应了菜单中的一个操作
 */
public class DutyRosterApp {
    DutyIntervalSet<Employee> dutySet; // 排班表
    Set<Employee> employeeSet; // 员工表
    LocalDate startDate; // 开始时间
    LocalDate endDate; // 结束时间
    RobustScanner scanner; // 健壮的输入流

    /**
     * @param start 开始日期
     * @param end   结束日期
     */
    public DutyRosterApp(RobustScanner scanner, LocalDate start, LocalDate end) {
        this.dutySet = new DutyIntervalSet<>(start, end);
        this.employeeSet = new HashSet<>();
        this.startDate = start;
        this.endDate = end;
        this.scanner = scanner;
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
        String name = scanner.nextNotEmptyString("员工姓名");
        Employee employee = getEmployeeByName(name);

        if (employee != null) {
            System.out.printf("员工“%s”已存在！%n", name);
            return;
        }

        System.out.print("请输入员工职位：");
        String job = scanner.nextNotEmptyString("员工职位");
        System.out.print("请输入员工电话：");
        String phone = scanner.nextNotEmptyString("员工电话");

        employeeSet.add(new Employee(name, job, phone));
        System.out.printf("员工“%s”添加成功。%n", name);
    }

    /**
     * 移除员工
     */
    public void removeEmployee() {
        String name = scanner.nextNotEmptyString("员工姓名");
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
        start = scanner.nextDate();

        System.out.print("请输入结束时间：");
        end = scanner.nextDate();

        if (start.isAfter(end)) {
            System.out.println("结束时间不能晚于开始时间！");
            return;
        }

        System.out.print("请输入员工姓名：");
        input = scanner.nextNotEmptyString("员工姓名");
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

        input = scanner.nextNotEmptyString("员工姓名");
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
        double rate = dutySet.unscheduledRate();
        System.out.printf("完成率%.2f%%，", 100 - rate);
        if (rate == 0)
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
     * 从文件中载入一个新的{@link DutyRosterApp}对象
     *
     * @return 新的值班表对象；若发生异常，返回原对象
     */
    public DutyRosterApp loadFromFile() {
        System.out.print("请输入文件位置：");
        String fileName = scanner.nextNotEmptyString("文件路径");

        File file = new File(fileName);
        FileInputStream in;
        StringBuilder sb = new StringBuilder();

        try { // 打开文件
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println("未找到文件！");
            return this;
        }
        try { // 读入文件
            Scanner scanner = new Scanner(in);
            while (true) {
                String line = scanner.nextLine();
                if (line == null)
                    break;

                int index = line.indexOf("//"); // 删除注释
                if (index != -1)
                    line = line.substring(0, index);
                sb.append(line.trim());
            }
        } catch (NoSuchElementException ignored) {
        }
        try { // 关闭文件
            in.close();
        } catch (IOException e) {
            System.out.println("文件关闭异常！");
            return this;
        }

        String text = sb.toString(); // 文件内的全部内容
        DutyRosterApp newApp;

        final String datePattern = "[0-9]{4}-(((0[13578]|(10|12))-(0[1-9]|[1-2][0-9]|3[0-1]))|(02-(0[1-9]|[1-2][0-9]))|((0[469]|11)-(0[1-9]|[1-2][0-9]|30)))";
        final String namePattern = "[A-Za-z]+";
        final String jobPattern = "[A-Za-z ]+";
        final String phonePattern = "1[3-9][0-9]-[0-9]{4}-[0-9]{4}";

        Pattern r;
        Matcher m;

        /* 首先匹配起止日期 */
        r = Pattern.compile("Period\\{(" + datePattern + "),(" + datePattern + ")}");
        m = r.matcher(text);
        if (m.find()) {
            newApp = new DutyRosterApp(scanner, LocalDate.parse(m.group(1)), LocalDate.parse(m.group(12)));
        } else {
            System.out.println("文件格式有误！");
            return this;
        }

        /* 然后匹配员工信息 */
        int startIndex = text.indexOf("Employee");
        if (startIndex == -1) {
            System.out.println("未找到Employee字段，终止导入！");
            return this;
        }
        int endIndex = matchBrace(text, startIndex + "Employee".length());
        if (endIndex == -1) {
            System.out.println("格式有误，终止导入！");
            return this;
        }
        String employeeText = text.substring(startIndex, endIndex + 1);
        r = Pattern.compile("(" + namePattern + ")\\{(" + jobPattern + "),(" + phonePattern + ")}");
        m = r.matcher(employeeText);
        while (m.find()) {
            String name = m.group(1);
            String job = m.group(2);
            String phone = m.group(3);
            Employee employee = new Employee(name, job, phone);
            if (!newApp.employeeSet.add(employee)) {
                System.out.println("员工存在重复，终止导入！");
                return this;
            }
        }

        /* 最后匹配值班信息 */
        startIndex = text.indexOf("Roster");
        if (startIndex == -1) {
            System.out.println("未找到Employee字段，终止导入！");
            return this;
        }
        endIndex = matchBrace(text, startIndex + "Roster".length());
        if (endIndex == -1) {
            System.out.println("格式有误，终止导入！");
            return this;
        }
        String rosterText = text.substring(startIndex, endIndex + 1);
        r = Pattern.compile("(" + namePattern + ")\\{(" + datePattern + "),(" + datePattern + ")}");
        m = r.matcher(rosterText);
        while (m.find()) {
            String name = m.group(1);
            LocalDate startDate = LocalDate.parse(m.group(2));
            LocalDate endDate = LocalDate.parse(m.group(13));

            Employee employee = newApp.getEmployeeByName(name); // 查找员工
            if (employee == null) {
                System.out.println("员工" + name + "已排班但不存在，终止导入！");
                return this;
            }
            if (startDate.isAfter(endDate)) { // 验证日期
                System.out.println("排班时间有误，终止导入！");
                return this;
            }
            if (!newApp.dutySet.add(startDate, endDate, employee)) { // 添加排班
                System.out.println("排班存在冲突，终止导入！");
                return this;
            }
        }

        return newApp;
    }

    /**
     * 给定一个字符串的左括号，返回右括号下标
     *
     * @param text  字符串
     * @param start 左括号下标
     * @return 右括号下标；如果start不指向左括号或无法匹配右括号，返回-1
     */
    private static int matchBrace(String text, int start) {
        if (text.charAt(start) != '{')
            return -1;

        final int length = text.length();
        int counter = 1; // 左括号计数器，为0时说明匹配
        for (int i = start + 1; i < length; i++) {
            switch (text.charAt(i)) {
                case '{':
                    counter++;
                    break;
                case '}':
                    counter--;
                    if (counter == 0)
                        return i;
                    break;
            }
        }

        return -1;
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
     * @return 用户输入的编号；若不合法返回-1
     */
    public int menu() {
        System.out.println("1. 查看当前排班信息");
        System.out.println("2. 添加排班");
        System.out.println("3. 删除排班");
        System.out.println("4. 添加人员");
        System.out.println("5. 删除人员");
        System.out.println("6. 随机生成排班表");
        System.out.println("7. 从文件导入");
        System.out.println("0. 退出");

        return scanner.nextInt();
    }

    public static void main(String[] args) {
        RobustScanner scanner = new RobustScanner(new Scanner(System.in));
        LocalDate startDate;
        LocalDate endDate;

        // 设定排班日期
        System.out.print("排班开始日期：");
        startDate = scanner.nextDate();
        System.out.print("排班结束日期：");
        endDate = scanner.nextDate();

        // 初始化APP
        DutyRosterApp app = new DutyRosterApp(scanner, startDate, endDate);
        System.out.println();

        int num; // 操作编号
        while ((num = app.menu()) != 0) {
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
                    case 7:
                        app = app.loadFromFile();
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
