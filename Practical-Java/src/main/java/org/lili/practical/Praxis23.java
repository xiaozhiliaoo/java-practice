package org.lili.practical;

/**
 * @author lili
 * @date 2020/5/23 22:33
 * @description
 * @notes
 */
public class Praxis23 {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            try {
                if (i == 3) {
                    throw new Exception("break");
                }
                System.out.println(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            for (int i = 0; i < 10; i++) {
                if (i == 3) {
                    throw new Exception("break");
                }
                System.out.println(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
