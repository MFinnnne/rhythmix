package io.github.mfinnnne.rhythmix.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>PriorityTable class.</p>
 *
 * author MFine
 * version 1.0
 * date 2021/9/28 20:40
 */
public class PriorityTable {

    private List<List<String>> table = new ArrayList<>();

    /**
     * <p>Constructor for PriorityTable.</p>
     */
    public PriorityTable() {
        this.table.add(Arrays.asList("&", "|", "^"));
        this.table.add(Arrays.asList("==", "!=", ">", "<", ">=", "<="));
        this.table.add(Arrays.asList("+", "-"));
        this.table.add(Arrays.asList("*", "/"));
        this.table.add(Arrays.asList("<<", ">>"));
        this.table.add(List.of("||"));
        this.table.add(List.of("&&"));
        this.table.add(List.of("!"));
    }


    /**
     * <p>Constructor for PriorityTable.</p>
     *
     * @param table a {@link java.util.List} object.
     */
    @SafeVarargs
    public PriorityTable(List<String>... table) {
        this.table.addAll(Arrays.asList(table));
    }

    /**
     * <p>size.</p>
     *
     * @return a int.
     */
    public int size() {
        return this.table.size();
    }

    /**
     * <p>get.</p>
     *
     * @param level a int.
     * @return a {@link java.util.List} object.
     */
    public List<String> get(int level) {
        return this.table.get(level);
    }


}
