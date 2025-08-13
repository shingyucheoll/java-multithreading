package thread.sync;

import static util.MyLogger.*;

public class BankAccountV2 implements BankAccount {

    volatile private int balance;

    public BankAccountV2(int initialBalance) {
        this.balance = initialBalance;
    }

    @Override
    public synchronized boolean withdraw(int amount) {
        log("거래 시작 : " + getClass().getSimpleName());
        // 잔고가 출금액보다 적을 경우 false
        // 잔고가 출금액보다 많을 경우 true
        log("[검증 시작] 현재 잔액 : " + balance + ", 출금액 : " + amount);
        if (balance < amount) {
            log("[검증 실패] 현재 잔액 : " + balance + ", 출금액 : " + amount);
            return false;
        }

        log("[검증 완료] 현재 잔액 : " + balance + ", 출금액 : " + amount);
        try {
            // 출금에 걸리는 시간으로 가정한다.
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        balance -= amount;

        log("[출금 완료] 현재 잔액 : " + balance);
        log("거래 종료");

        return true;
    }

    @Override
    public synchronized int getBalance() {
        return balance;
    }
}
