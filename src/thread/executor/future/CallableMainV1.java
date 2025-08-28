package thread.executor.future;

import static util.MyLogger.*;
import static util.ThreadUtils.*;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
