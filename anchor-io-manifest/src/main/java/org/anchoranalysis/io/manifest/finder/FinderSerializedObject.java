/*-
 * #%L
 * anchor-io-manifest
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

package org.anchoranalysis.io.manifest.finder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import org.anchoranalysis.core.format.NonImageFileFormat;
import org.anchoranalysis.core.functional.OptionalFactory;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.serialize.DeserializationFailedException;
import org.anchoranalysis.core.serialize.Deserializer;
import org.anchoranalysis.core.serialize.DictionaryDeserializer;
import org.anchoranalysis.core.serialize.ObjectInputStreamDeserializer;
import org.anchoranalysis.core.serialize.XStreamDeserializer;
import org.anchoranalysis.io.manifest.Manifest;
import org.anchoranalysis.io.manifest.file.OutputtedFile;
import org.anchoranalysis.io.manifest.finder.match.FileMatch;

/**
 * @author Owen Feehan
 * @param <T> object-type
 */
public class FinderSerializedObject<T> extends FinderSingleFile {

    private final String function;

    private Optional<T> deserializedObject = Optional.empty();

    private Logger logger;

    /** Provides a memoized (cached) means of access the results of the finder */
    @Getter
    private SerializedObjectSupplier<T> memoized =
            SerializedObjectSupplier.cache(
                    () -> OptionalFactory.createChecked(exists(), this::getInternal));

    public FinderSerializedObject(String function, Logger logger) {
        super(logger.errorReporter());
        this.function = function;
        this.logger = logger;
    }

    public T get() throws IOException {
        return memoized.get().get(); // NOSONAR
    }

    private T getInternal() throws IOException {
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
    protected Optional<OutputtedFile> findFile(Manifest manifestRecorder)
            throws FindFailedException {
        List<OutputtedFile> files =
                FinderUtilities.findListFile(
                        manifestRecorder, FileMatch.description(function, "serialized"));

        if (files.isEmpty()) {
            return Optional.empty();
        }

        // We prioritise .ser ahead of anything else
        for (OutputtedFile fileToOutput : files) {
            if (NonImageFileFormat.SERIALIZED_BINARY.matches(fileToOutput.getFileName())) {
                return Optional.of(fileToOutput);
            }
        }

        return Optional.of(files.get(0));
    }

    private T deserialize(OutputtedFile fileWrite) throws DeserializationFailedException {
        return createDeserializer(fileWrite).deserialize(fileWrite.calculatePath(), logger);
    }

    private Deserializer<T> createDeserializer(OutputtedFile fileWrite) {
        if (NonImageFileFormat.PROPERTIES_XML.matches(fileWrite.getFileName())) {
            return new DictionaryDeserializer<>();
        } else if (NonImageFileFormat.XML.matches(fileWrite.getFileName())) {
            return new XStreamDeserializer<>();
        } else {
            return new ObjectInputStreamDeserializer<>();
        }
    }
}
