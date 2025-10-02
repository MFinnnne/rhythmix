package com.df.rhythmix.pebble;


import com.df.rhythmix.lexer.PeekIterator;
import com.df.rhythmix.pebble.node.DebugNode;
import io.pebbletemplates.pebble.error.ParserException;
import io.pebbletemplates.pebble.lexer.Token;
import io.pebbletemplates.pebble.lexer.TokenStream;
import io.pebbletemplates.pebble.node.RenderableNode;
import io.pebbletemplates.pebble.parser.Parser;
import io.pebbletemplates.pebble.tokenParser.TokenParser;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用方法：{%debug "变量1:{},变量2：{}",var1,var2 %}
 *
 * author MFine
 * version $Id: $Id
 */
public class DebugTokenParser implements TokenParser {
    /** {@inheritDoc} */
    @Override
    public String getTag() {
        return "debug";
    }

    /** {@inheritDoc} */
    @Override
    public RenderableNode parse(Token token, Parser parser) {
        TokenStream stream = parser.getStream();
        List<String> splitByBrace = new ArrayList<>();
        if (!stream.peek().test(Token.Type.EXECUTE_END)) {
            String template = stream.next().getValue();
            splitByBrace = this.splitByBrace(template);
        }
        if (stream.peek().test(Token.Type.EXECUTE_END)) {
            String value = stream.current().getValue();
            stream.next();
            stream.expect(Token.Type.EXECUTE_END);
            return new DebugNode(List.of(value), new ArrayList<>(), token.getLineNumber());
        }
        stream.next();
        stream.expect(Token.Type.PUNCTUATION, ",");
        List<String> args = new ArrayList<>();
        while (!stream.current().test(Token.Type.EXECUTE_END)) {
            args.add(stream.current().getValue());
            stream.next();
            if (!stream.current().test(Token.Type.EXECUTE_END)) {
                stream.expect(Token.Type.PUNCTUATION, ",");
            }
        }
        if (args.size() != splitByBrace.size()) {
            throw new ParserException(null, "debug tag 占位符'{}'数量和参数数量不匹配",
                    stream.current().getLineNumber(), stream.getFilename());
        }

        stream.expect(Token.Type.EXECUTE_END);
        return new DebugNode(splitByBrace, args, token.getLineNumber());
    }

    private List<String> splitByBrace(String source) {
        PeekIterator<Character> it = new PeekIterator<>(source.chars().mapToObj(x -> (char) x));
        List<String> split = new ArrayList<>();
        StringBuilder str = new StringBuilder();
        while (it.hasNext()) {
            Character next = it.next();
            if ("{".equals(next.toString())) {
                if (it.hasNext() && "}".equals(it.peek().toString())) {
                    split.add(str.toString());
                    str.delete(0, str.length());
                    it.next();
                } else {
                    str.append(next);
                }
            } else {
                str.append(next);
            }
        }
        return split;
    }
}
