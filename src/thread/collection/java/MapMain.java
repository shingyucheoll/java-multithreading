package thread.collection.java;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class MapMain {

    public static void main(String[] args) {

        // 동시성 관련된 HashMap
        Map<Integer, String> map1 = new ConcurrentHashMap<>();
        map1.put(3, "data3");
        map1.put(4, "data4");
        map1.put(5, "data5");
        // 3, 4, 5 순서를 보장하지 않음
        System.out.println("map1 = " + map1);

        // Comparator 적용되는 순서 보장되는 Map
        Map<Integer, String> map2 = new ConcurrentSkipListMap<>();
        map2.put(3, "data3");
        map2.put(1, "data1");
        map2.put(5, "data5");
        // 1, 3, 5 순서대로 출력됨
        System.out.println("map2 = " + map2);

    }
}
