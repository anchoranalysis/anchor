/*-
 * #%L
 * anchor-image-bean
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

package org.anchoranalysis.image.bean.nonbean.segment;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.image.bean.segment.binary.BinarySegmentation;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.math.histogram.Histogram;

/** Parameters that are optionally associated with a {@link BinarySegmentation} */
@AllArgsConstructor
public class BinarySegmentationParameters {

    @Getter private final Optional<Histogram> intensityHistogram;
    @Getter private final Optional<Resolution> resolution;

    public BinarySegmentationParameters() {
        this( Optional.empty(), Optional.empty() );
    }

    public BinarySegmentationParameters(Optional<Resolution> resolution) {
        this( Optional.empty(), resolution );
    }

    /**
     * Constructor
     *
     * @param resolution image-resolution
     * @param intensityHistogram a histogram describing the intensity-values of the entire channel
     */
    public BinarySegmentationParameters(
            Resolution resolution, Optional<Histogram> intensityHistogram) {
        this(intensityHistogram, Optional.of(resolution) );
    }
}
