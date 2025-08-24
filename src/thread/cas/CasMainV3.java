package thread.cas;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class CasMainV3 {

    private static int THREAD_COUNT = 5;

    public static void main(String[] args) throws InterruptedException {
        // AtomicInteger 객체를 생성하고 초기값을 0으로 설정
        // 멀티스레드 환경에서 thread-safe한 정수 연산을 제공
        // 내부적으로 volatile과 CAS(Compare-And-Swap) 연산을 사용
        AtomicInteger atomicInteger = new AtomicInteger(0);  // 0 생략 가능
        System.out.println("start value = " + atomicInteger.get());

        // Runnable 인터페이스를 구현한 익명 클래스 생성
        // 각 스레드가 실행할 작업을 정의
        // 여러 스레드가 동일한 atomicInteger 객체에 대해 증가 연산 수행
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                incremenetAndGet(atomicInteger);
            }
        };

        // 생성된 Thread 객체들을 저장할 리스트 생성
        // 나중에 모든 스레드의 작업 완료를 기다리기 위해 참조 보관
        // join() 메서드 호출 시 필요한 Thread 객체들을 관리
        List<Thread> threads = new ArrayList<>();

        // THREAD_COUNT 만큼 스레드를 생성하고 실행
        // 각 스레드는 동일한 runnable 작업을 수행
        // start() 호출로 스레드를 실제로 실행 시작
        for (int i = 0; i < THREAD_COUNT; i++) {
            Thread thread = new Thread(runnable);
            sleep(10);
            threads.add(thread);
            thread.start();
        }

        // 모든 스레드의 작업 완료를 기다림 (동기화)
        // join()은 해당 스레드가 종료될 때까지 현재 스레드를 블로킹
        // 이를 통해 모든 증가 연산이 완료된 후 최종 결과 출력 가능
        for (Thread thread : threads) {
            thread.join();
        }

        // 모든 스레드 작업 완료 후 최종 결과값 조회
        // AtomicInteger의 현재 값을 안전하게 읽어옴
        // 예상 결과: THREAD_COUNT 만큼 증가된 값
        int result = atomicInteger.get();
        System.out.println(atomicInteger.getClass().getSimpleName() + "resultValue = " + result);
    }

    private static int incremenetAndGet(AtomicInteger atomicInteger) {
        int getValue;
        boolean result;

        do {
            getValue = atomicInteger.get();
            sleep(100);
            log("getValue: " + getValue);
            result = atomicInteger.compareAndSet(getValue, getValue + 1);
            log("result: " + result);
        } while (!result);

        return getValue + 1;
    }
}