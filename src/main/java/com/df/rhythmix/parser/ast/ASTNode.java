package com.df.rhythmix.parser.ast;

import com.df.rhythmix.lexer.Token;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class ASTNode {


    protected List<ASTNode> children = new ArrayList<>();

    protected Token lexeme;


    protected String label;

    protected ASTNodeTypes type;


    public void setLabel(String label) {
        this.label = label;
    }

    protected ASTNode(ASTNodeTypes type, String label) {
        this.type = type;
        this.label = label;
    }

    protected ASTNode() {
    }


    public void setLexeme(Token lexeme) {
        this.lexeme = lexeme;
    }

    public ASTNode getChildren(int index) {
        return children.get(index);
    }


    public void addChild(ASTNode node) {
        children.add(node);
    }


    public Token getLexeme() {
        return lexeme;
    }


    public List<ASTNode> getChildren() {
        return children;
    }

    public void setType(ASTNodeTypes type) {
        this.type = type;
    }

    public ASTNodeTypes getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

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