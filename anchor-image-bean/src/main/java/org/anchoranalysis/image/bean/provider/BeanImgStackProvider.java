/* (C)2020 */
package org.anchoranalysis.image.bean.provider;

import org.anchoranalysis.bean.Provider;
import org.anchoranalysis.image.bean.ImageBean;

/**
 * A provider base-class that as well as providing type S, also provide a stack
 *
 * @param <T> family-type common base-class for all beans in this category
 * @param <S> provider-type what is provided
 */
public abstract class BeanImgStackProvider<T, S> extends ImageBean<T>
        implements ProviderImgStack, Provider<S> {}
