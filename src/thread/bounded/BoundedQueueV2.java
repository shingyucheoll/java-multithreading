package thread.bounded;

import java.util.ArrayDeque;
import java.util.Queue;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class BoundedQueueV2 implements BoundedQueue {

    private final Queue<String> queue = new ArrayDeque<>();

    private final int max;

    public BoundedQueueV2(int max) {
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
            sleep(200);
        }
        queue.offer(data);
    }

    @Override
    public synchronized String take() {
        // 큐가 비어있을 경우
        while (queue.isEmpty()) {
            // 대기한다.
            log("[take] 큐 데이터 없음, 소비자 대기");
            sleep(2000);
        }
        return queue.poll();
    }

    @Override
    public String toString() {
        return queue.toString();
    }
}
