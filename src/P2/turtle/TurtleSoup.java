/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package P2.turtle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 链表
 */
class Node {
    private final Point point;
    private Node next;

    public Node(Point point) {
        this.point = point;
        this.next = null;
    }

    public Node(Point point, Node next) {
        this.point = point;
        this.next = next;
    }

    public Point getPoint() {
        return point;
    }

    /**
     * 获取下一节点
     *
     * @return 下一节点
     */
    public Node getNext() {
        return next;
    }

    /**
     * 设置下一节点
     *
     * @param next 下一节点
     */
    public void setNext(Node next) {
        this.next = next;
    }

    /**
     * 计算该链表的长度（包括头节点！）
     *
     * @return 链表长度
     */
    public int getLength() {
        int count = 0;
        for (Node node = this; node != null; node = node.next) {
            count++;
        }
        return count;
    }

    /**
     * 在当前链表最后新增节点
     *
     * @param point 新增的点
     */
    public void add(Point point) {
        Node node = this;
        while (node.next != null) // 找到当前链表的尾节点
            node = node.next;
        node.next = new Node(point); // 新建节点加在最后
    }

    /**
     * 删除该节点的下一个节点
     */
    public void deleteNext() {
        if (this.next == null)
            throw new NullPointerException("Next node is null!");
        this.next = this.next.next;
    }

    /**
     * 通过简单的几何关系判断三点是否在一线上
     *
     * @param n1 第一个点
     * @param n2 第二个的
     * @param n3 第三个点
     * @return 给定的三点是否在同一直线上
     */
    public static boolean threeInOneLine(Node n1, Node n2, Node n3) {
        Point p1, p2, p3;
        p1 = n1.point;
        p2 = n2.point;
        p3 = n3.point;

        double a = p1.x() - p2.x();
        double b = p1.y() - p2.y();
        double c = p1.x() - p3.x();
        double d = p1.y() - p3.y();
        return Double.doubleToLongBits(a * d) == Double.doubleToLongBits(b * c);
    }

    /**
     * 当前节点、下一节点、下下节点在同一条线上
     */
    public boolean nextThreeInOneLine() {
        Node n1, n2;
        n1 = next;
        if (n1 == null)
            throw new NullPointerException("The next node is null!");
        n2 = n1.next;
        if (n2 == null)
            throw new NullPointerException("The 2nd next node is null!");

        return threeInOneLine(this, n1, n2);
    }
}

public class TurtleSoup {

    /**
     * Draw a square.
     *
     * @param turtle     the turtle context
     * @param sideLength length of each side
     */
    public static void drawSquare(Turtle turtle, int sideLength) {
        for (int i = 0; i < 4; i++) {
            turtle.forward(100);
            turtle.turn(90);
        }
    }

    /**
     * Determine inside angles of a regular polygon.
     * <p>
     * There is a simple formula for calculating the inside angles of a polygon;
     * you should derive it and use it here.
     *
     * @param sides number of sides, where sides must be > 2
     * @return angle in degrees, where 0 <= angle < 360
     */
    public static double calculateRegularPolygonAngle(int sides) {
        return sides > 2 ? (sides - 2) * 180 / (float) sides : 0.0;
    }

    /**
     * Determine number of sides given the size of interior angles of a regular polygon.
     * <p>
     * There is a simple formula for this; you should derive it and use it here.
     * Make sure you *properly round* the answer before you return it (see java.lang.Math).
     * HINT: it is easier if you think about the exterior angles.
     *
     * @param angle size of interior angles in degrees, where 0 < angle < 180
     * @return the integer number of sides
     */
    public static int calculatePolygonSidesFromAngle(double angle) {
        return (int) Math.round(360 / (180 - angle));
    }

    /**
     * Given the number of sides, draw a regular polygon.
     * <p>
     * (0,0) is the lower-left corner of the polygon; use only right-hand turns to draw.
     *
     * @param turtle     the turtle context
     * @param sides      number of sides of the polygon to draw
     * @param sideLength length of each side
     */
    public static void drawRegularPolygon(Turtle turtle, int sides, int sideLength) {
        final double angle = 180 - calculateRegularPolygonAngle(sides); // 每次旋转的角度为外角，故需求补角
        for (int i = 0; i < sides; i++) {
            turtle.forward(sideLength);
            turtle.turn(angle);
        }
    }

    /**
     * Given the current direction, current location, and a target location, calculate the Bearing
     * towards the target point.
     * <p>
     * The return value is the angle input to turn() that would point the turtle in the direction of
     * the target point (targetX,targetY), given that the turtle is already at the point
     * (currentX,currentY) and is facing at angle currentBearing. The angle must be expressed in
     * degrees, where 0 <= angle < 360.
     * <p>
     * HINT: look at http://en.wikipedia.org/wiki/Atan2 and Java's math libraries
     *
     * @param currentBearing current direction as clockwise from north
     * @param currentX       current location x-coordinate
     * @param currentY       current location y-coordinate
     * @param targetX        target point x-coordinate
     * @param targetY        target point y-coordinate
     * @return adjustment to Bearing (right turn amount) to get to target point,
     * must be 0 <= angle < 360
     */
    public static double calculateBearingToPoint(double currentBearing, int currentX, int currentY,
                                                 int targetX, int targetY) {
        double absoluteBearing; // 相对于y轴的绝对角度
        if (currentX == targetX) // 竖着走
            absoluteBearing = targetY > currentY ? 0.0 : 270.0;
        else if (currentY == targetY) // 横着走
            absoluteBearing = targetX > currentX ? 90.0 : 270.0;
        else { // 一般情况
            double tan = (double) (targetX - currentX) / (double) (targetY - currentY);
            absoluteBearing = Math.toDegrees(Math.atan(tan));
        }

        double bearing = 360 - currentBearing + absoluteBearing; // 相对旋转角度
        if (bearing >= 360)
            bearing -= 360;
        return bearing;
    }

    /**
     * Given a sequence of points, calculate the Bearing adjustments needed to get from each point
     * to the next.
     * <p>
     * Assumes that the turtle starts at the first point given, facing up (i.e. 0 degrees).
     * For each subsequent point, assumes that the turtle is still facing in the direction it was
     * facing when it moved to the previous point.
     * You should use calculateBearingToPoint() to implement this function.
     *
     * @param xCoords list of x-coordinates (must be same length as yCoords)
     * @param yCoords list of y-coordinates (must be same length as xCoords)
     * @return list of Bearing adjustments between points, of size 0 if (# of points) == 0,
     * otherwise of size (# of points) - 1
     */
    public static List<Double> calculateBearings(List<Integer> xCoords, List<Integer> yCoords) {
        assert xCoords.size() == yCoords.size();

        List<Double> bearings = new ArrayList<>();
        double currentBearing = 0.0;
        int currentX = xCoords.get(0), currentY = yCoords.get(0);
        int targetX, targetY;

        for (int i = 1; i < xCoords.size(); i++) {
            targetX = xCoords.get(i);
            targetY = yCoords.get(i);
            currentBearing = calculateBearingToPoint(currentBearing, currentX, currentY, targetX, targetY);
            bearings.add(currentBearing);

            currentX = targetX;
            currentY = targetY;
        }

        return bearings;
    }

    /**
     * Counter Clock Wise 以逆时针方向旋转
     *
     * @param a 起点
     * @param b 中间点
     * @param c 终点
     * @return a, b, c是否满足CCW
     */
    private static boolean CCW(Point a, Point b, Point c) {
        return ((c.x() - a.x()) * (b.y() - a.y()) - (c.y() - a.y()) * (b.x() - a.x())) < 0;
    }

    /**
     * Given a set of points, compute the convex hull, the smallest convex set that contains all the points
     * in a set of input points. The gift-wrapping algorithm is one simple approach to this problem, and
     * there are other algorithms too.
     *
     * @param points a set of points with xCoords and yCoords. It might be empty, contain only 1 point, two points or more.
     * @return minimal subset of the input points that form the vertices of the perimeter of the convex hull
     */
    public static Set<Point> convexHull(Set<Point> points) {
        int n = points.size(); // 点的个数
        if (n == 0) // 输入为空，直接返回
            return new HashSet<>();

        // 将输入转换为数组，便于计算
        Point[] pointArray = new Point[n];
        int i = 0;
        for (Point p : points) {
            pointArray[i++] = p;
        }

        // 找到最左的点，以保证初始点在凸包上
        int mostLeftP = 0; // 最左边的点的下标
        for (i = 0; i < n; i++) {
            if (pointArray[i].x() < pointArray[mostLeftP].x()
                    || (pointArray[i].x() == pointArray[mostLeftP].x() && pointArray[i].y() < pointArray[mostLeftP].y())) {
                mostLeftP = i;
            }
        }

        Node convexHull = new Node(new Point(0, 0)); // 凸包链表，初始化头节点

        int pA = mostLeftP; // index of Point on Hull
        int pC; // index of endpoint
        Node lastNode = convexHull; // 指向链表凸包尾
        do {
            lastNode.add(pointArray[pA]);
            lastNode = lastNode.getNext();

            pC = (pA + 1) % n;
            for (i = 0; i < n; i++) {
                if (pC == pA || CCW(pointArray[pA], pointArray[i], pointArray[pC])) {
                    pC = i;
                }
            }
            pA = pC;
        } while (pA != mostLeftP);

        // 此时凸包已生成
        // 当凸包内点的个数≥4时，可能存在共线的点，将其去除
        if (convexHull.getLength() > 3) {
            Node node;
            for (node = convexHull.getNext(); node.getNext() != null && node.getNext().getNext() != null; node = node.getNext()) {
                if (node.nextThreeInOneLine())
                    node.deleteNext();
            }

            // 此时node指向链表中的倒数第二个元素（下标-2）
            // 接下来对下标为-2、-1、0及-1、0、1的节点判断是否共线
            assert node.getNext() != null;
            Node first = convexHull.getNext();
            Node second = first.getNext();
            if (Node.threeInOneLine(node, node.getNext(), first)) { // 判断-2、-1、0
                node.deleteNext(); // -2、-1、0共线，删除最后一个节点
                // 此时node是最后一个节点，因此无需get next
            } else {
                node = node.getNext(); // 让node指向最后一个节点
            }
            if (Node.threeInOneLine(node, first, second)) // 判断-1、0、1
                node.deleteNext();
        }

        // 将自定义的链表转换为Set后返回
        Set<Point> convexHullWithoutLine = new HashSet<>();
        for (Node node = convexHull.getNext(); node != null; node = node.getNext()) {
            convexHullWithoutLine.add(node.getPoint());
        }
        return convexHullWithoutLine;
    }

    /**
     * Draw your personal, custom art.
     * <p>
     * Many interesting images can be drawn using the simple implementation of a turtle.  For this
     * function, draw something interesting; the complexity can be as little or as much as you want.
     *
     * @param turtle the turtle context
     */
    public static void drawPersonalArt(Turtle turtle) {
        PenColor[] palette = {PenColor.RED, PenColor.GREEN, PenColor.BLUE, PenColor.ORANGE};
        int length = 1;    // 初始长度
        int delta = 2;     // 每次增加的长度
        double angle = 91; // 旋转的角度

        for (int i = 0; i < 360; i++) {
            turtle.color(palette[i % palette.length]);
            turtle.forward(length + i * delta);
            turtle.turn(angle);
        }
    }

    /**
     * Main method.
     * <p>
     * This is the method that runs when you run "java TurtleSoup".
     *
     * @param args unused
     */
    public static void main(String[] args) {
        DrawableTurtle turtle = new DrawableTurtle();

//        drawSquare(turtle, 40);
//        drawRegularPolygon(turtle, 5, 50);
        drawPersonalArt(turtle);

        // draw the window
        turtle.draw();
    }

}
