package thread.control.join;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class JoinMainV4 {

    public static void main(String[] args) throws InterruptedException {

        log("start");

        SumTask task1 = new SumTask(1, 50);
        SumTask task2 = new SumTask(51, 100);

        Thread thread1 = new Thread(task1, "thread-1");
        Thread thread2 = new Thread(task2, "thread-1");

        thread1.start();
        // 스레드가 종료될 때 까지 대기한다 ( join() 메서드는 매개변수에 따라 오버로드가 가능 시간을 입력하여 해당 시간만 기다릴 수 있습니다. )
        // join 과 다르게 이 경우에는 TIMED_WAITING 상태가 됩니다. ( WAITING : 무기한 대기가 아닌, 특정 시간만 기다림 )
        // 위처럼 사용 시 thread 의 결과값에 대한 오류 추적이 필요합니다.
        thread1.join(1000);
        thread2.start();

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
