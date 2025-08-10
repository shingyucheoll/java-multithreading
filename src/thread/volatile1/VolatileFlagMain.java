package thread.volatile1;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

// 메모리 가시성 volatile
public class VolatileFlagMain {

    public static void main(String[] args) {

        MyTask task = new MyTask();

        Thread thread = new Thread(task, "task1");

        log("runFlag = " + task.runFlag);

        thread.start();

        sleep(1000);

        log("runFlag false 로 변경");

        task.runFlag = false;

        log("runFlag = " + task.runFlag);

        log("main 종료");

    }

    static class MyTask implements Runnable {

        // 캐시 메모리
        // boolean runFlag = true;

        // 메인 메모리에 직접 선언하여
        // Main Thread 에서 값이 변경되었을 때 즉시 반영된다.
        // 단, 캐시 메모리를 사용할 때 보다 성능이 느려지기 때문에 필요한 부분에 선언해서 사용하기
        volatile boolean runFlag = true;

        @Override
        public void run() {
            log("task 시작");

            while(runFlag) {

                // runFlag 가 false 일 때 탈출

                // 내부에 작업이 있을 경우 컨텍스트 스위치 과정에서 메인 메모리의 값도 변경되어 작업이 중단되지만
                // 아래와 같이 스레드 내부 아무 작업도 없을 때 컨텍스트 스위칭이 일어날 때 까지
                // Main Thread 에 설정한 runFlag 의 값을 적용하지 않고
                // 캐시 메모리에 적용되어 있던 runFlag 값만 계속 바라보고 있어
                // while 문이 계속 실행된다.
//                log("반복중");


            }

            log("task 종료");
        }
    }

}
