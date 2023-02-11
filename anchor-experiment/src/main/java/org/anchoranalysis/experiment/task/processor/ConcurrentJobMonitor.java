/*-
 * #%L
 * anchor-experiment
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.experiment.task.processor;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.functional.OptionalFactory;
import org.anchoranalysis.experiment.task.TaskStatistics;
import org.anchoranalysis.math.arithmetic.RunningSum;

/**
 * Monitors all submitted jobs and their current state of execution.
 *
 * <p>It provides statistics and a textual description of the aggregate state of execution across
 * all jobs.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class ConcurrentJobMonitor implements Iterable<SubmittedJob> {

    // START REQUIRED FIELDS
    /** The total number of submitted jobs to be executed. */
    @Getter private final long totalNumberJobs;
    // END REQUIRED FIELDS

    /** All submitted tasks. */
    private List<SubmittedJob> list = new LinkedList<>();

    /**
     * Adds a job to be considered in the aggregate view.
     *
     * @param job the job.
     */
    public synchronized void add(SubmittedJob job) {
        list.add(job);
    }

    /**
     * A human-understandable string describing the aggregate state.
     *
     * <p>It features:
     *
     * <ul>
     *   <li>The number of completed jobs.
     *   <li>The number of executing jobs.
     *   <li>The number of jobs, that have not yet started executing (remaining to be executed).
     * </ul>
     *
     * <p>The string is shortened using abbreviations to help fit in a line of console outut.
     *
     * @return the string, as per above.
     */
    public synchronized String currentStateDescription() {

        long numberJobsCompleted = numberCompletedJobs();
        long numberJobsExecuting = numberExecutingJobs();
        long numberJobsRemaining = totalNumberJobs - numberJobsCompleted - numberJobsExecuting;

        return String.format(
                "%3d compl, %3d exec, %3d rem of %3d",
                numberJobsCompleted, numberJobsExecuting, numberJobsRemaining, totalNumberJobs);
    }

    /**
     * A string that describes all jobs that are not tey completed, but only if there are fewer than
     * a certain number.
     *
     * <p>The string shows the number and a short-name for each job, and the number of seconds for
     * which it is has been executing.
     *
     * <p>The string shows the jobs on one long line, without using newlines.
     *
     * @param fewerThanThreshold a description is only shown if the total number of executing tasks
     *     is less than this threshold.
     * @return a string in the form above if the condition is fulfilled, or otherwise {@link
     *     Optional#empty}.
     */
    public synchronized Optional<String> describeUncompletedJobs(int fewerThanThreshold) {
        return OptionalFactory.create(
                numberUncompletedJobs() < fewerThanThreshold, this::describeAllUncompletedTasks);
    }

    /**
     * The number of jobs that remain uncompleted.
     *
     * @return the number of jobs.
     */
    public synchronized long numberUncompletedJobs() {
        return totalNumberJobs - numberCompletedJobs();
    }

    /**
     * The number of jobs that have completed, regardless of failure state.
     *
     * <p>This includes both jobs that have completed successfully and with failure.
     *
     * @return the number of jobs.
     */
    public synchronized long numberCompletedJobs() {
        return numberJobs(JobStateMonitor::isCompleted);
    }

    /**
     * The number of jobs that have completed in a state of success.
     *
     * @return the number of jobs.
     */
    public synchronized long numberCompletedSuccessfullyJobs() {
        return numberJobs(JobStateMonitor::isCompletedSuccessfully);
    }

    /**
     * The number of jobs that have completed in a state of failure.
     *
     * @return the number of jobs.
     */
    public synchronized long numberCompletedFailureJobs() {
        return numberJobs(JobStateMonitor::isCompletedFailure);
    }

    /**
     * The number of jobs that are currently executing.
     *
     * @return the number of jobs.
     */
    public synchronized long numberExecutingJobs() {
        return numberJobs(JobStateMonitor::isExecuting);
    }

    /**
     * Derive statistics on the aggregate state of jobs.
     *
     * @return a newly created {@link TaskStatistics} capturing the state at the point it was
     *     executed.
     */
    public synchronized TaskStatistics deriveStatistics() {
        return new TaskStatistics(
                getTotalNumberJobs(),
                runningSum(JobStateMonitor::isCompletedSuccessfully),
                runningSum(JobStateMonitor::isCompletedFailure));
    }

    @Override
    public Iterator<SubmittedJob> iterator() {
        return list.iterator();
    }

    private RunningSum runningSum(Predicate<JobStateMonitor> predicate) {
        return new RunningSum(sumExectionTime(predicate), numberJobs(predicate));
    }

    private long numberJobs(Predicate<JobStateMonitor> predicate) {
        return filteredJobs(predicate).count();
    }

    private long sumExectionTime(Predicate<JobStateMonitor> predicate) {
        return filteredJobs(predicate)
                .mapToLong(job -> job.getJobState().getExecutionDuration())
                .sum();
    }

    private Stream<SubmittedJob> filteredJobs(Predicate<JobStateMonitor> predicate) {
        return list.stream().filter(job -> predicate.test(job.getJobState()));
    }

    /** A string describing all currently executing tasks. */
    private String describeAllUncompletedTasks() {

        StringBuilder builder = new StringBuilder();
        for (SubmittedJob job : list) {
            if (job.getJobState().isExecuting()) {
                builder.append(
                        String.format(
                                "%d(%s,%ds), ",
                                job.getJobDescription().getNumber(),
                                job.getJobDescription().getShortName(),
                                job.getJobState().getExecutionDuration() / 1000));
            }
        }
        return builder.toString();
    }
}
