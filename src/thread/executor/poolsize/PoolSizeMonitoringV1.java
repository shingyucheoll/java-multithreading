package thread.executor.poolsize;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

/**
 * 코어 스레드와 초과 스레드의 상태는 같다.
 * 예를 들어 코어 스레드로 생성된 Thread 가 1,2
 * 초과 스레드로 생성된 Thread 가 3~6 이라고 가정했을 때
 * 마지막 queue 의 작업을 모두 끝내고
 * KeepAliveTime 이 적용되는 Thread 가 항상 초과 스레드로 생성된 스레드가 아닌
 * 코어 스레드로 생성된 스레드에도 적용되어
 * 기본으로 유지하고 있는 코어 스레드의 상태가 변경될 수 있음
 * ---
 * 마지막 남은 thread 는 3,4
 */
public class PoolSizeMonitoringV1 {

    private static AtomicInteger taskCounter = new AtomicInteger(0);
    private static AtomicInteger threadCreationOrder = new AtomicInteger(0);

    static class MonitoringTask implements Runnable {
        private final String taskName;
        private final int processingTime;

        public MonitoringTask(String taskName, int processingTime) {
            this.taskName = taskName;
            this.processingTime = processingTime;
        }

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();

            log("🔄 [" + taskName + "] 시작 - 스레드: " + threadName +
                    " | 처리시간: " + processingTime + "ms");

            sleep(processingTime);

            log("✅ [" + taskName + "] 완료 - 스레드: " + threadName);
        }
    }

    static class MonitoringThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            int order = threadCreationOrder.incrementAndGet();
            Thread t = new Thread(r, "CustomThread-" + order);

            log("🆕 새 스레드 생성: " + t.getName() + " (생성순서: " + order + ")");

            return t;
        }
    }

    public static void printDetailedState(ThreadPoolExecutor executor, String phase) {
        log("\n📊 === " + phase + " ===");
        log("Pool Size: " + executor.getPoolSize());
        log("Active Threads: " + executor.getActiveCount());
        log("Queue Size: " + executor.getQueue().size());
        log("Completed Tasks: " + executor.getCompletedTaskCount());

        // 현재 살아있는 스레드들 확인
        Thread.getAllStackTraces().keySet().stream()
                .filter(t -> t.getName().startsWith("CustomThread"))
                .forEach(t -> log("👥 살아있는 스레드: " + t.getName() + " (상태: " + t.getState() + ")"));

        log("=====================================\n");
    }

    public static void main(String[] args) {
        ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(2);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                2,                              // corePoolSize
                4,                              // maximumPoolSize
                3, TimeUnit.SECONDS,            // keepAliveTime
                workQueue,
                new MonitoringThreadFactory()   // 커스텀 스레드 팩토리
        );

        log("🚀 ThreadPoolExecutor 시작 (core=2, max=4, keepAlive=3초)");
        printDetailedState(executor, "초기 상태");

        // Phase 1: 코어 스레드 2개 생성
        log("📝 Phase 1: 코어 스레드들이 작업 시작");
        executor.execute(new MonitoringTask("task1", 5000));
        sleep(100);
        executor.execute(new MonitoringTask("task2", 5000));
        sleep(100);
        printDetailedState(executor, "코어 스레드 2개 작업 중");

        // Phase 2: Queue 채우기
        log("📝 Phase 2: Queue 채우기 (초과 스레드 생성 전)");
        executor.execute(new MonitoringTask("task3", 3000));
        sleep(100);
        executor.execute(new MonitoringTask("task4", 3000));
        sleep(100);
        printDetailedState(executor, "Queue 가득참 (2개)");

        // Phase 3: 초과 스레드 생성
        log("📝 Phase 3: 초과 스레드 생성");
        executor.execute(new MonitoringTask("task5", 3000));
        sleep(100);
        printDetailedState(executor, "초과 스레드 1개 생성");

        executor.execute(new MonitoringTask("task6", 3000));
        sleep(100);
        printDetailedState(executor, "초과 스레드 2개 생성 (최대)");

        // Phase 4: RejectedExecutionException 발생
        log("📝 Phase 4: 용량 초과로 작업 거절");
        try {
            executor.execute(new MonitoringTask("task7", 1000));
        } catch (RejectedExecutionException e) {
            log("❌ task7 실행 거절: " + e.getClass().getSimpleName());
        }
        printDetailedState(executor, "최대 용량 도달");

        // Phase 5: 작업 완료 대기
        log("📝 Phase 5: 모든 작업 완료 대기");
        sleep(6000);
        printDetailedState(executor, "모든 작업 완료");

        // Phase 6: KeepAliveTime 후 상태 확인
        log("📝 Phase 6: KeepAliveTime 적용 대기");
        log("⏰ 3초 후 초과 스레드들이 제거될 예정...");
        sleep(4000);
        printDetailedState(executor, "KeepAliveTime 후");

        // Phase 7: 추가 작업으로 어떤 스레드가 살아남았는지 확인
        log("📝 Phase 7: 살아남은 스레드 확인용 추가 작업");
        executor.execute(new MonitoringTask("final-task1", 1000));
        executor.execute(new MonitoringTask("final-task2", 1000));
        sleep(100);
        printDetailedState(executor, "살아남은 스레드들이 작업 중");

        sleep(2000);
        printDetailedState(executor, "최종 상태");

        executor.shutdown();

        log("\n🎯 실험 결과 분석:");
        log("- 먼저 생성된 CustomThread-1, CustomThread-2가 반드시 살아남았는가?");
        log("- 아니면 나중에 생성된 스레드가 살아남을 수도 있는가?");
        log("- 이를 통해 '코어/초과' 라벨이 고정적이지 않음을 확인");
    }
}