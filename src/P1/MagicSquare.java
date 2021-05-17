package P1;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * magic square检查器
 * 判断给定的方阵是否为magic square
 */
class MagicSquareChecker {
    private final int n;
    private final int[][] square;
    private final int sum;

    /**
     * 构造函数
     *
     * @param square 被检查的方阵
     */
    public MagicSquareChecker(int[][] square) {
        int n = square.length;

        for (int[] ints : square) {
            if (ints.length != n)
                throw new IllegalArgumentException("Square is not a square!");
        }

        this.square = square;
        this.n = n;

        // 计算第1行的总和
        int sum = 0;
        for (int num : square[0]) {
            sum += num;
        }
        this.sum = sum;
    }

    /**
     * 检查求得的和与构造函数中计算出的和是否一致
     *
     * @param sum 求得的和
     * @return 是否一致
     */
    private boolean sumNotEqual(int sum) {
        return this.sum != sum;
    }

    /**
     * 检查每行的和是否等于sum
     *
     * @return 是否全部通过检查
     */
    private boolean checkRow() {
        for (int[] ints : square) {
            int tmpSum = 0;
            for (int num : ints) {
                tmpSum += num;
            }
            if (sumNotEqual(tmpSum))
                return false;
        }
        return true;
    }

    /**
     * 检查每列的和是否等于sum
     *
     * @return 是否全部通过检查
     */
    private boolean checkColumn() {
        for (int j = 0; j < n; j++) {
            int tmpSum = 0;
            for (int i = 0; i < n; i++) {
                tmpSum += square[i][j];
            }
            if (sumNotEqual(tmpSum))
                return false;
        }
        return true;
    }

    /**
     * 检查两个对角线的和是否等于sum
     *
     * @return 是否全部通过检查
     */
    private boolean checkDiagonals() {
        // 首先检查主对角线
        int tmpSum = 0;
        for (int i = 0; i < n; i++) {
            tmpSum += square[i][i];
        }
        if (sumNotEqual(tmpSum))
            return false;

        // 然后检查副对角线
        tmpSum = 0;
        for (int i = 0; i < n; i++) {
            tmpSum += square[i][n - i - 1];
        }
        return !sumNotEqual(tmpSum);
    }

    /**
     * 检查方阵是否为magic square
     *
     * @return 是否全部通过检查
     */
    public boolean check() {
        return checkRow() && checkColumn() && checkDiagonals();
    }
}

public class MagicSquare {
    /**
     * 从scanner中读入一行，并将其转换为int数组
     *
     * @param scanner 一个输入流
     * @return 若读入正确则返回int数组；若错误则在标准输出中输出错误并返回null
     */
    private static int[] parseLine2Array(Scanner scanner) throws NoSuchElementException {
        String inputBuf = scanner.nextLine(); // 读取一行
        StringTokenizer token = new StringTokenizer(inputBuf, "\t");

        int[] array = new int[token.countTokens()];
        for (int i = 0; i < array.length; i++) {
            String strNum = token.nextToken(); // 获取下一个数字字符串

            try {
                array[i] = Integer.parseInt(strNum);
            } catch (NumberFormatException e) { // 输入非法，无法解析为int
                return null;
            }
        }
        return array;
    }

    /**
     * 打开给定文件，读入其中的方阵，并判断是否为magic square
     *
     * @param fileName 指定的文件名
     * @return 是否为magic square
     * @throws FileNotFoundException 当文件不存在时
     * @throws ValueException        当文件内的数据有误时
     */
    private static boolean isLegalMagicSquare(String fileName) throws FileNotFoundException, ValueException {
        InputStream f;
        try {
            f = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(fileName + " not found!");
        }
        Scanner scanner = new Scanner(f);

        int n = 1; // 总列数，初始设为1以进入循环，在第一次循环时确定其值
        int i; // 读取的行数
        int[][] matrix = null; // 输入的矩阵

        for (i = 0; i < n + 1; i++) {
            int[] array;
            try {
                array = parseLine2Array(scanner);

                if (i == n) // 成功读入第n+1行，说明不是方阵，错误
                    throw new ValueException("The input data in " + fileName + " is not a square!");
            } catch (NoSuchElementException e) { // 已经读完文件，退出循环
                break;
            }

            if (array == null) // 读入出现错误
                return false;

            if (i == 0) { // 确定列数
                n = array.length;
                matrix = new int[n][n];
            } else if (n != array.length) { // 与之前行的列数不一致，报错
                throw new ValueException("The data in \"" + fileName + "\" is not a square!");
            }

            matrix[i] = array; // 将该行引用到matrix上
        }

        if (i < n) { // 行数<列数，输入的不是方阵
            throw new ValueException("The data in \"" + fileName + "\" is not a square!");
        }

        // 输入完毕，下面判断是否为magic square
        MagicSquareChecker checker = new MagicSquareChecker(matrix);

        return checker.check();
    }

    public static void main(String[] args) {
        System.out.println("------------------ Request 1 ------------------");

        String[] inputFilenames = {
                "src/P1/txt/1.txt",
                "src/P1/txt/2.txt",
                "src/P1/txt/3.txt",
                "src/P1/txt/4.txt",
                "src/P1/txt/5.txt",
        };

        for (String filename : inputFilenames) {
            try {
                boolean result = isLegalMagicSquare(filename);
                System.out.print(filename);
                if (result)
                    System.out.println(" is a magic square.");
                else
                    System.out.println(" is not a magic square.");
            } catch (FileNotFoundException | ValueException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
