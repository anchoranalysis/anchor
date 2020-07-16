/* (C)2020 */
package org.anchoranalysis.io.bean.deserializer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;

public class ObjectInputStreamDeserializer<T> implements Deserializer<T> {

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(Path filePath) throws DeserializationFailedException {

        if (!filePath.toFile().exists()) {
            throw new DeserializationFailedException(
                    String.format("File '%s' does not exist", filePath));
        }

        try {
            // Deserialize from a file
            try (ObjectInputStream in = createInputStream(filePath)) {

                Object objOutUncast = in.readObject();

                if (objOutUncast == null) {
                    throw new DeserializationFailedException(
                            "Deserialization failed for unknown reasons");
                }

                return (T) objOutUncast;
            }

        } catch (ClassNotFoundException | IOException e) {
            throw new DeserializationFailedException(e);
        }
    }

    private static ObjectInputStream createInputStream(Path filePath) throws IOException {
        return new ObjectInputStream(new FileInputStream(filePath.toFile()));
    }
}
