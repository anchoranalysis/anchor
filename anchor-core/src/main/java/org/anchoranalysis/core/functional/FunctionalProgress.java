package org.anchoranalysis.core.functional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.progress.ProgressReporter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** 
 * Utilities for updating a {@link ProgressReporter} in a functional way
 **/
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class FunctionalProgress {

	/**
	 * Maps a list to new list, updating a progress-reporter for every element
	 * 
	 * @param <S> input-type to map
	 * @param <T> output-type of map
	 * @param <E> exception that can be thrown by {@link mapFunction}
	 * @param list the list to map
	 * @param progressReporter the progress-reporter to update
	 * @param mapFunction the function to use for mapping
	 * @return a newly-created list with the result of each mapped item
	 * @throws E if the exception is thrown during mapping 
	 */
	public static <S,T,E extends Exception> List<T> mapList(
		List<S> list,
		ProgressReporter progressReporter,
		FunctionWithException<S,T,E> mapFunction
	) throws E {
		List<T> listOut = new ArrayList<>();
		
		progressReporter.setMin( 0 );
		progressReporter.setMax( list.size() );
		progressReporter.open();
		
		try {
			for(int i=0; i<list.size(); i++) {
				
				S item = list.get(i);
					
				listOut.add(
					mapFunction.apply(item)
				);
				
				progressReporter.update(i+1);
			}
			return listOut;
			
		} finally {
			progressReporter.close();
		}
	}
	
	
	/**
	 * Maps a list to a new list, including only certain items, updating a progress-reporter for every element
	 * 
	 * <p>Items where the mapping returns {@link Optional.empty()} are not included in the outputted list.</p>
	 * 
	 * @param <S> input-type to map
	 * @param <T> output-type of map
	 * @param <E> exception that can be thrown by {@link mapFunction}
	 * @param list the list to map
	 * @param progressReporter the progress-reporter to update
	 * @param mapFunction the function to use for mapping
	 * @return a newly-created list with the result of each mapped item
	 * @throws E if the exception is thrown during mapping 
	 */
	public static <S,T,E extends Exception> List<T> mapListOptional(
		List<S> list,
		ProgressReporter progressReporter,
		FunctionWithException<S,Optional<T>,E> mapFunction
	) throws E {
		List<T> listOut = new ArrayList<>();
		
		progressReporter.setMin( 0 );
		progressReporter.setMax( list.size() );
		progressReporter.open();
		
		try {
			for(int i=0; i<list.size(); i++) {
				
				S item = list.get(i);
				mapFunction.apply(item).ifPresent(listOut::add);
				progressReporter.update(i+1);
			}
			return listOut;
			
		} finally {
			progressReporter.close();
		}
	}
}
