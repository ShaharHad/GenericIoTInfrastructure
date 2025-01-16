
package ThreadPool;

import WaitablePQ;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPool implements Executor {
    //private fileds
    private WaitablePQ<Task<?>> tasks = new WaitablePQ<>();
    private boolean isPoolShutdown = false;
    private boolean isPoolPaused = false;
    private final AtomicInteger numOfThreads = new AtomicInteger();
    private final Semaphore pauseSemaphore = new Semaphore(0);
    private final ArrayList<Future<?>> listOfFutureForShutdown = new ArrayList<>();

    //ctor
    // Default numberOfThreads depend on number of cores
    public ThreadPool() {
        this(calcNumOfThread());
    }

    private static int calcNumOfThread(){
        int numOfThread = 0;

        numOfThread = (int)(Runtime.getRuntime().availableProcessors() * 1.5);

        return numOfThread;
    }

    //receives the original number of threads
    public ThreadPool(int numberOfThreads) {
        if(0 >= numberOfThreads){
            throw new IllegalArgumentException("0 threads is illegal ");
        }
        numOfThreads.set(numberOfThreads);
        initiateThreads();
    }

    private void initiateThreads(){
        for(int i = 0; i < numOfThreads.get(); ++i){
            new Thread(this::runThread).start();
        }
    }

    private void runThread(){
        while(true){ //only the shutdown method closed all threads in thread pool
            try {
                Task<?> task = tasks.dequeue();
                task.executeTask();
                if(task.killThreadFlag){
                    return;
                }
            } catch (Exception ignoreException) {

            }
        }
    }

    private <T> Callable<T> convertRunnableToCallable(Runnable command, T value){
        return new Callable<T>() {
            @Override
            public T call() throws Exception {
                command.run();
                return value;
            }
        };
    }

    //add task methods
    @Override
    public void execute(Runnable runnable) {

        submit(runnable, Priority.MEDIUM);
    }

    public Future<Void> submit(Runnable command){
        if(null == command){
            throw new NullPointerException("command cannot be null");
        }

        return submit(convertRunnableToCallable(command, null), Priority.MEDIUM);
    }


    public Future<Void> submit(Runnable command, Priority p){
        if(null == command){
            throw new NullPointerException("command cannot be null");
        }

        return submit(convertRunnableToCallable(command, null), p);
    }
    public <T> Future<T> submit(Runnable command, Priority p, T value){
        if(null == command){
            throw new NullPointerException("command cannot be null");
        }

        return submit(convertRunnableToCallable(command, value), p);
    }

    public <T> Future<T> submit(Callable<T> command){
        if(null == command){
            throw new NullPointerException("command cannot be null");
        }

        return submit(command, Priority.MEDIUM);
    }

    public <T> Future<T> submit(Callable<T> command, Priority p){
        if(null == command){
            throw new NullPointerException("command cannot be null");
        }
        if(isPoolShutdown){
            throw new RejectedExecutionException("pool shutdown");
        }

        Task<T> newTask = new Task<>(command, p.getValue());
        tasks.enqueue(newTask);

        return newTask.future;
    }

    // if threads are removed, they should be the first threads that not running
    public void setNumOfThreads(int numOfThreads){
        if(isPoolShutdown){
            throw new RejectedExecutionException("pool shutdown");
        }

        if(0 >= numOfThreads){
            throw new IllegalArgumentException("number of threads cannot be 0 or less");
        }

        if(numOfThreads > this.numOfThreads.get()){
                int diff = numOfThreads - this.numOfThreads.get();
                for(int i = 0; i < diff; ++i){
                    new Thread(){
                        @Override
                        public void run() {
                           runThread();
                        }
                    }.start();
                }
        }
        else if(numOfThreads < this.numOfThreads.get()){
            int diff =  this.numOfThreads.get() - numOfThreads;
            for(int i = 0; i < diff; ++i){
                Task<Integer> taskToKillThread = new Task<>(() -> -1, Priority.HIGH.getValue() + 1);
                taskToKillThread.killThreadFlag = true;

                tasks.enqueue(taskToKillThread);
            }
        }

        this.numOfThreads.set(numOfThreads);
    }


    //operations
    public void pause(){
        if(isPoolShutdown){
            throw new RejectedExecutionException("pool shutdown");
        }

        if(!isPoolPaused){
            isPoolPaused = true;

            for(int i = 0; i < numOfThreads.get(); ++i){
                //new SleepingPill(),HIGHEST_PRIORITY
                Task<Void> taskToPauseThread = new Task<>(() -> {

                    pauseSemaphore.acquire();

                    return null;
                }, Priority.HIGH.getValue() + 1);

                tasks.enqueue(taskToPauseThread);
            }
        }


    }

    public void resume(){
        if(isPoolShutdown){
            throw new RejectedExecutionException("pool shutdown");
        }
        if(isPoolPaused){
            pauseSemaphore.release(numOfThreads.get());
            isPoolPaused = false;
        }
    }

    public void shutdown(){
        int numberOfThreads = numOfThreads.get();
        for(int i = 0; i < numberOfThreads; ++i){

            Task<Void> taskToStopThread = new Task<>(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    return null;
                }
            }, Priority.LOW.getValue() - 1);

            taskToStopThread.killThreadFlag = true;

            listOfFutureForShutdown.add(taskToStopThread.future);

            tasks.enqueue(taskToStopThread);

            isPoolShutdown = true;

        }
    }

    public void awaitTermination(){
        if(!isPoolShutdown){
            throw new RejectedExecutionException("pool shutdown");
        }

        for(Future<?> f: listOfFutureForShutdown){
            try {
                f.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public boolean awaitTermination(long timeout,TimeUnit unit){
        if(!isPoolShutdown){
            throw new RejectedExecutionException("pool shutdown");
        }
        long waitTime = unit.convert(timeout, unit);
        long startTime = System.currentTimeMillis();
        for(Future<?> f: listOfFutureForShutdown){
            try {
                f.get(timeout, unit);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
        long endTime = System.currentTimeMillis();

        return waitTime > (endTime - startTime);
    }


    private class Task<E> implements Comparable<Task<E>> {
        private final Callable<E> command;
        private final int priority;
        private final Future<E> future;
        private final Object monitorTask;
        private E result;
        private volatile boolean isTaskDone;
        private volatile boolean isTaskCancelled;
        private boolean killThreadFlag = false;


        public Task(Callable<E> command, int priority) {
            this.command= command;
            this.priority = priority;
            this.future = new TaskFuture();
            monitorTask = new Object();
            result = null;
            isTaskDone = false;
            isTaskCancelled = false;
        }

        private void executeTask(){
            try {
                if (isTaskCancelled) {
                    return;
                }

                synchronized (monitorTask) {
                    result = this.command.call();
                    isTaskDone = true;
                    monitorTask.notifyAll();
                }
            }
            catch(Exception e){
                throw new RuntimeException(e);
            }
        }

        @Override
        public int compareTo(Task<E> task){
            return task.priority - priority;
        }

        private class TaskFuture implements Future<E> {

            @Override
            public boolean cancel(boolean b) {
                if(isDone() || isCancelled()){
                    return false;
                }

                isTaskCancelled = tasks.remove(Task.this);

                isTaskDone = true;

                return isTaskCancelled;
            }

            @Override
            public boolean isCancelled() {
                return isTaskCancelled;
            }

            @Override
            public boolean isDone() {
                return isTaskDone;
            }

            @Override
            public E get() {

                synchronized (monitorTask){

                    while(!isDone() && !isCancelled()) {
                        try {
                            monitorTask.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }

                return result;
            }

            @Override
            public E get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException {
                long waitTime = System.currentTimeMillis() - timeUnit.toMillis(l);
                synchronized (monitorTask){
                    while(!isDone() && !isCancelled()) {
                        monitorTask.wait(waitTime);
                    }
                }
                return result;
            }
        }
    }
}


