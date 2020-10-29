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

package org.anchoranalysis.bean;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import javax.annotation.Nullable;
import lombok.Getter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.DefaultInstance;
import org.anchoranalysis.bean.exception.BeanDuplicateException;
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;

/**
 * The base class of all beans used in <a href="http://www.anchoranalysis.org">Anchor</a>.
 *
 * <p>The family-type exists as a templated parameter {@code <F>} to return a sensibly-typed object
 * when {@link #duplicateBean} is called. Typically, this is either the type of the class itself, or
 * an abstract base class representing a family of similar classes. A bean must always be assignable
 * from (i.e. be equal to or inherit from) the family-type it is associated with.
 *
 * @author Owen Feehan
 * @param <F> family-type, the type returned the {@link #duplicateBean} method is called
 */
public abstract class AnchorBean<F> {

    /**
     * Lazy-loading of a list of the fields associated with properties of the bean.
     *
     * <p>We cache this in the class, to avoid having to regenerate it every time an object is
     * duplicated, initialized, or {@link #checkMisconfigured} is called etc.
     */
    private List<Field> listBeanFields;

    /**
     * A local path on the filesystem associated with this bean (from serialization) if defined.
     * Otherwise null.
     */
    @Getter @Nullable private Path localPath;

    /**
     * A short-name identifying a bean (by default the name of the class associated with the bean).
     *
     * @return the short-name of the bean
     */
    public final String getBeanName() {
        return getClass().getSimpleName();
    }

    /**
     * A (maybe longer) description identifying the bean and perhaps its key parameters.
     *
     * <p>By default, it returns the same as {@link #getBeanName} but beans can optionally override
     * it
     *
     * @return either the short-name of the bean, or a longer description
     */
    public String describeBean() {
        return getBeanName();
    }

    /** By default, we use {@link #describeBean} as the string representation of the bean. */
    @Override
    public String toString() {
        return describeBean();
    }

    /**
     * Called once after the bean is created, localising the bean to a path on the filesystem.
     *
     * <p>It is sometimes useful to override this method so as to include other files.
     *
     * @param path a path on the filesystem which is associated with the bean (can be null,
     *     indicating no localization)
     * @throws BeanMisconfiguredException if a relative-path is passed
     */
    //
    public void localise(Path path) throws BeanMisconfiguredException {

        if (path != null && !path.isAbsolute()) {
            throw new BeanMisconfiguredException(
                    String.format("A bean may not be localized with a relative path: %s", path));
        }
        this.localPath = path;
    }

    /**
     * Checks that a bean's properties conform to expectations.
     *
     * @param defaultInstances all available default instances if the {@link DefaultInstance}
     *     annotation is used
     * @throws BeanMisconfiguredException if the bean has not been configured properly as XML
     */
    public void checkMisconfigured(BeanInstanceMap defaultInstances)
            throws BeanMisconfiguredException {
        HelperCheckMisconfigured helper = new HelperCheckMisconfigured(defaultInstances);
        helper.checkMisconfiguredWithFields(this, fields());
    }

    /**
     * Creates a new bean that deep-copies every property value.
     *
     * <p>Any state that is not a {@link BeanField} is ignored.
     *
     * @return the newly created bean
     */
    @SuppressWarnings("unchecked")
    public F duplicateBean() {
        try {
            return (F) HelperDuplication.duplicate(this);
        } catch (BeanDuplicateException e) {
            throw new BeanDuplicateException(
                    String.format("Error occurred while duplicating bean %s%n", getBeanName()), e);
        }
    }

    /**
     * Generates a string describing the children of the current bean.
     *
     * @return a string describing the children.
     */
    protected String describeChildren() {
        return HelperBeanFields.describeChildBeans(this);
    }

    /**
     * Finds all bean-fields that are instances of a certain class.
     *
     * <p>All immediate children are checked, and any items in immediate lists.
     *
     * @param match the class that a field must be assignable from (equal to or inherit from)
     * @param <T> the type of bean returned in the list
     * @return a list of all bean-fields that match the criteria
     * @throws BeanMisconfiguredException if we discover the bean has been misconfigured
     */
    public <T extends AnchorBean<?>> List<T> findFieldsOfClass(Class<?> match)
            throws BeanMisconfiguredException {
        return HelperFindChildren.findChildrenOfClass(this, fields(), match);
    }

    /**
     * A list of <i>all</i> bean-fields that are associated with this bean directly (fields of
     * children are not checked).
     *
     * <p>This operation is cached as the necessary reflection calls are costly.
     *
     * @return a list of bean-fields associated with the current bean
     */
    public List<Field> fields() {
        if (listBeanFields == null) {
            listBeanFields = HelperBeanFields.createListBeanPropertyFields(this.getClass());
        }
        return listBeanFields;
    }
}
