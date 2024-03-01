package com.celi.ferrum.util;


import com.celi.ferrum.exception.ParseException;
import com.celi.ferrum.parser.ast.ASTNode;

/**
 * @author MFine
 * @version 1.0
 * @date 2021/9/29 23:04
 **/
@FunctionalInterface
public interface ExprHOF {

    ASTNode hoc() throws ParseException;

}