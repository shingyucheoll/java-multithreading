package thread.executor.test;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class OrderService {

    private final ExecutorService es = Executors.newFixedThreadPool(3);

    public void order(String orderNo) {

        // try-with-resources 사용으로 정상적으로 작업을 마칠 경우 close()
        // 혹은 finally {
        //        es.close(); 사용
        //    }
        List<Callable<Boolean>> works = List.of(
                new InventoryWork(orderNo),
                new AccountingWork(orderNo),
                new ShippingWork(orderNo)
        );

        try {

            List<Future<Boolean>> futures = es.invokeAll(works);

            if (futures.stream().allMatch(Future::isDone)) {
                log("모든 주문 처리가 성공적으로 완료되었습니다.");
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
            log("작업 중 예외가 발생했습니다. " + e);
            throw new RuntimeException(e);
        }
    }

    public void close() {
        es.shutdown();
    }

    static class InventoryWork implements Callable<Boolean> {

        private final String orderNo;

        public InventoryWork(String orderNo) {
            this.orderNo = orderNo;
        }

        @Override
        public Boolean call() throws Exception {

            try {
                log("재고 업데이트 : " + orderNo);
                sleep(1000);
                return true;
            } catch (Exception e) {
                log("재고 업데이트 중 예외 발생 : " + orderNo);
                return false;
            }

        }
    }

    static class ShippingWork implements Callable<Boolean> {

        private final String orderNo;

        public ShippingWork(String orderNo) {
            this.orderNo = orderNo;
        }

        @Override
        public Boolean call() throws Exception {

            try {
                log("배송 시스템 알림 : " + orderNo);
                sleep(1000);
                return true;

            } catch (Exception e) {
                log("배송 시스템 알림 중 예외 발생 : " + orderNo);
                return false;
            }

        }
    }


    static class AccountingWork implements Callable<Boolean> {

        private final String orderNo;

        public AccountingWork(String orderNo) {
            this.orderNo = orderNo;
        }

        @Override
        public Boolean call() throws Exception {

            try {
                log("회계 시스템 업데이트 : " + orderNo);
                sleep(1000);
                return true;
            } catch (Exception e) {
                log("회계 시스템 업데이트 중 예외 발생 : " + orderNo);
                return false;
            }
        }
    }
}
