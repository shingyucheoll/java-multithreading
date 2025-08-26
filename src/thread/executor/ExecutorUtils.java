package thread.executor;

import static util.MyLogger.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class ExecutorUtils {

    // ExecutorService 인터페이스는 getPoolSize(), getActiveCount() 같은 자세한 기능을 제공하지 않는다.
    public static void printState(ExecutorService executorService) {
        /*
         * 캐스팅 과정
         * ThreadPoolExecutor abc = (ThreadPoolExecutor) executorService;
         */
        if (executorService instanceof ThreadPoolExecutor poolExecutor) {
            int pool = poolExecutor.getPoolSize();
            int active = poolExecutor.getActiveCount();
            int queuedTask = poolExecutor.getQueue().size();
            long completedTask = poolExecutor.getCompletedTaskCount();
            log("[pool = " + pool + ", active = " + active + ", queuedTask = " + queuedTask + ", "
                + "completedTask = " + completedTask + "]");
        } else {
            // 캐스팅이 불가능한 Executor 가 들어올 경우
            log(executorService);
        }
    }
}
