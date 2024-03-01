package com.df.rhythmix.pebble;


import com.df.rhythmix.pebble.node.VarNode;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;

/**
 * 使用方法：{%debug "变量1:{},变量2：{}",var1,var2 %}
 */
public class VarTokenParser implements TokenParser {
    @Override
    public String getTag() {
        return "var";
    }

    @Override
    public RenderableNode parse(Token token, Parser parser) {
        TokenStream stream = parser.getStream();
        stream.expect(Token.Type.NAME);
        String name = stream.expect(Token.Type.NAME).getValue();
        stream.expect(Token.Type.EXECUTE_END);
        return new VarNode(name, token.getLineNumber());
    }
}
