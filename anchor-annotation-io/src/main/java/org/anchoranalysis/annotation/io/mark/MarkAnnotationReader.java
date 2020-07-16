/* (C)2020 */
package org.anchoranalysis.annotation.io.mark;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.annotation.io.AnnotationReader;
import org.anchoranalysis.annotation.mark.MarkAnnotation;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.io.bean.deserializer.XStreamDeserializer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.mpp.io.cfg.CfgDeserializer;

public class MarkAnnotationReader implements AnnotationReader<MarkAnnotation> {

    private boolean acceptUnfinished;

    private static final CfgDeserializer DESERIALIZER = new CfgDeserializer();

    public MarkAnnotationReader(boolean acceptUnfinished) {
        super();
        this.acceptUnfinished = acceptUnfinished;
    }

    public boolean annotationExistsCorrespondTo(Path annotationPath) {
        return fileNameToRead(annotationPath).isPresent();
    }

    @Override
    public Optional<MarkAnnotation> read(Path path) throws AnchorIOException {

        Optional<Path> pathMaybeChanged = fileNameToRead(path);
        try {
            return OptionalUtilities.map(
                    pathMaybeChanged, MarkAnnotationReader::readAnnotationFromPath);
        } catch (DeserializationFailedException e) {
            throw new AnchorIOException("Cannot deserialize annotation", e);
        }
    }

    // Reads an annotation if it can, returns NULL otherwise
    public Cfg readDefaultCfg(Path path) throws DeserializationFailedException {
        return DESERIALIZER.deserialize(path);
    }

    private Optional<Path> fileNameToRead(Path annotationPath) {

        if (annotationPath.toFile().exists()) {
            return Optional.of(annotationPath);
        }

        if (!acceptUnfinished) {
            return Optional.empty();
        }

        Path pathUnfinished = TempPathCreator.deriveTempPath(annotationPath);

        if (pathUnfinished.toFile().exists()) {
            return Optional.of(pathUnfinished);
        }

        // No path to read
        return Optional.empty();
    }

    private static MarkAnnotation readAnnotationFromPath(Path annotationPath)
            throws DeserializationFailedException {
        XStreamDeserializer<MarkAnnotation> deserialized = new XStreamDeserializer<>();
        return deserialized.deserialize(annotationPath);
    }
}
