package io.github.mfinnnne.rhythmix.lexer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;

/**
 * An iterator that supports peeking at the next element without consuming it.
 * <p>
 * This class wraps a standard {@link Iterator} and adds functionality for looking ahead,
 * putting items back, and managing state for parsing algorithms that require lookahead capabilities.
 *
 * @param <T> the type of elements returned by this iterator.
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class PeekIterator<T> implements Iterator<T> {

    private static final int CACHE_SIZE = 10;

    private final Iterator<T> it;


    private final LinkedList<T> queueCache = new LinkedList<>();
    private final LinkedList<T> stackPutBacks = new LinkedList<>();

    private final LinkedList<LinkedList<T>> stackSaveBacks = new LinkedList<>();

    private T endToken = null;


    /**
     * Constructs a PeekIterator that wraps a {@link Stream}.
     *
     * @param stream the stream to iterate over
     */
    public PeekIterator(Stream<T> stream) {
        it = stream.iterator();
    }


    /**
     * Constructs a PeekIterator that wraps a {@link Stream} and has a designated end token.
     *
     * @param stream   the stream to iterate over
     * @param endToken the token to be returned when the stream is exhausted
     */
    public PeekIterator(Stream<T> stream, T endToken) {
        it = stream.iterator();
        this.endToken = endToken;
    }

    /**
     * Starts recording the sequence of subsequently consumed elements.
     * Use {@link #backRecord()} to rewind to this point, or {@link #destroyRecord()} to discard.
     */
    public void record() {
        this.stackSaveBacks.push(new LinkedList<>());
    }

    /**
     * Stops the current recording and restores the iterator to the state it was in
     * when {@link #record()} was called by pushing recorded elements back.
     * Assumes a prior call to {@link #record()}.
     */
    public void backRecord() {
        this.stackSaveBacks.pop().forEach(this.stackPutBacks::push);
    }

    /**
     * Stops the current recording without restoring the iterator's state.
     * Assumes a prior call to {@link #record()}.
     */
    public void destroyRecord() {
        this.stackSaveBacks.pop();
    }

    /**
     * Constructs a PeekIterator that wraps an {@link Iterator} and has a designated end token.
     *
     * @param it       the iterator to wrap
     * @param endToken the token to be returned when the iterator is exhausted
     */
    public PeekIterator(Iterator<T> it, T endToken) {
        this.it = it;
        this.endToken = endToken;
    }

    /**
     * Returns the next element without advancing the iterator.
     * If the underlying iterator is exhausted, returns the configured {@code endToken} (which may be {@code null}).
     *
     * @return the next element or {@code endToken} when no more elements are available
     */
    public T peek() {
        if (this.stackPutBacks.size() > 0) {
            return this.stackPutBacks.getFirst();
        }
        if (!it.hasNext()) {
            return endToken;
        }
        T next = this.next();

        this.putBack();
        return next;
    }

    // 缓存：A->B->C->D
    // 放回：D->C->B->A

    /**
     * Pushes the most recently consumed element back so it can be consumed again.
     * When recording, also removes that element from the current save buffer.
     */
    public void putBack() {
        if (!this.stackSaveBacks.isEmpty()) {
            this.stackSaveBacks.peek().pop();
        }
        this.stackPutBacks.push(this.queueCache.pollLast());
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext() {
        return this.endToken != null || !this.stackPutBacks.isEmpty() || it.hasNext();
    }

    /** {@inheritDoc} */
    @Override
    public T next() {
        T val = null;
        if (!this.stackPutBacks.isEmpty()) {
            val = this.stackPutBacks.pop();
        } else {
            if (!this.it.hasNext()) {
                T tmp = this.endToken;
                this.endToken = null;
                return tmp;
            }
            val = it.next();
        }
        if (!this.stackSaveBacks.isEmpty()) {
            this.stackSaveBacks.peek().push(val);
        }
        // 保存最近操作的10个
        while (queueCache.size() > CACHE_SIZE - 1) {
            queueCache.poll();
        }
        queueCache.add(val);

        return val;
    }
}
