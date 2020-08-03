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
import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.functional.function.CheckedSupplier;
import org.anchoranalysis.core.functional.OptionalUtilities;
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

    private CheckedSupplier<Optional<T>, IOException> operation =
            CachedSupplier.cache( ()->OptionalUtilities.createFromFlagChecked(exists(), this::get) );

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

    public CheckedSupplier<Optional<T>, IOException> operation() {
        return operation;
    }
}
