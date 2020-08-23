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

import java.util.Arrays;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.StringBeanCollection;
import org.anchoranalysis.bean.xml.error.BeanXmlException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.configuration.beanutils.BeanHelper;
import org.apache.commons.configuration.beanutils.XMLBeanDeclaration;
import org.apache.commons.configuration.tree.ConfigurationNode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class HelperUtilities {

    public static <T> T createBeanFromXML(
            XMLBeanDeclaration declXML, String configurationKey, Object param) {
        HierarchicalConfiguration itemConfig =
                declXML.getConfiguration().configurationAt(configurationKey);
        T bean = HelperUtilities.createBeanFromConfig(itemConfig, param); // NOSONAR
        return bean;
    }

    /*** Creates a bean from a HierarchicalConfiguration */
    public static <T> T createBeanFromConfig(HierarchicalConfiguration config, Object param) {
        BeanDeclaration subDecl = new XMLBeanDeclaration(config, ".");

        @SuppressWarnings("unchecked")
        T bean = (T) BeanHelper.createBean(subDecl, null, param);

        return bean;
    }

    /**
     * Populates a string-collection from an XML bean from two sources: a "items" attribute an any
     * "item" element
     *
     * <p>The items attribute can be a comma seperated string.
     *
     * @param collectionToPopulate the collection to populate
     * @param declaration the top-most XML bean declaration
     * @return {@code collectionToPopulate}
     */
    public static StringBeanCollection populateStringCollection(
            StringBeanCollection collectionToPopulate, BeanDeclaration declaration) {
        XMLBeanDeclaration delcarationCast = (XMLBeanDeclaration) declaration;
        populateStringCollectionFromElements(collectionToPopulate, delcarationCast);
        return populateStringCollectionFromAttribute(collectionToPopulate, delcarationCast);
    }

    /**
     * Populates a string-collection from an XML bean that any "item" elements
     *
     * @param collectionToPopulate the collection to populate
     * @param declaration the top-most XML bean declaration
     * @return {@code collectionToPopulate}
     */
    private static StringBeanCollection populateStringCollectionFromElements(
            StringBeanCollection collectionToPopulate, XMLBeanDeclaration declaration) {
        return populateStringCollection(
                collectionToPopulate,
                declaration,
                decl -> decl.getConfiguration().getStringArray("item"));
    }

    /**
     * Populates a string-collection from an XML bean that has a "items" attribute that can be split
     * by commas
     *
     * <p>e.g. value could be a comma seperated list of strings
     *
     * @param collectionToPopulate the collection to populate
     * @param declaration the top-most XML bean declaration
     * @return {@code collectionToPopulate}
     * @throws BeanXmlException
     */
    private static StringBeanCollection populateStringCollectionFromAttribute(
            StringBeanCollection collectionToPopulate, XMLBeanDeclaration declaration) {
        return populateStringCollection(
                collectionToPopulate,
                declaration,
                xmlDeclaration -> separatedValuesFromAttribute(xmlDeclaration, "items"));
    }

    private static StringBeanCollection populateStringCollection(
            StringBeanCollection collectionToPopulate,
            XMLBeanDeclaration declaration,
            Function<XMLBeanDeclaration, String[]> extractItems) {
        String[] items = extractItems.apply(declaration);
        Arrays.stream(items).forEach(collectionToPopulate::add);
        return collectionToPopulate;
    }

    /**
     * Extracts all values matching a top-level attribute-name (whose value is split into multiple
     * entities if comma separated)
     *
     * @param declaration xml-declaration
     * @param attributeName name of top-level attribute
     * @return an array with each part of the attribute-value (multiple parts occuring if separated
     *     by commas)
     */
    private static String[] separatedValuesFromAttribute(
            XMLBeanDeclaration declaration, String attributeName) {
        return declaration.getNode().getAttributes().stream()
                .filter(node -> node.getName().contentEquals(attributeName))
                .map(ConfigurationNode::getValue)
                .toArray(size -> new String[size]);
    }
}
