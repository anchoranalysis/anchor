/*-
 * #%L
 * anchor-plugin-io
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

package org.anchoranalysis.io.output.bean.path.prefixer;

import java.nio.file.Path;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.output.path.prefixer.DirectoryWithPrefix;
import org.anchoranalysis.io.output.path.prefixer.NamedPath;
import org.anchoranalysis.io.output.path.prefixer.PathPrefixerContext;

/**
 * Outputs an incrementing number for each output that occurs.
 *
 * <p>The sequence begins with 0 and increments always by 1.
 *
 * <p>This number is independent of the input file-name and occurs only by whatever sequential order
 * occurs with calls to {@link #outFilePrefixFromPath}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class IncrementingNumber extends PathPrefixerAvoidResolve {

    // TODO this counter should be initialized in a proper way, and not using a bean-wide variable
    private int count = 0;

    // START BEAN PROPERTIES
    /** Number of digits in the number, with leading zeros as necessary. */
    @BeanField @Getter @Setter private int numberDigits = 4;
    // END BEAN PROPERTIES

    /**
     * Creates with a prefix.
     *
     * @param prefix a string prefixed to each output-path.
     */
    public IncrementingNumber(String prefix) {
        super(prefix);
    }

    @Override
    public DirectoryWithPrefix outFilePrefixFromPath(
            NamedPath path, Path root, PathPrefixerContext context) {
        Path combinedDirectory = root.resolve(identifier(count++));
        return new DirectoryWithPrefix(combinedDirectory);
    }

    /** Creates a string identifier with leading zeros for a given integer index. */
    private String identifier(int index) {
        String formatSpecifier = "%0" + numberDigits + "d";
        return String.format(formatSpecifier, index);
    }
}
