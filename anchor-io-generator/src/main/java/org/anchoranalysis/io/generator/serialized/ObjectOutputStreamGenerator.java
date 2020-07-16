/* (C)2020 */
package org.anchoranalysis.io.generator.serialized;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class ObjectOutputStreamGenerator<T extends Serializable>
        extends SerializedIterableGenerator<T> {

    public ObjectOutputStreamGenerator(Optional<String> manifestFunction) {
        super(manifestFunction);
    }

    public ObjectOutputStreamGenerator(T rootObject, Optional<String> manifestFunction) {
        super(rootObject, manifestFunction);
    }

    @Override
    protected void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath, T element)
            throws OutputWriteFailedException {

        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {

            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(element);
            out.close();

        } catch (IOException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    protected String extensionSuffix(OutputWriteSettings outputWriteSettings) {
        return "";
    }
}
