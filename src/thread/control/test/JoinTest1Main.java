package thread.control.test;

import static util.MyLogger.log;

public class JoinTest1Main {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new MyTask(), "t1");
        Thread t2 = new Thread(new MyTask(), "t2");
        Thread t3 = new Thread(new MyTask(), "t3");

        /**
         * 스레드의 순서에 따라 진행해야 한다면,
         * 아래와 같이 join
         * t1 작업이 끝날 때 까지 대기, 그 이후 메인 스레드 실행
         * t2 작업이 끝날 때 까지 대기, ..
         * ..
         * 총 9초가 걸리게 됩니다
         */
        t1.start();
        t1.join();

        t2.start();
        t2.join();

        t3.start();
        t3.join();

        System.out.println("모든 스레드 실행 완료");
    }

    static class MyTask implements Runnable {

        @Override
        public void run() {

            for (int i = 1; i <= 3; i++) {
                log(i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log("인터럽트 발생, " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }

        }
    }
}

