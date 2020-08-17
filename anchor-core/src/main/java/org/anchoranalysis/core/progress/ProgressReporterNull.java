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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Placeholder that doesn't measure any progress
 *
 * <p>Implemented as a singleton, to avoid repeated instances of creating a new Autocloseable class,
 * which confuses lint tools like SonarQube
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProgressReporterNull implements ProgressReporter {

    private static ProgressReporterNull instance = null;

    public static ProgressReporterNull get() {
        if (instance == null) {
            instance = new ProgressReporterNull();
        }
        return instance;
    }

    @Override
    public void open() {
        // DOES NOTHING
    }

    @Override
    public void close() {
        // DOES NOTHING
    }

    @Override
    public int getMax() {
        // Arbitrary value
        return 0;
    }

    @Override
    public int getMin() {
        // Arbitrary value
        return 0;
    }

    @Override
    public void update(int val) {
        // DOES NOTHING
    }

    @Override
    public void setMin(int min) {
        // DOES NOTHING
    }

    @Override
    public void setMax(int max) {
        // DOES NOTHING
    }
}
