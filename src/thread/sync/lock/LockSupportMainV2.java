package thread.sync.lock;

import java.util.concurrent.locks.LockSupport;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class LockSupportMainV2 {

    public static void main(String[] args) {

        Thread thread1 = new Thread(new ParkTest(), "Thread-1");
        thread1.start();

        // 잠시 대기하여 Thread-1 park 상태에 빠질 시간을 준다
        sleep(100);
        log("Thread-1 state: " + thread1.getState());


    }

    static class ParkTest implements Runnable {


        @Override
        public void run() {
            log("park 시작");

            // 2초 뒤 스스로 대기 상태를 빠져나온다.
            LockSupport.parkNanos(2_000_000_000);  // 1,000,000 Nanso = 1 ms 따라서, 20억 Nanos  = 2000ms = 2초
            log("pakr 종료, state : " + Thread.currentThread().getState());
            log("인터럽트 상태, interrupted : " + Thread.currentThread().interrupted());
        }
    }
}
