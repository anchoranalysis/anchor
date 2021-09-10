package org.anchoranalysis.bean.xml;

import java.nio.file.Path;
import java.util.Optional;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.xml.exception.BeanXmlException;
import org.anchoranalysis.core.format.NonImageFileFormat;
import org.anchoranalysis.test.TestLoader;

/**
 * Loads a bean from a file in the resources directory.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class LoadFromResources {

    /** The base parent directory in which the XML files are located, possibly in a subdirectory. */
    public static final String RESOURCES_DIRECTORY = "org.anchoranalysis.bean.xml";

    private TestLoader loader = TestLoader.createFromMavenWorkingDirectory();

    /**
     * If defined, the name of a subdirectory relatively to {@link #RESOURCES_DIRECTORY} where the
     * XML files are located.
     */
    private Optional<String> subdirectory = Optional.empty();

    /**
     * Create to load from a particular subdirectory of {@link #RESOURCES_DIRECTORY}.
     *
     * @param subdirectory the name (or path) of the subdirectory relative to {@link
     *     #RESOURCES_DIRECTORY}.
     */
    public LoadFromResources(String subdirectory) {
        this.subdirectory = Optional.of(subdirectory);
    }

    /**
     * Loads a Bean from XML file stored in the resources.
     *
     * @param <T> the type of bean expected to be loaded
     * @param fileIdentifier the filename (without the XML extension) to be loaded from the
     *     resources directory.
     * @return the loaded bean.
     * @throws BeanXmlException
     */
    public <T> T loadBean(String fileIdentifier) throws BeanXmlException {
        RegisterBeanFactories.registerAllPackageBeanFactories();

        Path path = NonImageFileFormat.XML.buildPath(directory(), fileIdentifier);
        T bean = BeanXMLLoader.loadBean(path);
        return bean;
    }

    private Path directory() {
        if (subdirectory.isPresent()) {
            return loader.resolveTestPath(RESOURCES_DIRECTORY + "/" + subdirectory.get());
        } else {
            return loader.resolveTestPath(RESOURCES_DIRECTORY);
        }
    }
}
