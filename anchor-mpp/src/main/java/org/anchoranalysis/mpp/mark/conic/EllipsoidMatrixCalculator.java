/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.mark.conic;

import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import java.io.Serializable;

class EllipsoidMatrixCalculator implements Serializable {

    /** */
    private static final long serialVersionUID = -6844865854045587235L;

    private int matNumDim;

    private double radMax;
    private double radMaxSq;

    private DoubleMatrix2D matEll;
    private DoubleMatrix1D matBBox;

    public EllipsoidMatrixCalculator(int matNumDim) {
        super();
        this.matNumDim = matNumDim;

        this.matEll = DoubleFactory2D.dense.make(matNumDim, matNumDim);
        this.matBBox = DoubleFactory1D.dense.make(matNumDim);
    }

    public EllipsoidMatrixCalculator(EllipsoidMatrixCalculator src) {

        this.matNumDim = src.matNumDim;

        this.matEll = src.matEll.copy();
        this.matBBox = src.matBBox.copy();
        this.radMax = src.radMax;
        this.radMaxSq = src.radMaxSq;
    }

    public DoubleMatrix2D getEllipsoidMatrix() {
        return matEll;
    }

    public DoubleMatrix1D getBoundingBoxMatrix() {
        return matBBox;
    }

    public double getMaximumRadius() {
        return radMax;
    }

    public double getMaximumRadiusSquared() {
        return radMaxSq;
    }

    public void update(double[] radiusArray, DoubleMatrix2D matRot) {

        this.radMax = 0;
        for (int i = 0; i < matNumDim; i++) {
            double rad = radiusArray[i];
            assert (rad > 0.0);
            if (rad > radMax) {
                this.radMax = rad;
            }
        }

        this.radMaxSq = Math.pow(this.radMax, 2.0);

        DoubleMatrix2D ellpsd = DoubleFactory2D.dense.make(matNumDim, matNumDim);
        ellpsd.assign(0);
        for (int i = 0; i < matNumDim; i++) {
            ellpsd.setQuick(i, i, Math.pow(radiusArray[i], -2.0));
        }

        DoubleMatrix2D res2 = matRot.zMult(ellpsd, null);
        res2.zMult(matRot.viewDice(), this.matEll);
        assert (!Double.isNaN(matEll.get(0, 0)));

        DoubleMatrix2D matEllInv;
        try {
            matEllInv = new Algebra().inverse(matEll);
        } catch (IllegalArgumentException e) {
            matEllInv = matEll;
        }

        for (int i = 0; i < matNumDim; i++) {
            this.matBBox.set(i, matEllInv.get(i, i));
        }

        // 0.5 is due to pixel shift in how we calculate pixels inside or not
        this.matBBox.assign(val -> Math.sqrt(Math.abs(val)) + 0.5);

        if (this.radMax > 0) {
            assert (!Double.isNaN(matEll.get(0, 0)));
        }
    }
}
