package org.lili.practical;

import java.awt.*;

/**
 * @author lili
 * @date 2020/5/23 19:29
 * @description
 * @notes
 */
public class Praxis1 {


    public static void modifyPoint(Point point, int j) {
        point.setLocation(5, 5);
        j = 15;
        System.out.println("modifyPoint:pt=" + point + ", j=" + j);
    }

    public static void main(String[] args) {
        Point p = new Point(0, 0);
        int i = 10;
        System.out.println("before modifyPoint:pt=" + p + ", j=" + i);

        modifyPoint(p, i);

        System.out.println("after modifyPoint:pt=" + p + ", j=" + i);

    }
}
