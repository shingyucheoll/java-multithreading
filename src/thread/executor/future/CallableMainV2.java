package thread.executor.future;

import java.util.Random;
import java.util.concurrent.*;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class CallableMainV2 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        ExecutorService es = Executors.newFixedThreadPool(1);
        log("submit() 호출");

        Future<Integer> future = es.submit(new MyCallable());
        log("future 즉시 반환, future = " + future);
        // 아직 작업이 완료되지 않았을 때 아래와 같이 FutureTask@ [Not Completed, task = 작업중인 thread] 완료상태, 결과값
        // java.util.concurrent.FutureTask@6193b845[Not completed, task = thread.executor.future.CallableMainV2$MyCallable@4d405ef7]

        // future.get() 하기 전까지는 MainThread 의 상태는 RUNNABLE 상태
        log("future.get() 요청을 하기 전에는 MainThread 의 상태는 RUNNABLE 상태이며 해당 로그는 찍힌다.");

        // future.get() 호출 시점에 아직 작업이 완료되지 않을 경우 Main Thread 가 pool thread 의 작업을 기다리며 waiting 상태가 된다.
        log("future.get() 메서드 호출 시작 [블로킹] -> 작업이 완료되지 않았을 경우 main 스레드 상태가 WAITING 으로 변경됨");
        Integer result = future.get();

        // WAITING 시점 Main 에서 직접 자신의 상태를 확인할 순 없으며 작업이 완료 됐을 때 실행되어 Main Thread State = RUNNABLE 로 찍힘
        log("Main Thread State = " + Thread.currentThread().getState());

        // 해당 로그는 작업이 완료 될 때 까지 실행되지 않으며 ( Main Thread State : WAITING )
        log("get 시점 pool-Thread 의 작업이 완료되지 않았을 경우 Main Thread 가 WAITING 상태가 되어 이 로그는 작업 완료 후 발생");

        // 그러면, get 하지 않고 일정 간격으로 확인하여 처리하는 방법은..?

        log("future.get() 메서드 호출 완료 [블로킹] -> main 스레드 WAITING -> RUNNABLE (future 가 .get() 요청한 스레드를 깨운다)");
        log("result = " + result);
        // java.util.concurrent.FutureTask@...[Completed normally] ( 정상적으로 완료되었다. )
        log("future 완료, future = " + future);
        es.close();


//        ExecutorService es1 = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
//            new LinkedBlockingQueue<>());
//        Future<Integer> submit = es1.submit(new MyCallable());
//
//        log("submit.get()" + submit.get());
//        es1.close();
    }

    static class MyCallable implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            log("Callable 시작");

            sleep(3000);

            int value = new Random().nextInt(10);

            log("value:" + value);

            log("Callable 종료");

            return value;

        }
    }

}
