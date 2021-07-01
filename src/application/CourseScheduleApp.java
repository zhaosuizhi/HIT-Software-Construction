package application;

public class CourseScheduleApp {
    public static void main(String[] args) {

    }
}

/**
 * 一个课程
 * <p>Immutable
 */
class Course {
    private final int id; // 课程ID
    private final String name; // 课程名称
    private final String teacher; // 教师名字
    private final String place; // 地点

    Course(int id, String name, String teacher, String place) {
        this.id = id;
        this.name = name;
        this.teacher = teacher;
        this.place = place;
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
     * 获取课程的教师名字
     *
     * @return 教师名字
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
}
