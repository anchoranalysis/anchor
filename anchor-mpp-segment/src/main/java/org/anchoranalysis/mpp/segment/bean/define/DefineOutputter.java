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
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.SharedObjects;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.experiment.io.InitializationContext;
import org.anchoranalysis.experiment.task.InputBound;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitialization;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.io.output.enabled.OutputEnabledMutable;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.Outputter;
import org.anchoranalysis.mpp.io.input.ExportSharedObjects;
import org.anchoranalysis.mpp.io.input.MarksInitializationFactory;
import org.anchoranalysis.mpp.io.input.MultiInput;
import org.anchoranalysis.mpp.io.output.EnergyStackWriter;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.segment.define.OutputterDirectories;

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
 * <tr><td>{@value OutputterDirectories#STACKS}</td><td>yes</td><td>Image-stacks that are produced.</td></tr>
 * <tr><td>{@value OutputterDirectories#OBJECTS}</td><td>yes</td><td>Collections of {@link ObjectMask}s that are produced as HDF5</td></tr>
 * <tr><td>{@value OutputterDirectories#HISTOGRAMS}</td><td>yes</td><td>Histograms that are produced as CSV.</td></tr>
 * <tr><td>{@value OutputterDirectories#MARKS}</td><td>yes</td><td>Collections of {@link Mark}s that are produced as serialized XML.</td></tr>
 * </tbody>
 * </table>
 */
public class DefineOutputter extends AnchorBean<DefineOutputter> {

    // START BEAN PROPERTIES
    @BeanField @OptionalBean @Getter @Setter private Define define = new Define();

    @BeanField @Getter @Setter private boolean suppressSubfolders = false;
    // END BEAN PROPERTIES

    @FunctionalInterface
    public interface Processor<T> {
        void process(T initialization) throws OperationFailedException;
    }

    /**
     * Adds all possible output-names to a {@link OutputEnabledMutable}.
     *
     * @param outputEnabled where to add all possible output-names
     */
    public void addAllOutputNamesTo(OutputEnabledMutable outputEnabled) {
        SharedObjectsOutputter.addAllOutputNamesTo(outputEnabled);
    }

    public <S> void process(
            InputBound<MultiInput, S> input, Processor<ImageInitialization> operation)
            throws OperationFailedException {
        InitializationContext context = input.createInitializationContext();
        try {
            ImageInitialization initialization = createInitialization(context, input.getInput());

            operation.process(initialization);

            outputSharedObjects(
                    initialization.sharedObjects(), Optional.empty(), context.getOutputter());

        } catch (CreateException | OutputWriteFailedException e) {
            throw new OperationFailedException(e);
        }
    }

    protected ImageInitialization createInitialization(
            InitializationContext context, ExportSharedObjects input) throws CreateException {
        return MarksInitializationFactory.create(
                        Optional.of(input), context, Optional.ofNullable(define))
                .image();
    }

    protected ImageInitialization createInitialization(
            InitializationContext context,
            Optional<SharedObjects> sharedObjects,
            Optional<Dictionary> dictionary)
            throws CreateException {
        ImageInitialization initialization =
                MarksInitializationFactory.create(
                                Optional.empty(), context, Optional.ofNullable(define))
                        .image();
        try {
            initialization.addSharedObjectsDictionary(sharedObjects, dictionary);
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
        return initialization;
    }

    protected void outputSharedObjects(
            SharedObjects sharedObjects, Optional<EnergyStack> energyStack, Outputter outputter)
            throws OutputWriteFailedException {

        new SharedObjectsOutputter(sharedObjects, suppressSubfolders, outputter.getChecked())
                .output();

        if (energyStack.isPresent()) {
            new EnergyStackWriter(energyStack.get(), outputter).writeEnergyStack();
        }
    }
}
