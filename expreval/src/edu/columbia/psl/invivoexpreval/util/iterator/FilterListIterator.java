


package edu.columbia.psl.invivoexpreval.util.iterator;

import java.util.*;

/**
 * An {@link java.util.ListIterator} that retrieves its elements from a delegate
 * {@link java.util.ListIterator}. The default implementation simply passes
 * all method invocations to the delegate.
 */
public abstract class FilterListIterator implements ListIterator {
    /** */
    protected final ListIterator delegate;

    /** */
    public FilterListIterator(ListIterator delegate) {
        this.delegate = delegate;
    }

    /** Calls {@link #delegate}.{@link java.util.ListIterator#hasNext()} */
    public boolean hasNext()       { return this.delegate.hasNext(); }
    /** Calls {@link #delegate}.{@link java.util.ListIterator#next()} */
    public Object  next()          { return this.delegate.next(); }
    /** Calls {@link #delegate}.{@link java.util.ListIterator#hasPrevious()} */
    public boolean hasPrevious()   { return this.delegate.hasPrevious(); }
    /** Calls {@link #delegate}.{@link java.util.ListIterator#previous()} */
    public Object  previous()      { return this.delegate.previous(); }
    /** Calls {@link #delegate}.{@link java.util.ListIterator#nextIndex()} */
    public int     nextIndex()     { return this.delegate.nextIndex(); }
    /** Calls {@link #delegate}.{@link java.util.ListIterator#previousIndex()} */
    public int     previousIndex() { return this.delegate.previousIndex(); }
    /** Calls {@link #delegate}.{@link java.util.ListIterator#remove()} */
    public void    remove()        { this.delegate.remove(); }
    /** Calls {@link #delegate}.{@link java.util.ListIterator#set(java.lang.Object)} */
    public void    set(Object o)   { this.delegate.set(o); }
    /** Calls {@link #delegate}.{@link java.util.ListIterator#add(java.lang.Object)} */
    public void    add(Object o)   { this.delegate.add(o); }
}
