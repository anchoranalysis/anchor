package org.anchoranalysis.io.manifest.serializer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import org.anchoranalysis.io.manifest.deserializer.XStreamDeserializer;
import com.thoughtworks.xstream.XStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Serializes an object using the <a href="https://x-stream.github.io/">XStream library</a>.
 * 
 * @see XStreamDeserializer for the counterpart.
 * 
 * @author Owen Feehan
  */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class XStreamSerializer {
    
    /**
     * Writes a file with a serialized representation of an object.
     * 
     * @param <T> object-type
     * @param object the object to serialize
     * @param path path to write the object to
     * @throws IOException if the file cannot be written
     */
    public static <T> void serializeObjectToFile(T object, Path path) throws IOException {
        XStream xstream = new XStream();

        try (FileOutputStream outputStream = new FileOutputStream(path.toFile())) {
            try (PrintWriter printWriter = new PrintWriter(outputStream)) {
                printWriter.write(xstream.toXML(object));
            }
        }
    }
}
