/*
 * author: MFine
 * date: 2024-10-22 19:22:29
 * @LastEditTime: 2025-06-02 00:01:31
 * @LastEditors: MFine
 * @Description: 
 */
package io.github.mfinnnne.rhythmix.aviator;

import io.github.mfinnnne.rhythmix.lib.Register;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class BaseTest {

    @BeforeAll
    static void beforeAll() {
        Register.importFunction();
    }

    @Test
    void testQueueEnv() {
        HashMap<String, Object> env = new HashMap<>();
        Queue<String> queue = new LinkedList<>();

        env.put("queue",queue);

    }

    @Test
    void someFailingTest() {
        System.out.println(System.getProperty("java.class.path"));
        // ... rest of your test
    }
}
