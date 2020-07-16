/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.optscheme.feedback;

import org.anchoranalysis.core.log.MessageLogger;

public class OptimizationFeedbackEndParams<T> {

    private T state;
    private MessageLogger logger;

    public MessageLogger getLogReporter() {
        return logger;
    }

    public void setLogReporter(MessageLogger logger) {
        this.logger = logger;
    }

    public T getState() {
        return state;
    }

    public void setState(T state) {
        this.state = state;
    }
}
