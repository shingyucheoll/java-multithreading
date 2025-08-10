package thread.control.printer;

import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import static util.MyLogger.log;

public class MyPrinterV3 {

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

                if (jobQueue.isEmpty()) {
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
                        log("프로세스 종료 중");
                        Thread.sleep(5000);
                        log("프로세스 완료 중");
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
