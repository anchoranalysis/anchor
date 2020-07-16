/* (C)2020 */
package org.anchoranalysis.core.progress;

public interface ProgressReporter extends AutoCloseable {

    void open();

    @Override
    void close();

    int getMax();

    int getMin();

    void update(int val);

    void setMin(int min);

    void setMax(int max);
}
