package thread.start;

public class BadThreadMain {

    public static void main(String[] args) {

        // Main Thread 실행
        System.out.println(Thread.currentThread().getName() + ": main() start");

        HelloThread helloThread = new HelloThread();

        System.out.println(Thread.currentThread().getName() + ": start() 호출 전");

        // run 직접 실행시 main 스레드가 직접 실행하여 별도 스레드 생성이 되지 않는다.
        helloThread.run();

        System.out.println(Thread.currentThread().getName() + ": start() 호출 후");

        // Main Thread 종료
        System.out.println(Thread.currentThread().getName() + ": main() end");
    }
}
