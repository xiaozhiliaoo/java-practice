package lili.chapter2;


import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author lili
 * @date 2019/12/15 22:23
 * @description
 */
public class Chapter2 {

    /*public void foo() {
        final int i = 2;
        Runnable r = ()->{int i=3;};
    }

    public void createThread() {
        (Thread t) -> t.start();
    }*/

    Object i, j;

    IntUnaryOperator iou = i -> {
        int j = 3;
        return i + j;
    };

    Runnable r1 = () -> {
        System.out.println(this);
    };

    Runnable r2 = () -> {
        System.out.println(toString());
    };

    @Override
    public String toString() {
        return "hello";
    }

    IntUnaryOperator fact;

    public void Factorial() {
        fact = i -> i == 0 ? 1 : i * fact.applyAsInt(i = 1);
    }

    DoubleUnaryOperator sqrt = x -> Math.sqrt(x);

    Consumer consumer = s -> System.out.println(s);

    Predicate predicate = pc -> pc.equals("1");

    Supplier supplier = () -> "new String()";

    Function function = s -> s + "dd";

    LongFunction longFunction = longfunc -> longfunc + 1L;

    ToIntFunction<String> toIntFunction = toInt -> {
        return Integer.parseInt(toInt + 1);
    };

    LongToIntFunction longToIntFunction = value -> {
        return (int) (value + 1);
    };

    BiConsumer<Integer, Integer> biConsumer = (k1, v1) -> {
        System.out.println(k1 + v1);
    };

    BiConsumer<Integer, String> biConsumer2 = (k1, v1) -> {
        System.out.println(k1 + v1);
    };

    BiFunction<Integer, Integer, String> biFunction = (t, u) -> {
        return String.valueOf(t + u);
    };

    Runnable returnDatePrinter() {
        return () -> System.out.println(new Date());
    }


    public static void main(String[] args) throws Exception {
        Chapter2 chapter2 = new Chapter2();
        chapter2.r1.run();
        chapter2.r2.run();
        System.out.println(chapter2.iou.applyAsInt(1));
        //System.out.println(chapter2.fact.applyAsInt(11));
        double v = chapter2.sqrt.applyAsDouble(11.2d);
        log(v);


        List<String> fruits = new ArrayList<>();
        fruits.add("apple");
        fruits.add("banana");
        fruits.add("orange");
        fruits.replaceAll(x -> {
            return x + "ddd";
        });
        log(fruits);

        chapter2.consumer.accept("aaaa");

        boolean aa = chapter2.predicate.test("1");
        System.out.println(aa);

        Object o = chapter2.supplier.get();
        log(o);

        log(chapter2.function.apply("aaa"));

        log(chapter2.longFunction.apply(123L));

        log(chapter2.toIntFunction.applyAsInt("111"));

        log(chapter2.longToIntFunction.applyAsInt(111L));

        chapter2.biConsumer.accept(1, 3);

        chapter2.biConsumer2.accept(1, "3");

        String apply = chapter2.biFunction.apply(1111, 2222);
        log(apply);

        IntPredicate ip = i -> i > 1;
        log(ip.test(3));

        Comparator<String> cc = (String s1, String s2) -> s1.compareToIgnoreCase(s2);
        cc.compare("11", "app");

        IntBinaryOperator[] calculatorOps = new IntBinaryOperator[]{
                (x, y) -> x + y, (x, y) -> x - y, (x, y) -> x * y, (x, y) -> x / y
        };

        int add = calculatorOps[0].applyAsInt(1, 2);
        log(add);

        chapter2.returnDatePrinter().run();

        Callable<Runnable> c = () -> {
            return () -> System.out.println("hi");
        };
        c.call().run();

        Callable<Runnable> c2 = () -> () -> System.out.println("hi");

        c2.call().run();

        Callable<Integer> c3 = true ? (() -> 23) : (() -> 42);
        c3.call();

//        Object o1 = ()->"hi";
        Object s = (Supplier) () -> "hi";
        Object c4 = (Callable) () -> "hi";

        fruits.forEach(x -> System.out.println(x));

        fruits.forEach(System.out::println);

        Integer[] integerArray = new Integer[]{1, 4, 5, 3, 2, 5};
        Arrays.sort(integerArray, (x, y) -> Integer.compareUnsigned(x, y));

        Map<String, String> map = new TreeMap<>();
        map.put("alpha", "X");
        map.put("beavo", "Y");
        map.put("charlie", "Z");
        String str = "alpha-bravo-charlie";
//        BiFunction<String,String,String> biFunction = (v1,v2)->{
//            return v1+v2;
//        };
//        map.replaceAll(biFunction);
//        map.replaceAll(str::replace);
//        System.out.println(map);
        map.replaceAll(String::concat);
        log(map);

        Stream<String> stringStream = Stream.of("a.txt", "b.txt", "c.txt");
        Stream<File> fileStream = stringStream.map(File::new);
        List<File> collect = fileStream.collect(Collectors.toList());

        UnaryOperator<Integer> b = x -> x.intValue();

        Comparator<String> cs = Comparator.comparing(String::length);



    }

    public static void log(Object o) {
        System.out.println(o.toString());
    }
}
