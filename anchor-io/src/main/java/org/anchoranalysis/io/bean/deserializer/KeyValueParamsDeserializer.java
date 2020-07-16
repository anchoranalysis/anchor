/* (C)2020 */
package org.anchoranalysis.io.bean.deserializer;

import java.io.IOException;
import java.nio.file.Path;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;

/**
 * @author Owen Feehan
 * @param <T> object-type
 */
public class KeyValueParamsDeserializer<T> implements Deserializer<T> {

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(Path filePath) throws DeserializationFailedException {
        try {
            KeyValueParams obj = KeyValueParams.readFromFile(filePath);
            return (T) obj;

        } catch (IOException e) {
            throw new DeserializationFailedException(e);
        }
    }
}
