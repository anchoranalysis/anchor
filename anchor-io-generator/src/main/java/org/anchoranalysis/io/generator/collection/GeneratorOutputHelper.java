/*-
 * #%L
 * anchor-io-generator
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

package org.anchoranalysis.io.generator.collection;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.combinable.AnchorCombinableException;
import org.anchoranalysis.core.error.friendly.HasFriendlyErrorMessage;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.value.SimpleNameValue;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.sequence.OutputSequenceFactory;
import org.anchoranalysis.io.generator.sequence.OutputSequenceDirectory;
import org.anchoranalysis.io.generator.sequence.OutputSequenceNonIncremental;
import org.anchoranalysis.io.manifest.sequencetype.SetSequenceType;
import org.anchoranalysis.io.namestyle.StringSuffixOutputNameStyle;
import org.anchoranalysis.io.output.enabled.single.SingleLevelOutputEnabled;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.InputOutputContext;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeneratorOutputHelper {

    public static <T> void output(
            NamedProvider<T> providers,
            Generator<T> generator,
            InputOutputContext context,
            String outputName,
            String suffix,
            boolean suppressSubfoldersIn)
            throws OutputWriteFailedException {

        if (!context.getOutputter().outputsEnabled().isOutputEnabled(outputName)) {
            return;
        }

        OutputSequenceDirectory sequenceDirectory = new OutputSequenceDirectory(
            OptionalUtilities.createFromFlag(
                    !suppressSubfoldersIn, outputName),
            createOutputNameStyle(suffix, outputName),
            true,
            Optional.empty()
        ); 

        OutputSequenceNonIncremental<T> writer = new OutputSequenceFactory<>(generator, context).nonIncremental(sequenceDirectory, new SetSequenceType());
        try {
            for (String name : providers.keys()) {
    
                try {
                    writer.add(providers.getException(name), name);
                } catch (NamedProviderGetException e) {
                    throwExceptionInWriter(e, name);
                }
            }
        } finally {
            writer.end();
        }
    }

    public static <T> NamedProvider<T> subset(
            NamedProvider<T> providers, SingleLevelOutputEnabled outputEnabled)
            throws OutputWriteFailedException {

        NameValueSet<T> out = new NameValueSet<>();

        for (String name : providers.keys()) {

            if (outputEnabled.isOutputEnabled(name)) {
                try {
                    out.add(new SimpleNameValue<>(name, providers.getException(name)));
                } catch (NamedProviderGetException e) {
                    throw new OutputWriteFailedException(e.summarize());
                }
            }
        }

        return out;
    }

    private static StringSuffixOutputNameStyle createOutputNameStyle(String prefix, String outputName) {
        StringSuffixOutputNameStyle outputNameStyle =
                new StringSuffixOutputNameStyle(prefix, prefix + "%s");
        outputNameStyle.setOutputName(outputName);
        return outputNameStyle;
    }
        
    private static void throwExceptionInWriter(Exception e, String name)
            throws OutputWriteFailedException {

        String errorMsg = String.format("An error occurred outputting %s", name);

        if (e instanceof HasFriendlyErrorMessage) {
            HasFriendlyErrorMessage eCast = (HasFriendlyErrorMessage) e;
            throw new OutputWriteFailedException(errorMsg, eCast);
        }

        if (e instanceof AnchorCombinableException) {
            AnchorCombinableException eCast = (AnchorCombinableException) e;
            throw new OutputWriteFailedException(errorMsg, eCast);
        }

        throw new OutputWriteFailedException(errorMsg + ":" + e);
    }
}
