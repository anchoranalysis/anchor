/* (C)2020 */
package org.anchoranalysis.io.bean.deserializer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;
import java.nio.file.Path;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;

public class XStreamDeserializer<T> implements Deserializer<T> {

    private static final String[] ALLOWED_NAMESPACES =
            new String[] {"org.anchoranalysis.**", "cern.colt.matrix.**"};

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(Path filePath) throws DeserializationFailedException {

        if (!filePath.toFile().exists()) {
            throw new DeserializationFailedException(
                    String.format("File '%s' does not exist", filePath));
        }

        XStream xstream = setupXStream();

        Object o = xstream.fromXML(filePath.toFile());

        return (T) o;
    }

    private XStream setupXStream() {
        XStream xstream = new XStream(new Xpp3Driver());
        XStream.setupDefaultSecurity(xstream); // to be removed after 1.5
        xstream.allowTypesByWildcard(ALLOWED_NAMESPACES);
        return xstream;
    }
}
