package org.anchoranalysis.experiment.bean.io;

import org.anchoranalysis.experiment.log.Divider;
import org.anchoranalysis.experiment.task.ExecutionTimeStatistics;
import org.anchoranalysis.experiment.task.TaskStatistics;
import org.anchoranalysis.math.arithmetic.RunningSum;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


/**
 * Generates report of the execution-times of different operations.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
class ExecutionTimeStatisticsReport {

    private static final Divider DIVIDER = new Divider();

    private static final String AVERAGE_TOTAL_LINE =
            "\t\t\t\t\t\t\t\t\t\taverage       total      (ignoring any parallelism)";
    
    public static String report(TaskStatistics task, ExecutionTimeStatistics operation, long executionTimeTotal) {

        RunningSum taskTotal = task.executionTimeTotal();
        
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("The total execution time was %d seconds, including any parallelism.", executionTimeTotal));
        builder.append(System.lineSeparator());
        builder.append(DIVIDER.withLabel("Execution Time"));
        builder.append(System.lineSeparator());
        builder.append(AVERAGE_TOTAL_LINE);
        builder.append(System.lineSeparator());
        builder.append(DescribeExecutionTimes.individual(
                "Entire Job",
                taskTotal.mean() / 1000,
                taskTotal.getSum() / 1000,
                (int) task.numberCompletedTotal()));
        builder.append(DescribeExecutionTimes.allOperations(operation));
        builder.append(DIVIDER.withoutLabel());
        return builder.toString();
    }
}
