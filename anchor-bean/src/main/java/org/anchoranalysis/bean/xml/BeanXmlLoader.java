/* (C)2020 */
package org.anchoranalysis.bean.xml;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.anchoranalysis.bean.error.BeanStrangeException;
import org.anchoranalysis.bean.xml.error.BeanXmlException;
import org.anchoranalysis.bean.xml.error.HelperFriendlyExceptions;
import org.anchoranalysis.bean.xml.error.LocalisedBeanException;
import org.anchoranalysis.core.error.combinable.AnchorCombinableException;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.beanutils.BeanHelper;
import org.apache.commons.configuration.beanutils.XMLBeanDeclaration;

public class BeanXmlLoader {

    private static Logger logger = Logger.getLogger(BeanXmlLoader.class.getName());

    private BeanXmlLoader() {}

    /**
     * Creates a bean by loading an XML description from the filesystem
     *
     * <p>Assumes the bean is embedded in a single element {@code <bean> and </bean>}
     *
     * @param path file-path to the file containing the XML
     * @param <T> bean-type
     * @return an initialized bean
     * @throws BeanXmlException if something goes wrong
     */
    public static <T> T loadBean(Path path) throws BeanXmlException {
        return loadBean(path, "bean");
    }

    /**
     * Creates a bean by loading an XML description from the filesystem
     *
     * <p>Exceptions are summarized and user-friendly
     *
     * @param path file-path to the file containing the XML
     * @param xmlPath xml-path to where the bean is located within the XML
     * @param <T> bean-type
     * @return an initialized bean
     * @throws BeanXmlException if something goes wrong
     */
    public static <T> T loadBean(Path path, String xmlPath) throws BeanXmlException {
        try {
            return loadBeanLocalized(path, xmlPath);
        } catch (LocalisedBeanException e) {
            throw e.summarizeIgnoreIdenticalFilePath(path);
        }
    }

    /**
     * Creates a bean by loading an XML description from the filesystem
     *
     * <p>Does not create summarized and user-friendly exception, but throws lots of
     * LocalisedBeanException exceptions.
     *
     * <p>This should only be called from an IncludeFactory where we deliberately want these
     * LocalisedBeanException exceptions to be thrown, as a way of getting a include-file trace.
     *
     * @param path file-path to the file containing the XML
     * @param xmlPath xml-path to where the bean is located within the XML
     * @param <T> bean-type
     * @return an initialized bean
     * @throws BeanXmlException problem with reading the beanXML from the filesystem
     * @throws LocalisedBeanException problem occurs somewhere processing a configuration
     * @throws IOException some kind of io error
     */
    public static <T> T loadBeanLocalized(Path path, String xmlPath)
            throws BeanXmlException, LocalisedBeanException {
        checkBeansRegistered();

        XMLConfiguration includeXML;
        try {
            includeXML = HelperReadXml.readBeanXMLFromFilesystem(path);
        } catch (BeanXmlException e) {
            throw new LocalisedBeanException(path.toString(), e);
        }

        try {
            return createFromXMLConfigurationLocalised(includeXML, xmlPath, path);
        } catch (IllegalArgumentException e) {
            throw convertIllegalArgumentException(e, xmlPath);
        }
    }

    /**
     * Creates a bean by loading an XML description from the filesystem
     *
     * <p>Additionally associated the XmlConfiguration with the created Bean
     *
     * @param path file-path to the file containing the XML
     * @param xmlPath xml-path to where the bean is located within the XML
     * @param <T> bean-type
     * @return an initialized bean
     * @throws BeanXmlException if something goes wrong
     */
    public static <T extends IAssociateXmlUponLoad> T loadBeanAssociatedXml(
            Path path, String xmlPath) throws BeanXmlException {
        try {
            return loadBeanAssociatedXmlLocalized(path, xmlPath);
        } catch (LocalisedBeanException e) {
            throw e.summarizeIgnoreIdenticalFilePath(path);
        }
    }

    /**
     * Creates a bean by loading an XML description from the filesystem
     *
     * <p>Does not create summarized and user-friendly exception, but throws lots of
     * LocalisedBeanException exceptions.
     *
     * <p>This does function simply does the hard work for loadBeanAssociatedXml()
     *
     * <p>We keep the functions seperated, so as to mirror the division between loadBean() and
     * loadBeanLocalized()
     *
     * @param path file-path to the file containing the XML
     * @param xmlPath xml-path to where the bean is located within the XML
     * @param <T> bean-type
     */
    private static <T extends IAssociateXmlUponLoad> T loadBeanAssociatedXmlLocalized(
            Path path, String xmlPath) throws BeanXmlException, LocalisedBeanException {
        checkBeansRegistered();
        try {
            XMLConfiguration configXML = HelperReadXml.readBeanXMLFromFilesystem(path);

            T loadedBean = createFromXMLConfigurationLocalised(configXML, xmlPath, path);
            loadedBean.associateXml(configXML);
            return loadedBean;

        } catch (IllegalArgumentException e) {
            throw convertIllegalArgumentException(e, xmlPath);
        }
    }

    private static BeanXmlException convertIllegalArgumentException(
            IllegalArgumentException e, String xmlPath) {
        // We catch a particular message when the xpath fails
        if (e.getMessage().contains("Passed in key must select exactly one node")) {
            // We give a shorted error message, and suppress the original exception
            return new BeanXmlException(
                    String.format(
                            "An expected XML node could not be found: <%s/> or <%s></%s>",
                            xmlPath, xmlPath, xmlPath));
        } else {
            return new BeanXmlException(e);
        }
    }

    /**
     * Creates the bean from some XML
     *
     * @param config the config to create from
     * @param xmlPath the path of the bean in the XML file
     * @param currentFilePath the path to the XML-file on the filesystem, this should be an absolute
     *     path
     * @return
     * @throws LocalisedBeanException
     */
    private static <T> T createFromXMLConfigurationLocalised(
            HierarchicalConfiguration config, String xmlPath, Path currentFilePath)
            throws LocalisedBeanException {

        Path currentFilePathAbsolute = currentFilePath.toAbsolutePath();

        try {
            assert currentFilePathAbsolute.isAbsolute();

            // Read xml from file
            return createFromXMLConfiguration(config, xmlPath, currentFilePathAbsolute);
        } catch (ConfigurationRuntimeException e) {

            Throwable cause =
                    HelperFriendlyExceptions.maybeCreateUserFriendlyException(e.getCause());

            // If we can summarise the bean, then we do
            if (cause instanceof AnchorCombinableException) {
                AnchorCombinableException causeCast = (AnchorCombinableException) cause;
                cause = causeCast.summarize();
            }

            logger.log(Level.FINE, "XML Configuration error when loading BeanXML", e);

            throw new LocalisedBeanException(currentFilePathAbsolute.toString(), cause);

        } catch (IllegalArgumentException e) {
            throw new LocalisedBeanException(
                    currentFilePathAbsolute.toString(),
                    convertIllegalArgumentException(e, xmlPath));
        } catch (Exception e) {
            throw new LocalisedBeanException(currentFilePathAbsolute.toString(), e);
        }
    }

    // CurrentFilePath is the file where the xml was retrieved from, allowing us to process relative
    // paths to other files
    @SuppressWarnings("unchecked")
    private static <T> T createFromXMLConfiguration(
            HierarchicalConfiguration config, String xmlPath, Path currentFilePath) {
        assert currentFilePath.isAbsolute();
        return (T)
                BeanHelper.createBean(
                        new XMLBeanDeclaration(config, xmlPath), null, currentFilePath.toString());
    }

    /** Checks that the beans have been registered */
    private static void checkBeansRegistered() {
        if (!RegisterBeanFactories.isCalledRegisterAllPackage()) {
            throw new BeanStrangeException(
                    String.format(
                            "Please call %s.registerAllPackageBeanFactories() before loading any beans",
                            RegisterBeanFactories.class.getSimpleName()));
        }
    }
}
