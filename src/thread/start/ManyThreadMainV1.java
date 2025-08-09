package thread.start;

import static util.MyLogger.log;

public class ManyThreadMainV1 {

    public static void main(String[] args) {
        log("main() start");

        HelloRunnable runnable = new HelloRunnable();

        // 아래 내용은 순서를 보장하지 않습니다.
        /*
        13:51:38.962 [     main] main() start
        13:51:38.964 [     main] main() end
        Thread-1: run() start
        Thread-0: run() start
        Thread-2: run() start
        종료 코드 0(으)로 완료된 프로세스
         */

        Thread thread1 = new Thread(runnable);
        thread1.start();
        Thread thread2 = new Thread(runnable);
        thread2.start();
        Thread thread3 = new Thread(runnable);
        thread3.start();
        log("main() end");
    }
}
