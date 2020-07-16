/* (C)2020 */
package org.anchoranalysis.experiment.task.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.anchoranalysis.experiment.task.TaskStatistics;

public class ConcurrentJobMonitor implements Iterable<SubmittedJob> {

    private int totalNumJobs;

    // All submitted tasks
    private List<SubmittedJob> list = new ArrayList<>();

    public ConcurrentJobMonitor(int totalNumJobs) {
        super();
        this.totalNumJobs = totalNumJobs;
    }

    public synchronized boolean add(SubmittedJob e) {
        return list.add(e);
    }

    public synchronized String currentStateDescription() {

        long numJobsCompleted = numberCompletedJobs();
        long numJobsExecuting = numberExecutingJobs();
        long numJobsRemaining = totalNumJobs - numJobsCompleted - numJobsExecuting;

        return String.format(
                "%3d compl, %3d exec, %3d rem of %3d",
                numJobsCompleted, numJobsExecuting, numJobsRemaining, totalNumJobs);
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
        return totalNumJobs - numberCompletedJobs();
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

    public synchronized long sumCompletedSuccessfullyExectionTime() {
        return sumExectionTime(JobState::isCompletedSuccessfully);
    }

    public synchronized long sumCompletedFailureExectionTime() {
        return sumExectionTime(JobState::isCompletedFailure);
    }

    public synchronized TaskStatistics createStatistics() {
        TaskStatistics stats = new TaskStatistics(getTotalNumTasks());
        stats.setNumCompletedSuccess(
                numberCompletedSuccessfullyJobs(), sumCompletedSuccessfullyExectionTime());
        stats.setNumCompletedFailed(
                numberCompletedFailureJobs(), sumCompletedFailureExectionTime());
        return stats;
    }

    @Override
    public Iterator<SubmittedJob> iterator() {
        return list.iterator();
    }

    public int getTotalNumTasks() {
        return totalNumJobs;
    }

    private long numberJobs(Predicate<JobState> pred) {
        return filteredJobs(pred).count();
    }

    private long sumExectionTime(Predicate<JobState> pred) {
        return filteredJobs(pred).mapToLong(s -> s.getJobState().getTime()).sum();
    }

    private Stream<SubmittedJob> filteredJobs(Predicate<JobState> pred) {
        return list.stream().filter(s -> pred.test(s.getJobState()));
    }
}
