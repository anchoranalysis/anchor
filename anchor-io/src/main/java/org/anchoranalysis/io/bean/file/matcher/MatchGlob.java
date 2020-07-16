/* (C)2020 */
package org.anchoranalysis.io.bean.file.matcher;

import java.nio.file.Path;
import java.util.function.Predicate;
import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.params.InputContextParams;

public class MatchGlob extends FileMatcher {

    // START BEAN FIELDS
    /**
     * The string describing a glob e.g. "*.jpg". If empty, then the inputFilterGlob from
     * inputContext is used
     */
    @BeanField @AllowEmpty private String glob = "";
    // END BEAN FIELDS

    public MatchGlob() {}

    public MatchGlob(String glob) {
        this.glob = glob;
    }

    @Override
    protected Predicate<Path> createMatcherFile(Path dir, InputContextParams inputContext) {
        return PathMatcherUtilities.filter(dir, "glob", globString(inputContext));
    }

    private String globString(InputContextParams inputContext) {
        if (!glob.isEmpty()) {
            return glob;
        } else {
            return inputContext.getInputFilterGlob();
        }
    }

    public String getGlob() {
        return glob;
    }

    public void setGlob(String glob) {
        this.glob = glob;
    }
}
