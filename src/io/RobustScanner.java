package io;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * 具有一定健壮性的输入流
 * <p>Mutable
 */
public class RobustScanner {
    private final Scanner scanner;

    // 抽象函数:
    //   AF(scanner) = 输入流为scanner，在出错时进行更加友好的反馈
    // 表示不变量:
    //   scanner != null
    // 防止表示暴露:
    //   返回值均来自scanner的方法，因此防止表示暴露由Scanner类保证

    private void checkRep() {
        assert scanner != null;
    }

    public RobustScanner(Scanner scanner) {
        this.scanner = scanner;
        checkRep();
    }

    public String nextLine() {
        return scanner.nextLine();
    }

    /**
     * 获取下一个整型数
     */
    public int nextInt() {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("输入不是整数，请重新输入！");
            } finally {
                scanner.nextLine();
            }
        }
    }

    /**
     * 获取下一个长整形数
     */
    public long nextLong() {
        while (true) {
            try {
                return scanner.nextLong();
            } catch (InputMismatchException e) {
                System.out.println("输入不是整数，请重新输入！");
            } finally {
                scanner.nextLine();
            }
        }
    }

    /**
     * 获取下一个日期
     */
    public LocalDate nextDate() {
        String input;
        while (true) {
            try {
                do { // 获取第一个非空行
                    input = scanner.nextLine();
                } while (input.isEmpty());

                return LocalDate.parse(input);
            } catch (InputMismatchException | DateTimeParseException e) {
                System.out.println("日期格式有误，请重新输入！");
            }
        }
    }

    /**
     * 获取下一个非空白行
     *
     * @param title 字段提示的信息，如输入空行将打印：“title不能为空，请重新输入！”
     * @return 下一个非空白行去除首尾空白后组成的字符串
     */
    public String nextNotEmptyString(String title) {
        while (true) {
            String input = scanner.nextLine().trim();
            if (!input.isEmpty())
                return input;
            System.out.println(title + "不能为空，请重新输入！");
        }
    }

    /**
     * 获取下一个非空白行
     *
     * @return 下一个非空白行去除首尾空白后组成的字符串
     */
    public String nextNotEmptyString() {
        return nextNotEmptyString("输入");
    }
}
