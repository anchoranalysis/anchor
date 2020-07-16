/*-
 * #%L
 * anchor-annotation-io
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

package org.anchoranalysis.annotation.io.bean.strategy;

import java.nio.file.Path;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.annotation.io.bean.background.AnnotationBackgroundDefinition;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.DefaultInstance;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.input.ProvidesStackInput;
import org.anchoranalysis.io.error.AnchorIOException;

public abstract class AnnotatorStrategy extends AnchorBean<AnnotatorStrategy> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private AnnotationBackgroundDefinition background;

    @BeanField @DefaultInstance @Getter @Setter private RasterReader rasterReader;
    // END BEAN PROPERTIES

    public abstract Path annotationPathFor(ProvidesStackInput item) throws AnchorIOException;

    /**
     * Returns a label describing the annotation, or empty() if this makes no sense
     *
     * @throws AnchorIOException
     */
    public abstract Optional<String> annotationLabelFor(ProvidesStackInput item)
            throws AnchorIOException;

    public abstract int weightWidthDescription();
}
