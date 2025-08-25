package thread.collection.list;

import static util.ThreadUtils.*;

import java.util.Arrays;

public class SyncList implements SimpleList {

    private static final int DEFAULT_CAPACITY = 5;

    private Object[] elementData;

    private int size = 0;

    public SyncList() {
        elementData = new Object[DEFAULT_CAPACITY];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public synchronized void add(Object e) {
        elementData[size] = e;
        // 멀티 스레드 문제를 쉽게 확인하는 코드
        sleep(100);
        size++;

    }

    @Override
    public Object get(int index) {
        return null;
    }

    @Override
    public String toString() {
        return Arrays.toString(Arrays.copyOf(elementData, size)) +
            "size = " + size + ", capacity = " + elementData.length;
    }
}
