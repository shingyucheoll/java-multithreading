package thread.bounded;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static util.MyLogger.log;

public class BoundedQueueV5 implements BoundedQueue {

    private final Lock lock = new ReentrantLock();
    // 생산자 스레드 대기집합
    private final Condition producerCondition = lock.newCondition();
    // 소비자 스레드 대기집합
    private final Condition consumerCondition = lock.newCondition();

    private final Queue<String> queue = new ArrayDeque<>();
    private final int max;

    public BoundedQueueV5(int max) {
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
                    // 큐가 가득 찼기 때문에, 생산자 대기공간에 스레드를 대기시킨다.
                    producerCondition.await();
                    log("[put] 생산자 깨어남");

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            queue.offer(data);
            log("[put] 생산자 데이터 저장, consumerCondition.signal() 호출");
            // 새로운 데이터를 저장했기 때문에 소비자 스레드에 대기하고 있는 스레드가 있을 경우 깨운다
            consumerCondition.signal();
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
                    // 데이터가 없기 때문에 생산될 때 까지 소비자 스레드 대기 공간에 대기
                    consumerCondition.await();
                    log("[take] 소비자 깨어남");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            String data = queue.poll();
            log("[take] 소비자 데이터 획득, producerCondition.signal() 호출");
            // 큐에 있는 데이터를 소비했기 때문에 생산자 스레드를 깨운다.
            producerCondition.signal();
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
