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
import java.util.Iterator;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.DefaultInstance;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.input.ProvidesStackInput;
import org.anchoranalysis.io.bean.filepath.generator.FilePathGenerator;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.mpp.io.input.MultiInput;

// An input stack
@NoArgsConstructor
public class MultiInputManager extends MultiInputManagerBase {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String inputName = MultiInput.DEFAULT_IMAGE_INPUT_NAME;

    @BeanField @Getter @Setter private InputManager<? extends ProvidesStackInput> input;

    @BeanField @DefaultInstance @Getter @Setter
    private RasterReader rasterReader; // For reading appended files

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<FilePathGenerator>> appendStack = new ArrayList<>();

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<FilePathGenerator>> listAppendMarks = new ArrayList<>();

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<FilePathGenerator>> listAppendMarksFromAnnotation =
            new ArrayList<>(); // Uses both accepted and rejected

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<FilePathGenerator>> listAppendMarksFromAnnotationAcceptedOnly =
            new ArrayList<>(); // Uses both accepted only

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<FilePathGenerator>> listAppendMarksFromAnnotationRejectedOnly =
            new ArrayList<>(); // Uses both accepted rejectedonly

    /** Appends object-collections to the multi-input */
    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<FilePathGenerator>> appendObjects = new ArrayList<>();

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<FilePathGenerator>> listAppendKeyValueParams = new ArrayList<>();

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<FilePathGenerator>> appendHistogram = new ArrayList<>();

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<FilePathGenerator>> listAppendFilePath = new ArrayList<>();
    // END BEAN PROPERTIES

    public MultiInputManager(String inputName, InputManager<? extends ProvidesStackInput> input) {
        this.inputName = inputName;
        this.input = input;
    }

    @Override
    public List<MultiInput> inputObjects(InputManagerParams params) throws AnchorIOException {

        List<MultiInput> outList = new ArrayList<>();

        Iterator<? extends ProvidesStackInput> itr = input.inputObjects(params).iterator();

        while (itr.hasNext()) {
            ProvidesStackInput mainStack = itr.next();

            MultiInput inputObject = new MultiInput(inputName, mainStack);
            appendFromLists(inputObject, params.isDebugModeActivated());

            outList.add(inputObject);
        }

        return outList;
    }

    private void appendFromLists(MultiInput inputObject, boolean doDebug) {
        appendStack(appendStack, inputObject, doDebug, rasterReader);
        appendFromVariousMarksSources(inputObject, doDebug);
        appendObjects(appendObjects, inputObject, doDebug);
        appendKeyValueParams(listAppendKeyValueParams, inputObject, doDebug);
        appendHistogram(appendHistogram, inputObject, doDebug);
        appendFilePath(listAppendFilePath, inputObject, doDebug);
    }

    private void appendFromVariousMarksSources(MultiInput inputObject, boolean doDebug) {
        appendMarks(listAppendMarks, inputObject, doDebug);
        appendMarksFromAnnotation(listAppendMarksFromAnnotation, inputObject, true, true, doDebug);
        appendMarksFromAnnotation(
                listAppendMarksFromAnnotationAcceptedOnly, inputObject, true, false, doDebug);
        appendMarksFromAnnotation(
                listAppendMarksFromAnnotationRejectedOnly, inputObject, false, true, doDebug);
    }
}
