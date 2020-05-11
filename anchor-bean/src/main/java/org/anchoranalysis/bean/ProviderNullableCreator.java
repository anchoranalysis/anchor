package org.anchoranalysis.bean;

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
