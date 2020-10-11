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
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.shared.regex.RegEx;
import org.anchoranalysis.bean.shared.regex.RegExSimple;
import org.anchoranalysis.io.exception.AnchorIOException;

/**
 * Generates an out string where $digit$ is replaced with the #digit group from a regex
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor @AllArgsConstructor
public class InsertRegExGroups extends DerivePath {

    // START BEAN PROPERTIES
    /**
     * The regular-expression to use for matching groups.
     */
    @BeanField @Getter private RegEx regEx;

    /**
     * Constructs a path as an output, replacing $1, $2 etc. with the respective group from the matched regular-expression.
     */
    @BeanField @Getter @Setter private String outPath = "";
    // END BEAN PROPERTIES

    @Override
    public Path deriveFrom(Path source, boolean debugMode) throws AnchorIOException {

        String[] matches = match(source);

        String outStr = outPath;

        // We loop through each possible group, and replace if its found, counting down, so that we
        // don't mistake 11 for 1 (for example).
        for (int groupIndex = matches.length; groupIndex > 0; groupIndex--) {
            outStr = outStr.replaceAll("\\$" + Integer.toString(groupIndex), matches[groupIndex-1]);
        }

        return Paths.get(outStr);
    }
    
    private String[] match(Path pathIn) throws AnchorIOException {

        String pathInStr = ConvertPathUtilities.convertBackslashes(pathIn);
        
        Optional<String[]> matches = regEx.match(pathInStr);

        if (!matches.isPresent()) {
            throw new AnchorIOException(
                    String.format("RegEx string '%s' does not match '%s'", regEx, pathInStr));
        }

        return matches.get();
    }

    /** The regular-expression to use for matching groups, overloaded to handle legacy situations where a string is supplied. */
    public void setRegExString(String regEx) {
        this.regEx = new RegExSimple(regEx);
    }

    /** The regular-expression to use for matching groups. */
    public void setRegEx(RegEx regEx) {
        this.regEx = regEx;
    }
}
