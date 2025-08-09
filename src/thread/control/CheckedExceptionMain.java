package thread.control;

import static util.ThreadUtils.sleep;

public class CheckedExceptionMain {

    public static void main(String[] args) throws Exception {
        throw new Exception();
    }

    static class CheckedRunnable implements Runnable {
        /**
         * 부모 클래스가 예외를 던지지 않을 경우 체크 예외를 던질 수 없다. ( 부모 클래스 : Runnable )
         * 그리고, 자식 메서드는 부모 메서드가 던질 수 있는 체크 예외의 하위 타입만 던질 수 있다.
         * 따라서,
         * main() 은 체크 예외를 밖으로 던질 수 있으며
         * run() 메서드는 체크 예외를 밖으로 던질 수 없습니다.
         */
//        @Override
//        public void run() throws Exception {
//            throw new Exception();
//        }
        @Override
        public void run() {
            sleep(2000);
        }
    }
}
