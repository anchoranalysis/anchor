/* (C)2020 */
package org.anchoranalysis.bean.xml.factory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.anchoranalysis.bean.xml.BeanXmlLoader;
import org.anchoranalysis.bean.xml.error.BeanXmlException;
import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.configuration.beanutils.XMLBeanDeclaration;

public class IncludeBeanFactory extends AnchorBeanFactory {

    // Creates the bean. Checks if already an instance exists.
    @Override
    @SuppressWarnings("rawtypes")
    public synchronized Object createBean(Class beanClass, BeanDeclaration decl, Object param)
            throws Exception {
        XMLBeanDeclaration declXML = (XMLBeanDeclaration) decl;

        String filePathStr = (String) declXML.getBeanProperties().get("filePath");
        Path filePath = Paths.get(filePathStr);

        Path exstPath = Paths.get((String) param);

        Path totalPath = BeanPathUtilities.combine(exstPath, filePath);

        String xmlPath = (String) declXML.getBeanProperties().get("xpath");

        if (xmlPath == null) {
            xmlPath = "bean";
        }

        // A check in case its the same file, so we don't want to include it, and get a stack
        //  over flow. There's not much we can do if, X includes Y which includes X
        if (Files.isSameFile(exstPath, totalPath)) {
            throw new BeanXmlException(
                    String.format("Including file would cause overflow: %s", exstPath));
        }

        return BeanXmlLoader.loadBeanLocalized(totalPath, xmlPath);
    }
}
