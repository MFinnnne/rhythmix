package com.df.rhythmix.lexer;

public enum TokenType {
    // 关键字
    KEYWORD("keyword"),
    // 变量
    VARIABLE("var"),
    // 操作符
    OPERATOR("operator"),
    // 括号
    BRACKET("bracket"),
    // 字符串
    STRING("string"),
    // 浮点数
    FLOAT("float"),
    // 布尔型
    BOOLEAN("boolean"),
    // 整型
    INTEGER("integer");

    private String title;

    TokenType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
