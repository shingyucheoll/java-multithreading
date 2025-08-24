package thread.cas;

import java.util.concurrent.atomic.AtomicInteger;

public class CasMainV1 {

    /*
     * 원자적으로 실행되는 Atomic 연산에 대해 테스트
     * 값을 확인하고 변경하는 작업이 2개로 나누어진 명령어로 원자적이지 않은것처럼 보이지만
     * CPU 하드웨어 차원에서 특별하게 위 작업을 묶어서 하나의 명령으로 제공하는 기능이다.
     *
     * CPU 코어 자체적으로 두 작업을 하나로 묶어버려서 이어진 두 작업에 대해 진행할 때 다른곳에서 접근하지 못하도록 막아버리며
     * 하나의 작업으로 처리함으로써 원자적 값으로 관리가 가능해진다.
     *
     */
    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        System.out.println("start value = " + atomicInteger.get());

        // 현재 값이 0일경우 1로 변경하며 true 반환
        boolean result1 = atomicInteger.compareAndSet(0, 1);
        System.out.println("result1 = " + result1 + ", value = " + atomicInteger.get());

        // 실패 테스트 - 이미 1로 변경된 값 이므로 0을 예상했지만 1일 경우 ( false 반환하며 실행하지않는다 )
        boolean result2 = atomicInteger.compareAndSet(0, 1);
        System.out.println("result1 = " + result2 + ", value = " + atomicInteger.get());

    }
}
