package com.df.rhythmix.pebble;

import io.pebbletemplates.pebble.attributes.AttributeResolver;
import io.pebbletemplates.pebble.extension.*;
import io.pebbletemplates.pebble.operator.BinaryOperator;
import io.pebbletemplates.pebble.operator.UnaryOperator;
import io.pebbletemplates.pebble.tokenParser.TokenParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>CustomExtension class.</p>
 *
 * author MFine
 * version $Id: $Id
 */
public class CustomExtension implements Extension {
    /** {@inheritDoc} */
    @Override
    public Map<String, Filter> getFilters() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Test> getTests() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Function> getFunctions() {
        Map<String, Function> functions = new HashMap<>();

        /*
         * For efficiency purposes, some core functions are individually parsed
         * by our expression parser and compiled in their own unique way. This
         * includes the block and parent functions.
         */
        functions.put("debug", new DebugFunction());
        return functions;
    }

    /** {@inheritDoc} */
    @Override
    public List<TokenParser> getTokenParsers() {
        List<TokenParser> parsers = new ArrayList<>();
        parsers.add(new DebugTokenParser());
        parsers.add(new VarTokenParser());

        // verbatim tag is implemented directly in the LexerImpl
        return parsers;
    }

    /** {@inheritDoc} */
    @Override
    public List<BinaryOperator> getBinaryOperators() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<UnaryOperator> getUnaryOperators() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Object> getGlobalVariables() {
        Map<String, Object> map = new HashMap<>();
        map.put("debug", false);
        return map;
    }

    /** {@inheritDoc} */
    @Override
    public List<NodeVisitorFactory> getNodeVisitors() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<AttributeResolver> getAttributeResolver() {
        return null;
    }
}
