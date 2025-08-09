package thread.control.join;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class JoinMainV2 {

    public static void main(String[] args) {

        log("start");

        SumTask task1 = new SumTask(1, 50);
        SumTask task2 = new SumTask(51, 100);

        Thread thread1 = new Thread(task1, "thread-1");
        Thread thread2 = new Thread(task2, "thread-1");

        thread1.start();
        thread2.start();

        int count = 0;

        // Step 1 - 두 스레드의 상태가 종료될 때 까지 While 문을 반복하며 체크하는 작업 ( 2초간 약 16만번 )
        while (thread1.getState() != Thread.State.TERMINATED || thread2.getState() != Thread.State.TERMINATED) {
            log("상태 Check // count : " + count);
            count++;
        }
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

}
