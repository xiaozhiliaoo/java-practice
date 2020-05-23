package org.lili.practical;

/**
 * @author lili
 * @date 2020/5/23 22:17
 * @description
 * @notes
 */

public class Praxis18 {

    public void foo() throws Exception {
        try {
            throw new Exception("one");
        } catch (Exception e) {
            throw new Exception("two");
        } finally {
            throw new Exception("third");
        }
    }

    public static void main(String[] args) {
        Praxis18 praxis18 = new Praxis18();
        try {
            praxis18.foo();
        } catch (Exception e) {
            System.out.println("In main, cause:" + e.getMessage());
            e.printStackTrace();
        }
    }
}
