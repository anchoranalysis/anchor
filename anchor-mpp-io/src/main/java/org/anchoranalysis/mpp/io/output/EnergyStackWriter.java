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

package org.anchoranalysis.mpp.io.output;

import lombok.AllArgsConstructor;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.io.channel.output.ChannelGenerator;
import org.anchoranalysis.io.generator.sequence.OutputSequenceFactory;
import org.anchoranalysis.io.generator.sequence.pattern.OutputPatternIntegerSuffix;
import org.anchoranalysis.io.generator.serialized.DictionaryGenerator;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.Outputter;

/**
 * Writes an energy-stack to the file-system.
 *
 * <p>The following outputs are produced:
 *
 * <table>
 * <caption></caption>
 * <thead>
 * <tr><th>Output Name</th><th>Default?</th><th>Description</th></tr>
 * </thead>
 * <tbody>
 * <tr><td>{@value #OUTPUT_ENERGY_STACK_DIRECTORY}</td><td>no</td><td>Each channel of the energy-stack as a separate image.</td></tr>
 * <tr><td>{@value #OUTPUT_ENERGY_STACK_DICTIONARY}</td><td>no</td><td>XML serialization of the key-value parameters associated with the energy stack.</td></tr>
 * </tbody>
 * </table>
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class EnergyStackWriter {

    /** The output name for the directory containing energy stack channels. */
    public static final String OUTPUT_ENERGY_STACK_DIRECTORY = "energyStack";

    /** The output name for the energy stack dictionary. */
    private static final String OUTPUT_ENERGY_STACK_DICTIONARY = "energyStackDictionary";

    /** The energy stack to be written. */
    private final EnergyStack energyStack;

    /** The outputter used for writing. */
    private final Outputter outputter;

    /**
     * Writes the energy stack to output.
     *
     * @throws OutputWriteFailedException if the writing process fails
     */
    public void writeEnergyStack() throws OutputWriteFailedException {

        OutputPatternIntegerSuffix directory =
                new OutputPatternIntegerSuffix(
                        OUTPUT_ENERGY_STACK_DIRECTORY, OUTPUT_ENERGY_STACK_DIRECTORY, 2, true);

        createSequenceFactory()
                .incrementingByOneStream(
                        directory,
                        energyStack.withoutParameters().asStack().asListChannels().stream());

        if (energyStack.getParameters() != null) {
            outputter
                    .writerSelective()
                    .write(
                            OUTPUT_ENERGY_STACK_DICTIONARY,
                            DictionaryGenerator::new,
                            energyStack::getParameters);
        }
    }

    /**
     * Creates an output sequence factory for channels.
     *
     * @return a new {@link OutputSequenceFactory} for {@link Channel}s
     */
    private OutputSequenceFactory<Channel> createSequenceFactory() {
        ChannelGenerator generator = new ChannelGenerator();

        // We write the energy-stack separately as individual channels
        return new OutputSequenceFactory<>(generator, outputter.getChecked());
    }
}