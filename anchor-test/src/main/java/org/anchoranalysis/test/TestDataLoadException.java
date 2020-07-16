/* (C)2020 */
package org.anchoranalysis.test;

/**
 * An exception thrown when test-data cannot be loaded
 *
 * @author Owen Feehan
 */
public class TestDataLoadException extends RuntimeException {

    /** */
    private static final long serialVersionUID = 1L;

    public TestDataLoadException(String s) {
        super(s);
    }

    public TestDataLoadException(Throwable e) {
        super(e);
    }
}
