/*
 * @Author: MFine
 * @Date: 2024-10-22 19:22:29
 * @LastEditTime: 2025-07-08 21:27:12
 * @LastEditors: MFine
 * @Description:
 */
package com.df.rhythmix.lib;

import com.df.rhythmix.exception.ComputeException;
import com.googlecode.aviator.annotation.Import;
import com.googlecode.aviator.annotation.ImportScope;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Import(ns = "queue", scopes = {ImportScope.Static})
public class AviatorQueue {

    public static ArrayList<Object> create() {
        return new ArrayList<>();
    }

    public static Object poll(ArrayList<Object> queue) {
        return queue.remove(0);
    }

    public static void push(ArrayList<Object> queue, Object object) {
        queue.add(object);
    }

    public static void clear(ArrayList<Object> queue) {
        queue.clear();
    }

    public static Object first(ArrayList<Object> queue) {
        return queue.get(0);
    }

    public static Object last(ArrayList<Object> queue) {
        return queue.get(queue.size() - 1);
    }

    public static ArrayList<Object> sub(ArrayList<Object> queue, int startIndex, int endIndex) throws ComputeException {
        if (queue.size() >= endIndex) {
            List<Object> objects = queue.subList(startIndex, endIndex);
            return new ArrayList<>(objects);
        }
        return new ArrayList<>(queue);
    }
}
