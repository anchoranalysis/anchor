/* (C)2020 */
package org.anchoranalysis.experiment.task.processor;

public class JobDescription {

    private String jobShortName;
    private int jobNumber;

    public JobDescription(String jobShortName, int jobNumber) {
        super();
        this.jobShortName = jobShortName;
        this.jobNumber = jobNumber;
    }

    @Override
    public String toString() {
        return String.format("%4d(%s)", jobNumber, jobShortName);
    }

    public String getJobShortName() {
        return jobShortName;
    }

    public void setJobShortName(String jobShortName) {
        this.jobShortName = jobShortName;
    }

    public int getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(int jobNumber) {
        this.jobNumber = jobNumber;
    }
}
