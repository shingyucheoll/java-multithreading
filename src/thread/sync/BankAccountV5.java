package thread.sync;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static util.MyLogger.log;

public class BankAccountV5 implements BankAccount {

    private int balance;

    private final Lock lock = new ReentrantLock();

    public BankAccountV5(int initialBalance) {
        this.balance = initialBalance;
    }

    @Override
    public boolean withdraw(int amount) {
        log("거래 시작 : " + getClass().getSimpleName());

        // lock 을 획득하면 true, 실패하면 false ( 이미 사용중인 경우 ) - 실패했을 경우 즉시 종료하는 if문
        if(!lock.tryLock()) {
            log("[진입 실패] 이미 처리중인 작업이 있습니다.");
            return false;  // 즉시 종료
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
 * 실행 결과 :
 * 15:13:52.016 [       t1] 거래 시작 : BankAccountV5
 * 15:13:52.016 [       t2] 거래 시작 : BankAccountV5
 * 15:13:52.018 [       t2] [진입 실패] 이미 처리중인 작업이 있습니다.
 * 15:13:52.020 [       t1] [검증 시작] 현재 잔액 : 1000, 출금액 : 800
 * 15:13:52.020 [       t1] [검증 완료] 현재 잔액 : 1000, 출금액 : 800
 * 15:13:52.502 [     main] t1 state : TIMED_WAITING
 * 15:13:52.502 [     main] t2 state : TERMINATED
 * 15:13:53.032 [       t1] [출금 완료] 현재 잔액 : 200
 * 15:13:53.032 [       t1] 거래 종료
 * 15:13:53.032 [     main] 최종 잔액 : 200
 */