package thread.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static thread.executor.ExecutorUtils.printState;
import static util.MyLogger.log;

public class ExecutorShutDownMain {

    public static void main(String[] args) {

        ExecutorService es = Executors.newFixedThreadPool(2);
        es.execute(new RunnableTask("taskA"));
        es.execute(new RunnableTask("taskB"));
        es.execute(new RunnableTask("taskC"));
        es.execute(new RunnableTask("longTask", 100_000)); // 100초 대기

        printState(es);

        log("== shutdown 시작 ==");
        shutdownAndAwaitTermination(es);
        log("== shutdown 완료 ==");

        printState(es);

    }

    private static void shutdownAndAwaitTermination(ExecutorService es) {

        es.shutdown();  // non-blocking : 새로운 작업을 받지 않으며, 처리 중 or 큐에 이미 대기중인 작업까진 처리한다. 이후에 풀 스레드 종료

        try {
            // 이미 등록된 작업을 완료할 때 까지 대기한다. ( 10초만 기다리며, 10초 이상 걸릴 경우 false 반환 ) ! 로 체크해야함 ..
            if (!es.awaitTermination(10, TimeUnit.SECONDS)) {
                // 작업 종료가 오래 걸릴 경우
                log("서비스 정상 종료 실패 -> 강제 종료 시도");
                // 작업중인 스레드도 즉시 종료하기 때문에 스레드 내부에서 InterruptedException 발생   -  인터럽트를 받을 수 없는 코드의 경우 자바를 강제종료 해야함
                es.shutdownNow();
                // 작업이 취소될 때 까지 대기한다.
                if (es.awaitTermination(10, TimeUnit.SECONDS)) {
                    log("서비스가 종료되지 않았습니다.");  // 알림 발송 및 강제종료
                }
            }
        } catch (InterruptedException e) {
            // awaitTermination() 으로 대기중인 현재 스레드가 인터럽트 될 수 있다. ( es.shutdownNow() )
            es.shutdownNow();
        }
    }
}
