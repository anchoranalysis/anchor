/* (C)2020 */
package org.anchoranalysis.experiment.task.processor;

public class SubmittedJob {

    private JobDescription jobDescription;
    private JobState jobState;

    public SubmittedJob(JobDescription jobDescription, JobState jobState) {
        super();
        this.jobDescription = jobDescription;
        this.jobState = jobState;
    }

    public JobState getJobState() {
        return jobState;
    }

    public JobDescription getJobDescription() {
        return jobDescription;
    }
}
