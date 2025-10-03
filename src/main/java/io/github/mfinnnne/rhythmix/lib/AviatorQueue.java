/*
 * author: MFine
 * date: 2024-10-22 19:22:29
 * @LastEditTime: 2025-07-08 21:27:12
 * @LastEditors: MFine
 * @Description:
 */
package io.github.mfinnnne.rhythmix.lib;

import io.github.mfinnnne.rhythmix.exception.ComputeException;
import com.googlecode.aviator.annotation.Import;
import com.googlecode.aviator.annotation.ImportScope;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides queue manipulation functions for use within Aviator expressions.
 * <p>
 * This class is imported as a static namespace 'queue', allowing Rhythmix expressions
 * to perform queue operations like creating, pushing, polling, and clearing.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
@Import(ns = "queue", scopes = {ImportScope.Static})
public class AviatorQueue {

    /**
     * Creates a new, empty ArrayList to be used as a queue.
     *
     * @return a new {@link java.util.ArrayList} object
     */
    public static ArrayList<Object> create() {
        return new ArrayList<>();
    }

    /**
     * Removes and returns the first element from the queue.
     *
     * @param queue the queue to poll from
     * @return the first element of the queue
     */
    public static Object poll(ArrayList<Object> queue) {
        return queue.remove(0);
    }

    /**
     * Adds an object to the end of the queue.
     *
     * @param queue  the queue to add the object to
     * @param object the object to be added
     */
    public static void push(ArrayList<Object> queue, Object object) {
        queue.add(object);
    }

    /**
     * Removes all elements from the queue.
     *
     * @param queue the queue to be cleared
     */
    public static void clear(ArrayList<Object> queue) {
        queue.clear();
    }

    /**
     * Returns the first element of the queue without removing it.
     *
     * @param queue the queue to peek into
     * @return the first element of the queue
     */
    public static Object first(ArrayList<Object> queue) {
        return queue.get(0);
    }

    /**
     * Returns the last element of the queue without removing it.
     *
     * @param queue the queue to peek into
     * @return the last element of the queue
     */
    public static Object last(ArrayList<Object> queue) {
        return queue.get(queue.size() - 1);
    }

    /**
     * Returns a sub-list of the queue.
     * <p>
     * If the specified range is valid, a new ArrayList containing the elements
     * from {@code startIndex} (inclusive) to {@code endIndex} (exclusive) is returned.
     * If the range is invalid (e.g., endIndex is out of bounds), a copy of the original queue is returned.
     *
     * @param queue      the queue to extract the sub-list from
     * @param startIndex the starting index (inclusive)
     * @param endIndex   the ending index (exclusive)
     * @return a new {@link java.util.ArrayList} containing the specified portion of the queue
     * @throws ComputeException if any computation error occurs
     */
    public static ArrayList<Object> sub(ArrayList<Object> queue, int startIndex, int endIndex) throws ComputeException {
        if (queue.size() >= endIndex) {
            List<Object> objects = queue.subList(startIndex, endIndex);
            return new ArrayList<>(objects);
        }
        return new ArrayList<>(queue);
    }
}
