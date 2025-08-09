package thread.control.join;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class JoinMainV3 {

    public static void main(String[] args) throws InterruptedException {

        // 메인 스레드 참조 저장
        Thread mainThread = Thread.currentThread();

        log("start");
        printMainThreadState(mainThread);

        SumTask task1 = new SumTask(1, 50);
        SumTask task2 = new SumTask(51, 100);

        Thread thread1 = new Thread(task1, "thread-1");
        Thread thread2 = new Thread(task2, "thread-1");

        thread1.start();
        thread2.start();

        // 별도 스레드에서 메인 스레드 상태를 모니터링
        Thread stateMonitor = new Thread(() -> {
            try {
                sleep(1000); // 1초 후 확인 (join 실행 후)
                printMainThreadState(mainThread); // WAITING 상태 확인 가능
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "state-monitor");

        stateMonitor.start();

        // 스레드가 종료될 때 까지 대기한다 ( join )
        // 이 때 Main Thread 의 State 는 WAITING 상태가 됩니다.
        log("join() - main 스레드가 tread1, 2 종료까지 대기합니다.");
        thread1.join();
        thread2.join();
        log("대기 완료");

        int sumAll = task1.result + task2.result;

        log("sum = " + sumAll);

        log("end");

    }

    static class SumTask implements Runnable {

        int startValue;
        int endValue;
        int result = 0;

        public SumTask(int startValue, int endValue) {
            this.startValue = startValue;
            this.endValue = endValue;
        }


        @Override
        public void run() {
            log("thread 작업 시작");
            sleep(2000);

            int sum = 0;
            for (int i = startValue; i <= endValue; i++) {
                sum += i;
            }
            result += sum;

            log("thread 작업 완료 result = " + result);

        }
    }


    // 메인 스레드 상태를 출력하는 헬퍼 메서드
    private static void printMainThreadState(Thread mainThread) {
        log("Main thread state: " + mainThread.getState() +
                " (Thread: " + mainThread.getName() + ")");
    }

}
