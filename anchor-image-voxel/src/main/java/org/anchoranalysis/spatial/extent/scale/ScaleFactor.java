/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.spatial.extent.scale;

import com.google.common.base.Preconditions;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * What to scale x and y dimensions by
 *
 * <p>This class is <b>immutable</b>.
 *
 * @author Owen Feehan
 */
@Value
@Accessors(fluent = true)
public final class ScaleFactor {

    private final double x;
    private final double y;

    public ScaleFactor(double factor) {
        this(factor, factor);
    }
    
    public ScaleFactor(double x, double y) {
        Preconditions.checkArgument(x>0);
        Preconditions.checkArgument(y>0);
        this.x = x;
        this.y = y;
    }

    public ScaleFactor invert() {
        return new ScaleFactor(1 / x, 1 / y);
    }

    public boolean hasIdenticalXY() {
        return Math.abs(x - y) < 1e-3;
    }

    /** If the scale-factor involves no scaling at all */
    public boolean isNoScale() {
        return x == 1.0 && y == 1.0;
    }

    @Override
    public String toString() {
        return String.format("x=%f\ty=%f\t\tx^-1=%f\ty^-1=%f", x, y, 1 / x, 1 / y);
    }
}
