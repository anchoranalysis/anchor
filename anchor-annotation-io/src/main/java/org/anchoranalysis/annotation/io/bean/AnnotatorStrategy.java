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

package org.anchoranalysis.annotation.io.bean;

import java.nio.file.Path;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.DefaultInstance;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.time.OperationContext;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.bean.stack.reader.StackReader;
import org.anchoranalysis.image.io.stack.input.ProvidesStackInput;

/**
 * How an input is associated with annotations on the file-system, and how this is presented
 * visually.
 *
 * <p>This class gives instructions on how to present the annotations process visually.
 *
 * @author Owen Feehan
 */
public abstract class AnnotatorStrategy extends AnchorBean<AnnotatorStrategy> {

    // START BEAN PROPERTIES
    /** The visual background to use while annotating. */
    @BeanField @Getter @Setter private AnnotationBackground background;

    /** How to read raster {@link Stack}s from the file-system. */
    @BeanField @DefaultInstance @Getter @Setter private StackReader stackReader;

    // END BEAN PROPERTIES

    /**
     * The path to where an annotation file for a particular input would be located.
     *
     * <p>If the annotation is stored on multiple files, this refers to the principle file that must
     * be unique.
     *
     * @param input the input to find an annotation file for.
     * @return the path to where the annotation would be expected to be.
     * @throws OperationFailedException if the path cannot be successfully determined.
     */
    public abstract Path pathFor(ProvidesStackInput input) throws OperationFailedException;

    /**
     * A human-friendly textual description of the annotation, or {@link Optional#empty()} if no
     * label is available.
     *
     * @param input the input to find an annotation label for.
     * @param context TODO
     * @return the label, if available.
     * @throws OperationFailedException if a label cannot be successfully determined.
     */
    public abstract Optional<String> annotationLabelFor(
            ProvidesStackInput input, OperationContext context) throws OperationFailedException;

    /**
     * The degree as to how lengthy the labels from {@link #annotationLabelFor(ProvidesStackInput,
     * OperationContext)} can be.
     *
     * <p>The higher the number, the lengthier {@link #annotationLabelFor(ProvidesStackInput,
     * OperationContext)} can be.
     *
     * @return the weight.
     */
    public abstract int weightWidthDescription();
}
