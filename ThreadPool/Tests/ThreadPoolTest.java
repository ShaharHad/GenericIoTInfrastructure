package ThreadPool.Tests;

import ThreadPool.Priority;
import ThreadPool.ThreadPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThreadPoolTest {

    ThreadPool pool= new ThreadPool();

    @BeforeEach
    void setUp() {
//        pool = new ThreadPool();
    }

    @AfterEach
    void tearDown() {

    }


    @Test
    void execute() {
    }

    @Test
    void testSubmitRunnable() {
        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                System.out.println("Runnable1 Task1 assign to Thread id: " + Thread.currentThread());
            }
        };

        Runnable r2 = new Runnable() {
            @Override
            public void run() {
                System.out.println("Runnable1 Task2 assign to Thread id: " + Thread.currentThread());
            }
        };

        Runnable r3 = new Runnable() {
            @Override
            public void run() {
                System.out.println("Runnable1 Task3 assign to Thread id: " + Thread.currentThread());
            }
        };

        pool.submit(r1);
        pool.submit(r2);
        pool.submit(r3);

    }

    @Test
    void testSubmitRunnableAndPriority() {
        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                System.out.println("Runnable2 Task1 assign to Thread id: " + Thread.currentThread());
            }
        };

        Runnable r2 = new Runnable() {
            @Override
            public void run() {
                System.out.println("Runnable2 Task2 assign to Thread id: " + Thread.currentThread());
            }
        };

        Runnable r3 = new Runnable() {
            @Override
            public void run() {
                System.out.println("Runnable2 Task3 assign to Thread id: " + Thread.currentThread());
            }
        };

        pool.submit(r1, Priority.LOW);
        pool.submit(r2, Priority.MEDIUM);
        pool.submit(r3, Priority.HIGH);
    }

    @Test
    void testSubmitRunnableAndPriorityAndReturnValue() throws ExecutionException, InterruptedException {
        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                System.out.println("Runnable3 Task1 assign to Thread id: " + Thread.currentThread());
            }
        };

        Runnable r2 = new Runnable() {
            @Override
            public void run() {
                System.out.println("Runnable3 Task2 assign to Thread id: " + Thread.currentThread());
            }
        };

        Runnable r3 = new Runnable() {
            @Override
            public void run() {
                System.out.println("Runnable3 Task3 assign to Thread id: " + Thread.currentThread());
            }
        };

        Future<Integer> res1 = pool.submit(r1, Priority.LOW, 0);
        Future<Integer> res2 = pool.submit(r2, Priority.MEDIUM, 1);
        Future<Integer> res3 = pool.submit(r3, Priority.HIGH, 2);

    }

    @Test
    void testCallable() {
        Callable<Integer> c1 = new Callable() {

            @Override
            public Object call() throws Exception {
                System.out.println("Callable1 Task1 assign to Thread id: " + Thread.currentThread());
                return 0;
            }
        };

        Callable<Integer> c2 = new Callable() {

            @Override
            public Object call() throws Exception {
                System.out.println("Callable1 Task2 assign to Thread id: " + Thread.currentThread());
                return 1;
            }
        };

        Callable<Integer> c3 = new Callable() {

            @Override
            public Object call() throws Exception {
                System.out.println("Callable1 Task3 assign to Thread id: " + Thread.currentThread());
                return 2;
            }
        };

        pool.submit(c1);
        pool.submit(c2);
        pool.submit(c3);

    }

    @Test
    void testCallableAndPriority() {

        Callable<Integer> c1 = new Callable() {

            @Override
            public Object call() throws Exception {
                System.out.println("Callable2 Task1 assign to Thread id: " + Thread.currentThread());
                return 0;
            }
        };

        Callable<Integer> c2 = new Callable() {

            @Override
            public Object call() throws Exception {
                System.out.println("Callable2 Task2 assign to Thread id: " + Thread.currentThread());
                return 1;
            }
        };

        Callable<Integer> c3 = new Callable() {

            @Override
            public Object call() throws Exception {
                System.out.println("Callable2 Task3 assign to Thread id: " + Thread.currentThread());
                return 2;
            }
        };

        pool.submit(c1);
        pool.submit(c2);
        pool.submit(c3);

    }

    @Test
    void setNumOfThreads() {
    }

    @Test
    void pause() {

        Callable<Integer> c1 = new Callable() {

            @Override
            public Object call() throws Exception {
                System.out.println("Callable2 Task1 assign to Thread id: " + Thread.currentThread());
                return 0;
            }
        };

        Callable<Integer> c2 = new Callable() {

            @Override
            public Object call() throws Exception {
                System.out.println("Callable2 Task2 assign to Thread id: " + Thread.currentThread());
                return 1;
            }
        };

        Callable<Integer> c3 = new Callable() {

            @Override
            public Object call() throws Exception {
                System.out.println("Thread is sleeping");
                Thread.sleep(2000);
                System.out.println("Callable2 Task3 assign to Thread id: " + Thread.currentThread());
                return 2;
            }
        };

        Future<Integer> f1 = pool.submit(c1);
        Future<Integer> f2 = pool.submit(c2);
        Future<Integer> f3 = pool.submit(c3);

        try {
            assertEquals(f1.get(), 0);
            assertEquals(f2.get(), 1);
            assertEquals(f3.get(), 2);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        pool.pause();


        f1 = pool.submit(c1);
        f2 = pool.submit(c2);
        f3 = pool.submit(c3);

        pool.resume();

        try {
            assertEquals(f1.get(), 0);
            assertEquals(f2.get(), 1);
            assertEquals(f3.get(), 2);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        System.out.println("System shutdown");

        pool.shutdown();

        pool.awaitTermination();

    }

    @Test
    void resume() {
    }

    @Test
    void shutdown() {
    }

    @Test
    void awaitTermination() {
    }

    @Test
    void testAwaitTermination() {
    }
}