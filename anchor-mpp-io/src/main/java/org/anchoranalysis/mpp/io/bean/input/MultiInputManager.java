/*-
 * #%L
 * anchor-mpp-io
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

package org.anchoranalysis.mpp.io.bean.input;

import static org.anchoranalysis.mpp.io.bean.input.AppendHelper.*;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.image.io.bean.stack.reader.InputManagerWithStackReader;
import org.anchoranalysis.image.io.stack.input.ProvidesStackInput;
import org.anchoranalysis.io.input.InputReadFailedException;
import org.anchoranalysis.io.input.bean.InputManager;
import org.anchoranalysis.io.input.bean.InputManagerParams;
import org.anchoranalysis.io.input.bean.path.DerivePath;
import org.anchoranalysis.mpp.io.input.MultiInput;

// An input stack
@NoArgsConstructor
public class MultiInputManager extends InputManagerWithStackReader<MultiInput> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String inputName = MultiInput.DEFAULT_IMAGE_INPUT_NAME;

    @BeanField @Getter @Setter private InputManager<? extends ProvidesStackInput> input;

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<DerivePath>> appendStack = new ArrayList<>();

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<DerivePath>> appendMarks = new ArrayList<>();

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<DerivePath>> appendMarksFromAnnotation =
            new ArrayList<>(); // Uses both accepted and rejected

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<DerivePath>> appendMarksFromAnnotationAcceptedOnly =
            new ArrayList<>(); // Uses both accepted only

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<DerivePath>> appendMarksFromAnnotationRejectedOnly =
            new ArrayList<>(); // Uses both accepted rejected only

    /** Appends object-collections to the {@link MultiInput}. */
    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<DerivePath>> appendObjects = new ArrayList<>();

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<DerivePath>> appendDictionary = new ArrayList<>();

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<DerivePath>> appendHistogram = new ArrayList<>();

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<DerivePath>> appendFilePath = new ArrayList<>();
    // END BEAN PROPERTIES

    public MultiInputManager(String inputName, InputManager<? extends ProvidesStackInput> input) {
        this.inputName = inputName;
        this.input = input;
    }

    @Override
    public List<MultiInput> inputs(InputManagerParams params) throws InputReadFailedException {
        return FunctionalList.mapToList(
                input.inputs(params),
                mainStack -> {
                    MultiInput inputToAdd = new MultiInput(inputName, mainStack);
                    appendFromLists(inputToAdd, params.isDebugModeActivated());
                    return inputToAdd;
                });
    }

    private void appendFromLists(MultiInput input, boolean doDebug) {
        appendStack(appendStack, input, doDebug, getStackReader());
        appendFromVariousMarksSources(input, doDebug);
        appendObjects(appendObjects, input, doDebug);
        appendDictionary(appendDictionary, input, doDebug);
        appendHistogram(appendHistogram, input, doDebug);
        appendFilePath(appendFilePath, input, doDebug);
    }

    private void appendFromVariousMarksSources(MultiInput input, boolean doDebug) {
        appendMarks(appendMarks, input, doDebug);
        appendMarksFromAnnotation(appendMarksFromAnnotation, input, true, true, doDebug);
        appendMarksFromAnnotation(
                appendMarksFromAnnotationAcceptedOnly, input, true, false, doDebug);
        appendMarksFromAnnotation(
                appendMarksFromAnnotationRejectedOnly, input, false, true, doDebug);
    }
}
