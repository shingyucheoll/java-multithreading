package thread.control.printer;

import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import static util.MyLogger.log;

/**
 * Yield 를 사용하여 작업이 없을 때 다른 Thread 에게 실행 권한을 넘겨준다.
 * while(!Thread.interrupted()) { } 해당 while 문은 인터럽터의 상태를 계속 확인하여 자원을 소모하게 되는데,
 * 위와 같은 상태를 방지할 수 있다.
 */
public class MyPrinterV4 {

    public static void main(String[] args) {

        Printer printer = new Printer();
        Thread printerThread = new Thread(printer, "printer-1");
        printerThread.start();

        Scanner userInput = new Scanner(System.in);

        while (true) {
            log("프린터할 문서를 선택하세요. 종료 (q) : ");
            String input = userInput.nextLine();
            if (input.equals("q")) {
                log(" 작업을 종료합니다.");
                printerThread.interrupt();
                break;
            }
            printer.addJob(input);
        }
    }

    static class Printer implements Runnable {

        Queue<String> jobQueue = new ConcurrentLinkedQueue<>();

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                log("스레드 상태 체크");

                if (jobQueue.isEmpty()) {
                    Thread.yield();
                    continue;
                }

                try {
                    String job = jobQueue.poll();
                    log("출력 시작 : " + job + "남은 작업" + jobQueue);
                    // 작업중
                    Thread.sleep(2000);
                    log("출력 완료");
                } catch (InterruptedException e) {
                    log("isInterrupted !");
                    log("작업 강제종료");

                    try {
                        log("프로세스 종료 시작");
                        Thread.sleep(5000);
                        log("프로세스 종료 완료 ");
                    } catch (InterruptedException _) {

                    }
                    break;


                }
            }
        }

        public void addJob(String input) {
            // 큐에 작업 넣기
            jobQueue.offer(input);
        }


    }

}
