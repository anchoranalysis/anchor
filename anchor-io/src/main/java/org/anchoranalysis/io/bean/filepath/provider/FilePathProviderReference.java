/* (C)2020 */
package org.anchoranalysis.io.bean.filepath.provider;

import java.nio.file.Path;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;

public class FilePathProviderReference extends FilePathProvider {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String id = "";
    // END BEAN PROPERTIES

    private Path filePath;

    @Override
    public Path create() throws CreateException {
        assert (isInitialized());

        if (filePath == null) {
            try {
                filePath =
                        getInitializationParameters().getNamedFilePathCollection().getException(id);
            } catch (NamedProviderGetException e) {
                throw new CreateException(e);
            }
        }

        return filePath;
    }
}
