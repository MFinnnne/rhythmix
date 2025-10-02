package com.df.rhythmix.pebble;

import com.df.rhythmix.lexer.PeekIterator;
import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 已废弃
 * 请使用{@link com.df.rhythmix.pebble.DebugTokenParser}
 *
 * author MFine
 * version $Id: $Id
 */
@Deprecated
public class DebugFunction implements Function {

    /** {@inheritDoc} */
    @Override
    public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context,
                          int lineNumber) {
        String pattern = (String) args.get(String.valueOf(0));
        String[] param = new String[args.size() - 1];
        for (int i = 1; i < args.size(); i++) {
            param[i - 1] = (String) args.get(String.valueOf(i));
        }
        boolean debug = ((Boolean) context.getVariable("debug"));
        if (debug) {
            List<String> strings = this.split(pattern);
            StringBuilder format = new StringBuilder();
            format.append("p(");
            for (int i = 0; i < strings.size(); i++) {
                format.append("\"").append(strings.get(i)).append("\"");
                if (param.length >= i) {
                    format.append("+").append("(").append(param[i]).append(")");
                }
                if (i != strings.size() - 1) {
                    format.append("+");
                }
            }
            format.append(");");
            return format.toString();
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getArgumentNames() {
        return null;
    }

    private List<String> split(String source) {
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
