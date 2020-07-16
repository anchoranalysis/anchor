/* (C)2020 */
package org.anchoranalysis.io.manifest.finder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.cache.WrapOperationAsCached;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.io.bean.deserializer.Deserializer;
import org.anchoranalysis.io.bean.deserializer.KeyValueParamsDeserializer;
import org.anchoranalysis.io.bean.deserializer.ObjectInputStreamDeserializer;
import org.anchoranalysis.io.bean.deserializer.XStreamDeserializer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.match.helper.filewrite.FileWriteFileFunctionType;

/**
 * @author Owen Feehan
 * @param <T> object-type
 */
public class FinderSerializedObject<T> extends FinderSingleFile {

    private Optional<T> deserializedObject = Optional.empty();
    private String function;

    private Operation<Optional<T>, IOException> operation =
            new WrapOperationAsCached<>(
                    () -> {
                        if (!exists()) {
                            return Optional.empty();
                        }
                        return Optional.of(get());
                    });

    public FinderSerializedObject(String function, ErrorReporter errorReporter) {
        super(errorReporter);
        this.function = function;
    }

    private T deserialize(FileWrite fileWrite) throws DeserializationFailedException {

        Deserializer<T> deserializer;
        if (fileWrite.getFileName().toLowerCase().endsWith(".properties.xml")) {
            deserializer = new KeyValueParamsDeserializer<>();
        } else if (fileWrite.getFileName().toLowerCase().endsWith(".xml")) {
            deserializer = new XStreamDeserializer<>();
        } else {
            deserializer = new ObjectInputStreamDeserializer<>();
        }

        return deserializer.deserialize(fileWrite.calcPath());
    }

    public T get() throws IOException {
        assert (exists());
        if (!deserializedObject.isPresent()) {
            try {
                deserializedObject = Optional.of(deserialize(getFoundFile()));
            } catch (DeserializationFailedException e) {
                throw new IOException(e);
            }
        }
        return deserializedObject.get();
    }

    @Override
    protected Optional<FileWrite> findFile(ManifestRecorder manifestRecorder)
            throws MultipleFilesException {
        List<FileWrite> files =
                FinderUtilities.findListFile(
                        manifestRecorder, new FileWriteFileFunctionType(function, "serialized"));

        if (files.isEmpty()) {
            return Optional.empty();
        }

        // We prioritise .ser ahead of anything else
        for (FileWrite f : files) {
            if (f.getFileName().endsWith(".ser")) {
                return Optional.of(f);
            }
        }

        return Optional.of(files.get(0));
    }

    public Operation<Optional<T>, IOException> operation() {
        return operation;
    }
}
