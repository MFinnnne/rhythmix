package io.github.mfinnnne.rhythmix.monitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores metadata about the state flow structure.
 * <p>
 * This class contains information about the expression structure,
 * including the parsed state units and their position mappings.
 *
 * @author MFine
 * @version 1.0
 * @since 1.1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StateFlowMetadata {

    /**
     * The original expression string.
     * Example: "{a}->{b}->{c}" or "{count(>4,3)}->{==3}"
     */
    private String expression;

    /**
     * List of parsed state units in order.
     * Example: ["{a}", "{b}", "{c}"]
     */
    private List<String> stateUnits;

    /**
     * Total number of state units in the flow.
     */
    private Integer totalStates;

    /**
     * Mapping from position (0-based index) to state unit expression.
     * Example: {0: "{a}", 1: "{b}", 2: "{c}"}
     */
    @Builder.Default
    private Map<Integer, String> statePositionMap = new HashMap<>();

    /**
     * Gets the state unit at the specified position.
     *
     * @param position the position (0-based index)
     * @return the state unit expression at that position, or null if not found
     */
    public String getStateUnitAtPosition(Integer position) {
        if (position == null || position < 0 || position >= totalStates) {
            return null;
        }
        return statePositionMap.get(position);
    }

    /**
     * Checks if the given position is valid.
     *
     * @param position the position to check
     * @return true if the position is valid, false otherwise
     */
    public boolean isValidPosition(Integer position) {
        return position != null && position >= 0 && position < totalStates;
    }

    /**
     * Returns a human-readable string representation of the state flow metadata.
     *
     * @return formatted string with state flow details
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StateFlowMetadata{\n");
        sb.append("  expression='").append(expression).append("'\n");
        sb.append("  totalStates=").append(totalStates).append("\n");
        sb.append("  stateUnits=[\n");
        for (int i = 0; i < totalStates; i++) {
            sb.append("    ").append(i).append(": '").append(statePositionMap.get(i)).append("'\n");
        }
        sb.append("  ]\n");
        sb.append("}");
        return sb.toString();
    }
}

