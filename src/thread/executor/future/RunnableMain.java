package thread.executor.future;

import java.util.Random;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class RunnableMain {

    public static void main(String[] args) throws InterruptedException {

        MyRunnable task = new MyRunnable();
        Thread thread1 = new Thread(task, "Thread-1");
        thread1.start();
        thread1.join();

        log("value = " + task.value);


    }

    static class MyRunnable implements Runnable {

        int value;

        @Override
        public void run() {

            log("작업 시작");

            sleep(1000);

            value = new Random().nextInt(10);

            log("작업 종료");
        }
    }


}
