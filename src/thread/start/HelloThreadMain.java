package thread.start;

public class HelloThreadMain {

    public static void main(String[] args) {

        // Main Thread 실행
        System.out.println(Thread.currentThread().getName() + ": main() start");

        HelloThread helloThread1 = new HelloThread();
        HelloThread helloThread2 = new HelloThread();

        System.out.println(Thread.currentThread().getName() + ": start() 호출 전");

        // run 이 아닌 start 메서드를 호출해야 별도의 스레드에서 run() 코드 실행
        helloThread1.start();
        helloThread2.start();

        // 새로 생성한 Thread 의 메세지보다 아래 메세지가 먼저 나오는 이유는 Thread 생성은 비동기로 처리됨 ( The thread will execute independently of the current thread : 현재 스레드와 독립적으로 실행된다 )
        // start 메서드의 synchronized 의미는 동일한 Thread 객체에 대해 여러 번 start() 호출을 방지
        System.out.println(Thread.currentThread().getName() + ": start() 호출 후");

        // Main Thread 종료
        System.out.println(Thread.currentThread().getName() + ": main() end");
    }
}
