package thread.collection.simple;

import java.util.ArrayList;
import java.util.List;

public class SimpleListMainV0 {

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();

        // 스레드 1, 2 동시에 실행을 가정한다.
        list.add("A");
        list.add("B");
        System.out.println(list);

    }
}
