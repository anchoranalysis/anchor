/*-
 * #%L
 * anchor-io
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

package org.anchoranalysis.io.bean.path.derive;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringJoiner;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.shared.regex.RegEx;
import org.anchoranalysis.core.path.FilePathToUnixStyleConverter;
import org.anchoranalysis.io.exception.DerivePathException;

/**
 * Generates an outstring of the form
 *
 * <p>group1/group2/group3
 *
 * <p>etc. for as many groups as are found in the regular expression
 *
 * @author Owen Feehan
 */
public class JoinRegExGroups extends DerivePath {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private RegEx regEx;
    // END BEAN PROPERTIES

    @Override
    public Path deriveFrom(Path source, boolean debugMode) throws DerivePathException {

        String pathInStr = FilePathToUnixStyleConverter.toStringUnixStyle(source);

        String[] components =
                regEx.match(pathInStr)
                        .orElseThrow(
                                () ->
                                        new DerivePathException(
                                                String.format(
                                                        "RegEx string '%s' does not match '%s'",
                                                        regEx, pathInStr)));

        return Paths.get(createOutString(components));
    }

    // String in the form g1/g2/g3 for group 1, 2, 3 etc.
    private String createOutString(String[] components) {
        StringJoiner sj = new StringJoiner("/");
        for (int i = 0; i < components.length; i++) {
            sj.add(components[i]);
        }
        return sj.toString();
    }
}
