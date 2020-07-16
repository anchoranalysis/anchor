/* (C)2020 */
package org.anchoranalysis.annotation.io.bean.strategy;

import java.nio.file.Path;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.annotation.io.bean.background.AnnotationBackgroundDefinition;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.DefaultInstance;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.input.ProvidesStackInput;
import org.anchoranalysis.io.error.AnchorIOException;

public abstract class AnnotatorStrategy extends AnchorBean<AnnotatorStrategy> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private AnnotationBackgroundDefinition background;

    @BeanField @DefaultInstance @Getter @Setter private RasterReader rasterReader;
    // END BEAN PROPERTIES

    public abstract Path annotationPathFor(ProvidesStackInput item) throws AnchorIOException;

    /**
     * Returns a label describing the annotation, or empty() if this makes no sense
     *
     * @throws AnchorIOException
     */
    public abstract Optional<String> annotationLabelFor(ProvidesStackInput item)
            throws AnchorIOException;

    public abstract int weightWidthDescription();
}
