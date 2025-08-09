package thread.start;

import static util.MyLogger.log;

public class ManyThreadMainV2 {

    public static void main(String[] args) {
        log("main() start");

        HelloRunnable runnable = new HelloRunnable();

        for (int i = 0; i < 100; i++) {
            Thread thread = new Thread(runnable);
            thread.start();
        }

        /*
        Thread-1: run() start
        Thread-6: run() start
        Thread-4: run() start
        Thread-2: run() start
        Thread-5: run() start
        Thread-9: run() start
        Thread-3: run() start
        Thread-10: run() start
        Thread-11: run() start
        Thread-0: run() start
         */

        log("main() end");
    }
}
