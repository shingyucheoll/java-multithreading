package thread.control.join;

import util.ThreadUtils;

import static util.MyLogger.log;

public class JoinMainV0 {

    /**
     * Main Thread 는 다른 스레드를 기다리지 않는다.
     * 아래처럼 사용 시 CPU 실행 순서에 따라 로그가 달라지지만 Main Thread 의 start ~ end 내부에 선언된 스레드의 작업은 기다리지 않는다.
     */
    public static void main(String[] args) {
        log("start");

        Thread thread1 = new Thread(new Job(), "thread-1");
        Thread thread2 = new Thread(new Job(), "thread-2");

        log(thread1.getState());
        log(thread2.getState());

        thread1.start();
        thread2.start();

        log(thread1.getState());
        log(thread2.getState());
        log("End");
    }

    static class Job implements Runnable {

        @Override
        public void run() {
            log("작업 시작");

            ThreadUtils.sleep(2000);

            log("작업 완료");

        }
    }
}
