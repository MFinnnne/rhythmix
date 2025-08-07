package com.df.rhythmix.translate.chain;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.parser.ast.ASTNode;
import com.df.rhythmix.util.ParserUtils;
import lombok.Getter;

import java.util.*;

public class ChainExprSyntaxCheck {

    @Getter
    private static final List<String> START_FUNC = new ArrayList<>();
    @Getter
    private static final List<String> END_FUNC = new ArrayList<>();


    @Getter
    private static final Map<String, List<String>> CALL_TREE = new HashMap<>();

    static {
        START_FUNC.add("filter");
        START_FUNC.add("collect");
        END_FUNC.add("meet");
        CALL_TREE.put("filter", List.of("take", "sum", "hitRate", "count", "avg", "stddev", "window", "limit"));
        CALL_TREE.put("collect", List.of("take", "sum", "hitRate", "count", "avg", "stddev", "window", "limit"));
        CALL_TREE.put("limit", List.of("take", "sum", "count", "avg", "stddev"));
        CALL_TREE.put("take", List.of("sum", "count", "avg", "stddev", "limit"));
        CALL_TREE.put("window", List.of("sum", "count", "avg", "stddev", "limit"));
        CALL_TREE.put("sum", List.of("meet"));
        CALL_TREE.put("count", List.of("meet"));
        CALL_TREE.put("avg", List.of("meet"));
        CALL_TREE.put("stddev", List.of("meet"));
        CALL_TREE.put("hitRate", List.of("meet"));
    }

    public static void check(ASTNode astNode) throws TranslatorException {
        List<ASTNode> nodes = ParserUtils.getAllCallStmtNode(astNode);
        for (int i = 0; i < nodes.size(); i++) {
            if (i == 0) {
                if (!START_FUNC.contains(nodes.get(0).getLabel())) {
                    throw new TranslatorException("{} cannot be the first operator", nodes.get(0).getLexeme(), nodes.get(0).getLabel());
                }
            }
            if (i == nodes.size() - 1) {
                if (!END_FUNC.contains(nodes.get(nodes.size() - 1).getLabel())) {
                    throw new TranslatorException("{} cannot be the last operator", nodes.get(nodes.size() - 1).getLexeme(), nodes.get(nodes.size() - 1).getLabel());
                }
                break;
            }
            var curFunc = nodes.get(i).getLabel();
            var nextFunc = nodes.get(i + 1).getLabel();
            if (CALL_TREE.containsKey(curFunc)) {
                if (!CALL_TREE.get(curFunc).contains(nextFunc)) {
                    throw new TranslatorException("'{}' operator cannot be followed by '{}' operator", nodes.get(i).getLexeme(), curFunc, nextFunc);
                }
            } else {
                throw new TranslatorException("{} operator is not defined", nodes.get(i).getLexeme(), curFunc);
            }
        }
    }

}
