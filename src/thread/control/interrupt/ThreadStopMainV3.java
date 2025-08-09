package thread.control.interrupt;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class ThreadStopMainV3 {

    public static void main(String[] args) {

        ThreadStopMainV3.MyTask myTask = new ThreadStopMainV3.MyTask();
        Thread thread1 = new Thread(myTask, "work");
        thread1.start();

        sleep(100);
        log("작업 중단 지시 thread.interrupt()");
        thread1.interrupt();
        log("work 스레드 인터럽트 상태 1 = " + thread1.isInterrupted());


    }

    static class MyTask implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) { // 인터럽트 상태를 체크 ( 변경 x )
                log("작업중");
            }

            // 예외를 발생시켜서 Interrupted 상태를 false 로 다시 변경시키는게 아니기 때문에 true 로 유지된다.
            log("work 스레드 인터럽트 상태 2 = " + Thread.currentThread().isInterrupted());

            // 따라서 해당 스레드의 인터럽트 상태가 계속 true인 상태에서
            // InterruptedException 을 발생시키는 메서드를 실행했을 때
            // 예외가 발생하여 정상적으로 종료시킬 수 없게 된다.

            // 따라서 인터럽트를 true로 변경 후 목적을 달성시켰을 때
            // 다시 false 상태로 돌려두어야 합니다.
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
