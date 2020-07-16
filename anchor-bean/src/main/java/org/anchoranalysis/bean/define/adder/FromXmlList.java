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
/* (C)2020 */
package org.anchoranalysis.bean.define.adder;

import java.nio.file.Path;
import java.util.List;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.xml.BeanXmlLoader;
import org.anchoranalysis.bean.xml.error.BeanXmlException;
import org.anchoranalysis.bean.xml.error.LocalisedBeanException;
import org.anchoranalysis.bean.xml.factory.BeanPathUtilities;

/**
 * Adds a list of Named-Items define in a XML file in the current directory
 *
 * @author Owen Feehan
 */
public class FromXmlList extends DefineAdderBean {

    // START BEAN PROPERTIES
    /** The name of the file in the current working directory WITHOUT THE .xml EXTENSION */
    @BeanField private String name;

    /**
     * If TRUE, a prefix is prepended to the name of each added bean. The prefix is: the name
     * followed by a full-stop.
     */
    @BeanField private boolean prefix = false;
    // END BEAN PROPERTIES

    @Override
    public void addTo(Define define) throws BeanXmlException {
        try {
            List<NamedBean<?>> beans = loadList();

            if (prefix) {
                addPrefix(beans, name + ".");
            }

            DefineAdderUtilities.addBeansFromList(define, beans);
        } catch (BeanXmlException e) {
            // We embed any XML exception in the file-name from where it originated
            throw new BeanXmlException(new LocalisedBeanException(resolvedPath().toString(), e));
        }
    }

    private static void addPrefix(List<NamedBean<?>> beans, String prefix) {
        for (NamedBean<?> nb : beans) {
            nb.setName(prefix + nb.getName());
        }
    }

    private Path resolvedPath() {
        return BeanPathUtilities.pathRelativeToBean(this, nameWithExtension());
    }

    private List<NamedBean<?>> loadList() throws BeanXmlException {
        return BeanXmlLoader.loadBean(resolvedPath());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPrefix() {
        return prefix;
    }

    public void setPrefix(boolean prefix) {
        this.prefix = prefix;
    }

    private String nameWithExtension() {
        return name + ".xml";
    }
}
