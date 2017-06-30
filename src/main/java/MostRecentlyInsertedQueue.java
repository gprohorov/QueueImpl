import java.util.*;

/**
 *  Created by Prokhorov George on 6/13/17.
 */
public class MostRecentlyInsertedQueue<E>  extends AbstractQueue<E> {

    private final int capacity;
    private E[] queue;
    private int index;



    public MostRecentlyInsertedQueue(int capacity) {
        this.capacity = capacity;
        this.queue = (E[]) new Object[capacity];
        this.index =  0;

    }


    @Override
    public int size() {
        return (int) Arrays.stream(this.queue).filter(el -> el != null).count();
    }

    @Override
    public Iterator<E> iterator() {return new queueIterator();}

    private final class queueIterator implements Iterator<E> {

        @Override
        public boolean hasNext() {
            boolean rtn = true;
            if (index == size() ) {
                rtn = false;
                index =0;
            }

            return rtn;
        }

        @Override
        public E next() {
            E el = queue[index];
            index++;
            return el;
        }


        @Override
        public void remove(){
            for (int i = index-1; i < capacity - 1; i++) {
                queue[i] = queue[i+1];
                queue[capacity - 1] = null;

            }
        }
    }


    @Override
    public boolean offer(E el) {
        if (el == null)
            throw new NullPointerException("Offered element mustn't be null ");

        if (size() < capacity) {
            queue[this.size()] = el;
        } else {
            this.leftShift();
            queue[capacity-1] =el;
        }
        return true;
    }

    @Override
    public E peek() {
        return (size() == 0) ? null : (E) queue[0];
    }

    @Override
    public E poll() {
        E el = this.peek();
        this.leftShift();
        return el;

    }

   @Override
    public void clear() {
       for (int i=0; i<capacity; i++) {
           queue[i] = null;
       }
    }

    private void leftShift() {
        for (int i=0; i<capacity-1; i++){
            queue[i]=queue[i+1];
        }
        queue[capacity-1] = null;
    }

    @Override
    public E[] toArray() {
        E[] buf = (E[]) new Object[size()];
        for (int i=0; i<size();i++){
            buf[i] = queue[i];
        }
        return buf;
    }
}
