package thread.start;

// 보조 스레드 메인
public class DaemonThreadMain {

    public static void main(String[] args) {
        System.out.println(Thread.currentThread().getName() + " : main() start");
        DaemonThread daemonThread = new DaemonThread();
        daemonThread.setDaemon(true);    // 데몬 스레드 여부
        daemonThread.start();
        System.out.println(Thread.currentThread().getName() + " : main() end");

        /*
          case 1:
          daemonThread.setDaemon(true);     // Daemon true 로 설정 시
          메인 스레드가 종료됐기 때문에 데몬 스레드는 즉시 종료된다.
          main : main() start
          main : main() end
          Thread-0 : run()
          종료 코드 0(으)로 완료된 프로세스

          case 2:
          daemonThread.setDaemon(false);     // Daemon false 로 설정 시 10초를 기다립니다.  ( 기본값이 기다리는걸로 되어있다 )
          main : main() start
          main : main() end
          Thread-0 : run()
          Thread-0 : run() end
          종료 코드 0(으)로 완료된 프로세스
         */
    }

    static class DaemonThread extends Thread {
        @Override
        public void run() {

            System.out.println(Thread.currentThread().getName() + " : run() start");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + " : run() end");
        }
    }
}
