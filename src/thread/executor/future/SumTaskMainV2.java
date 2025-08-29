package thread.executor.future;

import java.util.concurrent.*;

import static util.MyLogger.log;


/**
 * 비동기 병렬 처리를 통한 성능 최적화 가이드
 * <p>
 * = 기술 발전 과정 =
 * 1. Thread + join() - 원시적 스레드 관리
 * 2. ExecutorService + Callable + Future - 스레드 풀 도입 (현재 코드 방식)
 * 3. @Async + CompletableFuture - Spring 통합 자동화 (실무 권장)
 * 4. WebFlux + Reactive - 논블로킹 I/O (최신 트렌드)
 * <p>
 * = Callable + Future 방식의 장점 =
 * - Runnable과 달리 반환값을 받을 수 있음
 * - Thread.join()이나 내부 필드 접근 불필요
 * - 여러 스레드 작업 후 결과값 대기 가능
 * - Main Thread가 오케스트레이터 역할 수행
 * <p>
 * 사례 1) 전자상거래 상품 상세 페이지
 * - 상품 정보, 재고, 리뷰, 추천상품, 쿠폰을 병렬 조회
 * - 순차 처리: 1,950ms → 병렬 처리: 800ms (60% 단축)
 * <p>
 * 사례 2) 관리자 대시보드 데이터 수집
 * - 매출, 사용자 활동, 재고 알림, KPI, 주문 목록을 병렬 조회
 * - 순차 처리: 5.8초 → 병렬 처리: 2초 (65% 단축)
 * <p>
 * 사례 3) 회원가입 프로세스 최적화
 * - 이메일 중복체크, SMS 인증, 추천인 검증을 병렬 처리
 * - 의존성 있는 작업은 순차, 독립적 작업만 병렬 처리
 * <p>
 * = 현재 코드의 위치 =
 * - 학습용/이해용으로 완벽한 예시
 * - 비동기 병렬 처리의 핵심 원리 학습
 * - 실무에서는 @Async + CompletableFuture 방식 권장
 * <p>
 * = 주의사항 =
 * - 타임아웃 설정 필수 (전체 사용자 경험 고려)
 * - 필수 데이터 vs 선택 데이터 구분
 * - 일부 실패 시에도 서비스 제공 가능하도록 설계
 * - 스레드 풀 크기와 메모리 사용량 모니터링
 */
public class SumTaskMainV2 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        SumTask task1 = new SumTask(1, 50);
        SumTask task2 = new SumTask(51, 100);

        ExecutorService es = Executors.newFixedThreadPool(2);

        Future<Integer> future1 = es.submit(task1);
        Future<Integer> future2 = es.submit(task2);

        Integer sum1 = future1.get();
        Integer sum2 = future2.get();

        log("task1 : result = " + sum1);
        log("task2 : result = " + sum2);

        int sumAll = sum1 + sum2;

        log("sumAll = " + sumAll);
        log("End");

        es.close();
    }

    static class SumTask implements Callable<Integer> {

        int startValue;
        int endValue;

        public SumTask(int startValue, int endValue) {
            this.endValue = endValue;
            this.startValue = startValue;
        }

        @Override
        public Integer call() throws Exception {
            log("작업 시작");

            Thread.sleep(2000);

            int sum = 0;

            for (int i = startValue; i <= endValue; i++) {
                sum += i;
            }

            log("작업 완료 result = " + sum);

            return sum;
        }
    }
}