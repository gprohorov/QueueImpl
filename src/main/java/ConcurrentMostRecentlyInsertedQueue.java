import java.util.AbstractQueue;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

/**
 * Created by george on 06/15/17.
 */
public class ConcurrentMostRecentlyInsertedQueue<E> extends AbstractQueue<E>  {

    private MostRecentlyInsertedQueue<E> queue;
    private final Object cmriq;  // ConcurrentMostRecentlyInsertedQueue
    private int pollCounter;  // for thread-safe testing
    private int index; // iteration pointer to element
    protected transient int changesCounter; // for iteration protection




    public ConcurrentMostRecentlyInsertedQueue(int capacity) {
        this.queue = new MostRecentlyInsertedQueue<>(capacity);
        this.cmriq = this;
        this.index=0;
        this.pollCounter = 0;
        this.changesCounter = 0;

    }

    public int getPollConuter() {
        return pollCounter;
    }

    public void setPollConuter(int pollConuter) {
        this.pollCounter = pollConuter;
    }

    @Override
    public synchronized int size() { return queue.size(); }

    @Override
    public synchronized Iterator<E> iterator() {
        return new SynchQueueIterator();
    }

    private class SynchQueueIterator implements Iterator<E> {
         //  ATTENTION
        //  I haven't implemented <remove> method
       //  because  (it is not mandatory) && (it was not asked) && (lack of time)
      //   though it is rather important method . My apologies.
        @Override
        public boolean hasNext() {
            changesCounter=0;
            boolean rtn = index < size();
            if (index == size()) {index=0;}  // if the tail of the queue then
            return rtn; }                    // index return back to the head

        @Override
        public E next() {
           synchronized (cmriq){
                checkChangesWhileIterating();
                if (!hasNext()) throw new IndexOutOfBoundsException("End of capacity.");
                E el = queue.toArray()[index];
                index++;
                return el;
           }
        }

        final void checkChangesWhileIterating() {
            if ( changesCounter!=0)
                throw new ConcurrentModificationException("Iteration failed because the queue was changed.");
        }

    }

    @Override
    public synchronized boolean offer(E e) {
        changesCounter++;
          return queue.offer(e);
    }

    @Override
    public synchronized E poll() {
        E el = queue.poll();
        if (el != null) {
            pollCounter++;
            changesCounter++;
        }
        return el;
    }

    @Override
    public synchronized E peek() {return queue.peek();
    }

    @Override
    public synchronized void clear() {
        changesCounter++;
        queue.clear();
    }

}
