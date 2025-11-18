package io.github.mfinnnne.rhythmix.monitor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for formatting monitoring data into human-readable reports.
 * <p>
 * This class provides methods to generate various types of reports from
 * ExecutionMonitorData.
 *
 * @author MFine
 * @version 1.0
 * @since 1.1.0
 */
public class ExecutionReportFormatter {

    private static final String SEPARATOR = "=".repeat(80);
    private static final String SUB_SEPARATOR = "-".repeat(80);

    /**
     * Generates a comprehensive monitoring report.
     *
     * @param data the monitoring data to format
     * @return formatted report string
     */
    public static String generateReport(ExecutionMonitorData data) {
        StringBuilder report = new StringBuilder();

        report.append(SEPARATOR).append("\n");
        report.append("RHYTHMIX EXECUTOR MONITORING REPORT\n");
        report.append(SEPARATOR).append("\n\n");

        // Expression Information
        report.append(formatExpressionInfo(data.getStateFlowMetadata()));
        report.append("\n");

        // Execution Statistics
        report.append(formatExecutionStatistics(data));
        report.append("\n");

        // Timing Statistics
        report.append(formatTimingStatistics(data));
        report.append("\n");

        // State Position Distribution
        report.append(formatStatePositionDistribution(data));
        report.append("\n");

        // Recent Executions
        report.append(formatRecentExecutions(data, 10));

        report.append(SEPARATOR).append("\n");

        return report.toString();
    }

    /**
     * Formats expression information section.
     */
    private static String formatExpressionInfo(StateFlowMetadata metadata) {
        StringBuilder sb = new StringBuilder();
        sb.append("Expression Information:\n");
        sb.append(SUB_SEPARATOR).append("\n");
        sb.append(String.format("  Expression: %s\n", metadata.getExpression()));
        sb.append(String.format("  Total States: %d\n", metadata.getTotalStates()));
        sb.append("  State Units:\n");
        for (int i = 0; i < metadata.getTotalStates(); i++) {
            sb.append(String.format("    Position %d: %s\n", i, metadata.getStateUnitAtPosition(i)));
        }
        return sb.toString();
    }

    /**
     * Formats execution statistics section.
     */
    private static String formatExecutionStatistics(ExecutionMonitorData data) {
        StringBuilder sb = new StringBuilder();
        sb.append("Execution Statistics:\n");
        sb.append(SUB_SEPARATOR).append("\n");
        sb.append(String.format("  Total Events Processed: %d\n", data.getTotalEventsProcessed()));
        sb.append(String.format("  Successful Matches: %d\n", data.getSuccessfulMatches()));
        sb.append(String.format("  Failed Matches: %d\n", data.getFailedMatches()));
        sb.append(String.format("  Success Rate: %.2f%%\n", data.getSuccessRate()));
        return sb.toString();
    }

    /**
     * Formats timing statistics section.
     */
    private static String formatTimingStatistics(ExecutionMonitorData data) {
        StringBuilder sb = new StringBuilder();
        sb.append("Timing Statistics:\n");
        sb.append(SUB_SEPARATOR).append("\n");
        sb.append(String.format("  Total Execution Time: %.3f ms\n", data.getTotalExecutionTimeMillis()));
        sb.append(String.format("  Average Execution Time: %.3f ms\n", data.getAverageExecutionTimeMillis()));
        
        Long minNanos = data.getMinExecutionTimeNanos();
        Long maxNanos = data.getMaxExecutionTimeNanos();
        
        if (minNanos != null) {
            sb.append(String.format("  Min Execution Time: %.3f ms\n", minNanos / 1_000_000.0));
        }
        if (maxNanos != null) {
            sb.append(String.format("  Max Execution Time: %.3f ms\n", maxNanos / 1_000_000.0));
        }
        return sb.toString();
    }

    /**
     * Formats state position distribution section.
     */
    private static String formatStatePositionDistribution(ExecutionMonitorData data) {
        StringBuilder sb = new StringBuilder();
        sb.append("State Position Distribution:\n");
        sb.append(SUB_SEPARATOR).append("\n");

        // Group records by state position
        Map<Integer, Long> positionCounts = data.getExecutionRecords().stream()
                .filter(record -> record.getCurrentStatePosition() != null)
                .collect(Collectors.groupingBy(
                        ExecutionRecord::getCurrentStatePosition,
                        Collectors.counting()
                ));

        for (int i = 0; i < data.getStateFlowMetadata().getTotalStates(); i++) {
            long count = positionCounts.getOrDefault(i, 0L);
            String stateUnit = data.getStateFlowMetadata().getStateUnitAtPosition(i);
            sb.append(String.format("  Position %d (%s): %d events\n", i, stateUnit, count));
        }

        return sb.toString();
    }

    /**
     * Formats recent executions section.
     */
    private static String formatRecentExecutions(ExecutionMonitorData data, int limit) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Recent Executions (last %d):\n", limit));
        sb.append(SUB_SEPARATOR).append("\n");

        List<ExecutionRecord> records = data.getExecutionRecords();
        int start = Math.max(0, records.size() - limit);
        List<ExecutionRecord> recentRecords = records.subList(start, records.size());

        for (ExecutionRecord record : recentRecords) {
            sb.append(String.format("  %s\n", record.toString()));
        }

        return sb.toString();
    }
}

