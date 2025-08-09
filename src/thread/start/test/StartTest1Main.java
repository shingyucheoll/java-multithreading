package thread.start.test;

import static util.MyLogger.log;

public class StartTest1Main {

    public static void main(String[] args) {

        Thread thread = new countThread();
        thread.start();

    }

    static class countThread extends Thread {
        @Override
        public void run() {
            for (int i = 1; i < 6; i++) {

                log("value: " + i);

            }

        }
    }
}
