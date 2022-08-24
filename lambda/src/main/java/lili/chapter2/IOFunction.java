package lili.chapter2;

import java.io.IOException;

/**
 * @author lili
 * @date 2019/12/16 0:22
 * @description
 */
@FunctionalInterface
public interface IOFunction<T, R> {
    R apply(T t) throws IOException;
}
