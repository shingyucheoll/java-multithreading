package thread.sync;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static util.MyLogger.log;

public class BankAccountV6 implements BankAccount {

    private int balance;

    private final Lock lock = new ReentrantLock();

    public BankAccountV6(int initialBalance) {
        this.balance = initialBalance;
    }

    @Override
    public boolean withdraw(int amount) {
        log("거래 시작 : " + getClass().getSimpleName());


        try {
            // lock.tryLock(time, TimeUnit) 사용하여 일정 시간동안 대기하고 그 시간동안 lock 을 획득하지 못할 경우 TERMINATED 상태로 변경
            if(!lock.tryLock(500, TimeUnit.MILLISECONDS)) {
                log("[진입 실패] 이미 처리중인 작업이 있습니다.");
                return false;  // 즉시 종료
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            // 임계 영역 시작
            log("[검증 시작] 현재 잔액 : " + balance + ", 출금액 : " + amount);
            if (balance < amount) {
                log("[검증 실패] 현재 잔액 : " + balance + ", 출금액 : " + amount);
                return false;
            }

            log("[검증 완료] 현재 잔액 : " + balance + ", 출금액 : " + amount);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            balance -= amount;

            log("[출금 완료] 현재 잔액 : " + balance);

        } finally {
            lock.unlock();  // 대기하는 스레드가 락을 얻을 수 있도록 finally - unlock 사용
        }
        log("거래 종료");
        return true;
    }

    @Override
    public int getBalance() {

        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }

    }
}

/**
 *
 * 15:18:10.376 [       t1] 거래 시작 : BankAccountV6
 * 15:18:10.376 [       t2] 거래 시작 : BankAccountV6
 * 15:18:10.382 [       t1] [검증 시작] 현재 잔액 : 1000, 출금액 : 800
 * 15:18:10.382 [       t1] [검증 완료] 현재 잔액 : 1000, 출금액 : 800
 * 15:18:10.868 [     main] t1 state : TIMED_WAITING
 * 15:18:10.868 [     main] t2 state : TIMED_WAITING        -  여기에서 t2 는 TIMED_WAITING 상태가 되며, 내부에서는 LockSupport.parkNanos(시간) 이 호출 된다.
 * 15:18:10.884 [       t2] [진입 실패] 이미 처리중인 작업이 있습니다.
 * 15:18:11.396 [       t1] [출금 완료] 현재 잔액 : 200
 * 15:18:11.397 [       t1] 거래 종료
 * 15:18:11.397 [     main] 최종 잔액 : 200
 */
