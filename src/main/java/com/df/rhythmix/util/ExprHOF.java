package com.df.rhythmix.util;


import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.parser.ast.ASTNode;

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
     * @return a {@link com.df.rhythmix.parser.ast.ASTNode} object.
     * @throws com.df.rhythmix.exception.ParseException if any.
     */
    ASTNode hoc() throws ParseException;

}
