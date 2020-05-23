package org.lili.practical;

/**
 * @author lili
 * @date 2020/5/24 0:01
 * @description
 * @notes
 */
public class Praxis38 {
    public int usePrimitive(int increment) {
        int i = 5;
        i = i + increment;
        return i;
    }

    public int useObject(Integer increment) {
        int i = 5;
        i = i + increment.intValue();
        return i;
    }

    public Integer useObject2(int increment) {
        int i = 5;
        i = i + increment;
        return new Integer(i);
    }
}
