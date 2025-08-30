package thread.executor.poolsize;

import thread.executor.RunnableTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static thread.executor.ExecutorUtils.printState;
import static util.MyLogger.log;

public class FixedThreadPoolMain {

    public static void main(String[] args) {

        /**
         * 장점
         * newFixedThreadPool 는 메모리 리소스가 어느정도 예측이 가능하다 : 고정된 스레드의 수
         * 큐 사이즈 제한이 없어서 작업을 많이 담아두어도 문제가 없다. -> 하지만 OOM 문제가 발생할 수 있으니 고정 Queue 크기를 사용하는게 더 좋아보임
         * 단점
         * 1. 사용자들이 서비스 응답이 점점 느려진다고 항의한다.
         * 2. 갑작스런 트래픽이 증가할 경우 고객이 응답을 받을 수 없는 문제가 생긴다
         * ( thread 가 2개만 돌아가고 있기 때문에 처리량이 트래픽을 따라가지 못해 queue 가 계속 쌓이고 있는 상태 )
         *
         */
        ExecutorService es = Executors.newFixedThreadPool(2);
        // 위와 같은 내용
//        ExecutorService es = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

        log("pool 생성");
        printState(es);

        for (int i = 1; i <= 6; i++) {
            String taskName = "task" + i;
            es.execute(new RunnableTask(taskName));
            printState(es);
        }

        es.shutdown();
        log(" == shutdown 완료 == ");
    }
}
