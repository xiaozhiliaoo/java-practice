package org.lili.practical;

import java.awt.*;

/**
 * @author lili
 * @date 2020/5/23 19:29
 * @description
 * @notes
 */
public class Praxis1 {

    /**
     *
     * @param point 可变对象
     * @param j 基本类型
     * @param k 不可变对象
     */
    public static void modifyPoint(Point point, int j, String k) {
        point.setLocation(5, 5);
        j = 15;
        //k不可改变，只是引用
        k = "init";
        //k = k.substring(3);
        System.out.println("modifyPoint:pt=" + point + ", j=" + j + ",k=" + k);
    }

    public static void main(String[] args) {
        Point p = new Point(0, 0);
        int i = 10;
        String k = "hi-beijing";
        System.out.println("before modifyPoint:pt=" + p + ", j=" + i + ".k=" + k);

        modifyPoint(p, i, k);

        System.out.println("after modifyPoint:pt=" + p + ", j=" + i + ".k=" + k);

    }
}
