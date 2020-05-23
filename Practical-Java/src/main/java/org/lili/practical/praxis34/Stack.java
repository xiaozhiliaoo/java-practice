package org.lili.practical.praxis34;

/**
 * @author lili
 * @date 2020/5/23 23:18
 * @description
 * @notes
 */
public class Stack {
    private int [] intArr;
    private int index;

    Stack(int v) {
        intArr = new int[v];
    }

    public int top() {
        return intArr[0];
    }
}
