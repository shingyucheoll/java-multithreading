package thread.cas.increment;

public class IncrementPerformanceMain {

    public static final long COUNT = 100_000_000;

    public static void main(String[] args) {
        // CPU 캐시를 적극 사용하기 때문에 상당히 빠르다.
        // 단일 스레드가 사용하는 경우 효율적
        test(new BasicInteger());

        // 스레드의 CPU 캐시 메모리를 사용하지 않고 메인 메모리를 사용
        // 안전한 임계 영역이 없기 때문에 멀티 스레드에서 사용할 수 없으며
        // 단일 스레드가 사용할 경우 캐시 메모리를 사용하지 않기 때문에 느리다.  ( 사용할 케이스가 아님 - IDE 자체적으로 경고표시 )
        test(new VolatileInteger());

        // 임계영역이 있기 때문에 결과값이 제대로 나오지만, Atomic 보다 성능이 느리다.
        test(new SyncInteger());

        // 자바가 제공하는 atomic 을 사용하며 멀티스레드 상황에서 안전하게 사용할 수 있으며
        // synchronized 를 사용하는 경우보다 1.5 ~ 2배 정도 빠르다.
        // 하지만 atomic 에서 제공하는 메서드는 락을 사용하지 않고 원자적 연산을 만들어낸다.
        test(new MyAtomicInteger());
    }

    private static void test(IncrementInteger incrementInteger) {

        long startMs = System.currentTimeMillis();

        for (long i = 0; i < COUNT; i++) {
            incrementInteger.increment();
        }

        long endMs = System.currentTimeMillis();

        System.out.println(incrementInteger.getClass().getSimpleName() + ": ms= " + (endMs - startMs));
    }

}
