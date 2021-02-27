/*-
 * #%L
 * anchor-mpp-sgmn
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

package org.anchoranalysis.mpp.segment.bean.define;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.identifier.provider.NamedProvider;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.experiment.io.InitializationContext;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.io.output.enabled.OutputEnabledMutable;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.mpp.bean.init.MarksInitialization;
import org.anchoranalysis.mpp.io.input.InputForMarksBean;
import org.anchoranalysis.mpp.io.input.MarksInitializationFactory;
import org.anchoranalysis.mpp.mark.Mark;

/**
 * After using applying a {@link Define} on inputs, output produced entities (images, histograms,
 * objects etc.)
 *
 * <p>The following outputs are produced:
 *
 * <table>
 * <caption></caption>
 * <thead>
 * <tr><th>Output Name</th><th>Default?</th><th>Description</th></tr>
 * </thead>
 * <tbody>
 * <tr><td>stacks</td><td>yes</td><td>Image-stacks that are produced.</td></tr>
 * <tr><td>objects</td><td>yes</td><td>Collections of {@link ObjectMask}s that are produced as HDF5</td></tr>
 * <tr><td>histograms</td><td>yes</td><td>Histograms that are produced as CSV.</td></tr>
 * <tr><td>marks</td><td>yes</td><td>Collections of {@link Mark}s that are produced as serialized XML.</td></tr>
 * </tbody>
 * </table>
 */
public abstract class DefineOutputter extends AnchorBean<DefineOutputter> {

    // START BEAN PROPERTIES
    @BeanField @OptionalBean @Getter @Setter private Define define = new Define();

    @BeanField @Getter @Setter private boolean suppressSubfolders = false;
    // END BEAN PROPERTIES

    /**
     * Adds all possible output-names to a {@link OutputEnabledMutable}.
     *
     * @param outputEnabled where to add all possible output-names
     */
    public void addAllOutputNamesTo(OutputEnabledMutable outputEnabled) {
        InitializationOutputter.addAllOutputNamesTo(outputEnabled);
    }

    protected MarksInitialization createInitialization(InputForMarksBean input, InitializationContext context)
            throws CreateException {
        return MarksInitializationFactory.create(
                context, Optional.ofNullable(define), Optional.of(input));
    }

    protected MarksInitialization createInitialization(InitializationContext context) throws CreateException {
        return MarksInitializationFactory.create(context, Optional.ofNullable(define), Optional.empty());
    }

    protected MarksInitialization createInitialization(
            InitializationContext context,
            Optional<NamedProvider<Stack>> stacks,
            Optional<NamedProvider<ObjectCollection>> objects,
            Optional<Dictionary> dictionary)
            throws CreateException {
        return MarksInitializationFactory.createFromExistingCollections(
                context, Optional.ofNullable(define), stacks, objects, dictionary);
    }

    protected void outputSharedObjects(MarksInitialization initialization, OutputterChecked outputter)
            throws OutputWriteFailedException {
        new InitializationOutputter(initialization, suppressSubfolders, outputter).output();
    }
}
