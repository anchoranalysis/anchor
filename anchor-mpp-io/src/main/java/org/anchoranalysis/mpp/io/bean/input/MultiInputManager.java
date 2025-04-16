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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.image.io.bean.stack.reader.InputManagerWithStackReader;
import org.anchoranalysis.image.io.stack.input.ProvidesStackInput;
import org.anchoranalysis.io.input.InputReadFailedException;
import org.anchoranalysis.io.input.InputsWithDirectory;
import org.anchoranalysis.io.input.bean.InputManager;
import org.anchoranalysis.io.input.bean.InputManagerParameters;
import org.anchoranalysis.io.input.bean.path.DerivePath;
import org.anchoranalysis.mpp.io.input.MultiInput;

/**
 * Manages input for {@link MultiInput} objects, combining various input sources.
 */
@NoArgsConstructor
public class MultiInputManager extends InputManagerWithStackReader<MultiInput> {

    /** The name of the input to be used in the {@link MultiInput}. */
    @BeanField @Getter @Setter private String inputName = MultiInput.DEFAULT_IMAGE_INPUT_NAME;

    /** The input manager for the main stack. */
    @BeanField @Getter @Setter private InputManager<? extends ProvidesStackInput> input;

    /** List of additional stacks to append. */
    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<DerivePath>> appendStack = Arrays.asList();

    /** List of marks to append. */
    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<DerivePath>> appendMarks = Arrays.asList();

    /** List of marks from annotations (both accepted and rejected) to append. */
    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<DerivePath>> appendMarksFromAnnotation = new ArrayList<>();

    /** List of marks from annotations (accepted only) to append. */
    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<DerivePath>> appendMarksFromAnnotationAcceptedOnly = new ArrayList<>();

    /** List of marks from annotations (rejected only) to append. */
    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<DerivePath>> appendMarksFromAnnotationRejectedOnly = new ArrayList<>();

    /** List of object-collections to append to the {@link MultiInput}. */
    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<DerivePath>> appendObjects = new ArrayList<>();

    /** List of dictionaries to append. */
    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<DerivePath>> appendDictionary = new ArrayList<>();

    /** List of histograms to append. */
    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<DerivePath>> appendHistogram = new ArrayList<>();

    /** List of file paths to append. */
    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<DerivePath>> appendFilePath = new ArrayList<>();

    /**
     * Creates a new {@link MultiInputManager} with specified input name and input manager.
     *
     * @param inputName the name of the input
     * @param input the input manager for the main stack
     */
    public MultiInputManager(String inputName, InputManager<? extends ProvidesStackInput> input) {
        this.inputName = inputName;
        this.input = input;
    }

    @Override
    public InputsWithDirectory<MultiInput> inputs(InputManagerParameters parameters)
            throws InputReadFailedException {
        return input.inputs(parameters)
                .map(
                        mainStack -> {
                            MultiInput inputToAdd = new MultiInput(inputName, mainStack);
                            appendFromLists(
                                    new AppendHelper(
                                            inputToAdd,
                                            parameters.isDebugModeActivated(),
                                            parameters.getOperationContext()));
                            return inputToAdd;
                        });
    }

    /**
     * Appends various inputs to the {@link MultiInput} object.
     *
     * @param helper the {@link AppendHelper} to use for appending
     */
    private void appendFromLists(AppendHelper helper) {
        helper.appendStack(appendStack, getStackReader());
        appendFromVariousMarksSources(helper);
        helper.appendObjects(appendObjects);
        helper.appendDictionary(appendDictionary);
        helper.appendHistogram(appendHistogram);
        helper.appendFilePath(appendFilePath);
    }

    /**
     * Appends marks from various sources to the {@link MultiInput} object.
     *
     * @param helper the {@link AppendHelper} to use for appending
     */
    private void appendFromVariousMarksSources(AppendHelper helper) {
        helper.appendMarks(appendMarks);
        helper.appendMarksFromAnnotation(appendMarksFromAnnotation, true, true);
        helper.appendMarksFromAnnotation(appendMarksFromAnnotationAcceptedOnly, true, false);
        helper.appendMarksFromAnnotation(appendMarksFromAnnotationRejectedOnly, false, true);
    }
}