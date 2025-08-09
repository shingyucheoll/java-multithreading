package thread.control.interrupt;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class ThreadStopMainV2 {

    /**
     * 1. Main thread 에서 Task Thread 의 상태를 interrupt true 로 변경
     * 2. Task Thread 의 상태가 isInterrupted() = True 일 대
     * 3. Task Thread 가 Sleep 과 같이 InterruptedException을 발생하는 메서드를 호출하거나 이미 호출 대기중일 경우
     * 인터럽트 예외가 발생
     * <p>
     * 4. Task Thread 의 상태가 RUNNABLE 상태로 변경되며 isInterrupted() 상태가 false 로 변경됩니다.
     * <p>
     * 따라서 WAITING, TIME_WAITING 상태의 Thread 를 바로 RUNNABLE 로 변경하며
     * runFlag 와 같이 반복적으로 해당 스레드의 상태값을 확인하는 로직을 사용하지 않아도 된다.
     */

    public static void main(String[] args) {

        MyTask myTask = new MyTask();
        Thread thread1 = new Thread(myTask, "work");
        thread1.start();

        sleep(4000);
        log("작업 중단 지시 thread.interrupt()");
        thread1.interrupt();
        log("work 스레드 인터럽트 상태 1 = " + thread1.isInterrupted());

    }

    static class MyTask implements Runnable {

        @Override
        public void run() {

            try {
                while (true) {
                    log("작업 중");
                    Thread.sleep(3000);
                }
            } catch (InterruptedException e) {
                log("work 스레드 인터럽트 상태 2 = " + Thread.currentThread().isInterrupted());
                log("interrupt message = " + e.getMessage());
                log("state = " + Thread.currentThread().getName());
            }
            log("자원 정리");
            log("작업 종료");
        }
    }
}
