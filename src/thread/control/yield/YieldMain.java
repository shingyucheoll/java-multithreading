package thread.control.yield;

import static util.ThreadUtils.sleep;

public class YieldMain {

    static final int THREAD_COUNT = 100;

    public static void main(String[] args) {

        for (int i = 0; i < THREAD_COUNT; i++) {

            Thread thread = new Thread(new MyRunnable3());
            thread.start();

        }


    }

    static class MyRunnable1 implements Runnable {

        @Override
        public void run() {

            for (int i = 0; i < 10; i++) {
                System.out.println(Thread.currentThread().getName() + " - " + i);
                // 1. empty
                // sleep(1); // 2. sleep
                // Thread.yield(); // 3. yield

            }
        }
    }

    // Thread 내부에서 Sleep 을 사용할 경우 하나의 스레드에서 1~10까지 실행하지 않고 계속 스레드가 변경된다.
    // RUNNABLE -> TIMED_WAITING -> RUNNABLE 계속 반복하며 스케줄링에서 제외 - 포함 - 제외 - 포함 반복을 한다.
    // 양보할 스레드가 없을 경우 차라리 스레드를 더 실행하는것이 나을 선택일 수 있다.
    static class MyRunnable2 implements Runnable {

        @Override
        public void run() {

            for (int i = 0; i < 10; i++) {
                sleep(1);
                System.out.println(Thread.currentThread().getName() + " - " + i);
            }
        }
    }

    // yield() 는 WAITING 상태가 아닌, RUNNABLE 상태로 유지된다.
    // 자바 스레드의 RUNNABLE 상태 2가지 ( 실행 상태 : Running, 실행 대기 상태 : Ready )
    // Running - Ready - Running - Ready 이런식으로 동작 // 자바에서 두 상태를 구분할 순 없다.

    //Thread-906 - 3
    //Thread-906 - 4
    //Thread-906 - 5
    //Thread-906 - 6
    //Thread-906 - 7
    //Thread-903 - 5
    //Thread-908 - 0

    // sleep 이나, empty 처럼 1~10 전부 실행하는게 아닌 어느정도 작업을 진행하다가,
    // 다른 thread 에게 작업을 넘기고
    // 다시 받아서 작업을 이어서 하게 된다.

    // sleep 과 다르게, 스케줄러에게 힌트를 제공하며 양보할 스레드가 없을 경우 혼자 실행한다.
    static class MyRunnable3 implements Runnable {

        @Override
        public void run() {

            for (int i = 0; i < 10; i++) {
                Thread.yield();
                System.out.println(Thread.currentThread().getName() + " - " + i);
            }
        }
    }
}
