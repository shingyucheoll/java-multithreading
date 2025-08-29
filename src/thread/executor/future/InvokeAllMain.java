package thread.executor.future;

import thread.executor.CallableTask;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static util.MyLogger.log;

/**
 * ExecutorService의 invokeAll()과 invokeAny() 현재 활용 가능한 사례
 * ===============================================================================
 * invokeAll() - 여러 API 호출 후 데이터 병합
 * ===============================================================================
 * 사용자 정보를 여러 서비스에서 동시 조회 후 통합
 * - 예: 프로필, 주문내역, 포인트 정보를 동시 조회 후 마이페이지 구성
 * List<Callable<Object>> tasks = List.of(
 * () -> profileService.getProfile(userId),
 * () -> orderService.getOrders(userId),
 * () -> pointService.getPoints(userId)
 * );
 * List<Future<Object>> results = es.invokeAll(tasks);
 * // 모든 데이터 수집 후 통합 객체 생성
 * <p>
 * 더 나은 방식: CompletableFuture.allOf()
 * - 더 유연한 예외 처리와 타입 안정성
 * - 체이닝을 통한 후처리 작업 연결 가능
 * CompletableFuture<Profile> profileFuture = CompletableFuture.supplyAsync(() -> profileService.getProfile(userId));
 * CompletableFuture<Orders> ordersFuture = CompletableFuture.supplyAsync(() -> orderService.getOrders(userId));
 * CompletableFuture<Points> pointsFuture = CompletableFuture.supplyAsync(() -> pointService.getPoints(userId));
 * CompletableFuture<MyPageData> result = CompletableFuture.allOf(profileFuture, ordersFuture, pointsFuture)
 * .thenApply(v -> new MyPageData(profileFuture.join(), ordersFuture.join(), pointsFuture.join()));
 * <p>
 * ===============================================================================
 * invokeAny() - 타임아웃 처리
 * ===============================================================================
 * 외부 API 호출과 타임아웃 작업을 동시 실행
 * - 예: 느린 외부 API 호출 시 일정 시간 후 타임아웃 처리
 * List<Callable<String>> tasks = List.of(
 * () -> externalAPI.slowCall(),
 * () -> { Thread.sleep(3000); throw new TimeoutException("3초 타임아웃"); }
 * );
 * String result = es.invokeAny(tasks); // 3초 내 응답 또는 타임아웃 예외
 * <p>
 * 더 나은 방식: CompletableFuture.orTimeout() (Java 9+)
 * - 더 직관적이고 간단한 타임아웃 처리
 * - 별도의 타임아웃 작업 생성 불필요
 * CompletableFuture<String> result = CompletableFuture
 * .supplyAsync(() -> externalAPI.slowCall())
 * .orTimeout(3, TimeUnit.SECONDS)
 * .exceptionally(ex -> "타임아웃 또는 에러 발생: " + ex.getMessage());
 */
public class InvokeAllMain {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        ExecutorService es = Executors.newFixedThreadPool(10);

        CallableTask task1 = new CallableTask("task1", 1000);
        CallableTask task2 = new CallableTask("task2", 2000);
        CallableTask task3 = new CallableTask("task3", 3000);

        List<CallableTask> tasks = List.of(task1, task2, task3);

        // 3가지 작업이 모두 완료되어야 다음 작업 진행
//        List<Future<Integer>> futures = es.invokeAll(tasks);
//        for(Future<Integer> future : futures){
//            Integer value = future.get();
//            log("value = " + value);
//        }

        // 가장 먼저 완료된 작업 하나만 반환하고 나머지는 취소
        // 인터럽트 발생 sleep interrupted
        Integer value = es.invokeAny(tasks);
        log("value = " + value);
        es.close();


    }
}
