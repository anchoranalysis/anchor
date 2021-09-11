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
package org.anchoranalysis.bean.xml.factory.primitive;

import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.primitive.PrimitiveBeanCollection;
import org.anchoranalysis.bean.xml.exception.BeanXMLException;
import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.configuration.beanutils.XMLBeanDeclaration;
import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * Populates a {@link PrimitiveBeanCollection} with elements from BeanXML.
 *
 * <p>It uses:
 *
 * <ul>
 *   <li>all "item" sub-elements, and
 *   <li>the "items" attribute, split by commas, if it exists
 * </ul>
 *
 * @param <T> the primitive-type in the collection.
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class PopulatePrimitives<T> {

    /** The top-most XML bean declaration. */
    private XMLBeanDeclaration declaration;

    /** The collection to populate. */
    private PrimitiveBeanCollection<T> collectionToPopulate;

    /** Converts the String extracted from the bean to type {@code T}. */
    private Function<String, T> convertToPrimitive;

    /**
     * Creates for a particular XML declaration.
     *
     * @param declaration the declaration of the Bean containing the contents from which we extract
     *     primitives.
     * @param collectionToPopulate the collection to populate.
     * @param convertToPrimitive converts the String extracted from the bean to type {@code T}.
     */
    public PopulatePrimitives(
            BeanDeclaration declaration,
            PrimitiveBeanCollection<T> collectionToPopulate,
            Function<String, T> convertToPrimitive) {
        this.declaration = (XMLBeanDeclaration) declaration;
        this.collectionToPopulate = collectionToPopulate;
        this.convertToPrimitive = convertToPrimitive;
    }

    /**
     * Populates a {@link PrimitiveBeanCollection} from an XML bean from two sources: a "items"
     * attribute an any "item" element.
     *
     * <p>The items attribute can be a comma separated string.
     *
     * @return {@code collectionToPopulate}
     * @throws BeanXMLException if the BeanXML contains content that cannot be formatted to the
     *     desired number.
     */
    public PrimitiveBeanCollection<T> populate() throws BeanXMLException {
        populateFromElements();
        return populateFromAttribute();
    }

    /**
     * Populates from an XML bean from any sub-elements from "item".
     *
     * @return {@code collectionToPopulate}
     * @throws BeanXMLException if the BeanXML contains content that cannot be formatted to the
     *     desired number.
     */
    private PrimitiveBeanCollection<T> populateFromElements() throws BeanXMLException {
        return populateFromExtractedStrings(decl -> decl.getConfiguration().getStringArray("item"));
    }

    /**
     * Populates from an XML bean that has a "items" attribute that can be split by commas.
     *
     * <p>e.g. value could be a comma separated list of strings
     *
     * @return {@code collectionToPopulate}
     * @throws BeanXMLException if the BeanXML contains content that cannot be formatted to the
     *     desired number.
     */
    private PrimitiveBeanCollection<T> populateFromAttribute() throws BeanXMLException {
        return populateFromExtractedStrings(
                xmlDeclaration -> separatedValuesFromAttribute(xmlDeclaration, "items"));
    }

    private PrimitiveBeanCollection<T> populateFromExtractedStrings(
            Function<XMLBeanDeclaration, String[]> extractItems) throws BeanXMLException {
        String[] items = extractItems.apply(declaration);
        for (String item : items) {
            try {
                T converted = convertToPrimitive.apply(item);
                collectionToPopulate.add(converted);
            } catch (Exception e) {
                throw new BeanXMLException(
                        "The BeanXML contains invalid content for the desired primitives.", e);
            }
        }
        return collectionToPopulate;
    }

    /**
     * Extracts all values matching a top-level attribute-name (whose value is split into multiple
     * entities if comma separated).
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
