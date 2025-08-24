package thread.bounded;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static util.MyLogger.log;

public class BoundedQueueV4 implements BoundedQueue {

    private final Lock lock = new ReentrantLock();
    // 스레드의 커스텀 대기집합
    private final Condition condition = lock.newCondition();

    private final Queue<String> queue = new ArrayDeque<>();
    private final int max;

    public BoundedQueueV4(int max) {
        this.max = max;
    }

    public static void main(String[] args) {

    }

    @Override
    public void put(String data) {
        lock.lock();
        try {
            while (queue.size() == max) {
                log("[put] 큐가 가득 참, 생산자 대기");
                try {
                    // 모든 자바 객체가 wait 를 제공하기 때문에 condition 의 wait 는 await 로 선언되어 있음
                    // concurrent 의 condition ( 스레드 대기집합 ) 에 저장
                    condition.await();
                    log("[put] 생산자 깨어남");

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            queue.offer(data);
            log("[put] 생산자 데이터 저장, signal() 호출");
            condition.signal();
        } finally {
            lock.unlock();
        }


    }

    @Override
    public String take() {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                log("[take] 큐 데이터 없음, 소비자 대기");
                try {
                    condition.await();
                    log("[take] 소비자 깨어남");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            String data = queue.poll();
            log("[take] 소비자 데이터 획득, signal() 호출");
            condition.signal();
            return data;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return queue.toString();
    }
}
