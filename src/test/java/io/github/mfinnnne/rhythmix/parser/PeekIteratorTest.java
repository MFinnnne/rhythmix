package io.github.mfinnnne.rhythmix.parser;

import io.github.mfinnnne.rhythmix.lexer.PeekIterator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PeekIteratorTest {
    @Test
    void test_peek() {
        var source = "abcdefg";
        var it = new PeekIterator<>(source.chars().mapToObj(c -> (char) c));

        assertEquals('a', it.next());
        assertEquals('b', it.next());
        it.next();
        it.next();
        assertEquals('e', it.next());
        assertEquals('f', it.peek());
        assertEquals('f', it.peek());
        assertEquals('f', it.next());
        assertEquals('g', it.next());
    }

    @Test
    void testLookAhead() {
        var source = "abcdefg";
        var it = new PeekIterator<>(source.chars().mapToObj(c -> (char) c));
        assertEquals('a', it.next());
        assertEquals('b', it.next());
        it.putBack();
        it.putBack();
        assertEquals('a', it.next());
    }

    @Test
    void testEndToken() {
        var source = "abcdefg";
        var it = new PeekIterator<>(source.chars().mapToObj(c -> (char) c), (char) 0);
        var i = 0;
        while (it.hasNext()) {
            if (i == 7) {
                assertEquals((char) 0, it.next());
            } else {
                assertEquals(source.charAt(i++), it.next());
            }
        }
    }

    @Test
    void testSaveAndBack() {
        var source = "abcdefgh";
        var it = new PeekIterator<>(source.chars().mapToObj(c -> (char) c));

        assertEquals('a', it.next());
        it.record();
        assertEquals('b', it.next());
        it.next();
        it.next();
        it.backRecord();
        assertEquals('b', it.next());
        assertEquals('c', it.next());
        it.record();
        assertEquals('d', it.next());
        assertEquals('e', it.next());
        it.record();
        assertEquals('f', it.next());
        assertEquals('g', it.next());
        assertEquals('h', it.peek());
        it.backRecord();
        assertEquals('f', it.next());
        assertEquals('g', it.next());
        it.backRecord();
        assertEquals('d', it.next());
        assertEquals('e', it.next());
        assertEquals('f', it.next());
        assertEquals('g', it.next());
    }
}