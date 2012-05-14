


package edu.columbia.psl.invivoexpreval.util.iterator;

import java.util.*;

/**
 * A {@link java.util.ListIterator} that reverses the direction of all operations
 * of a delegate {@link java.util.ListIterator}.
 */
public class ReverseListIterator extends FilterListIterator {
    /** */
    public ReverseListIterator(ListIterator delegate) {
        super(delegate);
    }

    /** Calls {@link #delegate}.{@link java.util.ListIterator#hasPrevious()} */
    public boolean hasNext()       { return super.hasPrevious(); }
    /** Calls {@link #delegate}.{@link java.util.ListIterator#hasNext()} */
    public boolean hasPrevious()   { return super.hasNext(); }
    /** Calls {@link #delegate}.{@link java.util.ListIterator#previous()} */
    public Object  next()          { return super.previous(); }
    /** Calls {@link #delegate}.{@link java.util.ListIterator#next()} */
    public Object  previous()      { return super.next(); }
    /** Throws an {@link UnsupportedOperationException}. */
    public int     nextIndex()     { throw new UnsupportedOperationException(); }
    /** Throws an {@link UnsupportedOperationException}. */
    public int     previousIndex() { throw new UnsupportedOperationException(); }
}
