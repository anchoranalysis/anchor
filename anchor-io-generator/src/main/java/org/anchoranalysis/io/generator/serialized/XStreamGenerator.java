/* (C)2020 */
package org.anchoranalysis.io.generator.serialized;

import com.thoughtworks.xstream.XStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class XStreamGenerator<T> extends SerializedIterableGenerator<T> {

    public XStreamGenerator(Optional<String> manifestFunction) {
        super(manifestFunction);
    }

    public XStreamGenerator(T rootObject, Optional<String> manifestFunction) {
        super(rootObject, manifestFunction);
    }

    public static <T> void writeObjectToFile(T rootObject, Path filePath) throws IOException {
        XStream xstream = new XStream();

        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            try (PrintWriter pw = new PrintWriter(fos)) {
                String xml = xstream.toXML(rootObject);
                pw.write(xml);
            }
        }
    }

    @Override
    protected void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath, T element)
            throws OutputWriteFailedException {
        try {
            writeObjectToFile(getIterableElement(), filePath);
        } catch (IOException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    protected String extensionSuffix(OutputWriteSettings outputWriteSettings) {
        return "." + outputWriteSettings.getExtensionXML();
    }
}
