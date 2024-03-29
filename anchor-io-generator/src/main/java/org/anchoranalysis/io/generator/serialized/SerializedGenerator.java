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

package org.anchoranalysis.io.generator.serialized;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.format.NonImageFileFormat;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.io.generator.OneStageGenerator;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;

/**
 * A generator that writes <i>binary serialized files</i> to the file-system.
 *
 * <p>These files are in the <a
 * href="https://docs.oracle.com/javase/6/docs/platform/serialization/spec/protocol.html">Java
 * serialization format</a>.
 *
 * @param <T> iteration-type
 */
@AllArgsConstructor
public abstract class SerializedGenerator<T> extends OneStageGenerator<T> {

    @Override
    public String selectFileExtension(OutputWriteSettings settings, Optional<Logger> logger) {
        return NonImageFileFormat.SERIALIZED_BINARY.getDefaultExtension()
                + extensionSuffix(settings);
    }

    /**
     * Appended to the standard "serialized" extension, to form the complete extension.
     *
     * @param outputWriteSettings the associated {@link OutputWriteSettings}.
     * @return the suffix for the extension including any leading period, when appropriate. This may
     *     be the empty string if no suffix exists.
     */
    protected abstract String extensionSuffix(OutputWriteSettings outputWriteSettings);
}
