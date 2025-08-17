package thread.bounded;

public interface BoundedQueue {

    // 버퍼에 데이터 저장 ( 생산자 스레드가 호출 데이터 생산 )
    void put(String data);

    // 버퍼에 보관된 값을 가져간다 ( 소비자가 스레드 호출 데이터 소비 )
    String take();
}
