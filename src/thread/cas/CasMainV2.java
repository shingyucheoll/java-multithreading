package thread.cas;

import java.util.concurrent.atomic.AtomicInteger;

import static util.MyLogger.log;

public class CasMainV2 {

    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        System.out.println("start value = " + atomicInteger.get());

        // int result = atomicInteger.incrementAndGet();
        // 위 내용을 직접 구현한다.

        int result1 = incrementAndGet(atomicInteger);
        System.out.println("result1 value = " + result1);

        int result2 = incrementAndGet(atomicInteger);
        System.out.println("result2 value = " + result2);
    }

    private static int incrementAndGet(AtomicInteger atomicInteger) {
        int getValue;
        boolean result;
        do {
            // 현재 값을 읽어오고,
            getValue = atomicInteger.get();  // thread1: 0
            log("getValue: " + getValue);

            // 아무도 값을 변경하지 않은 케이스에서만 내가 원하는 값으로 ( +1 ) 변경한다.
            result = atomicInteger.compareAndSet(getValue, getValue + 1);
            log("result: " + result);

            // result가 false면 (!result가 true) do 루프를 계속해서 실행시킨다.
            // result가 true면 (!result가 false) do 루프를 빠져나와 마지막 getValue + 1 값으로 반환한다.
        } while (!result);

        return getValue + 1;

    }
}
