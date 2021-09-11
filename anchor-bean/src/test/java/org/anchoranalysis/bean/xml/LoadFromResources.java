/*-
 * #%L
 * anchor-bean
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package org.anchoranalysis.bean.xml;

import java.nio.file.Path;
import java.util.Optional;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.xml.exception.BeanXMLException;
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
     * @throws BeanXMLException if the XML describing the bean is not valid.
     */
    public <T> T loadBean(String fileIdentifier) throws BeanXMLException {
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
