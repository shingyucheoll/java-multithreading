package thread.control.join;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class JoinMainV1 {

    /**
     * Main Thread 는 내부 스레드의 작업을 기다리지 않기 때문에
     * 아래와 같이 결과값을 담아낼 수 없습니다.
     */
    //22:12:48.046 [     main] start
    //22:12:48.058 [ thread-1] thread 작업 시작
    //22:12:48.058 [ thread-1] thread 작업 시작
    //22:12:48.061 [     main] task1.result = 0
    //22:12:48.061 [     main] task2.result = 0
    //22:12:48.061 [     main] sum = 0
    //22:12:48.061 [     main] end
    //22:12:50.070 [ thread-1] thread 작업 완료 result = 1275
    //22:12:50.070 [ thread-1] thread 작업 완료 result = 3775
    public static void main(String[] args) {

        log("start");

        SumTask task1 = new SumTask(1, 50);
        SumTask task2 = new SumTask(51, 100);

        Thread thread1 = new Thread(task1, "thread-1");
        Thread thread2 = new Thread(task2, "thread-1");

        thread1.start();
        thread2.start();

        log("task1.result = " + task1.result);
        log("task2.result = " + task2.result);

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
