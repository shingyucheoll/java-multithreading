package thread.executor.future;

import java.util.concurrent.*;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class FutureCancelMain {

//    private static boolean mayInterruptIfRunning = true;

    // 이미 작업중인 Task 는 끝까지 작업한다 ??
    private static final boolean mayInterruptIfRunning = false;

    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(1);
        Future<String> future = es.submit(new MyTask());
        log("Future.state : " + future.state());

        sleep(3000);

        log("future.cancel(" + mayInterruptIfRunning + ")");

        boolean cancelResult = future.cancel(mayInterruptIfRunning);

        log("cancel (" + mayInterruptIfRunning + ") cancelResult : " + cancelResult);

        try {
            // 이미 취소된 작업은 반환값을 받을 수 없습니다.
            log("Future result = " + future.get());

            // RunTimeException
        } catch (CancellationException e) {
            log("Future 는 이미 취소 되었습니다.");

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        es.close();

    }

    static class MyTask implements Callable<String> {

        @Override
        public String call() {

            for (int i = 1; i <= 10; i++) {
                try {
                    log("작업 중 : " + i);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log("인터럽트 발생");
                    return "Interrupted";
                }
            }
            return "Completed";
        }
    }
}