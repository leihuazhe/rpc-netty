package test;

import java.lang.reflect.Field;
import java.util.Random;

/**
 * @author maple 2018.09.08 下午1:15
 */
public class TestIntegerCache {

    public static void main(String[] args) throws Exception {

        // Extract the IntegerCache through reflection
        Class<?> clazz = Class.forName(
                "java.lang.Integer$IntegerCache");
        Field field = clazz.getDeclaredField("cache");
        field.setAccessible(true);
        Integer[] cache = (Integer[]) field.get(clazz);

        // Rewrite the Integer cache
        for (int i = 0; i < cache.length; i++) {
            cache[i] = new Random().nextInt(cache.length);
//            cache[i] = new Integer(new Random().nextInt(cache.length));
        }

        // Prove randomness
        for (int i = 0; i < 10; i++) {
            System.out.println((Integer) i);
        }
    }
}
