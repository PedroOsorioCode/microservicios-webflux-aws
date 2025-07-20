package co.com.microservice.aws;

import java.util.concurrent.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;

public class RequestComparison {

    public static void main(String[] args) throws InterruptedException {
        runBlockingExample();
        runReactiveExample();
    }

    static void runBlockingExample() throws InterruptedException {
        System.out.println("---- Starting blocking execution ----");

        int totalRequests = 100;
        int threadPoolSize = 10;
        ExecutorService pool = Executors.newFixedThreadPool(threadPoolSize);

        long start = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(totalRequests);

        for (int i = 0; i < totalRequests; i++) {
            pool.submit(() -> {
                try {
                    Thread.sleep(1000); // Simulates blocking I/O
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                latch.countDown();
            });
        }

        latch.await();
        long end = System.currentTimeMillis();
        System.out.println("Blocking total time: " + (end - start) + " ms");

        pool.shutdown();
        System.out.println("---- Finished blocking execution ----\n");
    }

    static void runReactiveExample() {
        System.out.println("---- Starting reactive execution ----");

        int totalRequests = 100;
        long start = System.currentTimeMillis();

        Flux.range(1, totalRequests)
                .flatMap(i -> Mono.delay(Duration.ofMillis(1000)))
                .doOnComplete(() -> {
                    long end = System.currentTimeMillis();
                    System.out.println("Reactive total time: " + (end - start) + " ms");
                    System.out.println("---- Finished reactive execution ----");
                })
                .blockLast(); // Wait for all reactive flows to complete
    }
}