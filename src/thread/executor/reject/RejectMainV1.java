package thread.executor.reject;

import thread.executor.RunnableTask;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static util.MyLogger.log;

public class RejectMainV1 {

    public static void main(String[] args) {
        ThreadPoolExecutor executor =
                new ThreadPoolExecutor(
                        1,
                        1,
                        0,
                        TimeUnit.SECONDS,
                        new SynchronousQueue<>(),
                        new ThreadPoolExecutor.AbortPolicy() // RejectedExecutionHandler 인터페이스를 구현하고 있다. ( 이게 기본 정책이므로 생략해도 된다. )
                );


        executor.submit(new RunnableTask("task1"));

        try {
            executor.submit(new RunnableTask("task2"));

        } catch (RejectedExecutionException e) {
            log("요청 초과");
            // 포기, 다시 시도, 알람 등 예외 처리 고민
            log(e);
        }

        executor.close();
    }
}
