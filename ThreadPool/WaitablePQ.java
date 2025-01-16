package il.co.ilrd.WaitablePQ;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class WaitablePQ<E> {

    private final PriorityQueue<E> pq;
    private final Semaphore semaphore = new Semaphore(0);


    public WaitablePQ(){
        this(null);

    }

    public WaitablePQ(Comparator<E> comparator){
        pq = new PriorityQueue<>(comparator);
    }

    public void enqueue(E e){
        synchronized (pq){
            pq.add(e);
        }

        try {
            semaphore.release();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public E dequeue() {
        E element = null;
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        synchronized (pq){
            element = pq.poll();
        }

        return element;
    }

    public E dequeue(long timeout, TimeUnit unit) {
        return dequeue();
    }

    public boolean remove(E element) {
        boolean res = false;
        synchronized (pq){
            res = pq.remove(element);
        }
        semaphore.release();
        return res;
    }

    public E peek(){
        E res = null;
        synchronized (pq){
            res = pq.peek();
        }
        return res;
    }

    public int size(){
        int res = 0;
        synchronized (pq){
            res = pq.size();
        }
        return res;
    }

    public boolean isEmpty(){
        boolean res = false;
        synchronized (pq){
            res = pq.isEmpty();
        }
        return res;
    }

}
