package thread.bounded;

import java.util.ArrayList;
import java.util.List;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class BoundedMain {

    public static void main(String[] args) {
        // 1. BoundedQueue 선택
//        BoundedQueue queue = new BoundedQueueV1(2);
//        BoundedQueue queue = new BoundedQueueV2(2);
//        BoundedQueue queue = new BoundedQueueV3(2);
        BoundedQueue queue = new BoundedQueueV4(2);
        // 2. 생상자 소비자 실행 순서 선택, 반드시 하나만 선택하기
        producerFirst(queue);  // 생산자 먼저 실행

//        consumerFirst(queue);  // 소비자 먼저 실행
    }

    private static void producerFirst(BoundedQueue queue) {

        log(" == [Producer First] Start " + queue.getClass().getSimpleName() + " ==");

        List<Thread> threads = new ArrayList<>();

        startProducer(queue, threads);
        printAllState(queue, threads);

        startConsumer(queue, threads);
        printAllState(queue, threads);

        log(" == [Producer First] End " + queue.getClass().getSimpleName() + " ==");

    }

    private static void consumerFirst(BoundedQueue queue) {

        log(" == [consumer First] Start " + queue.getClass().getSimpleName() + " ==");

        List<Thread> threads = new ArrayList<>();

        startConsumer(queue, threads);
        printAllState(queue, threads);

        startProducer(queue, threads);
        printAllState(queue, threads);

        log(" == [consumer First] End " + queue.getClass().getSimpleName() + " ==");
    }

    private static void startProducer(BoundedQueue queue, List<Thread> threads) {
        System.out.println();

        log("[startProducer]");

        for (int i = 1; i <= 3; i++) {
            Thread producer = new Thread(new ProducerTask(queue, "data" + i), "Producer " + i);
            // Thread 상태 확인을 위해 생선된 Thread 를 List 에 담는다.
            threads.add(producer);
            // 생산자 Thread 시작
            producer.start();
            // 1번 2번 3번 순서 구분을 위해서 ( 로그 확인용 )
            sleep(100);
        }
    }

    private static void startConsumer(BoundedQueue queue, List<Thread> threads) {
        System.out.println();

        log("[startConsumer]");

        for (int i = 1; i <= 3; i++) {
            Thread consumer = new Thread(new ConsumerTask(queue), "consumer" + i);
            threads.add(consumer);
            consumer.start();
            sleep(100);
        }
    }


    private static void printAllState(BoundedQueue queue, List<Thread> threads) {

        System.out.println();

        log("현재 상태 출력, 큐 데이터 : " + queue);

        for (Thread thread : threads) {
            log(thread.getName() + " : " + thread.getState());
        }
    }



}