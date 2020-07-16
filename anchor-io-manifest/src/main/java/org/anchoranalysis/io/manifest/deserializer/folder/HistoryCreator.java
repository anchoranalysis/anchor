/* (C)2020 */
package org.anchoranalysis.io.manifest.deserializer.folder;

import org.anchoranalysis.io.deserializer.DeserializationFailedException;

public interface HistoryCreator<T> {

    LoadContainer<T> create() throws DeserializationFailedException;
}
