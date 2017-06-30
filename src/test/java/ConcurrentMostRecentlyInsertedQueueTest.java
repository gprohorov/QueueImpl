import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by george on 6/16/17.
 */



public class ConcurrentMostRecentlyInsertedQueueTest {
    private ConcurrentMostRecentlyInsertedQueue<Integer> queue;

        /* EXPLANATION
    * The sense of the test is in the following.
    *  When two parallel threads poll the queue and offers are absent
    *  it is possible to poll the same element twice.
    *  To monitor this a counter of successfull pols was involved.
    *  (Success means that the poll value was not null)
    *  For the above reason in the ordinary case this counter can exceed
    *  the capacity of the queue.
    *  In the thread-safe case they are equal
*/

    @Test//[1]
    public void testPollInThreadSafeManner() throws Exception {

        int capacity = 10;
        queue = new ConcurrentMostRecentlyInsertedQueue<>(capacity);
        int nThreads = 2;

        for (int i = 0; i < capacity; i++) {
            queue.offer(i);
        }
        ExecutorService service = Executors.newFixedThreadPool(nThreads);
        CountDownLatch latch = new CountDownLatch(nThreads);

        while (queue.size() > 0) {
            service.execute(() -> {
                queue.poll();
                latch.countDown();
            });
        }
        latch.await();
        service.shutdown();

        Assert.assertEquals(queue.getPollConuter(), capacity);
    }

    /*
    * The sense of the test is in the following.
    *  5 threads fill the queue by int numbers.
    *  Each one contributes a set of 10 numbers following one-by-one.
    *  In an ordinary case all these numbers in the queue  must be mixed.
    *  In the thread-safe case  all these numbers in the queue
    *  must be arranged continiously one-by-one in ASC order head->tail .
    *  In this test the final queue must be [56,57,58,59,60]
*/

    @Test //[2]
    public void testOfferInThreadSafeMode() throws Exception {
        int capacity = 5;
        queue = new ConcurrentMostRecentlyInsertedQueue<>(capacity);
        int nThreads = 5;
        ExecutorService service = Executors.newFixedThreadPool(nThreads);
        CountDownLatch latch = new CountDownLatch(nThreads);
        AtomicInteger ai = new AtomicInteger();
        Integer itr = 10;

        for (int i = 0; i < nThreads; i++) {
            service.execute(() -> {
                for (int j = 0; j < itr; j++) {
                    queue.offer(ai.incrementAndGet());
                }
                latch.countDown();
            });
        }

        latch.await();
        service.shutdown();

        Integer[] expected = {itr * nThreads - 4, itr * nThreads - 3, itr * nThreads - 2, itr * nThreads - 1, itr * nThreads - 0,};
        Assert.assertArrayEquals(queue.toArray(), expected);
    }
           //  Complex test for offer, poll, iterate
    @Test //[3]
    public void fullTestWithoutThreads() throws Exception {
        queue = new ConcurrentMostRecentlyInsertedQueue<>(5);
        queue.offer(1);
        queue.offer(2);
        queue.offer(3);
        queue.offer(4);
        queue.offer(5);
        queue.offer(6);
        queue.offer(7);

        Integer[]  expectedArray =  {3,4,5,6,7};
        List<Integer> iterList = new ArrayList<>();

        while (queue.iterator().hasNext()){
            iterList.add(queue.iterator().next());
        }
        Assert.assertArrayEquals(expectedArray,iterList.toArray());

        queue.poll();
        queue.poll();
        Integer[]  expectedArray2 =  {5,6,7};
        List<Integer> iterList2 = new ArrayList<>();

        while (queue.iterator().hasNext()){
            iterList2.add(queue.iterator().next());
        }

        Assert.assertArrayEquals(expectedArray2,iterList2.toArray());

        queue.offer(8);
        Integer[]  expectedArray3 =  {5,6,7,8};
        List<Integer> iterList3 = new ArrayList<>();

        while (queue.iterator().hasNext()){
            iterList3.add(queue.iterator().next());
        }

        Assert.assertArrayEquals(expectedArray3,iterList3.toArray());

        queue.clear();
        Integer[]  expectedArray4 =  {};
        List<Integer> iterList4 = new ArrayList<>();

        while (queue.iterator().hasNext()){
            iterList4.add(queue.iterator().next());
        }

        Assert.assertArrayEquals(expectedArray4,iterList4.toArray());

    }


    // ----------    MULTI-THREAD TESTING

            // stupid test . Two threads are offering a queue. The queue must filled
           // by the elements of the longer queue
    @Test //[5]
    public void testOfferingByTwoThread() throws Exception {
        queue = new ConcurrentMostRecentlyInsertedQueue<>(5);

        Thread offeringQueue1 = new OfferingThread(1000, 500, 20);
        Thread offeringQueue2 = new OfferingThread(500, 100, 10);
        offeringQueue1.start();
        offeringQueue2.start();
        offeringQueue1.join();
        offeringQueue2.join();

        Integer[] expected = {15,16,17,18,19};
        Assert.assertArrayEquals(queue.toArray(), expected);
    }
      // the first thread is offering, the second one is iterating
     // The 2nd thread throws the correct exception, but the first thread procceds offering
    // for this reason the test fails
     //[6]
    @Test(expected = ConcurrentModificationException.class)
    public void whenItteratingAndOfferingThenException() throws Exception {
        queue = new ConcurrentMostRecentlyInsertedQueue<>(5);

        Thread offeringQueue2 = new OfferingThread(500, 100, 20);
        Thread iteratingThread = new IteratingThread(1000, 500);
        iteratingThread.start();
        offeringQueue2.start();
        offeringQueue2.join();
        iteratingThread.join();

    }

    //----------------  Assistant classes for threads

    //   OFFERING THREAD
    private class OfferingThread extends Thread {
        private int delayBeforeStart;
        private int period;
        private int itr;

        public OfferingThread(int delayBeforeStart, int period, int itr) {
            this.delayBeforeStart = delayBeforeStart;
            this.period = period;
            this.itr = itr;
        }

        public void run() {
            try {
                Thread.sleep(delayBeforeStart);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < itr; i++) {
                try {
                    Thread.sleep(period);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                queue.offer(i);
              System.out.println(i + " was offered" + "  size " + queue.size() + "  peek " + queue.peek());
            }
        }
    }

    //   ITERATING THREAD
    private class IteratingThread extends Thread {
        private int delayBeforeStart;
        private int period;

        public IteratingThread(int delayBeforeStart, int period) {
            this.delayBeforeStart = delayBeforeStart;
            this.period = period;

        }

        public void run() {
            try {
                Thread.sleep(delayBeforeStart);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (queue.iterator().hasNext()){
                try {
                    Thread.sleep(period);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

          System.out.print(" try to iterate next " );
          System.out.println(" iteration next " + queue.iterator().next());
            }
        }
    }



}

