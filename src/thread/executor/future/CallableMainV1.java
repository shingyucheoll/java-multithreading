package thread.executor.future;

import java.util.Random;
import java.util.concurrent.*;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class CallableMainV1 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        ExecutorService es = Executors.newFixedThreadPool(1);
        Future<Integer> future = es.submit(new MyCallable());
        Integer result = future.get();
        log("result:" + result);
        es.close();

        ExecutorService es1 = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
        Future<Integer> submit = es1.submit(new MyCallable());

        log("submit.get()" + submit.get());
        es1.close();
    }

    static class MyCallable implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            log("Callable 시작");

            sleep(2000);

            int value = new Random().nextInt(10);

            log("value:" + value);

            log("Callable 종료");

            return value;

        }
    }

}
