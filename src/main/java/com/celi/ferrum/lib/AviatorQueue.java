package com.celi.ferrum.lib;

import com.celi.ferrum.exception.ComputeException;
import com.googlecode.aviator.annotation.Import;
import com.googlecode.aviator.annotation.ImportScope;

import java.util.LinkedList;
import java.util.List;

@Import(ns = "queue", scopes = {ImportScope.Static})
public class AviatorQueue {

    public static LinkedList<Object> create() {
        return new LinkedList<>();
    }

    public static Object poll(LinkedList<Object> queue) {
        return queue.poll();
    }

    public static void push(LinkedList<Object> queue, Object object) {
        queue.add(object);
    }

    public static void clear(LinkedList<Object> queue) {
        queue.clear();
    }

    public static Object first(LinkedList<Object> queue){
       return queue.getFirst();
    }

    public static Object last(LinkedList<Object> queue){
        return queue.getLast();
    }
    public static LinkedList<Object> sub(LinkedList<Object> queue, int startIndex, int endIndex) throws ComputeException {
        if (queue.size() >= endIndex) {
            List<Object> objects = queue.subList(startIndex, endIndex);
            return new LinkedList<>(objects);
        }
        return new LinkedList<>(queue);
    }
}
