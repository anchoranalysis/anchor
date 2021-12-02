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

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.io.channel.output.ChannelGenerator;
import org.anchoranalysis.io.generator.sequence.OutputSequenceFactory;
import org.anchoranalysis.io.generator.sequence.pattern.OutputPatternIntegerSuffix;
import org.anchoranalysis.io.generator.serialized.DictionaryGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.Outputter;

/**
 * Writes an energy-stack.
 *
 * <p>The following outputs are produced:
 *
 * <table>
 * <caption></caption>
 * <thead>
 * <tr><th>Output Name</th><th>Default?</th><th>Description</th></tr>
 * </thead>
 * <tbody>
 * <tr><td>energyStack</td><td>no</td><td>Each channel of the energy-stack as a seperate image.</td></tr>
 * <tr><td>energyStackParameters</td><td>no</td><td>XML serialization of the key-value parameters associated with the energy stack.</td></tr>
 * </tbody>
 * </table>
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class EnergyStackWriter {

    private static final String OUTPUT_ENERGY_STACK_DIRECTORY = "energyStack";
    private static final String OUTPUT_PARAMS = "energyStackParameters";

    private static final String MANIFEST_FUNCTION_CHANNEL = "energyStackChannel";
    private static final String MANIFEST_FUNCTION_PARAMS = "energyStackParameters";

    private final EnergyStack energyStack;
    private final Outputter outputter;

    public void writeEnergyStack() throws OutputWriteFailedException {

        OutputPatternIntegerSuffix directory =
                new OutputPatternIntegerSuffix(
                        OUTPUT_ENERGY_STACK_DIRECTORY,
                        OUTPUT_ENERGY_STACK_DIRECTORY,
                        2,
                        true,
                        Optional.of(
                                new ManifestDescription("raster", OUTPUT_ENERGY_STACK_DIRECTORY)));

        createSequenceFactory()
                .incrementingByOneStream(
                        directory,
                        energyStack.withoutParameters().asStack().asListChannels().stream());

        if (energyStack.getParameters() != null) {
            outputter
                    .writerSelective()
                    .write(
                            OUTPUT_PARAMS,
                            () -> new DictionaryGenerator(MANIFEST_FUNCTION_PARAMS),
                            energyStack::getParameters);
        }
    }

    private OutputSequenceFactory<Channel> createSequenceFactory() {
        ChannelGenerator generator = new ChannelGenerator(MANIFEST_FUNCTION_CHANNEL);

        // We write the energy-stack separately as individual channels
        return new OutputSequenceFactory<>(generator, outputter.getChecked());
    }
}
