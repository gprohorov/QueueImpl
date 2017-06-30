import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * Created by george on 6/15/17.
 */
public class MostRecentlyInsertedBlockingQueue<E> extends MostRecentlyInsertedQueue<E> implements BlockingQueue<E> {

    private int  capacity;

    public MostRecentlyInsertedBlockingQueue(int capacity) {
        super(capacity);
        this.capacity = capacity;
    }

    @Override
    public synchronized void put(E el) throws InterruptedException {

        while (this.size() == this.capacity) {
            wait();
        }
        if (this.size() == 0) {
            notifyAll();
        }
        this.offer(el);
    }

    @Override
    public synchronized E take() throws InterruptedException {
        while (this.size() == 0){
            wait();
        }
        if (this.size() == this.capacity) {
            notifyAll();
        }
        return this.poll();
    }


         //       There are 2 ways of the implementation
        // 1. Queue is full and offering failed, then renew an effort AFTER the timeout
       //  2. Queue is full and offering failed, then renew an effort DURING the timeout
      //   I have chosen the 1st one.
     //     When the time is expired an element must be offerd ANYWAY (???)
    //     instead of DO NOTHING  and return FALSE. IMHO
    @Override
    public synchronized boolean offer(E el, long timeout, TimeUnit unit) throws InterruptedException {

        if ((this.size() == this.capacity) ) {
            unit.sleep(timeout);
        }
        return this.offer(el);
    }


    @Override // the way is similar to the above one
    public synchronized E poll(long timeout, TimeUnit unit) throws InterruptedException {
        if (this.size() == 0) {
            unit.sleep(timeout);
        }
        return this.poll();
    }

    @Override // not actual
    public int remainingCapacity() {
        return 0;
    }


 /*      EXPLANATION
 * <drainTo> removes all available elements from this queue and
 * adds them to the given collection.  The behavior of this operation
 * is undefined if the specified collection is modified   while
 * the operation is in progress.
 * Thread-safety is absent for the above reasons.
 *  So, the implementation of the method has two ways
 *  1. Throw ConcurrentModificationException and revert.
 *  2. Lock the queue while draining.
 * I decided to implement the 2nd one.
 *  This means that a snap-shot is created
 *  and then from this snap-shot (dump) the queue drains to a collection.
 *  While the real queue (not snap-shot one) can be mofified by another thread.
 *  May be it is a desired solution. (????)
 * */

    @Override
    public synchronized int drainTo(Collection<? super E> c) {

        Lock lock = new ReentrantLock();
        lock.lock();
        System.out.println(" Locking while draining ... ");
        int counter =0;
        try{
            int itr = this.size();
            for (int i = 0; i <itr; i++) {
                c.add(poll());
                counter++;
                TimeUnit.MILLISECONDS.sleep(100L); // for testing only
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("drainTo result " +counter);
            System.out.println("Unlock");
            lock.unlock();
        }

        return counter;
    }



    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        return 0;
    }
}
