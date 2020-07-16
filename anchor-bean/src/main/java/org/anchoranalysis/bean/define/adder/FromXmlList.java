/* (C)2020 */
package org.anchoranalysis.bean.define.adder;

import java.nio.file.Path;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
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
    @BeanField @Getter @Setter private String name;

    /**
     * If TRUE, a prefix is prepended to the name of each added bean. The prefix is: the name
     * followed by a full-stop.
     */
    @BeanField @Getter @Setter private boolean prefix = false;
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

    private String nameWithExtension() {
        return name + ".xml";
    }
}
