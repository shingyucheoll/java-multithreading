package thread.sync;

import static util.MyLogger.log;

public class BankAccountV3 implements BankAccount {

    volatile private int balance;

    public BankAccountV3(int initialBalance) {
        this.balance = initialBalance;
    }

    @Override
    public boolean withdraw(int amount) {
        log("거래 시작 : " + getClass().getSimpleName());

        synchronized (this) {
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
        }

        log("[출금 완료] 현재 잔액 : " + balance);
        log("거래 종료");

        return true;
    }

    @Override
    public synchronized int getBalance() {

        return balance;
    }
}
