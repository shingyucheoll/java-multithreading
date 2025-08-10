package thread.control.printer;

import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import static util.MyLogger.log;

public class MyPrinterV2Iam {

    public static void main(String[] args) {

        Printer printer = new Printer();
        Thread printerTask1 = new Thread(printer, "printer-1");

        printerTask1.start();

        Scanner sc = new Scanner(System.in);


        while (true) {
            System.out.println("입력해");
            String input = sc.nextLine();

            if (input.equals("q")) {
                printerTask1.interrupt();
                break;
            }
            printer.addJob(input);
        }

    }

    static class Printer implements Runnable {

        Queue<String> queue = new ConcurrentLinkedQueue<>();

        @Override
        public void run() {

            log("Printer 스레드 실행 됨");

            while (!Thread.interrupted()) {

                if (queue.isEmpty()) {
                    continue;
                }

                String job = queue.poll();


                log("JOB 작업 시작 : " + job);
                log("남은 작업 List :" + queue);
                log("JOB 작업 완료 :  " + job);


            }
        }

        public void addJob(String input) {
            queue.offer(input);
        }
    }
}
