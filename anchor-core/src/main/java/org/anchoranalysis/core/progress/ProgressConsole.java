/*-
 * #%L
 * anchor-core
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

package org.anchoranalysis.core.progress;

import lombok.RequiredArgsConstructor;

/**
 * Displays the current progress to the console.
 * 
 * <p>Percentages are incrementlly reported to the console as an operation progresses.
 * 
 * @author Owen Feehan
 *
 */
@RequiredArgsConstructor
public class ProgressConsole implements Progress {

    // START REQUIRED ARGUMENTS
    private final int incrementSize;
    // END REQUIRED ARGUMENTS
    
    private int max;
    private int min;

    private int nextPercentageToReport = 0;

    private double percentCompleted(int val) {
        return ((double) (val - min)) / (max - min) * 100;
    }

    @Override
    public void open() {
        System.out.printf("[ "); // NOSONAR
    }

    private void reportPercentage(int percent) {
        System.out.printf("%d%s ", percent, "%"); // NOSONAR
        nextPercentageToReport += incrementSize;
    }

    @Override
    public void close() {
        if (nextPercentageToReport >= 100) {
            reportPercentage(100);
        }
        System.out.printf("]%n"); // NOSONAR
    }

    @Override
    public int getMax() {
        return max;
    }

    @Override
    public int getMin() {
        return min;
    }

    @Override
    public void update(int value) {

        double percent = percentCompleted(value);
        while (percent > nextPercentageToReport) {
            reportPercentage(nextPercentageToReport);
        }
    }

    @Override
    public void setMin(int min) {
        this.min = min;
    }

    @Override
    public void setMax(int max) {
        this.max = max;
    }
}
