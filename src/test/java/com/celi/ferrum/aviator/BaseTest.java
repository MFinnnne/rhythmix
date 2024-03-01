package com.celi.ferrum.aviator;

import com.celi.ferrum.lib.Register;
import com.googlecode.aviator.AviatorEvaluator;
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
