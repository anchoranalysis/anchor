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

package org.anchoranalysis.mpp.bean.mark.factory;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.conic.Ellipsoid;

/**
 * A factory for creating Ellipsoid marks.
 *
 * <p>This class extends MarkFactory, providing a specific implementation for creating Ellipsoid
 * objects in the MPP (Marked Point Process) framework.
 */
public class MarkEllipsoidFactory extends MarkFactory {

    // START BEAN PROPERTIES
    /**
     * The shell thickness of the ellipsoid.
     *
     * <p>This value determines the thickness of the ellipsoid's outer shell. The default value is
     * 0.1.
     */
    @BeanField @Getter @Setter private double shell = 0.1;
    // END BEAN PROPERTIES

    @Override
    public Mark create() {
        Ellipsoid mark = new Ellipsoid();
        mark.setShell(shell);
        return mark;
    }
}
