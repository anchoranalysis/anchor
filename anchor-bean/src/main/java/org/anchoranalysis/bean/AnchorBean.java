package org.anchoranalysis.bean;

/*
 * #%L
 * anchor-bean
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;

import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.anchoranalysis.bean.annotation.DefaultInstance;
import org.anchoranalysis.bean.error.BeanDuplicateException;

/**
 * The base class of all beans used in Anchor.
 * 
 * The family-type exists as a Template to allow us to return a sensible-typed object when duplicate is
 *   called. It is usually set as the same type of the class itself, or an abstract base type of it (a family
 *   of similar classes).
 *
 * Thus, a bean must always be assignable from (e.g. equal or inherit from) the family it is associated with
 *  
 * @author Owen Feehan
 *
 * @param <F> family-type when the duplicate method is called, what type is returned
 */
public abstract class AnchorBean<F> {

	/**
	 * Lazy-loading of a list of Fields associated with properties of the bean.
	 * 
	 * We cache this in the class, to avoid having to regenerate it every
	 *  time an object is duplicated, init-ed, or checkInitParams() is called etc.
	 */
	private transient List<Field> listBeanFields;


	/**
	 * If non-null, a local path on the file-system associate with this bean (from serialization).
	 * If null, no such local-path has been assigned.
	 */
	private transient Path localPath;

	/**
	 * A short-name identifying a bean (by default the name of the class associated with the bean)
	 * @return the short-name of the bean
	 */
	public final String getBeanName() {
		return getClass().getSimpleName();
	}
	
	/**
	 * A potentially longer description of the bean, identifying the bean and also giving some
	 *  information about its parameterization.
	 *  
	 *  By default, it returns the same as getBeanName() but beans can optionally override it
	 *  
	 * @return either the short-name of the bean, or a longer description
	 */
	public String getBeanDscr() {
		return getBeanName();
	}
	
	/**
	 * By default, we use getBeanDscr() as the string representation of the bean
	 */
	@Override
	public String toString() {
		return getBeanDscr();
	}
	
	/**
	 *  Called once after the bean is created, localising the bean to a path on the filesystem
	 * 
	 * 
	 *  It is sometimes useful to override this method so as to include other files
	 *  
	 * @param path 		a path on the file-system which is associated with the bean (can be null, indicating no localization)
	 * @throws BeanMisconfiguredException if a relative-path is passed
	 */
	// 
	public void localise( Path path ) throws BeanMisconfiguredException {
		
		if( path!=null && !path.isAbsolute() ) {
			throw new BeanMisconfiguredException(
				String.format("A bean may not be localized with a relative path: %s", path)
			);
		}
		this.localPath = path;
	}
	
	/**
	 * Checks that a mark's initial parameters are correct
	 * 
	 * @param defaultInstances all available default instances if the {{@link DefaultInstance} annotation is used
	 * @throws BeanMisconfiguredException if the bean has not been configured properly as XML
	 */
	public void checkMisconfigured( BeanInstanceMap defaultInstances) throws BeanMisconfiguredException {
		HelperCheckMisconfigured helper = new HelperCheckMisconfigured(defaultInstances);
		helper.checkMisconfiguredWithFields(this, getOrCreateBeanFields() );
	}
	
	/**
	 * Creates a new bean, that deep-copies every property value
	 * 
	 * Any state that is not a @BeanField is ignored.
	 * 
	 * @return the newly created bean
	 */
	@SuppressWarnings("unchecked")
	public F duplicateBean() {
		try {
			return (F) HelperDuplication.duplicate(this);
		} catch (BeanDuplicateException e) {
			throw new BeanDuplicateException( String.format("Error occurred while duplicating bean %s%n",getBeanName()), e );
		}
	}

	/**
	 * Retrieves a list of the bean-fields (this is cached to make it quicker necess time as the reflection calls
	 *  are costly)
	 *  
	 * @return a list of bean-fields associated with the current bean
	 */
	public List<Field> getOrCreateBeanFields() {
		if (listBeanFields==null) {
			listBeanFields = HelperBeanFields.createListBeanPropertyFields( this.getClass() );
		}
		return listBeanFields;
	}
	
	/**
	 * Generates a string describing the child-beans of the current bean
	 * 
	 * @return a string describing the child-beans
	 */
	protected String describeChildBeans() {
		return HelperBeanFields.describeChildBeans(this);
	}
	
	/**
	 * Finds all bean-fields that are instance of a certain class.  All immediate children are checked, and any items
	 *   in immediate lists.
	 * 
	 * @param listFields 	the list of fields associated with the bean
	 * @param match 		the class that a field must be assignable from (equal to or inherit from)
	 * @param <T>			the type of bean returned in the list
	 * @return a list of all bean-fields that match the criteria
	 * @throws BeanMisconfiguredException if we discover the bean has been misconfigured 
	 */
	public <T extends AnchorBean<?>> List<T> findChildrenOfClass(
		List<Field> listFields,
		Class<?> match
	) throws BeanMisconfiguredException {
		// TODO return to protected
		return HelperFindChildren.findChildrenOfClass(this, listFields, match);
	}
	
	public Path getLocalPath() {
		return localPath;
	}
}
