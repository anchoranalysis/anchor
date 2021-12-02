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

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.annotation.io.AnnotationWithStrategy;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.progress.ProgressMultiple;
import org.anchoranalysis.image.io.stack.input.ProvidesStackInput;
import org.anchoranalysis.io.input.InputReadFailedException;
import org.anchoranalysis.io.input.InputsWithDirectory;
import org.anchoranalysis.io.input.bean.InputManager;
import org.anchoranalysis.io.input.bean.InputManagerParameters;

/**
 * An {@link InputManager} that provides annotations corresponding to images.
 *
 * @author Owen Feehan
 * @param <T> the input-type that provides image(s).
 * @param <S> indicates the process of how annotation occurs.
 */
public class AnnotationInputManager<T extends ProvidesStackInput, S extends AnnotatorStrategy>
        extends InputManager<AnnotationWithStrategy<S>> {

    // START BEAN PROPERTIES
    /** The inputs to be annotated. */
    @BeanField @Getter @Setter private InputManager<T> input;

    /** The strategy to be used for annotating. */
    @BeanField @Getter @Setter private S annotatorStrategy;
    // END BEAN PROPERTIES

    @Override
    public InputsWithDirectory<AnnotationWithStrategy<S>> inputs(InputManagerParameters parameters)
            throws InputReadFailedException {

        try (ProgressMultiple progress = new ProgressMultiple(parameters.getProgress(), 2)) {

            InputsWithDirectory<T> inputs = input.inputs(parameters);

            progress.incrementChild();

            InputsWithDirectory<AnnotationWithStrategy<S>> transformed =
                    inputs.map(this::combineInput, progress.trackCurrentChild());

            progress.incrementChild();

            return transformed;
        } catch (OperationFailedException e) {
            throw new InputReadFailedException(e);
        }
    }

    /** Combines an input with the associated strategy. */
    private AnnotationWithStrategy<S> combineInput(T input) throws OperationFailedException {
        return new AnnotationWithStrategy<>(input, annotatorStrategy);
    }
}
