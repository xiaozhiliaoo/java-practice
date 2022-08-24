package lili.chapter3;

import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author lili
 * @date 2019/12/16 0:38
 * @description
 */
public class Chapter3 {
    public static void main(String[] args) {
        IntStream.iterate(1, i -> i * 2).limit(10).forEachOrdered(System.out::print);
        System.out.println("-------------");
        IntStream.iterate(1, i -> i * 2).limit(10).forEach(System.out::println);

        Arrays.asList(1, 2, 3, 4, 5).stream().map(i -> i + 1).max(Integer::compareTo);

        IntStream.range(1, 5).map(i -> i + 1).max();

        OptionalDouble max = IntStream.range(1, 5).asDoubleStream().max();
        System.out.println(max.getAsDouble());

        Stream<Integer> boxed = IntStream.range(1, 10).boxed();

        Stream<Integer> integerStream = Stream.of(1, 2);
        IntStream intStream = integerStream.mapToInt(Integer::intValue);

        
    }
}
