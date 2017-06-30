import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by george on 6/14/17.
 */
public class MostRecentlyInsertedBlockingQueueTest {

    MostRecentlyInsertedBlockingQueue<Integer> queue
            = new MostRecentlyInsertedBlockingQueue<>(5);

            // [1] take
    @Test  // queue is empty, take is waiting  for an element as long as possible
          //  after 5 secs an outer thread injects an element into the queue
         //           - taking is triggered successfully
    public void whenQueueIsEmptyThenWaitForAnElement() throws Exception {

        long start = System.currentTimeMillis();

        // start an offering thread with a 5 sec delay
        new Thread(() -> {

            try {
                System.out.println("Waiting for a 5 secs, before taking.");
                TimeUnit.SECONDS.sleep(5L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            queue.offer(5);

        }).run();

        Integer expected =queue.take();

        long finish = System.currentTimeMillis();
        long deltaTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(finish - start);

        Assert.assertEquals(expected.intValue(),5);
        Assert.assertTrue( deltaTimeSeconds >= 5);
    }


            // [2] put
    @Test  // queue is full, put is waiting for a free space as long as possible,
          //
    public void whenQueueIsFullThenWaitForFreeSpace() throws Exception {


        queue.offer(0);
        queue.offer(0);
        queue.offer(0);
        queue.offer(1);
        queue.offer(2);
        queue.offer(3);
        long start = System.currentTimeMillis();

        new Thread(() -> {

            try {
                System.out.println("Waiting for a 5 secs, before poll.");
                TimeUnit.SECONDS.sleep(5L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            queue.poll();

        }).run();

        queue.put(4);

        Integer[] expected = {0,1,2,3,4};
        long finish = System.currentTimeMillis();
        long deltaTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(finish - start);

        Assert.assertArrayEquals(expected,queue.toArray());
        Assert.assertTrue( deltaTimeSeconds >= 5);
    }

           // [3] offer with parameter: timeout
    @Test  // offering timeout(5 secs) >  polling delay(1 sec)  =>  success after polling
    public void whenQueueIsFullThenOfferingWaitsForAWhile() throws Exception {

        queue.offer(0);
        queue.offer(0);
        queue.offer(1);
        queue.offer(2);
        queue.offer(3);

        long start = System.currentTimeMillis();

        new Thread(() -> {

            try {
                System.out.println("Waiting for a 1 sec, before poll.");
                TimeUnit.SECONDS.sleep(1L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            queue.poll();

        }).run();

        queue.offer(4, 5L, TimeUnit.SECONDS);


        Integer[] expected = {0,1,2,3,4};
        long finish = System.currentTimeMillis();
        long deltaTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(finish - start);

        Assert.assertArrayEquals(expected,queue.toArray());
        Assert.assertTrue( deltaTimeSeconds >= 1);
    }

             // [4] poll with parameter: timeout
            // Queue is empty.
    @Test  // polling timeout(5 secs) > offer (delay =1 sec)  =>  success
    public void whenQueueEmptyButTimeoutIsExpiredThenPoll() throws Exception {

        queue.clear();

        long startTime = System.currentTimeMillis();

        new Thread(() -> {

            try {
                System.out.println("Waiting for a 1 sec, before poll.");
                TimeUnit.SECONDS.sleep(1L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            queue.offer(1);
            queue.offer(2);

        }).run();

        queue.poll(5L, TimeUnit.SECONDS);
        Integer[] expected = {2};
        long finishTime = System.currentTimeMillis();
        long deltaTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(finishTime - startTime);
        System.out.println(deltaTimeSeconds);
        Assert.assertArrayEquals(expected,queue.toArray());
        Assert.assertTrue( deltaTimeSeconds >= 1);
    }


    //-------------------      DRAINTO      ----------

    //  one-thread
    @Test
    public void whenQueueIsFullThenDraintoEqualsThree() throws Exception {

        queue.offer(0);
        queue.offer(1);
        queue.offer(2);
        queue.offer(3);
        queue.offer(4);

        List<Integer> list = new ArrayList<>();
        Integer expected = queue.drainTo(list);
        Integer[] expectedArray = {0,1,2,3,4};
        Integer[] expectedQueue = {};


        Assert.assertEquals( expected.intValue(), 5);
        Assert.assertArrayEquals(expectedArray,  list.toArray());
        Assert.assertArrayEquals(expectedQueue,  queue.toArray());
    }

     // ----- The test is similar to the previous one bur the queue
    // ------  is not full
    @Test
    public void whenQueueIsNotFullThenDraintoEqualsTwo() throws Exception {

        queue.offer(0);
        queue.offer(1);

        List<Integer> list = new ArrayList<>();
        Integer expected = queue.drainTo(list);
        Integer[] expectedArray = {0,1};
        Integer[] expectedQueue = {};


        Assert.assertEquals( expected.intValue(), 2);
        Assert.assertArrayEquals(expectedArray,  list.toArray());
        Assert.assertArrayEquals(expectedQueue,  queue.toArray());
    }


         //-----    the first thread is draining while the 2nd one is offering
        // when the draining  had begun the size was 2
       //  while the queue was draining another thread tried to offer it
      //   but the queue was locked while draining => drainTo = 2
     //  I suspect that these two threads runs continiously :(
    //  I'll try another way if it is not enough.
    @Test
    public void whenOfferingAndDrainThenDrainingLock() throws Exception {

        final Integer[] expected = {0};
        List<Integer> list = new ArrayList<>();
            queue.offer(1);
            queue.offer(2);

        new Thread(() -> {

            try {
                TimeUnit.MILLISECONDS.sleep(10L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            expected[0] = queue.drainTo(list);

        }).run();


        Thread offeringQueue = new OfferingThread(50, 50, 20);
        offeringQueue.start();
        offeringQueue.join();


        //drainingThread.getDrn();


        Assert.assertEquals( expected[0].intValue(), 2);
    }




    //  --------------------------- assistant thread classes


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
                //   System.out.print(i + " try to offer" + "  peek " + queue.peek() +
                //          "  size " + queue.size());
                System.out.printf("Try to offer(" + i + ")" + " current size = " + queue.size());
                queue.offer(i);
                System.out.println(" - success ");
            }
        }
    }
        //   DRAINING THREAD
        private class DrainingThread extends Thread {
            private int delayBeforeStart;
            private List<Integer> lst;
            private int drn;



            public DrainingThread(int delayBeforeStart) {
                this.delayBeforeStart = delayBeforeStart;
                this.lst = new ArrayList<>();
                this.drn = 0;
            }

            public int getDrn() {
                return drn;
            }

            public void run() {
                try {
                    Thread.sleep(delayBeforeStart);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Drain start");
                drn = queue.drainTo(lst);
                System.out.println("Drain finish");
            }
        }


    }








