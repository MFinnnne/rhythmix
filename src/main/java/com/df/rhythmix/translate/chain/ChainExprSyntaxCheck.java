package com.df.rhythmix.translate.chain;

import com.df.rhythmix.exception.TranslatorException;

import java.util.*;

public class ChainExprSyntaxCheck {

    private static final List<String> START_FUNC = new ArrayList<>();
    private static final List<String> END_FUNC = new ArrayList<>();


    private static final Map<String, List<String>> CALL_TREE = new HashMap<>();

    static {
        //第一个函数只能为filter 或者 collect
        START_FUNC.add("filter");
        START_FUNC.add("collect");
        //最后一个函数只能是meet
        END_FUNC.add("meet");

        CALL_TREE.put("filter", List.of("collect"));
        CALL_TREE.put("collect", List.of("limit", "take", "sum", "hitRate", "count", "avg", "stddev","window"));
        CALL_TREE.put("limit", List.of("take", "sum", "count", "avg", "stddev","window"));
        CALL_TREE.put("take", List.of("sum", "count", "avg", "stddev"));
        CALL_TREE.put("window", List.of("sum", "count", "avg", "stddev"));
        CALL_TREE.put("sum", List.of("meet"));
        CALL_TREE.put("count", List.of("meet"));
        CALL_TREE.put("avg", List.of("meet"));
        CALL_TREE.put("stddev", List.of("meet"));
        CALL_TREE.put("hitRate", List.of("meet"));
    }

    public static void check(List<String> chainCall) throws TranslatorException {
//        List<String> chainCall = chainCall.stream().map(label -> label.split("_")[1]).collect(Collectors.toList());
        for (int i = 0; i < chainCall.size(); i++) {
            if (i == 0) {
                if (!START_FUNC.contains(chainCall.get(0))) {
                    throw new TranslatorException("{}不能为调用第一个算子", chainCall.get(0));
                }
            }
            if (i == chainCall.size() - 1) {
                if (!END_FUNC.contains(chainCall.get(chainCall.size() - 1))) {
                    throw new TranslatorException("{}不能为调用的最后一个算子", chainCall.get(chainCall.size() - 1));
                }
                break;
            }
            var curFunc = chainCall.get(i);
            var nextFunc = chainCall.get(i + 1);
            if (CALL_TREE.containsKey(curFunc)) {
                if (!CALL_TREE.get(curFunc).contains(nextFunc)) {
                    throw new TranslatorException("'{}' 算子后不能使用 '{}' 算子", curFunc, nextFunc);
                }
            } else {
                throw new TranslatorException("{} 算子未定义", curFunc);
            }

        }
    }

}
