/*
 * @Author: MFine
 * @Date: 2024-10-22 19:22:29
 * @LastEditTime: 2025-06-02 00:01:31
 * @LastEditors: MFine
 * @Description: 
 */
package com.df.rhythmix.aviator;

import com.df.rhythmix.lib.Register;
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
    void test2() {
        int a = 5;
        if ((a<=7&&a>1)||a>10&&a!=5){
            System.out.println("ok");
        }
        System.out.println(Double.parseDouble("10"));
    }
}
