package thread.sync;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static util.MyLogger.log;

public class BankAccountV4 implements BankAccount {

    private int balance;

    private final Lock lock = new ReentrantLock();


    public BankAccountV4(int initialBalance) {
        this.balance = initialBalance;
    }

    @Override
    public boolean withdraw(int amount) {
        log("거래 시작 : " + getClass().getSimpleName());

        lock.lock();  // ReentrantLock 사용하여 lock 걸기
        // ** lock 사용 후 반드시 try - finally ( unlock ) 사용하기

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
