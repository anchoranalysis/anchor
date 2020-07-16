/* (C)2020 */
package org.anchoranalysis.anchor.mpp.plot.execution;

public class ExecutionTimeItem {

    private String objectID;
    private long executionTime;

    public ExecutionTimeItem(String objectID, long executionTime) {
        super();
        this.objectID = objectID;
        this.executionTime = executionTime;
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
}
