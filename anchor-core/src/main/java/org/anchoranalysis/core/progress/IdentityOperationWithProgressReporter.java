/* (C)2020 */
package org.anchoranalysis.core.progress;

public class IdentityOperationWithProgressReporter<T, E extends Exception>
        implements OperationWithProgressReporter<T, E> {

    private T obj;

    public IdentityOperationWithProgressReporter(T obj) {
        super();
        this.obj = obj;
    }

    @Override
    public T doOperation(ProgressReporter progressReporter) throws E {
        return obj;
    }
}
