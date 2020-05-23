package org.lili.practical.praxis34;

/**
 * @author lili
 * @date 2020/5/23 23:19
 * @description
 * @notes
 */
public class SyncStack {
    private int [] intArr;
    private int index;

    SyncStack(int v) {
        intArr = new int[v];
    }

    public synchronized int top() {
        return intArr[0];
    }

    public synchronized int top2() {
        synchronized (this) {
            return intArr[0];
        }
    }
}
