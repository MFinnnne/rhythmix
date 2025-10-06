package io.github.mfinnnne.rhythmix.translate.chain;

import io.github.mfinnnne.rhythmix.exception.LexicalException;
import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.exception.TranslatorException;
import io.github.mfinnnne.rhythmix.execute.RhythmixCompiler;
import io.github.mfinnnne.rhythmix.execute.RhythmixExecutor;
import io.github.mfinnnne.rhythmix.lexer.Lexer;
import io.github.mfinnnne.rhythmix.lexer.Token;
import io.github.mfinnnne.rhythmix.pebble.TemplateEngine;
import io.github.mfinnnne.rhythmix.translate.ChainExpr;
import io.github.mfinnnne.rhythmix.translate.EnvProxy;
import io.github.mfinnnne.rhythmix.translate.Translator;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;
import io.github.mfinnnne.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class WindowTest {

    @Test
    void translate1() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).window(2).sum().meet(>1)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);
        RhythmixEventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        rhythmixExecutor.execute(p1);
        boolean execute = rhythmixExecutor.execute(p2);
        Assertions.assertFalse(execute);
        boolean execute1 = rhythmixExecutor.execute(p3);
        Assertions.assertTrue(execute1);
    }

    @Test
    void translate2() {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).collect().window(0).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        Assertions.assertThrows(TranslatorException.class, () -> {
            String transCode = Translator.translate(code, env);
        });
    }

    @Test
    void translate3() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).window(100ms).sum().meet(>=7)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);
        RhythmixEventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis() + 51));
        RhythmixEventData p21 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis() + 67));
        RhythmixEventData p3 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis() + 101));
        RhythmixEventData p4 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis() + 150));
        RhythmixEventData p5 = Util.genEventData("1", "4", new Timestamp(System.currentTimeMillis() + 250));
//        SensorEvent p5 = Util.genPointData("1", "3", new Timestamp(System.currentTimeMillis() + 160));
        rhythmixExecutor.execute(p1); //0
        boolean execute = rhythmixExecutor.execute(p2); //1
        Assertions.assertFalse(execute);
        rhythmixExecutor.execute(p21);//1
        boolean execute1 = rhythmixExecutor.execute(p3);//2
        Assertions.assertFalse(execute1);
        boolean execute2 = rhythmixExecutor.execute(p4);//3
        Assertions.assertFalse(execute2);
        execute2 = rhythmixExecutor.execute(p5);
        Assertions.assertTrue(execute2);
    }

    @Test
    void translate4() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).collect().limit(5).window(0ms).sum().meet(>=6)";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        EnvProxy env = new EnvProxy();
        Assertions.assertThrows(TranslatorException.class, () -> {
            String transCode = ChainExpr.translate(tokens, env);
        });

    }

    @Test
    void translate5() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).window(1s).avg().meet(<=0.5)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);
        RhythmixEventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis() + 510));
        RhythmixEventData p3 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis() + 1010));
        RhythmixEventData p4 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis() + 1510));
        rhythmixExecutor.execute(p1);//0
        boolean execute = rhythmixExecutor.execute(p2);//1
        Assertions.assertFalse(execute);
        boolean execute1 = rhythmixExecutor.execute(p3);//2
        Assertions.assertTrue(execute1);
        boolean execute2 = rhythmixExecutor.execute(p4);//3
        Assertions.assertFalse(execute2);
    }

    @Test
    void testTimeWindow() throws TranslatorException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter().window(3ms).sum().meet(>0)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);
        RhythmixEventData p1 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis() + 1));
        RhythmixEventData p3 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis() + 2));
        RhythmixEventData p4 = Util.genEventData("1", "4", new Timestamp(System.currentTimeMillis() + 3));
        RhythmixEventData p5 = Util.genEventData("1", "5", new Timestamp(System.currentTimeMillis() + 4));
        RhythmixEventData p6 = Util.genEventData("1", "6", new Timestamp(System.currentTimeMillis() + 10));
        RhythmixEventData p7 = Util.genEventData("1", "7", new Timestamp(System.currentTimeMillis() + 11));
        RhythmixEventData p8 = Util.genEventData("1", "8", new Timestamp(System.currentTimeMillis() + 12));
        RhythmixEventData p9 = Util.genEventData("1", "9", new Timestamp(System.currentTimeMillis() + 13));

        rhythmixExecutor.execute(p1);
        rhythmixExecutor.execute(p2);
        rhythmixExecutor.execute(p3);
        List<String> chainQueueData = getChainProcessedQueueData(rhythmixExecutor);
        Assertions.assertEquals(0, chainQueueData.size());
        boolean execute = rhythmixExecutor.execute(p4);
        chainQueueData = getChainProcessedQueueData(rhythmixExecutor);
        Assertions.assertArrayEquals(new String[]{"1","2","3","4"}, chainQueueData.toArray());
        Assertions.assertTrue(execute);
        boolean execute1 = rhythmixExecutor.execute(p5);
        chainQueueData = getChainProcessedQueueData(rhythmixExecutor);
        Assertions.assertArrayEquals(new String[]{"2","3","4","5"}, chainQueueData.toArray());
        Assertions.assertTrue(execute1);
        rhythmixExecutor.execute(p6);
        chainQueueData = getChainProcessedQueueData(rhythmixExecutor);
        Assertions.assertArrayEquals(new String[]{"3","4","5"}, chainQueueData.toArray());
        rhythmixExecutor.execute(p7);
        chainQueueData = getChainProcessedQueueData(rhythmixExecutor);
        Assertions.assertArrayEquals(new String[]{"4","5"}, chainQueueData.toArray());
        rhythmixExecutor.execute(p8);
        chainQueueData = getChainProcessedQueueData(rhythmixExecutor);
        Assertions.assertArrayEquals(new String[]{"5"}, chainQueueData.toArray());
        rhythmixExecutor.execute(p8);
        chainQueueData = getChainProcessedQueueData(rhythmixExecutor);
        Assertions.assertEquals(0,chainQueueData.size());
        rhythmixExecutor.execute(p9);
        chainQueueData = getChainProcessedQueueData(rhythmixExecutor);
        Assertions.assertArrayEquals(new String[]{"6","7","8","8","9"}, chainQueueData.toArray());
    }


    private List<String> getChainProcessedQueueData(RhythmixExecutor executor) {
        final EnvProxy envProxy = executor.getEnvProxy();
        List<RhythmixEventData> data = new ArrayList<>();
        envProxy.getEnv().forEach((k, v) -> {
           if (k.contains("processedChainQueue")) {
               data.addAll(((List<RhythmixEventData>) v));
           }
        });
        return  data.stream().map(RhythmixEventData::getValue).collect(Collectors.toList());
    }
}