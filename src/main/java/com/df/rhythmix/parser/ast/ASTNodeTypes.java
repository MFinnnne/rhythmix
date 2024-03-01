package com.df.rhythmix.parser.ast;

public enum ASTNodeTypes {
    /**
     * 块
     */
    BLOCK,
    /**
     * 二元表示
     */
    BINARY_EXPR,
    /**
     * 链式调用表达式
     */
    CHAIN_EXPR,

    /**
     * 区间表达式
     */
    RANGE_EXPR,

    /**
     * 比较表达式
     */
    COMPARE_EXPR,

    /**
     * 箭头表达式
     */
    ARROW_EXPR,


    /**
     * 匿名函数
     */
    ARROW_FUNC,

    /**
     * 一元表达式
     */
    UNARY_EXPR,
    /**
     * 变量
     */
    VARIABLE,
    /**
     * 标量
     */
    SCALAR,
    /**
     * 如果语句
     */
    IF_STMT,
    /**
     * while语句
     */
    WHILE_STMT,
    /**
     * for语句
     */
    FOR_STMT,
    /**
     * 赋值语句
     */
    ASSIGN_STMT,
    /**
     * 函数声明语句
     */
    FUNCTION_DECLARE_STMT,

    /**
     * 定义语句
     */
    DECLARE_STMT,

    /**
     * 返回语句
     */
    RETURN_STMT,
    /**
     * 函数调用
     */
    CALL_STMT,

    PROGRAM;
}