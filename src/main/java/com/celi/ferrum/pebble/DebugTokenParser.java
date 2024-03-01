package com.celi.ferrum.pebble;


import com.celi.ferrum.lexer.PeekIterator;
import com.celi.ferrum.pebble.node.DebugNode;
import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 使用方法：{%debug "变量1:{},变量2：{}",var1,var2 %}
 */
public class DebugTokenParser implements TokenParser {
    @Override
    public String getTag() {
        return "debug";
    }

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
