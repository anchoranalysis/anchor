/*-
 * #%L
 * anchor-mpp-sgmn
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

package org.anchoranalysis.mpp.segment.optscheme.feedback.aggregate;

import java.io.PrintWriter;

public class AggIDCounter {

    private double[] idArr;

    public AggIDCounter(int size) {
        idArr = new double[size];
    }

    public AggIDCounter deepCopy() {
        AggIDCounter aic = new AggIDCounter(idArr.length);
        System.arraycopy(idArr, 0, aic.idArr, 0, idArr.length);
        return aic;
    }

    public double get(int id) {
        return idArr[id];
    }

    public void incr(int id) {

        // We ignore negative Ids
        if (id >= 0) {
            idArr[id]++;
        }
    }

    public void reset() {

        for (int i = 0; i < this.idArr.length; i++) {
            idArr[i] = 0;
        }
    }

    public double sum() {
        double v = 0;
        for (int i = 0; i < this.idArr.length; i++) {
            v += idArr[i];
        }
        return v;
    }

    // divison
    public void div(int divider) {
        for (int i = 0; i < this.idArr.length; i++) {
            idArr[i] /= divider;
        }
    }

    // division
    public void div(AggIDCounter divider) {

        if (this.idArr.length != divider.idArr.length) {
            throw new IllegalArgumentException("AggIDCounter sizes are not equal");
        }

        for (int i = 0; i < this.idArr.length; i++) {
            idArr[i] /= divider.idArr[i];
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[");

        for (int i = 0; i < this.idArr.length; i++) {
            s.append(String.format(" %3.6f ", idArr[i]));
        }
        s.append("]");
        return s.toString();
    }

    public void outputToWriter(PrintWriter writer) {
        for (int i = 0; i < this.idArr.length; i++) {

            if (i != 0) {
                writer.print(",");
            }

            writer.printf("%e", idArr[i]);
        }
    }

    public void outputHeaderToWriter(PrintWriter writer, String prefix) {
        for (int i = 0; i < this.idArr.length; i++) {

            if (i != 0) {
                writer.print(",");
            }

            writer.printf("%s%d", prefix, i);
        }
    }
}
