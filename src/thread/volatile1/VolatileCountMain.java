package thread.volatile1;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class VolatileCountMain {

    public static void main(String[] args) {

        MyTask task = new MyTask();
        Thread t = new Thread(task, "wrok-1");
        t.start();

        sleep(1000);

        task.flag = false;


        log("flag = " + task.flag + ", count = " + task.count + " in while()");

    }



    static class MyTask implements Runnable{

        // 23:56:39.755 [   wrok-1] flag = true, count = 1000000000 in while()
        // 23:56:39.766 [     main] flag = false, count = 1010774855 in while()
        // 23:56:39.850 [   wrok-1] flag = true, count = 1100000000 in while()
        // 23:56:39.851 [   wrok-1] flag = false, count = 1100000000 out while()
        // 위와같이 log를 찍는 순간 flag 의 값이 변경되는 이유는 해당 시점에 컨텍스트 스위칭이 발생하고
        // 그 이후에 flag 의 값이 변경되기 때문이다.

//        boolean flag = true;
//        long count;

        // 00:00:29.730 [   wrok-1] flag = true, count = 500000000 in while()
        // 00:00:29.880 [   wrok-1] flag = true, count = 600000000 in while()
        // 00:00:29.905 [     main] flag = false, count = 616710058 in while()
        // 00:00:29.905 [   wrok-1] flag = false, count = 616710058 out while()
        // 반면, 발러틸을 사용할 경우 Main Memory 에 직접 접근하는 volatile 사용할 경우
        // Main Thread 에서 flag 값을 변경하는 순간 즉시 Thread 내부에서도 값이 변경되어 ( 캐시 메모리가 아닌 메인 메모리를 사용하기 때문에 - ( 메모리 가시성 ) )
        // 동일한 count 를 찍는것을 확인할 수 있다.  단, 캐시 메모리가 아닌 메인 메모리를 바라보기 때문에 성능의 차이가 난다.
        volatile boolean flag = true;
        long count;

        @Override
        public void run() {

            while(flag) {
                count++;
                if (count % 100_000_000 == 0) {

                    log("flag = " + flag + ", count = " + count + " in while()");
                }
            }

            log("flag = " + flag + ", count = " + count + " out while() ");

        }
    }
}
