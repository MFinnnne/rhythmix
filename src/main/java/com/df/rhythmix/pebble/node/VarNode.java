package com.df.rhythmix.pebble.node;

import com.df.rhythmix.config.Config;
import io.pebbletemplates.pebble.extension.NodeVisitor;
import io.pebbletemplates.pebble.node.AbstractRenderableNode;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

import java.io.IOException;
import java.io.Writer;

public class VarNode extends AbstractRenderableNode {


    private String name;

    public VarNode(String name, int lineNumber) {
        super(lineNumber);
        this.name = name;
    }

    @Override
    public void render(PebbleTemplateImpl self, Writer writer, EvaluationContextImpl context) throws IOException {
        if (this.name.split("\\"+Config.SPLIT_SYMBOL).length==2) {
            writer.write(this.name);
            return;
        }
        if (this.name.startsWith("__") && this.name.endsWith("__")) {
            this.name = this.name.substring(2, this.name.length() - 2);
            if (context.getScopeChain().containsKey(this.name)) {
                this.name = context.getScopeChain().get(this.name).toString();
            }
            writer.write(this.name);
            return;
        } else if (context.getScopeChain().containsKey(this.name)) {
            this.name = context.getScopeChain().get(this.name).toString();
            if (this.name.startsWith("!")) {
                writer.write(this.name.replaceAll("!",""));
                return;
            }
        }
        writer.write(this.name + Config.SPLIT_SYMBOL + Config.VAR_COUNTER.get());
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
