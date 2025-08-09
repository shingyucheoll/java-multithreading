package thread.control.interrupt;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class ThreadStopMainV4 {

    public static void main(String[] args) {

        ThreadStopMainV4.MyTask myTask = new ThreadStopMainV4.MyTask();
        Thread thread1 = new Thread(myTask, "work");
        thread1.start();

        sleep(50);
        log("작업 중단 지시 thread.interrupt()");
        thread1.interrupt();
        log("work 스레드 인터럽트 상태 1 = " + thread1.isInterrupted());


    }

    static class MyTask implements Runnable {

        @Override
        public void run() {
            while (!Thread.interrupted()) {   // 인터럽트 상태 변경 o
                log("작업중");
            }

            log("work 스레드 인터럽트 상태 2 = " + Thread.currentThread().isInterrupted());

            try {
                log("자원 정리");

                Thread.sleep(1000);

                log("자원 정리 성공");

            } catch (InterruptedException e) {
                log("자원 정리 실패 - 자원 정리 중 인터럽트 발생");
                log("Task Thread 인터럽트 상태 3 = " + Thread.currentThread().isInterrupted());
            }

            log("자원 종료");
            log("state = " + Thread.currentThread().getName());
        }
    }
}
