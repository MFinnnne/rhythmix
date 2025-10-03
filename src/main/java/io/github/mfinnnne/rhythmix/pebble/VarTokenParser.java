package io.github.mfinnnne.rhythmix.pebble;


import io.github.mfinnnne.rhythmix.pebble.node.VarNode;
import io.pebbletemplates.pebble.lexer.Token;
import io.pebbletemplates.pebble.lexer.TokenStream;
import io.pebbletemplates.pebble.node.RenderableNode;
import io.pebbletemplates.pebble.parser.Parser;
import io.pebbletemplates.pebble.tokenParser.TokenParser;

/**
 * 使用方法：{%debug "变量1:{},变量2：{}",var1,var2 %}
 *
 * author MFine
 * version $Id: $Id
 */
public class VarTokenParser implements TokenParser {
    /** {@inheritDoc} */
    @Override
    public String getTag() {
        return "var";
    }

    /** {@inheritDoc} */
    @Override
    public RenderableNode parse(Token token, Parser parser) {
        TokenStream stream = parser.getStream();
        stream.expect(Token.Type.NAME);
        String name = stream.expect(Token.Type.NAME).getValue();
        stream.expect(Token.Type.EXECUTE_END);
        return new VarNode(name, token.getLineNumber());
    }
}
