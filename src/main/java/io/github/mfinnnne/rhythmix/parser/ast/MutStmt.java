package io.github.mfinnnne.rhythmix.parser.ast;


import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.exception.TranslatorException;
import io.github.mfinnnne.rhythmix.lexer.Token;
import io.github.mfinnnne.rhythmix.translate.MutExpr;
import io.github.mfinnnne.rhythmix.util.PeekTokenIterator;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses a mutation/state-transition expression and rewrites it into an arrow expression.
 * <p>
 * Mutation expressions have the form {@code <a,b,c> > <x,y>} and are translated by
 * {@link MutExpr} into an equivalent arrow expression which is then
 * parsed by {@link ArrowStmt}.
 *
 * author MFine
 * @version 1.0
 * @since 1.0
 */
public class MutStmt extends Stmt {

    /**
     * <p>Constructor for MutStmt.</p>
     */
    protected MutStmt() {
        super(ASTNodeTypes.ARROW_EXPR, "arrow expr");
    }


    /**
     * Parses a mutation expression and returns an equivalent {@link ArrowStmt} result.
     * If the upcoming tokens are not a mutation, returns {@code null}.
     *
     * @param it the token iterator
     * @return the translated arrow statement, or {@code null} if not a mutation expression
     * @throws ParseException if translation fails or a syntax error is encountered
     */
    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        if (isMutStmt(it)) {
            List<Token> tokens = new ArrayList<>();
            while (it.hasNext()) {
                if (">".equals(it.peek().getValue())) {
                    tokens.add(it.next());
                    break;
                }
                tokens.add(it.next());
            }
            try {
                List<Token> tks = MutExpr.translateMutExpr(tokens);
                return ArrowStmt.parse(new PeekTokenIterator(tks.stream()));
            } catch (TranslatorException e) {
                throw new ParseException(e.getMessage());
            }
        }
        return null;
    }

    /**
     * Checks whether the upcoming tokens match the mutation expression pattern.
     * Uses a temporary recording/rewind on the iterator.
     *
     * @param it the token iterator
     * @return {@code true} if a mutation expression is detected; {@code false} otherwise
     */
    public static boolean isMutStmt(PeekTokenIterator it) {
        try {
            it.record();
            if (!it.nextMatch1("<")) {
                return false;
            }
            it.next();
            if (!it.next().isScalar()) {
                return false;
            }
            while (it.hasNext() && !">".equals(it.peek().getValue())) {
                if (!",".equals(it.next().getValue())) {
                    return false;
                }
                if (!it.next().isScalar()) {
                    return false;
                }
                if (">".equals(it.next().getValue())) {
                    return true;
                }
            }
        } finally {
            it.backRecord();
        }
        return false;
    }
}
