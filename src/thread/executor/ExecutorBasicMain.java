package thread.executor;

import static thread.executor.ExecutorUtils.*;
import static util.MyLogger.*;
import static util.ThreadUtils.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorBasicMain {

    public static void main(String[] args) {
        // corePoolSize : 스레드 풀에서 관리되는 기본 스레드 수
        // maximumPoolSize : 스레드 풀에서 관리되는 최대 스레드 수
        // keepAliveTime, Timeunit : 기본 스레드 수를 초과해서 만들어진 스레드가 생존할 수 있는 대기 시간
        // -> 이 시간 동안 처리할 작업이 없다면 초과 스레드는 제거된다. ( corePoolSize < x < maximumPoolSize ) x 구간의 스레드 생존시간
        // workQueue : 작업을 보관할 블로킹 큐
        ExecutorService es = new ThreadPoolExecutor(2, 2, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        log("== 초기 상태 ==");
        printState(es);
        es.execute(new RunnableTask("taskA"));
        es.execute(new RunnableTask("taskB"));
        es.execute(new RunnableTask("taskC"));
        es.execute(new RunnableTask("taskD"));
        log("== 작업 수행중 ==");
        printState(es);

        sleep(3000);
        log("== 작업 완료 ==");
        printState(es);


        es.close();
        log("== shut down ==");
        printState(es);
    }
}
