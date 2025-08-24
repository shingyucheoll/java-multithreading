package thread.cas.spinlock;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class SpinLockMain {

    public static void main(String[] args) {
//        SpinLockBad spinLock = new SpinLockBad();
        SpinLock spinLock = new SpinLock();

        Runnable task = new Runnable() {
            @Override
            public void run() {
                spinLock.lock();
                try {
                    // critical section
                    log("비즈니스 로직 실행");
                    sleep(10);  // 오래 걸리는 로직에서는 스핀 락 사용하면 안된다. ( 계속 제자리에서 돌면서 스핀 대기 하기 때문에 )
                    // 로그를 찍었기 때문에 10ms 에 300회 정도 반복하지만 로그도 없었다면 계속 제자리 스핀 대기를 하기 때문에 적절하게 사용해야 한다.
                } finally {
                    spinLock.unlock();
                }
            }
        };

        Thread thread1 = new Thread(task, "Thread-1");
        Thread thread2 = new Thread(task, "Thread-2");

        thread1.start();
        thread2.start();
    }
}
