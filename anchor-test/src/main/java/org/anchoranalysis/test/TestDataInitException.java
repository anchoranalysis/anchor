/* (C)2020 */
package org.anchoranalysis.test;

/**
 * An exception thrown when test-data cannot be loaded
 *
 * @author Owen Feehan
 */
public class TestDataInitException extends RuntimeException {

    /** */
    private static final long serialVersionUID = 1L;

    public TestDataInitException(String msg) {
        super(msg);
    }

    public TestDataInitException(Throwable e) {
        super(e);
    }
}
