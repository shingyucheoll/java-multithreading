package thread.cas.spinlock;

import java.util.concurrent.atomic.AtomicBoolean;

import static util.MyLogger.log;

public class SpinLock {

    private final AtomicBoolean lock = new AtomicBoolean(false);

    public void lock() {
        log("락 획득 시도");
        // lock 이 false 일 경우 true 로 변경 ( 실패할 경우 while 계속 실행 )
        while (!lock.compareAndSet(false, true)) {
            log("락 획득 실패 - 스핀 대기");
        }
        // true ( false 상태에서 true 로 변경되어 !lock = false ) 로 while 문을 빠져나왔을 경우
        log("락 획득 완료");
    }

    public void unlock() {
        lock.set(false);
        log("락 반납 완료");
    }
}
