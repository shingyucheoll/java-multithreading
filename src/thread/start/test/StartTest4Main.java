package thread.start.test;

import static util.MyLogger.log;

public class StartTest4Main {


    private final boolean isStop = false;

    public static void main(String[] args) {

        Thread a = new Thread(new PrintWork("A", 1000), "Thread-A");
        Thread b = new Thread(new PrintWork("B", 500), "Thread-B");

        a.start();
        b.start();
    }

    static class PrintWork implements Runnable {

        private String message;
        private int sleepMs;

        public PrintWork(String message, int sleepMs) {
            this.message = message;
            this.sleepMs = sleepMs;
        }

        @Override
        public void run() {
            while (true) {

                try {
                    Thread.sleep(sleepMs);
                    log(message);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }
}