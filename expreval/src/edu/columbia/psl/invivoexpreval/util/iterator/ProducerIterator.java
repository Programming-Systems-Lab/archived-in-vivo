


package edu.columbia.psl.invivoexpreval.util.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.columbia.psl.invivoexpreval.util.Producer;

/**
 * An {@link java.util.Iterator} that iterates over all the objects produced by a delegate
 * {@link edu.columbia.psl.invivoexpreval.util.Producer}.
 *
 * @see edu.columbia.psl.invivoexpreval.util.Producer
 */
public class ProducerIterator implements Iterator {
    private final Producer producer;

    private static final Object UNKNOWN = new Object();
    private static final Object AT_END = null;
    private Object              nextElement = UNKNOWN;

    public ProducerIterator(Producer producer) {
        this.producer = producer;
    }

    public boolean hasNext() {
        if (this.nextElement == UNKNOWN) this.nextElement = this.producer.produce();
        return this.nextElement != AT_END;
    }

    public Object next() {
        if (this.nextElement == UNKNOWN) this.nextElement = this.producer.produce();
        if (this.nextElement == AT_END) throw new NoSuchElementException();
        Object result = this.nextElement;
        this.nextElement = UNKNOWN;
        return result;
    }

    public void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
