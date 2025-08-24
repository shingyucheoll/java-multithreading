package thread.cas.increment;

public class SyncInteger implements IncrementInteger {

    volatile private int value;

    @Override
    public synchronized void increment() {
        value++;
    }

    @Override
    public synchronized int get() {
        return value;
    }
}
