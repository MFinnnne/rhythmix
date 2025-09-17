package com.df.rhythmix.translate.chain;

import com.df.rhythmix.config.ChainFunctionConfig;
import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.parser.ast.ASTNode;
import com.df.rhythmix.util.ParserUtils;
import lombok.Getter;

import java.util.*;

/**
 * <p>ChainExprSyntaxCheck class.</p>
 *
 * author MFine
 * version $Id: $Id
 */
public class ChainExprSyntaxCheck {


    /**
     * <p>check.</p>
     *
     * @param astNode a {@link com.df.rhythmix.parser.ast.ASTNode} object.
     * @throws com.df.rhythmix.exception.TranslatorException if any.
     */
    public static void check(ASTNode astNode) throws TranslatorException {
        List<ASTNode> nodes = ParserUtils.getAllCallStmtNode(astNode);

        // Get current configuration from singleton
        ChainFunctionConfig config = ChainFunctionConfig.getInstance();
        List<String> startFunc = config.getStartFunc();
        List<String> endFunc = config.getEndFunc();
        Map<String, List<String>> callTree = config.getCallTree();

        for (int i = 0; i < nodes.size(); i++) {
            if (i == 0) {
                // Check if first function is allowed to start a chain
                if (!startFunc.contains(nodes.get(0).getLabel())) {
                    throw new TranslatorException("{} cannot be the first operator", nodes.get(0).getLexeme(), nodes.get(0).getLabel());
                }
            }
            if (i == nodes.size() - 1) {
                // Check if last function is allowed to end a chain
                if (!endFunc.contains(nodes.get(nodes.size() - 1).getLabel())) {
                    throw new TranslatorException("{} cannot be the last operator", nodes.get(nodes.size() - 1).getLexeme(), nodes.get(nodes.size() - 1).getLabel());
                }
                break;
            }

            var curFunc = nodes.get(i).getLabel();
            var nextFunc = nodes.get(i + 1).getLabel();

            // Check if current function is defined in call tree
            if (callTree.containsKey(curFunc)) {
                // Check if transition from current to next function is allowed
                if (!callTree.get(curFunc).contains(nextFunc)) {
                    throw new TranslatorException("'{}' operator cannot be followed by '{}' operator", nodes.get(i).getLexeme(), curFunc, nextFunc);
                }
            } else {
                throw new TranslatorException("{} operator is not defined", nodes.get(i).getLexeme(), curFunc);
            }
        }
    }

}
