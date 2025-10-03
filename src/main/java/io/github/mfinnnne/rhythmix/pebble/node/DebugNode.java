package io.github.mfinnnne.rhythmix.pebble.node;

import io.github.mfinnnne.rhythmix.pebble.TemplateEngine;
import io.github.mfinnnne.rhythmix.config.Config;
import io.pebbletemplates.pebble.extension.NodeVisitor;
import io.pebbletemplates.pebble.node.AbstractRenderableNode;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;
import io.pebbletemplates.pebble.template.ScopeChain;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * <p>DebugNode class.</p>
 *
 * author MFine
 * version $Id: $Id
 */
public class DebugNode extends AbstractRenderableNode {

    private final List<String> template;
    private final List<String> args;

    /**
     * <p>Constructor for DebugNode.</p>
     *
     * @param template a {@link java.util.List} object.
     * @param args a {@link java.util.List} object.
     * @param lineNumber a int.
     */
    public DebugNode(List<String> template, List<String> args, int lineNumber) {
        super(lineNumber);
        this.args = args;
        this.template = template;
    }

    /** {@inheritDoc} */
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
            code.append("\n");
            writer.write(code.toString());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
