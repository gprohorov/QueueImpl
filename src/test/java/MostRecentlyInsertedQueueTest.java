import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by george on 13/15/17.
 */
public class MostRecentlyInsertedQueueTest {
    private  int  capacity=3;
    private MostRecentlyInsertedQueue<Integer> queue = new MostRecentlyInsertedQueue<>(capacity);

    //  -- the following 4 tests are the ones of size methods
    @Test
    public void whenQueueIsEmptyThenSizeIsZero() throws Exception {

        Integer  expected = 0 ;

        Assert.assertEquals(expected, (Integer) queue.size());
    }

   @Test
    public void whenTheFirstOfferThenSizeIsOne() throws Exception {

        queue.offer(1);

       Integer  expectedSize = 1 ;
       Integer[]  expectedArray ={1};

       Assert.assertEquals(expectedSize, (Integer) queue.size());
       Assert.assertArrayEquals(expectedArray,queue.toArray());

    }

   @Test
    public void whenOfferFiveThenSizeIsStillThree() throws Exception {

        queue.offer(1);
        queue.offer(2);
        queue.offer(3);
        queue.offer(4);
        queue.offer(5);

       Integer  expectedSize = 3 ;
       Integer[]  expectedArray ={3,4,5};

       Assert.assertEquals(expectedSize, (Integer) queue.size());
       Assert.assertArrayEquals(expectedArray,queue.toArray());
    }

    @Test
    public void whenPollTheFullQueueThenSizeIsTwo() throws Exception {

        queue.offer(1);
        queue.offer(2);
        queue.offer(3);
        queue.offer(4);
        queue.offer(5);
        Integer poll1 = queue.poll();

       Integer  expectedSize = 2 ;
       Integer[]  expectedArray ={4,5};


       Assert.assertEquals(expectedSize, (Integer) queue.size());
       Assert.assertEquals( poll1.intValue(), 3);
       Assert.assertArrayEquals(expectedArray,queue.toArray());
    }

    @Test
    public void whenQueueIsClearedThenPollIsNull() throws Exception {

        queue.offer(1);
        queue.offer(2);
        queue.offer(3);
        queue.offer(4);
        queue.offer(5);
        queue.clear();
        Integer poll1 = queue.poll();

       Integer  expectedSize = 0 ;
       Integer[]  expectedArray ={};


       Assert.assertEquals(expectedSize, (Integer) queue.size());
       Assert.assertEquals( poll1, null);
       Assert.assertArrayEquals(expectedArray,queue.toArray());
    }

    // ------------------- Tests for iterator
     @Test
    public void whenIterateFullQueueThenAllElementsAreSeen() throws Exception {

        queue.offer(1);
        queue.offer(2);
        queue.offer(3);
        queue.offer(4);
        queue.offer(5);

       Integer[]  expectedArray =  {3,4,5};
       List<Integer> iterList = new ArrayList<>();

       while (queue.iterator().hasNext()){
           iterList.add(queue.iterator().next());
       }

       Assert.assertArrayEquals(expectedArray,iterList.toArray());
    }

     @Test
    public void whenIterateOneElementQueueThenOneElementsIsSeen() throws Exception {

        queue.offer(1);

       Integer[]  expectedArray =  {1};
       List<Integer> iterList = new ArrayList<>();

       while (queue.iterator().hasNext()){
           iterList.add(queue.iterator().next());
       }

       Assert.assertArrayEquals(expectedArray,iterList.toArray());
    }

     @Test
    public void whenIterateEmptyQueueThenEmptyList() throws Exception {

       Integer[]  expectedArray =  {};
       List<Integer> iterList = new ArrayList<>();

       while (queue.iterator().hasNext()){
           iterList.add(queue.iterator().next());
       }

       Assert.assertArrayEquals(expectedArray,iterList.toArray());
    }

     @Test //iterator remove
    public void whenRemoveThenQueueShrinks() throws Exception {
         queue.offer(1);
         queue.offer(2);
         queue.offer(3);

       while (queue.iterator().hasNext()){
            if (queue.iterator().next().equals(2)){ queue.iterator().remove(); }
       }

         Integer[]  expectedArray =  {1,3};
         Integer  expected =  queue.size();



       Assert.assertArrayEquals(expectedArray,queue.toArray());
       Assert.assertEquals(expected.intValue(),2);

    }

 @Test
    public void whenTwoContiniousIterationsThenTwoDifferentLists() throws Exception {

     queue.offer(1);
     queue.offer(2);
     queue.offer(3);
     queue.offer(4);
     queue.offer(5);

     Integer[]  expectedArray =  {3,4,5};
     List<Integer> iterList = new ArrayList<>();

     while (queue.iterator().hasNext()){
         iterList.add(queue.iterator().next());
     }

     queue.poll();

     Integer[]  expectedArray2 =  {4,5};
     List<Integer> iterList2 = new ArrayList<>();

     while (queue.iterator().hasNext()){
         iterList2.add(queue.iterator().next());
     }


     Assert.assertArrayEquals(expectedArray,iterList.toArray());
     Assert.assertArrayEquals(expectedArray2,iterList2.toArray());
    }

    /*    ----------   MANDATORY TESTS SET ---------------------
        Queue<Integer> queue = new firstversion.MostRecentlyInsertedQueue<Integer>(3);
    // queue.size(): 0, contents (head -> tail): [ ]
    queue.offer(1); // queue.size(): 1, contents (head -> tail): [ 1 ]
    queue.offer(2); // queue.size(): 2, contents (head -> tail): [ 1, 2 ]
    queue.offer(3); // queue.size(): 3, contents (head -> tail): [ 1, 2, 3 ]
    queue.offer(4); // queue.size(): 3, contents (head -> tail): [ 2, 3, 4 ]
    queue.offer(5); // queue.size(): 3, contents (head -> tail): [ 3, 4, 5 ]
        int poll1 = queue.poll(); // queue.size(): 2, contents (head -> tail): [ 4, 5 ], poll1 = 3
        int poll2 = queue.poll(); // queue.size(): 1, contents (head -> tail): [ 5 ], poll2 = 4
    queue.clear(); // queue.size(): 0, contents (head -> tail): [ ]
    */
    @Test
    public void mandatoryTestsSet() throws Exception {

        MostRecentlyInsertedQueue queue = new MostRecentlyInsertedQueue(3);

        //[1] queue.size(): 0, contents (head -> tail): [ ]
        Integer[] expectedQueueToArray = {};
        Assert.assertEquals(queue.size(),0);
        Assert.assertArrayEquals(queue.toArray(), expectedQueueToArray);

        //[2] queue.offer(1); // queue.size(): 1, contents (head -> tail): [ 1 ]
        queue.offer(1);
        expectedQueueToArray = new Integer[]{1};
        Assert.assertEquals(queue.size(),1);
        Assert.assertArrayEquals(queue.toArray(), expectedQueueToArray);

        //[3] queue.offer(2); // queue.size(): 2, contents (head -> tail): [ 1,2 ]
        queue.offer(2);
        expectedQueueToArray = new Integer[]{1,2};
        Assert.assertEquals(queue.size(),2);
        Assert.assertArrayEquals(queue.toArray(), expectedQueueToArray);

        //[4] queue.offer(3); // queue.size(): 3, contents (head -> tail): [ 1,2,3 ]
        queue.offer(3);
        expectedQueueToArray = new Integer[]{1,2,3};
        Assert.assertEquals(queue.size(),3);
        Assert.assertArrayEquals(queue.toArray(), expectedQueueToArray);

        //[5] queue.offer(4); // queue.size(): 3, contents (head -> tail): [ 2, 3, 4 ]
        queue.offer(4);
        expectedQueueToArray = new Integer[]{2,3,4};
        Assert.assertEquals(queue.size(),3);
        Assert.assertArrayEquals(queue.toArray(), expectedQueueToArray);

        //[6] queue.offer(5); // queue.size(): 3, contents (head -> tail): [ 3, 4,5 ]
        queue.offer(5);
        expectedQueueToArray = new Integer[]{3,4,5};
        Assert.assertEquals(queue.size(),3);
        Assert.assertArrayEquals(queue.toArray(), expectedQueueToArray);

        //[7] qint poll1 = queue.poll();
        // queue.size(): 2, contents (head -> tail): [ 4, 5 ], poll1 = 3
        Integer poll1 = (Integer) queue.poll();
        expectedQueueToArray = new Integer[]{4,5};
        Assert.assertEquals(queue.size(),2);
        Assert.assertEquals(poll1.intValue(),3);
        Assert.assertArrayEquals(queue.toArray(), expectedQueueToArray);

        //[8] qint poll2 = queue.poll();
        // queue.size(): 1, contents (head -> tail): [  5 ], poll1 = 4
        int poll2 = ((Integer) queue.poll()).intValue();
        expectedQueueToArray = new Integer[]{5};
        Assert.assertEquals(queue.size(),1);
        Assert.assertEquals(poll2,4);
        Assert.assertArrayEquals(queue.toArray(), expectedQueueToArray);

        //[9] qint queue.clear();
        // queue.size(): 0, contents (head -> tail): []
        queue.clear();
        expectedQueueToArray = new Integer[]{};
        Assert.assertEquals(queue.size(),0);
        Assert.assertArrayEquals(queue.toArray(), expectedQueueToArray);
    }



}