/* (C)2020 */
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
     * if TRUE, the root is not added to the outFilePath, and the path is instead localized against
     * the location of the BeanXML. if FALSE, nothing is changed
     */
    @BeanField @Getter @Setter private boolean suppressRootOut = false;

    /** if TRUE, the pathIn and pathOut are logged. Useful for debugging */
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

        Path pathOut = item.outFilePath(pathInWithoutRoot.getPath(), debugMode);

        if (suppressRootOut) {
            pathOut = BeanPathUtilities.combine(getLocalPath(), pathOut);
        } else {
            pathOut = pathInWithoutRoot.getRoot().resolve(pathOut);
        }

        return pathOut;
    }
}
