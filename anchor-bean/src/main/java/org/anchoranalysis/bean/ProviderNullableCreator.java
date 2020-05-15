package org.anchoranalysis.bean;

/*-
 * #%L
 * anchor-bean
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import java.util.Optional;

import org.anchoranalysis.core.error.CreateException;

/**
 * Utility function to create Optional<> from providers, which may be null
 * 
 * @author Owen Feehan
 *
 */
public class ProviderNullableCreator {

	private ProviderNullableCreator() {}
	
	/**
	 * Creates from a provider if non-null
	 * 
	 * @param <T> provider-type
	 * @param provider a provider or null (if it doesn't exist)
	 * @return the result of the create() option if provider is non-NULL, otherwise {@link Optional.empty()}
	 * @throws CreateException
	 */
	public static <T> Optional<T> createOptional( Provider<T> provider ) throws CreateException {
		if (provider!=null) {
			return Optional.of(
				provider.create()
			);
		} else {
			return Optional.empty();
		}
	}
}
