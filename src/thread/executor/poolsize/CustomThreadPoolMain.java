package thread.executor.poolsize;

import thread.executor.RunnableTask;

import java.util.concurrent.*;

import static thread.executor.ExecutorUtils.printState;
import static util.MyLogger.log;

public class CustomThreadPoolMain {

    // 1. 일반
    // 스레드 개수가 100개로 처리되기 때문에 100개의 스레드로 처리되기 때문에 정상 범위의 속도가 나오게 된다.
//    static final int TASK_SIZE = 1100;

    // 2. 긴급 ( Maximum Size 까지 Thread 를 늘린다 )
    // 스레드의 개수가 순간적으로 200개 까지 늘어나기 때문에
    // 일반 설정보다 작업 속도가 빠르게 처리될 수 있다.
//    static final int TASK_SIZE = 1200;

    // 3. 거절
    // Maximum Size 까지 늘어나도 Queue 의 작업량을 전부 처리하지 못했을 때는 요청을 거절하여 서버의 다운을 막아야한다.
    // RejectedExecutionException:
    // Task thread.executor.RunnableTask@368239c8 rejected from
    // java.util.concurrent.ThreadPoolExecutor@12edcd21[Running, pool size = 200, active threads = 200, queued tasks = 1000, completed tasks = 0]

    // 따라서 서버의 스펙에 맞게 queue size, core Thread, max Thread 전부 적절하게 설정하여 처리해야한다.

    // 4. 커스텀 풀 실수하기 좋은 내용
    // ThreadPoolExecutor(
    //          100,
    //          200,
    //          60,
    //          TimeUnit.SECONDS,
    //          new ArrayBlockingQueue<>(1000)
    //      );
    // 위 내용에서 Queue Size 를 MAX 로 설정하게 되면 초과 스레드는 절대 생성되지 않는다
    // new ArrayBlockingQueue<>() -> Queue 가 무한대로 늘어나기 때문에 core Size 의 개수만큼 Thread 로 전부 처리하게 된다.
    static final int TASK_SIZE = 1201;


    public static void main(String[] args) {

        ExecutorService es =
                new ThreadPoolExecutor(100,
                        200,
                        60,
                        TimeUnit.SECONDS,
                        new ArrayBlockingQueue<>(1000));

        printState(es);

        for (int i = 0; i < TASK_SIZE; i++) {

            String taskName = "task" + i;

            try {
                es.execute(new RunnableTask(taskName));
                printState(es, taskName);
            } catch (RejectedExecutionException e) {
                log(taskName + " -> " + e);
            }
        }

        long startMs = System.currentTimeMillis();
        es.close();
        // close()가 포함하는 기능들
        //shutdown() 호출 - 새 작업 접수 중단
        //awaitTermination() 호출 - 작업 완료까지 대기 (최대 1일)
        //shutdownNow() 호출 - 시간 초과시 강제 종료
        long endMs = System.currentTimeMillis();
        log("time : " + (endMs - startMs));


        /* es.shutdown() 사용 ( java 19 버전 미만 )

        long startMs = System.currentTimeMillis();

        es.shutdown(); // 이 순간 시간 측정
        // 1. 새로운 작업 접수 거부
        // 2. 기존 실행 중인 작업들은 계속 진행
        // 3. 큐에 대기 중인 작업들도 계속 처리
        // 4. 메서드는 즉시 리턴 (작업 완료를 기다리지 않음)
        try {
            // 모든 작업이 완료될 때까지 최대 60초 대기
            boolean finished = es.awaitTermination(60, TimeUnit.SECONDS);
            if (!finished) {
                log("시간 초과! 강제 종료합니다.");
                es.shutdownNow();
            }
        } catch (InterruptedException e) {
            es.shutdownNow();
        }

        long endMs = System.currentTimeMillis();

        log("time : " + (endMs - startMs));
        */


    }
}

// ===== CPU 사용률 기반 ThreadPool 운영 가이드 =====

// 🎯 추천 CPU 사용률
// - 평상시: 50% (안전)
// - 긴급시: 80% (최대)
// - 위험: 90%+ (즉시 대응 필요)

// 📊 실무 알람 설정
// CPU_WARNING_THRESHOLD = 70%;   // 1차 알람 (스케일링 준비)
// CPU_CRITICAL_THRESHOLD = 85%;  // 2차 알람 (트래픽 제한)
// CPU_EMERGENCY_THRESHOLD = 95%; // 3차 알람 (서킷브레이커)

// 💡 ThreadPool 설정 전략
// 보수적(안정성): core = CPU코어, max = CPU코어*2, queue = 작게
// 공격적(처리량): core = CPU코어*2, max = CPU코어*4, queue = 크게

// 🏢 업계별 차이
// - 금융권: 평상시 40-60%, 최대 75% (안정성 우선)
// - 이커머스: 평상시 60-75%, 최대 90% (처리량 우선)
// - 게임/실시간: 평상시 50-70%, 최대 85% (응답속도 우선)

// ⚠️ 3단계 방어선
// 70%: 스케일링 시작
// 85%: 신규 요청 제한
// 95%: 서킷브레이커 작동




