package thread.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static thread.executor.ExecutorUtils.printState;

public class PreStartPoolMain {

    public static void main(String[] args) {

        // 서버 실행 시 순간적으로 트래픽이 몰릴 경우 아래와 같이 설정하여 처리할 수 있다.
        ExecutorService es = Executors.newFixedThreadPool(1000);
        printState(es);

        ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor) es;
        poolExecutor.prestartAllCoreThreads();
        printState(es);

    }
}
