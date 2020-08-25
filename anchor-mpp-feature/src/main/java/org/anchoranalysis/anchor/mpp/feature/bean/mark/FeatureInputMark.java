/*-
 * #%L
 * anchor-mpp-feature
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

package org.anchoranalysis.anchor.mpp.feature.bean.mark;

import java.util.Optional;
import lombok.EqualsAndHashCode;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.input.FeatureInputParams;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.extent.Resolution;
import org.anchoranalysis.mpp.mark.Mark;

@EqualsAndHashCode
public class FeatureInputMark implements FeatureInputParams {

    private final Mark mark;
    private final Optional<Dimensions> dimensions;
    private final Optional<KeyValueParams> params;

    public FeatureInputMark(Mark mark, Optional<Dimensions> dimensions) {
        this.mark = mark;
        this.dimensions = dimensions;
        this.params = Optional.empty();
    }

    public FeatureInputMark(Mark mark, Dimensions dimensions, KeyValueParams params) {
        this(mark, Optional.of(dimensions), Optional.of(params));
    }

    public FeatureInputMark(Mark mark, Optional<Dimensions> dim, Optional<KeyValueParams> params) {
        super();
        this.mark = mark;
        this.dimensions = dim;
        this.params = params;
    }

    public Mark getMark() {
        return mark;
    }

    @Override
    public Optional<Resolution> getResolutionOptional() {
        return dimensions.map(Dimensions::resolution);
    }

    @Override
    public Optional<KeyValueParams> getParamsOptional() {
        return params;
    }

    public Optional<Dimensions> getDimensionsOptional() {
        return dimensions;
    }

    public Dimensions getDimensionsRequired() throws FeatureCalculationException {
        return dimensions.orElseThrow(
                () ->
                        new FeatureCalculationException(
                                "Dimensions are required in the input for this operation"));
    }
}
