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

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.annotation.io.AnnotationWithStrategy;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.functional.FunctionalProgress;
import org.anchoranalysis.core.progress.Progress;
import org.anchoranalysis.core.progress.ProgressMultiple;
import org.anchoranalysis.core.progress.ProgressOneOfMany;
import org.anchoranalysis.image.io.stack.input.ProvidesStackInput;
import org.anchoranalysis.io.input.InputReadFailedException;
import org.anchoranalysis.io.input.bean.InputManager;
import org.anchoranalysis.io.input.bean.InputManagerParams;

public class AnnotationInputManager<T extends ProvidesStackInput, S extends AnnotatorStrategy>
        extends InputManager<AnnotationWithStrategy<S>> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private InputManager<T> input;

    @BeanField @Getter @Setter private S annotatorStrategy;
    // END BEAN PROPERTIES

    @Override
    public List<AnnotationWithStrategy<S>> inputs(InputManagerParams params)
            throws InputReadFailedException {

        try (ProgressMultiple progressMultiple = new ProgressMultiple(params.getProgress(), 2)) {

            List<T> inputs = input.inputs(params);

            progressMultiple.incrementWorker();

            List<AnnotationWithStrategy<S>> outList =
                    createListInput(inputs, new ProgressOneOfMany(progressMultiple));
            progressMultiple.incrementWorker();

            return outList;
        }
    }

    private List<AnnotationWithStrategy<S>> createListInput(List<T> listInputs, Progress progress)
            throws InputReadFailedException {
        return FunctionalProgress.mapList(
                listInputs,
                progress,
                annotationInput ->
                        new AnnotationWithStrategy<S>(annotationInput, annotatorStrategy));
    }
}
