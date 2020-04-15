package org.anchoranalysis.bean.init.property;

import java.util.Optional;
import java.util.function.Function;

/**
 * Extracts on type of parameter from another
 * 
 * @author owen
 *
 * @param <S> source-type
 * @param <T> destination-type
 */
public class ExtractFromParam<S,T> {

	private Class<?> classOfTarget;
	private Function<S,T> funcToExtract;
	private Optional<Class<?>> baseClassOfSource;
	
	/**
	 * Constructor
	 * 
	 * @param classOfTarget the class of the target type
	 * @param funcToExtract a function to extract the target from the source
	 */
	public ExtractFromParam(Class<?> classOfTarget, Function<S, T> funcToExtract) {
		this.classOfTarget = classOfTarget;
		this.funcToExtract = funcToExtract;
		this.baseClassOfSource = Optional.empty();
	}
	
	/**
	 * Constructor
	 * 
	 * @param classOfTarget the class of the target type
	 * @param funcToExtract a function to extract the target from the source
	 * @param baseClassOfSource a class the source-param must be assignable to. It will be checked by reflection.
	 */
	public ExtractFromParam(Class<?> classOfTarget, Function<S, T> funcToExtract, Class<?> baseClassOfSource) {
		this.classOfTarget = classOfTarget;
		this.funcToExtract = funcToExtract;
		this.baseClassOfSource = Optional.of(baseClassOfSource);
	}
	
	public T extract(S params) {
		return funcToExtract.apply(params);
	}
	
	/** Are the source-parameters acceptable for extraction? **/
	public boolean acceptsAsSource(Class<?> classOfSource) {
		
		if (!baseClassOfSource.isPresent()) {
			return true;
		}
		
		return baseClassOfSource.get().isAssignableFrom(
			classOfSource.getClass()
		);
	}

	public Class<?> getClassOfTarget() {
		return classOfTarget;
	}
}
