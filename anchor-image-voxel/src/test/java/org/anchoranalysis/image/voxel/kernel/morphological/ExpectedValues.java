/*-
 * #%L
 * anchor-image-voxel
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.voxel.kernel.morphological;

import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.kernel.OutsideKernelPolicy;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

/** 
 * Values expected for test result given a particular states of {@link KernelApplicationParameters}.
 *
 * @author Owen Feehan
 */
@Value @AllArgsConstructor @Accessors(fluent=true) class ExpectedValues {

    /** With {@link OutsideKernelPolicy#AS_ON} and {@code useZ==true}. */ 
    private final int on3D;

    /** With {@link OutsideKernelPolicy#AS_ON} and {@code useZ==false}. */
    private final int on2D;

    /** Otherwise when {@code useZ==true}. */
    private final int otherwise3D;
    
    /** Otherwise when {@code useZ==false}. */
    private final int otherwise2D;
    
    public ExpectedValues(int on, int otherwise) {
        this(on, on, otherwise);
    }
    
    public ExpectedValues(int on3D, int on2D, int otherwise) {
        this.on3D = on3D;
        this.on2D = on2D;
        this.otherwise2D = otherwise;
        this.otherwise3D = otherwise;
    }
}
