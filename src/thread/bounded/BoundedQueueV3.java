package thread.bounded;

import java.util.ArrayDeque;
import java.util.Queue;

import static util.MyLogger.log;

public class BoundedQueueV3 implements BoundedQueue {

    private final Queue<String> queue = new ArrayDeque<>();

    private final int max;

    public BoundedQueueV3(int max) {
        this.max = max;
    }

    public static void main(String[] args) {

    }

    @Override
    public synchronized void put(String data) {
        // queue 가 가득차있을 경우
        while (queue.size() == max) {
            // 대기한다
            log("[put] 큐가 가득 참, 생산자 대기");

            try {
                // 모든 객체는 Object 의 자식이기 때문에 wait() 를 사용할 수 있으며
                // InterruptedException을 발생시키기 때문에 예외 처리를 해주어야 한다.

                wait();     // RUNNABLE -> WAITING, 락을 반납하고 대기한다.

                log("[put] 생산자 깨어남");

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        queue.offer(data);
        log("[put] 생산자 데이터 저장, notify() 호출");
        notify();    // 대기 스레드를 깨운다. - 생산자를 기다리던 소비자 스레드를 깨운다. ( WAITING -> BLOCKED (?) )

    }

    @Override
    public synchronized String take() {
        // 큐가 비어있을 경우
        while (queue.isEmpty()) {
            // 대기한다.
            log("[take] 큐 데이터 없음, 소비자 대기");
            try {

                wait();
                log("[take] 소비자 깨어남");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
        String data = queue.poll();

        log("[take] 소비자 데이터 획득, notify() 호출");
        notify();   //   큐가 가득차 소비를 기다리던 생산자 스레드를 깨운다.  ( WAITING -> BLOCKED (?) )
        return data;

    }

    @Override
    public String toString() {
        return queue.toString();
    }
}
