/* (C)2020 */
package org.anchoranalysis.io.bean.deserializer;

import java.nio.file.Path;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;

public interface Deserializer<T> {

    T deserialize(Path filePath) throws DeserializationFailedException;
}
