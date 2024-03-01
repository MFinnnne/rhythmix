package com.celi.ferrum.pebble.node;

import com.celi.ferrum.pebble.TemplateEngine;
import com.celi.ferrum.util.Config;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.AbstractRenderableNode;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.template.ScopeChain;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

public class DebugNode extends AbstractRenderableNode {

    private final List<String> template;
    private final List<String> args;

    public DebugNode(List<String> template, List<String> args, int lineNumber) {
        super(lineNumber);
        this.args = args;
        this.template = template;
    }

    @Override
    public void render(PebbleTemplateImpl self, Writer writer, EvaluationContextImpl context) throws IOException {
        if (TemplateEngine.isDebugMode()) {
            StringBuilder code = new StringBuilder();
            code.append("p('");
            ScopeChain scopeChain = context.getScopeChain();
            for (int i = 0; i < args.size(); i++) {
                code.append(template.get(i)).append("'").append("+");
                if (scopeChain.containsKey(args.get(i))) {
                    Object value = scopeChain.get(args.get(i));
                    if (code.charAt(code.length() - 3) == '!') {
                        code.replace(code.length() - 3, code.length() - 2, "");
                        code.append("'").append(value).append("'");
                    } else {
                        code.append("'").append(value).append(Config.SPLIT_SYMBOL).append(Config.VAR_COUNTER.get()).append("'");
                    }
                } else {
                    if (code.charAt(code.length() - 3) != '!') {
                        code.append("(").append(args.get(i));
                        code.append(Config.SPLIT_SYMBOL).append(Config.VAR_COUNTER.get());
                    } else {
                        code.replace(code.length() - 3, code.length() - 2, "");
                        code.append("(").append(args.get(i));
                    }
                    code.append(")");
                }
                if (i != args.size() - 1) {
                    code.append("+'");
                }
            }
            code.append(");");
            if (args.size() == 0) {
                code.delete(0, code.length());
                code.append("p('").append(template.get(0)).append("');");
            }
            writer.write(code.toString());
        }
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
