package io.github.mfinnnne.rhythmix.util;


import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.parser.ast.ASTNode;

/**
 * <p>ExprHOF interface.</p>
 *
 * author MFine
 * version 1.0
 * date 2021/9/29 23:04
 */
@FunctionalInterface
public interface ExprHOF {

    /**
     * <p>hoc.</p>
     *
     * @return a {@link ASTNode} object.
     * @throws ParseException if any.
     */
    ASTNode hoc() throws ParseException;

}
