package storage;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by george on 06/15/17.
 */
public class ConcurrentMostRecentlyInsertedQueue<E> extends AbstractQueue<E> {


    private final int capacity;
    private E[] queue;
    private int index;
    private int pollCounter; //especially for concurrent testing

    public ConcurrentMostRecentlyInsertedQueue(int capacity) {
        this.capacity = capacity;
        this.queue = (E[]) new Object[capacity];
        this.index =  0;
        this.pollCounter = 0;
    }

    public int getPollCounter() {
        return pollCounter;
    }

    public void setPollCounter(int pollCounter) {
        this.pollCounter = pollCounter;
    }

    @Override
    public synchronized int size() {
        return (int) Arrays.stream(this.queue).filter(el->el!=null).count();
    }

    @Override
    public synchronized Iterator<E> iterator() {
        return new ConcurrentMostRecentlyInsertedQueue.queueIterator();
    }

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

        // is not not implemented because is similar to method poll
        @Override
        public void remove(){

        }
    }

    @Override
    public synchronized boolean offer(E e) {

        if (size() < capacity) {
            queue[this.size()] = e;
        } else {
            this.leftShift();
            queue[capacity-1] =e;
        }
        return true;
    }

    @Override
    public synchronized E poll() {
        E el = queue[0];
        this.leftShift();
        if(el!=null) this.pollCounter++;
        return el;
    }

    @Override
    public synchronized E peek() {
        return (size() == 0) ? null : (E) queue[0];
    }

    @Override
    public synchronized void clear() {
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
