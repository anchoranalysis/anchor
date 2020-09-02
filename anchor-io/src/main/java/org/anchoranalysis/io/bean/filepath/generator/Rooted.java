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

package org.anchoranalysis.io.bean.filepath.generator;

import java.nio.file.Path;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.xml.factory.BeanPathUtilities;
import org.anchoranalysis.io.bean.root.RootPathMap;
import org.anchoranalysis.io.bean.root.SplitPath;
import org.anchoranalysis.io.error.AnchorIOException;

public class Rooted extends FilePathGenerator {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private FilePathGenerator item;

    // The name of the RootPath to associate with this fileset
    @BeanField @Getter @Setter private String rootName;

    /**
     * if true, the root is not added to the outFilePath, and the path is instead localized against
     * the location of the BeanXML. if false, nothing is changed
     */
    @BeanField @Getter @Setter private boolean suppressRootOut = false;

    /** if true, the pathIn and pathOut are logged. Useful for debugging */
    @BeanField @Getter @Setter private boolean logPath = false;
    // END BEAN PROPERTIES

    private Logger logger = Logger.getLogger(Rooted.class.getName());

    @Override
    public Path outFilePath(Path pathIn, boolean debugMode) throws AnchorIOException {

        SplitPath pathInWithoutRoot = RootPathMap.instance().split(pathIn, rootName, debugMode);

        Path pathOut = determinePathOut(pathInWithoutRoot, debugMode);
        if (logPath) {
            logger.info(() -> String.format("pathIn=%s", pathIn));
            logger.info(() -> String.format("pathOut=%s", pathOut));
        }
        return pathOut;
    }

    private Path determinePathOut(SplitPath pathInWithoutRoot, boolean debugMode)
            throws AnchorIOException {

        Path pathOut = item.outFilePath(pathInWithoutRoot.getRemainder(), debugMode);

        if (suppressRootOut) {
            pathOut = BeanPathUtilities.combine(getLocalPath(), pathOut);
        } else {
            pathOut = pathInWithoutRoot.getRoot().resolve(pathOut);
        }

        return pathOut;
    }
}
