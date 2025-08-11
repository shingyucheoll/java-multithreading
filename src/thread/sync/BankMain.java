package thread.sync;

import static util.MyLogger.*;
import static util.ThreadUtils.*;

public class BankMain {

    public static void main(String[] args) throws InterruptedException {
        BankAccount account = new BankAccountV1(1000);

        Thread t1 = new Thread(new WithdrawTask(account, 800), "t1");
        Thread t2 = new Thread(new WithdrawTask(account, 800), "t2");

        t1.start();
        t1.join();

        sleep(500);
        log("t1 state : " + t1.getState());
        log("t2 state : " + t1.getState());

        t2.start();
        t2.join();

        log("최종 잔액 : " + account.getBalance());
    }

}
