/*-
 * #%L
 * anchor-bean
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.bean.xml.factory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.xml.BeanXMLLoader;
import org.anchoranalysis.bean.xml.exception.BeanXMLException;
import org.anchoranalysis.core.system.path.ResolvePathAbsolute;
import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.configuration.beanutils.XMLBeanDeclaration;

/**
 * Defines an {@link AnchorBean} in a separate file on the file-system.
 *
 * <p>This file is read from {@code filePath} and loaded and integrated with current bean, as if the
 * XML was contained directly in the object being loaded.
 *
 * <p>By default, the referenced bean should be specified as {@code <config><bean>bla
 * blah</bean></config>} in the included XML.
 *
 * <p>An optional parameter {@code xpath} allows referencing an another element in the XML tree. It
 * defaults to {@code bean}.
 *
 * <p>The {@code config} high-level tag is not considered in the XML tree, and should not be part of
 * {@code xpath}.
 *
 * <p>As an example: {@code <input filePath="inputManager.xml"
 * config-class="org.anchoranalysis.io.bean.input.InputManager" config-factory="include"/> }
 *
 * @author Owen Feehan
 */
public class IncludeBeanFactory extends AnchorBeanFactory {

    // Creates the bean. Checks if already an instance exists.
    @Override
    @SuppressWarnings("rawtypes")
    public synchronized Object createBean(Class beanClass, BeanDeclaration decl, Object param)
            throws Exception {
        XMLBeanDeclaration declaration = (XMLBeanDeclaration) decl;

        Path existingPath = Paths.get((String) param);

        Path totalPath = calculateTotalPath(existingPath, declaration);

        // A check in case its the same file, so we don't want to include it, and get a stack
        //  over flow. There's not much we can do if, X includes Y which includes X
        if (Files.isSameFile(existingPath, totalPath)) {
            throw new BeanXMLException(
                    String.format("Including file would cause overflow: %s", existingPath));
        }

        return BeanXMLLoader.loadBeanLocalized(totalPath, calculateXMLPath(declaration));
    }

    private String calculateXMLPath(XMLBeanDeclaration declaration) {
        String xmlPath = (String) declaration.getBeanProperties().get("xpath");

        if (xmlPath != null) {
            return xmlPath;
        } else {
            return "bean";
        }
    }

    private Path calculateTotalPath(Path existingPath, XMLBeanDeclaration declaration) {
        String filePathStr = (String) declaration.getBeanProperties().get("filePath");
        Path filePath = Paths.get(filePathStr);

        return ResolvePathAbsolute.resolve(existingPath, filePath);
    }
}
