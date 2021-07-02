package application;

import adt.CourseIntervalSet;
import io.RobustScanner;

import java.time.LocalDate;
import java.util.*;

public class CourseScheduleApp {

    private final RobustScanner scanner; // 健壮的输入流
    private final CourseIntervalSet<Course> set; // 课表
    private final Set<Course> courseSet; // 已添加的课

    /**
     * @param scanner   输入流
     * @param startDate 开始日期
     * @param weekCNT   总周数
     * @throws IllegalArgumentException 当总周数<0时
     */
    public CourseScheduleApp(RobustScanner scanner, LocalDate startDate, int weekCNT) throws IllegalArgumentException {
        this.scanner = scanner;

        this.set = new CourseIntervalSet<>(startDate, weekCNT);
        this.courseSet = new HashSet<>();
    }

    /**
     * 查看某一天的课表
     */
    void watch() {
        System.out.print("请输入要查看的日期：");
        LocalDate date = scanner.nextDate();

        List<Set<Course>> daily = set.getDateSchedule(date);
        if (daily == null) {
            System.out.println("日期不属于本学期！");
            return;
        }

        List<String> text = Arrays.asList("8-10时", "10-12时", "13-15时", "15-17时", "19-21时");
        System.out.println("时间段   课程ID  课程名       教师名   上课地点");
        for (int i = 0; i < 5; i++) {
            Set<Course> courseSet = daily.get(i);
            if (courseSet.isEmpty()) {
                System.out.println(text.get(i));
                continue;
            }

            for (Course c : courseSet) {
                System.out.printf("%-8s %-5d %-7s %-6s %s%n",
                        text.get(i), c.getId(), c.getName(), c.getTeacher(), c.getPlace()
                );
            }
        }
    }

    /**
     * 安排一次课
     */
    void schedule() {
        System.out.print("请输入要安排的课程ID：");
        int id = scanner.nextInt();

        Course course = null; // 待安排的课程
        for (Course c : courseSet) {
            if (id == c.getId()) {
                course = c;
                break;
            }
        }
        if (course == null) {
            System.out.println("该ID不存在！");
            return;
        } else if (!course.hasHoursLeft()) {
            System.out.println("课程剩余学时数不足！");
            return;
        }

        System.out.print("请输入要安排的日期：");
        LocalDate date = scanner.nextDate();
        if (!set.dateInSemester(date)) {
            System.out.println("该日期不在本学期内！");
            return;
        }

        System.out.println("时段选项 1、8-10时 2、10-12时 3、13-15时 4、15-17时 5、19-21时");
        System.out.print("请选择要安排的时段（1-5）：");
        int num = scanner.nextInt() - 1;
        if (num < 0 || num > 4) {
            System.out.println("无此选项！");
            return;
        }

        /* 首先查看老师、教室是否存在冲突 */
        Set<Course> currentCourses = set.getDateSchedule(date).get(num); // 该时段的所有课程
        for (Course current : currentCourses) {
            if (current.equals(course)) {
                System.out.println("该节课已被安排过此时间段！");
                return;
            } else if (current.getTeacher().equals(course.getTeacher())) {
                System.out.println("教师在此时间段已经安排了课程！");
                return;
            } else if (current.getPlace().equals(course.getPlace())) {
                System.out.println("地点在此时间段已被占用！");
                return;
            }
        }

        /* 无冲突，添加课程 */
        boolean ret = set.add(date, num, course);
        assert ret;
        course.scheduleOnce();
        System.out.println("安排成功。");
    }

    /**
     * 添加一个课程
     */
    void addCourse() {
        System.out.print("请输入课程ID：");
        int id = scanner.nextInt(); // 课程ID
        if (id < 0) {
            System.out.println("ID必须>0！");
            return;
        }
        for (Course c : courseSet) {
            if (id == c.getId()) {
                System.out.println("ID已存在！");
                return;
            }
        }

        System.out.print("请输入课程名称：");
        String name = scanner.nextNotEmptyString(); // 课程名称
        if (name.isEmpty()) {
            System.out.println("课程名不能为空！");
            return;
        }

        System.out.print("请输入教师名：");
        String teacher = scanner.nextNotEmptyString("教师名"); // 教师名字

        System.out.print("请输入地点：");
        String place = scanner.nextNotEmptyString("地点"); // 地点

        System.out.print("请输入学时数：");
        int hours = scanner.nextInt(); // 剩余学时数
        if (hours < 2) {
            System.out.println("学时数至少为2！");
            return;
        } else if (hours % 2 != 0) {
            System.out.println("学时数必须为偶数！");
            return;
        }

        Course course = new Course(id, name, teacher, place, hours);
        courseSet.add(course);
        System.out.println("课程添加成功。");
    }

    /**
     * 在控制台打印目录，并从输入流读取用户输入
     *
     * @return 用户输入的编号
     */
    public int menu() {
        System.out.println("1. 查看某一天的课表");
        System.out.println("2. 安排一次课");
        System.out.println("3. 添加课程");
        System.out.println("0. 退出");

        return scanner.nextInt();
    }

    public static void main(String[] args) {
        RobustScanner scanner = new RobustScanner(new Scanner(System.in));

        System.out.print("请输入学期开始日期：");
        LocalDate startDate = scanner.nextDate();

        System.out.print("请输入学期周数：");
        int weekCNT = scanner.nextInt();
        while (weekCNT <= 0) {
            System.out.println("周数必须大于0！");
            weekCNT = scanner.nextInt();
        }

        CourseScheduleApp app = new CourseScheduleApp(scanner, startDate, weekCNT);

        System.out.println();

        int num; // 操作编号
        while ((num = app.menu()) != 0) {
            try {
                switch (num) {
                    case 1:
                        app.watch();
                        System.out.println();
                        break;
                    case 2:
                        app.schedule();
                        System.out.println();
                        break;
                    case 3:
                        app.addCourse();
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
 * 一个课程
 * <p>Mutable
 */
class Course {
    private final int id; // 课程ID
    private final String name; // 课程名称
    private final String teacher; // 教师名字
    private final String place; // 地点
    private int hours; // 剩余学时数

    void checkRep() {
        assert id > 0;
        assert name != null;
        assert teacher != null;
        assert place != null;
        assert hours >= 0 && hours % 2 == 0;
    }

    /**
     * @param id      课程ID，必须为正整数
     * @param name    课程名称
     * @param teacher 教师名字
     * @param place   课程地点
     * @param hours   周学时数，必须为正偶数
     */
    public Course(int id, String name, String teacher, String place, int hours) {
        this.id = id;
        this.name = name;
        this.teacher = teacher;
        this.place = place;
        this.hours = hours;

        checkRep();
    }

    /**
     * 获取课程ID
     *
     * @return 课程ID
     */
    public int getId() {
        return id;
    }

    /**
     * 获取课程名称
     *
     * @return 课程名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取课程的教师
     *
     * @return 教师
     */
    public String getTeacher() {
        return teacher;
    }

    /**
     * 获取课程地点
     *
     * @return 地点
     */
    public String getPlace() {
        return place;
    }

    /**
     * 课程是否有剩余学时
     */
    public boolean hasHoursLeft() {
        return hours > 0;
    }

    /**
     * 安排2学时的课程，剩余学时数减2
     *
     * @return 是否安排成功；false说明无剩余学时
     */
    public boolean scheduleOnce() {
        if (hours >= 2) {
            hours -= 2;
            checkRep();
            return true;
        } else
            return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id == course.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
