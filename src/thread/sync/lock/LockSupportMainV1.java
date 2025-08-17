package thread.sync.lock;

import java.util.concurrent.locks.LockSupport;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class LockSupportMainV1 {

    public static void main(String[] args) {

        Thread thread1 = new Thread(new ParkTest(), "Thread-1");
        thread1.start();

        // 잠시 대기하여 Thread-1 park 상태에 빠질 시간을 준다
        sleep(100);
        log("Thread-1 state: " + thread1.getState());


        // WAITING 상태를 RUNNABLE 상태로 변경하며 interrupted 상태를 false 상태로 유지합니다.
//        log("main -> unpark(Thread-1)");
//        LockSupport.unpark(thread1);


        // unpark 처럼 WAITING 상태를 RUNNABLE 상태로 변경하지만 Interrupted 상태를 true 로 변경합니다.
        log("main -> interrupt (Thread-1)");
        thread1.interrupt();

        // + 위 두 작업을 진행하지 않고 스레드 내부에서 LockSupport.park() 를 사용한 상태로 두게 되면
        // WAITING 상태에서 계속 대기하여 스레드가 종료되지 않습니다.
    }

    static class ParkTest implements Runnable {


        @Override
        public void run() {
            log("park 시작");
            // 시작 하자마자 WAITING 상태로 변경
            LockSupport.park();
            log("pakr 종료, state : " + Thread.currentThread().getState());
            log("인터럽트 상태, interrupted : " + Thread.currentThread().interrupted());
        }
    }
}
