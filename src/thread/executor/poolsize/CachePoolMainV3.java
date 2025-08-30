package thread.executor.poolsize;

import thread.executor.RunnableTask;

import java.util.concurrent.*;

import static thread.executor.ExecutorUtils.printState;
import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

/**
 * SynchronousQueue 는 사이즈가 0인 특별한 큐이며
 * newCachedThreadPool 은 core Thread Size = 0 / maximum Thread Size = MAX 이다.
 * 그래서 요청이 들어오면 바로 초과 스레드가 생성되고 직접 처리하게 된다.
 *
 * 장점
 * 사용자의 트래픽에 따라 유연하게 스레드의 수가 변하기 때문에 작업의 속도가 빠르고 CPU, 메모리를 매우 유연하게 사용할 수 있다.
 *
 * 단점
 * 하지만, CPU 와 메모리의 자원은 한계가 있기 때문에 적절한 시점에 시스템을 증설해야 하며, 대처를 하지 못하게 될 경우 서버가 다운될 수 있다.
 *
 * 주의점
 * 스레드의 생산 비용은 생각보다 높은 편이며 ( 1개당 최소 1MB )
 * 너무 많은 스레드를 생성하게 될 경우 스레드에 시스템이 잠식 당해 다운된다.
 *
 * 따라서, CachedThreadPool 전략은 서버의 자원을 최대한 사용하지만 서버가 감당할 수 있는 임계점을 넘는 순간 시스템이 다운된다.
 */
public class CachePoolMainV3 {

    public static void main(String[] args) {

        ExecutorService es = Executors.newCachedThreadPool();

        // Executors.newCachedThreadPool() 실행 코드
        //        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
        //                60L, TimeUnit.SECONDS,
        //                new SynchronousQueue<Runnable>());

        // 초과 스레드는 3초기 자닜을 때 전부 제거 되어야 한다.
        ExecutorService es1 = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 3, TimeUnit.SECONDS, new SynchronousQueue<>());

        log("pool 생성");
        printState(es1);

        for(int i = 0; i <= 4; i++) {
            String taskName = "task" + i;
            es1.execute(new RunnableTask(taskName));
            printState(es1, taskName);
        }

        sleep(3000);
        log("== 작업 수행 완료 =="); // [pool = 5, active = 0, queuedTask = 0, completedTask = 5]
        printState(es1);

        sleep(3000);
        log("== maximumPoolSize KeepAliveTime 초과 ==");  // [pool = 0, active = 0, queuedTask = 0, completedTask = 5]
        printState(es1);

        // 위와 아래와 비교해보면 shutdown 을 사용하지 않아도, 이미 스레드는 KeepAliveTime 이 지나 사라지게 된다.
        // 급증하는 사용자 트래픽에 어울리는 전략으로 요청이 늘어나는 만큼 스레드를 늘려 처리하기 때문에
        // 매우 유연한 전략이다.

        es1.shutdown();
        log("shutdown 완료");                             // [pool = 0, active = 0, queuedTask = 0, completedTask = 5]
        printState(es1);
    }
}
