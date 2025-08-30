package thread.executor.poolsize;

import thread.executor.RunnableTask;

import java.util.concurrent.*;

import static thread.executor.ExecutorUtils.printState;
import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class PoolSizeMainV1 {

    public static void main(String[] args) {

        ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(2);

        ExecutorService es
                = new ThreadPoolExecutor(2, 4, 3, TimeUnit.SECONDS, workQueue);

        printState(es, Thread.currentThread().getName());


        es.execute(new RunnableTask("task1"));
        // task1 -> [pool = 1, active = 1, queuedTask = 0, completedTask = 0]
        printState(es, "task1");

        es.execute(new RunnableTask("task2"));
        // task2 -> [pool = 2, active = 2, queuedTask = 0, completedTask = 0]
        printState(es, "task2");

        // 이 시점 작업이 3개가 된다고 Pool 사이즈가 커지는게 아니라, queue 가 늘어나게 된다.
        es.execute(new RunnableTask("task3"));
        // task3 -> [pool = 2, active = 2, queuedTask = 1, completedTask = 0]
        printState(es, "task3");

        es.execute(new RunnableTask("task4"));
        // task4 -> [pool = 2, active = 2, queuedTask = 2, completedTask = 0]
        printState(es, "task4");


        // workQueue 도 가득찬 이후 maximum Size 까지 Pool 이 늘어나게 된다.
        es.execute(new RunnableTask("task5"));
        // task5 -> [pool = 3, active = 3, queuedTask = 2, completedTask = 0]
        printState(es, "task5");

        es.execute(new RunnableTask("task6"));
        // task6 -> [pool = 4, active = 4, queuedTask = 2, completedTask = 0]
        printState(es, "task6");

        // 풀과 Queue 가 가득차서 처리할 수 없을 때 실행 거절 예외를 발생 시킨다. ( RejectedExecutionException )
        try {
            es.execute(new RunnableTask("task7"));
            printState(es, "task7");
        } catch (RejectedExecutionException e) {
            log("task7 실행 거절 예외 발생" + e);
        }

        sleep(3000);
        log("== 작업 수행 완료 ==");
        // Maximum Pool Size 만큼 늘어난 스레드가 그대로 남아있게 된다..?
        // [pool = 4, active = 0, queuedTask = 0, completedTask = 6]
        printState(es);

        sleep(3000);
        log("== Maximum Pool Size 대기 시간 초과 ( KeepAliveTime ) ==");
        // [pool = 2, active = 0, queuedTask = 0, completedTask = 6]  -> KeepAliveTime 동안 작업을 하지 않은 초과 스레드가 제거된다.
        printState(es);


    }
}
