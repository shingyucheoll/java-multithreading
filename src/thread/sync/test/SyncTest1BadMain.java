package thread.sync.test;

import static util.MyLogger.log;

public class SyncTest1BadMain {

    public static void main(String[] args) throws InterruptedException {

        Counter counter = new Counter();

        Runnable task = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    counter.incremenet();
                }
            }
        };

        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);

        log(" count : " + counter.getCount());

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        log(" count : " + counter.getCount());

    }

    static class Counter {
        private int count = 0;

        private void incremenet() {
            count = count + 1;
        }

        public int getCount() {
            return count;
        }

    }


}
