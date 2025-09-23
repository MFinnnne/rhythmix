package com.df.rhythmix.parser.ast;

import com.df.rhythmix.lexer.Token;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * The abstract base class for all nodes in the Abstract Syntax Tree (AST).
 * <p>
 * Each ASTNode represents a construct in the source code, such as an expression,
 * a statement, or a variable. It holds references to its children, the associated
 * token (lexeme), and its type and label.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
@Data
public abstract class ASTNode {

	/**
	 * The list of all child nodes.
	 * <p>
	 * This list contains all direct children of this AST node in the parse tree.
	 * Children are ordered according to their appearance in the source code during
	 * parsing. The list is mutable and can be modified during AST construction
	 * and transformation phases.
	 * <p>
	 * For leaf nodes (nodes with no children), this list will be empty but never null.
	 * 
	 * @see ASTNode
	 */
    protected List<ASTNode> children = new ArrayList<>();

	/**
	 * The token (lexeme) associated with this node, typically the primary
	 * source token that originated the node during parsing.
	 */
    protected Token lexeme;

	/**
	 * A descriptive label for this node, used for debugging and pretty-printing.
	 */
    protected String label;

	/**
	 * The specific {@link ASTNodeTypes} classification of this node.
	 */
    protected ASTNodeTypes type;


    /**
     * Sets the label for this AST node. The label is a descriptive name for the node.
     *
     * @param label a {@link java.lang.String} object.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Constructs an ASTNode with a specified type and label.
     *
     * @param type  the {@link ASTNodeTypes} of this node.
     * @param label a descriptive string label for this node.
     */
    protected ASTNode(ASTNodeTypes type, String label) {
        this.type = type;
        this.label = label;
    }

    /**
     * Default constructor for an ASTNode.
     */
    protected ASTNode() {
    }


    /**
     * Sets the token (lexeme) associated with this AST node.
     *
     * @param lexeme the {@link com.df.rhythmix.lexer.Token} object.
     */
    public void setLexeme(Token lexeme) {
        this.lexeme = lexeme;
    }

    /**
     * Gets a child node at a specific index.
     *
     * @param index the index of the child to retrieve.
     * @return the {@link com.df.rhythmix.parser.ast.ASTNode} at the specified index.
     */
    public ASTNode getChildren(int index) {
        return children.get(index);
    }


    /**
     * Adds a child node to this node.
     *
     * @param node the {@link com.df.rhythmix.parser.ast.ASTNode} to add as a child.
     */
    public void addChild(ASTNode node) {
        children.add(node);
    }


    /**
     * Gets the token (lexeme) associated with this AST node.
     *
     * @return the {@link com.df.rhythmix.lexer.Token} object.
     */
    public Token getLexeme() {
        return lexeme;
    }


    /**
     * Gets the list of all child nodes.
     *
     * @return a {@link java.util.List} of child {@link com.df.rhythmix.parser.ast.ASTNode}s.
     */
    public List<ASTNode> getChildren() {
        return children;
    }

    /**
     * Sets the type of this AST node.
     *
     * @param type the {@link com.df.rhythmix.parser.ast.ASTNodeTypes} to set.
     */
    public void setType(ASTNodeTypes type) {
        this.type = type;
    }

    /**
     * Gets the type of this AST node.
     *
     * @return the {@link com.df.rhythmix.parser.ast.ASTNodeTypes} of this node.
     */
    public ASTNodeTypes getType() {
        return type;
    }

    /**
     * Gets the label of this AST node.
     *
     * @return the descriptive string label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Prints a representation of the AST subtree rooted at this node to the console.
     *
     * @param intent the indentation level for printing.
     */
    protected void print(int intent) {
        if (intent == 0) {
            System.out.println("print:" + this);
        }
        for (int i = 0; i < intent * 2; i++) {
            System.out.printf(" ");
        }
        System.out.println(label);
        for (ASTNode child : children) {
            child.print(intent + 1);
        }

    }
}
