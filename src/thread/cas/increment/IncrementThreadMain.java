package thread.cas.increment;

import java.util.ArrayList;
import java.util.List;

import static util.ThreadUtils.sleep;

public class IncrementThreadMain {

    public static final int THREAD_COUNT = 1000;

    public static void main(String[] args) throws InterruptedException {
        test(new BasicInteger());
        // volatile 을 사용한다고 하더라도, 메인메모리의 필드를 참조할 뿐이지, 동시성 문제를 해결해주진 않기 때문에 원하던 값인 1_000이 출력되진 않는다.
        test(new VolatileInteger());
        // synchronized 를 통해 임계영역으로 설정할 경우에는 정확히 1000으로 출력됨
        test(new SyncInteger());
        // Atomic
        test(new MyAtomicInteger());
    }

    private static void test(IncrementInteger incrementInteger) throws InterruptedException {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // 너무 빠르게 실행되기 때문에 다른 스레드와 동시 실행을 위해 잠시 대기
                sleep(10);
                incrementInteger.increment();
            }
        };

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            Thread thread = new Thread(runnable);
            thread.start();
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.join();
        }

        int result = incrementInteger.get();

        System.out.println(incrementInteger.getClass().getSimpleName() + " result : " + result);
    }

}
