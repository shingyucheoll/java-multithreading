package thread.bounded;

import java.util.ArrayDeque;
import java.util.Queue;

import static util.MyLogger.log;

public class BoundedQueueV1 implements BoundedQueue {

    private final Queue<String> queue = new ArrayDeque<>();

    // 같은 인스턴스의 synchronized ( Monitor Lock 을 사용하기 때문에 동시성 문제가 발생할 일은 없다. )
    private final int max;

    public BoundedQueueV1(int max) {
        this.max = max;
    }

    public static void main(String[] args) {

    }

    /**
     * 임계 영역으로 지정해야 한다. ( t1, t2, t3 가 있다고 가정했을 때 9/10 으로 전부 if 문 실행 후 offer 동시에 작업 시 data max 치를 넘기게 된다. )
     */

    @Override
    public synchronized void put(String data) {
        if (queue.size() >= max) {
            log("[put] 큐가 가득 참, 버림 : " + data);
            return;
        }

        // queue 가 가득차지 않았을 경우 데이터 추가
        queue.offer(data);
    }


    @Override
    public synchronized String take() {
        if (queue.isEmpty()) {
            return null;
        }

        // queue 에 데이터가 있을 경우 poll
        return queue.poll();
    }

    @Override
    public String toString() {
        // queue에 들어있는 data 출력
        return queue.toString();
    }
}
