package com.df.rhythmix.util;

import com.df.rhythmix.lexer.TokenType;
import com.df.rhythmix.parser.ast.ASTNode;
import com.df.rhythmix.parser.ast.ASTNodeTypes;
import com.df.rhythmix.parser.ast.Factor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * <p>ParserUtils class.</p>
 *
 * author MFine
 * version $Id: $Id
 */
public class ParserUtils {

    /**
     * <p>toPostfixExpression.</p>
     *
     * @param node a {@link com.df.rhythmix.parser.ast.ASTNode} object.
     * @return a {@link java.lang.String} object.
     */
    public static String toPostfixExpression(ASTNode node) {
        if (node instanceof Factor) {
            return node.getLexeme().getValue();
        }
        List<String> ptr = new ArrayList<>();
        for (ASTNode child : node.getChildren()) {
            String s = toPostfixExpression(child);
            ptr.add(s);
        }
        String s = node.getLabel();
        if (!s.isEmpty()) {
            ptr.add(s);
        }
        return String.join(" ", ptr);
    }

    /**
     * <p>toBFSString.</p>
     *
     * @param root a {@link com.df.rhythmix.parser.ast.ASTNode} object.
     * @param max a int.
     * @return a {@link java.lang.String} object.
     */
    public static String toBFSString(ASTNode root, int max) {
        var queue = new LinkedList<ASTNode>();
        var list = new ArrayList<String>();
        queue.add(root);
        int c = 0;
        while (queue.size() > 0 && c++ < max) {
            var node = queue.poll();
            list.add(node.getLabel());
            queue.addAll(node.getChildren());
        }
        return String.join(" ", list);
    }

    /**
     * <p>toBFSASTType.</p>
     *
     * @param root a {@link com.df.rhythmix.parser.ast.ASTNode} object.
     * @param max a int.
     * @return a {@link java.lang.String} object.
     */
    public static String toBFSASTType(ASTNode root, int max) {
        var queue = new LinkedList<ASTNode>();
        var list = new ArrayList<String>();
        queue.add(root);
        int c = 0;
        while (queue.size() > 0 && c++ < max) {
            var node = queue.poll();
            list.add(node.getLabel());
            queue.addAll(node.getChildren());
        }
        return String.join(" ", list);
    }

    /**
     * <p>toBFSASTType.</p>
     *
     * @param root a {@link com.df.rhythmix.parser.ast.ASTNode} object.
     * @return a {@link java.util.List} object.
     */
    public static List<ASTNodeTypes> toBFSASTType(ASTNode root) {
        var queue = new LinkedList<ASTNode>();
        var list = new ArrayList<ASTNodeTypes>();
        queue.add(root);
        int c = 0;
        while (queue.size() > 0) {
            var node = queue.poll();
            switch (node.getType()) {
                case RANGE_EXPR:
                case COMPARE_EXPR:
                    list.add(node.getType());
                    break;
                default:
                    list.add(node.getType());
                    queue.addAll(node.getChildren());
                    break;
            }
        }
        return list;
    }


    /**
     * <p>printTree.</p>
     *
     * @param node a {@link com.df.rhythmix.parser.ast.ASTNode} object.
     * @param depth a int.
     */
    public static void printTree(ASTNode node, int depth) {
        for (int i = 0; i < depth * 2; i++) {
            System.out.print(" ");
        }
        System.out.println(node.getLabel());

        List<ASTNode> children = node.getChildren();
        for (ASTNode child : children) {
            printTree(child, depth + 1);
        }
    }

    /**
     * <p>printLevelOrder.</p>
     *
     * @param root a {@link com.df.rhythmix.parser.ast.ASTNode} object.
     */
    public static void printLevelOrder(ASTNode root) {
        if (root == null) return;

        Queue<ASTNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            int levelNodes = queue.size();
            for (int i = 0; i < levelNodes; i++) {
                ASTNode node = queue.poll();
                assert node != null;
                System.out.print(node.getLabel() + " ");

                queue.addAll(node.getChildren());
            }
            System.out.println();
        }
    }


    /**
     * <p>getAllCallStmtLabel.</p>
     *
     * @param root a {@link com.df.rhythmix.parser.ast.ASTNode} object.
     * @return a {@link java.util.List} object.
     */
    public static List<String> getAllCallStmtLabel(ASTNode root) {
        var queue = new LinkedList<ASTNode>();
        var list = new ArrayList<String>();
        queue.add(root);
        int c = 0;
        while (queue.size() > 0) {
            var node = queue.poll();
            if (node.getType() == ASTNodeTypes.VARIABLE) {
                if (!node.getChildren().isEmpty() && node.getChildren(0).getType() == ASTNodeTypes.CALL_STMT) {
                    String label = node.getLabel();
                    list.add(label);
                }
            } else {
                queue.addAll(node.getChildren());
            }
        }
        return list;
    }

    /**
     * <p>getAllCallStmtNode.</p>
     *
     * @param root a {@link com.df.rhythmix.parser.ast.ASTNode} object.
     * @return a {@link java.util.List} object.
     */
    public static List<ASTNode> getAllCallStmtNode(ASTNode root) {
        var queue = new LinkedList<ASTNode>();
        var list = new ArrayList<ASTNode>();
        queue.add(root);
        while (!queue.isEmpty()) {
            var node = queue.poll();
            if (node.getType() == ASTNodeTypes.VARIABLE) {
                if (!node.getChildren().isEmpty() && node.getChildren(0).getType() == ASTNodeTypes.CALL_STMT) {
                    list.add(node);
                }
            } else {
                queue.addAll(node.getChildren());
            }
        }
        return list;
    }

    /**
     * <p>getAllVarLabel.</p>
     *
     * @param root a {@link com.df.rhythmix.parser.ast.ASTNode} object.
     * @return a {@link java.util.List} object.
     */
    public static List<String> getAllVarLabel(ASTNode root) {
        var queue = new LinkedList<ASTNode>();
        var list = new ArrayList<String>();
        queue.add(root);
        int c = 0;
        while (queue.size() > 0) {
            var node = queue.poll();
            if (node.getType() == ASTNodeTypes.VARIABLE) {
                list.add(node.getLabel());
            } else {
                queue.addAll(node.getChildren());
            }
        }
        return list;
    }

    /**
     * <p>getAllType.</p>
     *
     * @param root a {@link com.df.rhythmix.parser.ast.ASTNode} object.
     * @return a {@link java.util.List} object.
     */
    public static List<TokenType> getAllType(ASTNode root) {
        var queue = new LinkedList<ASTNode>();
        var list = new ArrayList<TokenType>();
        queue.add(root);
        int c = 0;
        while (queue.size() > 0) {
            var node = queue.poll();
            TokenType type = node.getLexeme().getType();
            list.add(type);
            if (!node.getChildren().isEmpty()) {
                queue.addAll(node.getChildren());
            }

        }
        return list;
    }


    /**
     * <p>getAllVar.</p>
     *
     * @param root a {@link com.df.rhythmix.parser.ast.ASTNode} object.
     * @return a {@link java.util.List} object.
     */
    public static List<String> getAllVar(ASTNode root) {
        var queue = new LinkedList<ASTNode>();
        var list = new ArrayList<String>();
        queue.add(root);
        while (queue.size() > 0) {
            var node = queue.poll();
            if (node.getType() == ASTNodeTypes.VARIABLE) {
                list.add(node.getLabel());
            }
            if (!node.getChildren().isEmpty()) {
                queue.addAll(node.getChildren());
            }
        }
        return list;
    }


    /**
     * <p>getNodeByLabel.</p>
     *
     * @param root a {@link com.df.rhythmix.parser.ast.ASTNode} object.
     * @param label a {@link java.lang.String} object.
     * @return a {@link com.df.rhythmix.parser.ast.ASTNode} object.
     */
    public static ASTNode getNodeByLabel(ASTNode root, String label) {
        var queue = new LinkedList<ASTNode>();
        queue.add(root);
        while (queue.size() > 0) {
            var node = queue.poll();
            if (label.equals(node.getLabel())) {
                return node;
            }else{
                queue.addAll(node.getChildren());
            }
        }
        return null;
    }

    /**
     * <p>getNodeParentByLabel.</p>
     *
     * @param root a {@link com.df.rhythmix.parser.ast.ASTNode} object.
     * @param label a {@link java.lang.String} object.
     * @return a {@link com.df.rhythmix.parser.ast.ASTNode} object.
     */
    public static ASTNode getNodeParentByLabel(ASTNode root, String label) {
        var queue = new LinkedList<ASTNode>();
        queue.add(root);
        while (queue.size() > 0) {
            var node = queue.poll();
            for (ASTNode child : node.getChildren()) {
                if (label.equals(child.getLabel())) {
                    return node;
                }else{
                    queue.addAll(node.getChildren());
                }
            }
        }
        return null;
    }

    /**
     * 检测当前语法数是否含有变量
     *
     * @param astNode 抽象语法树
     * @return 是否存在变量
     */
    public static Boolean hasVariable(ASTNode astNode) {
        if (astNode.getLexeme().isVariable()) {
            return true;
        }
        int left = astNode.getChildren().size();
        int right = astNode.getChildren().size();
        if (left == 0 && right == 0) {
            return false;
        }
        for (ASTNode child : astNode.getChildren()) {
            return hasVariable(child);
        }
        return false;
    }

}
