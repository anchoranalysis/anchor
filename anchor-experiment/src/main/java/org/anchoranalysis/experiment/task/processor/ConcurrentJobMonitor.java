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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.experiment.task.TaskStatistics;
import org.anchoranalysis.math.arithmetic.RunningSum;

@RequiredArgsConstructor
public class ConcurrentJobMonitor implements Iterable<SubmittedJob> {

    // START REQUIRED FIELDS
    private final long totalNumberJobs;
    // END REQUIRED FIELDS

    // All submitted tasks
    private List<SubmittedJob> list = new ArrayList<>();

    public synchronized boolean add(SubmittedJob e) {
        return list.add(e);
    }

    public synchronized String currentStateDescription() {

        long numberJobsCompleted = numberCompletedJobs();
        long numberJobsExecuting = numberExecutingJobs();
        long numberJobsRemaining = totalNumberJobs - numberJobsCompleted - numberJobsExecuting;

        return String.format(
                "%3d compl, %3d exec, %3d rem of %3d",
                numberJobsCompleted, numberJobsExecuting, numberJobsRemaining, totalNumberJobs);
    }

    public synchronized String ongoingTasksLessThan(int num) {
        if (numberOngoingJobs() < num) {
            return executingTasks();
        } else {
            return "";
        }
    }

    public synchronized String executingTasks() {

        StringBuilder sb = new StringBuilder();
        for (SubmittedJob st : list) {
            if (st.getJobState().isExecuting()) {
                sb.append(
                        String.format(
                                "%d(%s,%ds), ",
                                st.getJobDescription().getJobNumber(),
                                st.getJobDescription().getJobShortName(),
                                st.getJobState().getTime() / 1000));
            }
        }
        return sb.toString();
    }

    public synchronized long numberOngoingJobs() {
        return totalNumberJobs - numberCompletedJobs();
    }

    public synchronized long numberCompletedJobs() {
        return numberJobs(JobState::isCompleted);
    }

    public synchronized long numberCompletedSuccessfullyJobs() {
        return numberJobs(JobState::isCompletedSuccessfully);
    }

    public synchronized long numberCompletedFailureJobs() {
        return numberJobs(JobState::isCompletedFailure);
    }

    public synchronized long numberExecutingJobs() {
        return numberJobs(JobState::isExecuting);
    }

    public synchronized TaskStatistics createStatistics() {
        return new TaskStatistics(
                getTotalNumberTasks(),
                runningSum(JobState::isCompletedSuccessfully),
                runningSum(JobState::isCompletedFailure));
    }

    @Override
    public Iterator<SubmittedJob> iterator() {
        return list.iterator();
    }

    public long getTotalNumberTasks() {
        return totalNumberJobs;
    }

    private RunningSum runningSum(Predicate<JobState> predicate) {
        return new RunningSum(sumExectionTime(predicate), numberJobs(predicate));
    }

    private long numberJobs(Predicate<JobState> predicate) {
        return filteredJobs(predicate).count();
    }

    private long sumExectionTime(Predicate<JobState> predicate) {
        return filteredJobs(predicate).mapToLong(s -> s.getJobState().getTime()).sum();
    }

    private Stream<SubmittedJob> filteredJobs(Predicate<JobState> pred) {
        return list.stream().filter(s -> pred.test(s.getJobState()));
    }
}
