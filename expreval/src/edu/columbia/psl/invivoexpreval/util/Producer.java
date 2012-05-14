


package edu.columbia.psl.invivoexpreval.util;

/**
 * An object that produces some {@link java.lang.Object} each time the
 * {@link #produce()} method is invoked. This behavior is similar to the
 * {@link java.util.Iterator}, but is represented by one single
 * {@link #produce()} method as opposed to {@link java.util.Iterator}'s
 * two methods {@link java.util.Iterator#hasNext()} and
 * {@link java.util.Iterator#next()}. This simplifies the implementation of
 * certain complex iterations.
 *
 * @see edu.columbia.psl.invivoexpreval.util.iterator.DirectoryIterator
 * @see edu.columbia.psl.invivoexpreval.util.iterator.ProducerIterator
 */
public interface Producer {

    /**
     * Produce the next object.
     *
     * @return the next object or <code>null</code> to indicate that no more objects can be produced
     */
    Object produce();
}
