package thread.control.printer;

import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class MyPrinterV1 {

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
                printer.work = false;
                break;
            }
            printer.addJob(input);
        }
    }

    static class Printer implements Runnable {

        /**
         * 여러 스레드가 동시에 접근하는 변수에 volatile ( 발러틸? )을 붙여줘야 한다
         * 일반 변수 = "개인 수첩에 메모해두고 계속 그거 보기"
         * volatile 변수 = "매번 중앙 게시판에서 확인하기"
         */
        volatile boolean work = true;

        // java.util 의 concurrent 사용하기
        // 여러 스레드가 동시에 접근하는 경우 컬렉션 프레임워크가 제공하는 일반적인 자료구조를 사용하면 안전하지 않기 때문에
        // 동시성을 지원하는 컬렉션을 사용해야 합니다.
        // 일반 큐로 이해
        Queue<String> jobQueue = new ConcurrentLinkedQueue<>();

        @Override
        public void run() {
            while (work) {
                if (jobQueue.isEmpty()) {
                    continue;
                }
                String job = jobQueue.poll();
                log("출력 시작 : " + job + ", 대기 문서 : " + jobQueue);
                sleep(3000);
                log("출력 완료");
            }
            log("프린터 종료");
        }

        public void addJob(String input) {
            // 큐에 작업 넣기
            jobQueue.offer(input);
        }


    }

}
