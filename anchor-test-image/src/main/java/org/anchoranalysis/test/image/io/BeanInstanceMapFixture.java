/*-
 * #%L
 * anchor-test-image
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

package org.anchoranalysis.test.image.io;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;
import org.anchoranalysis.bean.xml.RegisterBeanFactories;
import org.anchoranalysis.bean.xml.factory.AnchorDefaultBeanFactory;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.image.bean.interpolator.ImgLib2Linear;
import org.anchoranalysis.image.bean.interpolator.Interpolator;
import org.anchoranalysis.image.io.bean.stack.metadata.reader.FromStackReader;
import org.anchoranalysis.image.io.bean.stack.metadata.reader.ImageMetadataReader;
import org.anchoranalysis.image.io.bean.stack.reader.StackReader;
import org.anchoranalysis.image.io.bean.stack.writer.StackWriter;
import org.anchoranalysis.io.bioformats.ConfigureBioformatsLogging;
import org.anchoranalysis.io.bioformats.bean.BioformatsReader;
import org.anchoranalysis.io.bioformats.bean.options.ForceTimeSeriesToStack;
import org.anchoranalysis.io.bioformats.bean.writer.Tiff;
import org.anchoranalysis.io.imagej.bean.stack.writer.PNG;
import org.apache.commons.configuration.beanutils.BeanHelper;

/**
 * Creates a {@link BeanInstanceMap} with useful entries for testing, and associates it with bean
 * creation.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanInstanceMapFixture {

    static {
        ConfigureBioformatsLogging.instance().makeSureConfigured();
    }

    /**
     * Ensure a {@link StackReader} instance exists in the underlying {@link BeanInstanceMap}.
     *
     * @return the instance, as already exists, or if newly created.
     */
    public static StackReader ensureStackReader() {
        return addOrReplace(StackReader.class, new BioformatsReader(new ForceTimeSeriesToStack()));
    }

    /**
     * Ensure a {@link StackWriter} instance exists in the underlying {@link BeanInstanceMap}.
     *
     * @param tiff if true, TIFFs are created, otherwise PNGs.
     * @return the instance, as already exists, or if newly created.
     */
    public static StackWriter ensureStackWriter(boolean tiff) {
        return addOrReplace(StackWriter.class, tiff ? new Tiff() : new PNG());
    }

    /**
     * Ensure a {@link ImageMetadataReader} instance exists in the underlying {@link
     * BeanInstanceMap}.
     *
     * @return the instance, as already exists, or if newly created.
     */
    public static ImageMetadataReader ensureImageMetadataReader() {
        return addOrReplace(ImageMetadataReader.class, new FromStackReader(ensureStackReader()));
    }

    /**
     * Ensure a {@link Interpolator} instance exists in the underlying {@link BeanInstanceMap}.
     *
     * @return the instance, as already exists, or if newly created.
     */
    public static Interpolator ensureInterpolator() {
        return ensureInterpolator(new ImgLib2Linear());
    }

    /**
     * Ensure a {@link Interpolator} instance exists in the underlying {@link BeanInstanceMap}.
     *
     * @param interpolator the interpolator to add, if necessary.
     * @return the instance, as already exists, or if newly created.
     */
    public static Interpolator ensureInterpolator(Interpolator interpolator) {
        return addOrReplace(Interpolator.class, interpolator);
    }

    /**
     * Checks if a bean has all necessary items, throwing a run-time exception if it does not.
     *
     * @param <T> bean-type
     * @param bean bean to check.
     */
    public static <T extends AnchorBean<?>> T check(T bean) {
        try {
            bean.checkMisconfigured(getOrCreateBeanFactory().getDefaultInstances());
        } catch (BeanMisconfiguredException e) {
            throw new AnchorFriendlyRuntimeException(e);
        }
        return bean;
    }

    /**
     * Get the {@link AnchorDefaultBeanFactory} if it already registers it, otherwise registers it.
     */
    private static AnchorDefaultBeanFactory getOrCreateBeanFactory() {
        if (!RegisterBeanFactories.isCalledRegisterAllPackage()) {
            return RegisterBeanFactories.registerAllPackageBeanFactories();
        } else {
            return (AnchorDefaultBeanFactory) BeanHelper.getDefaultBeanFactory();
        }
    }

    /**
     * Adds a new instance if none exists for {@code cls}, or else replaces the existing instance.
     */
    private static <T> T addOrReplace(Class<T> cls, T instance) {
        getOrCreateBeanFactory().getDefaultInstances().putInstanceFor(cls, instance);
        return instance;
    }
}
