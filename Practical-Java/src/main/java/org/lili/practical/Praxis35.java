package org.lili.practical;

/**
 * @author lili
 * @date 2020/5/23 23:49
 * @description
 * @notes
 */
public class Praxis35 {
    private int instanceVar;
    private static int classVar;

    void stackAccess(int val) {
        int j = 0;
        for (int i = 0; i < val; i++) {
            j += 1;
        }
    }

    void instaceAccess(int val) {
        for (int i = 0; i < val; i++) {
            instanceVar += 1;
        }
    }

    void staticAccess(int val) {
        for (int i = 0; i < val; i++) {
            classVar += 1;
        }
    }
}
