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

package org.anchoranalysis.bean.define.adder;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.define.DefineAddException;
import org.anchoranalysis.bean.xml.BeanXMLLoader;
import org.anchoranalysis.bean.xml.exception.BeanXMLException;
import org.anchoranalysis.bean.xml.exception.LocalisedBeanException;
import org.anchoranalysis.bean.xml.factory.BeanPathCalculator;
import org.anchoranalysis.core.format.NonImageFileFormat;

/**
 * Adds a list of Named-Items define in a XML file in the current directory.
 *
 * @author Owen Feehan
 */
public class FromXMLList extends DefineAdderBean {

    // START BEAN PROPERTIES
    /** The name of the file in the current working directory WITHOUT THE .xml EXTENSION */
    @BeanField @Getter @Setter private String name;

    /**
     * If true, a prefix is prepended to the name of each added bean. The prefix is: the name
     * followed by a full-stop.
     */
    @BeanField @Getter @Setter private boolean prefix = false;
    // END BEAN PROPERTIES

    @Override
    public void addTo(Define define) throws DefineAddException {
    	
    	Optional<Path> path = resolvedPath();
    	
    	if (!path.isPresent()) {
    		throw new DefineAddException("No path is associated with this bean");
    	}
    	
        try {
            List<NamedBean<AnchorBean<?>>> beans = loadList(path.get());

            if (prefix) {
                addPrefix(beans, name + ".");
            }

            define.addAll(beans);
        } catch (BeanXMLException e) {
        	
            // We embed any XML exception in the file-name from where it originated
            throw new DefineAddException(new LocalisedBeanException(path.get().toString(), e));
        }
    }

    private static void addPrefix(List<NamedBean<AnchorBean<?>>> beans, String prefix) {
        for (NamedBean<?> bean : beans) {
            bean.setName(prefix + bean.getName());
        }
    }

    private Optional<Path> resolvedPath() {
		return BeanPathCalculator.pathFromBean(this, nameWithExtension());
    }

    private List<NamedBean<AnchorBean<?>>> loadList(Path path) throws BeanXMLException {
        return BeanXMLLoader.loadBean(path);
    }

    private String nameWithExtension() {
        return NonImageFileFormat.XML.buildPath(name);
    }
}
