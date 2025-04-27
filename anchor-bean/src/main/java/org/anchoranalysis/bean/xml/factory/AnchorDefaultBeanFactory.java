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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.xml.exception.BeanMisconfiguredXMLException;
import org.anchoranalysis.bean.xml.exception.BeanXMLException;
import org.anchoranalysis.bean.xml.exception.FriendlyExceptionCreator;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.configuration.beanutils.BeanFactory;
import org.apache.commons.configuration.beanutils.XMLBeanDeclaration;

/**
 * The default bean factory used for initializing {@link AnchorBean}s.
 *
 * <p>i.e. the factory used when no {@code config-factory} attribute is set in the XML.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class AnchorDefaultBeanFactory implements BeanFactory {

    /** Default bean-classes to use for particular family types of beans. */
    @Getter private BeanInstanceMap defaultInstances;

    @Override
    public Object createBean(Class<?> beanClass, BeanDeclaration data, Object parameter)
            throws Exception {
        try {
            Object result = createBeanInstance(beanClass, data);
            initBeanInstance(result, data, parameter);

            // We check initialization and localize if it's a bean
            if (result instanceof AnchorBean) {
                AnchorBean<?> resultCast = (AnchorBean<?>) result;
                resultCast.checkMisconfigured(defaultInstances);

                Path localPath = Paths.get((String) parameter);
                resultCast.localise(localPath);
            }

            return result;
        } catch (ConfigurationRuntimeException e) {
            // We suppress these exceptions as they are ugly to read, and instead
            //   focus on where in the XML file the error is occurring
            if (data instanceof XMLBeanDeclaration dataCast) {
                throw createMisconfiguredBeanException(e, dataCast);
            }

            String msg =
                    String.format(
                            "A misconfigured bean (%s) exists at unknown location",
                            beanClass.getName());
            throw new BeanXMLException(msg, e.getCause());
        }
    }

    private static BeanMisconfiguredXMLException createMisconfiguredBeanException(
            ConfigurationRuntimeException exception, XMLBeanDeclaration dataCast) {

        // We can read the ClassType from beanClass.getName() but we don't report this to
        //  the user, as we assume it is presented later as part of the trace of the XML
        //  element by DescribeXMLNodeUtilities.describeXMLNode

        String msg =
                String.format(
                        "A misconfigured bean exists%n%s",
                        HelperDescribeXmlNode.describeXMLNode(dataCast.getNode()));
        return new BeanMisconfiguredXMLException(msg, maybeRepaceException(exception));
    }

    /**
     * In certain cases we display a more simplified or alternative exception as the cause
     *
     * @param exc exception to consider replacing
     * @return either the same exception passed in or a meaingful replacement
     */
    private static Throwable maybeRepaceException(ConfigurationRuntimeException exc) {

        if (isListMissingFactory(exc.getCause())) {
            return new BeanXMLException(
                    "A list declaration in BeanXML is missing its factory. Please add config-factory=\"list\"");
        }

        return FriendlyExceptionCreator.maybeCreateUserFriendlyException(exc);
    }

    /** Returns null as {@link Optional} is not supported by {@link BeanFactory} interface. */
    @Override
    public Class<?> getDefaultBeanClass() {
        return null;
    }

    private Object createBeanInstance(Class<?> beanClass, BeanDeclaration data) // NOSONAR
            throws Exception // NOSONAR
            {
        return beanClass.getConstructor().newInstance();
    }

    private void initBeanInstance(Object bean, BeanDeclaration data, Object parameter)
            throws Exception // NOSONAR
            {
        DefaultBeanFactoryHelperInit.initBean(bean, data, parameter);
    }

    private static boolean isListMissingFactory(Throwable exc) {
        return (exc instanceof NoSuchMethodException
                && exc.getMessage().equals("java.util.List.<init>()"));
    }
}
