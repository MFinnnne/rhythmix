package io.github.mfinnnne.rhythmix.parser.ast;

import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.lexer.Token;
import io.github.mfinnnne.rhythmix.util.PeekTokenIterator;

/**
 * Represents a function definition statement in the AST.
 * e.g., {@code func myFunc(int a, int b) int { ... }}
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class FunctionDefineStmt extends Stmt {
    /**
     * <p>Constructor for FunctionDefineStmt.</p>
     */
    protected FunctionDefineStmt() {
        super( ASTNodeTypes.FUNCTION_DECLARE_STMT, "function");
    }

    /**
     * Parses a function definition statement.
     * Expects 'func' keyword, function name, argument list, return type, and a block of code.
     *
     * @param it a {@link PeekTokenIterator} object.
     * @return a {@link ASTNode} object representing the function definition.
     * @throws ParseException if the syntax is incorrect.
     */
    public static ASTNode parse( PeekTokenIterator it) throws ParseException {
        it.nextMatch("func");
        FunctionDefineStmt functionDefineStmt = new FunctionDefineStmt();
        Token peek = it.peek();
        functionDefineStmt.setLexeme(peek);
        Variable var = (Variable) Factor.parse( it);
        functionDefineStmt.addChild(var);
        it.nextMatch("(");
        ASTNode parse = FunctionArgs.parse( it);
        functionDefineStmt.addChild(parse);
        it.nextMatch(")");
        Token returnType = it.next();
        if (!returnType.isType()) {
            throw new ParseException(returnType);
        }
        assert var != null;
        var.setTypeLexeme(returnType);
        ASTNode block = Block.parse( it);
        functionDefineStmt.addChild(block);
        return functionDefineStmt;
    }

    /**
     * Gets the arguments of the function.
     *
     * @return an {@link ASTNode} representing the function's arguments.
     */
    public ASTNode getArgs() {
        return this.getChildren(1);
    }

    /**
     * Gets the variable node representing the function's name and return type.
     *
     * @return a {@link Variable} object for the function.
     */
    public Variable getFunctionVariable() {
        return (Variable) this.getChildren(0);
    }

    /**
     * Gets the return type of the function as a string.
     *
     * @return a {@link java.lang.String} object representing the function's return type.
     */
    public String getFuncType() {
        return this.getFunctionVariable().getTypeLexeme().getValue();
    }

    /**
     * Gets the block of code that constitutes the function's body.
     *
     * @return a {@link Block} object representing the function's body.
     */
    public Block getBlock() {
        return (Block) this.getChildren(2);
    }
}
