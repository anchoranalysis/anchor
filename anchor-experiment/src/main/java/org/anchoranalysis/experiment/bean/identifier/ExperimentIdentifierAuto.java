package org.anchoranalysis.experiment.bean.identifier;

import java.nio.file.Path;
import java.nio.file.Paths;

/*-
 * #%L
 * anchor-experiment
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.anchoranalysis.bean.annotation.BeanField;

import lombok.Getter;
import lombok.Setter;

/**
 * Automatically populates a experiment-name and version number
 * @author Owen Feehan
 *
 */
public class ExperimentIdentifierAuto extends ExperimentIdentifier {

	// START BEAN FIELDS
	/** If there's no task-name, then this constant is used as a fallback name */
	@BeanField @Getter @Setter
	private String fallbackName = "experiment";
	
	/** If true, the current day (yyyy.MM.dd) is included in the output */
	@BeanField @Getter @Setter
	private boolean day = true;
	
	/** If true, the current time (HH.mm.ss) is included in the output */
	@BeanField @Getter @Setter
	private boolean time = true;
	
	/** Only relevant if time==true. Whether the second is included in the time */
	@BeanField @Getter @Setter
	private boolean second = true;
	// END BEAN FIELDS

	private static final String FORMAT_DAY = "yyyy.MM.dd";
	private static final String FORMAT_TIME_BASE = "HH.mm";
	private static final String FORMAT_TIME_SECOND = "ss";
	
	@Override
	public String identifier(Optional<String> taskName) {
		return IdentifierUtilities.identifierFromNameVersion(
			determineExperimentName(taskName),
			determineVersion()
		);
	}
		
	/**
	 * Calculates the experiment-name part of the identifier
	 * 
	 * @param taskName if non-null a string describing the current task, or null if none exists
	 * @return the experiment-name
	 */
	private String determineExperimentName(Optional<String> taskName) {
		return taskName.map( name->
			removeSpecialChars(
				finalPartOfTaskName(name)
			)
		).orElse(fallbackName);
	}
	
	/**
	 * Calculates the version part of the identifier
	 * 
	 * @return a string describing the version or null if version should be omitted
	 */
	private Optional<String> determineVersion() {
		return createFormatterPatternStr()
			.map(DateTimeFormatter::ofPattern)
			.map( formatter->
				formatter.format( LocalDateTime.now() )
			);
	}
	
	private Optional<String> createFormatterPatternStr() {
		if (day) {
			if (time) {
				return Optional.of(
					FORMAT_DAY + "." + createPatternStrTime()
				);
			} else {
				return Optional.of(FORMAT_DAY);
			}
		} else if (time) {
			return Optional.of(
				createPatternStrTime()
			);
		} else {
			return Optional.empty();
		}
	}
	
	private String createPatternStrTime() {
		if (second) {
			return FORMAT_TIME_BASE + "." + FORMAT_TIME_SECOND;
		} else {
			return FORMAT_TIME_BASE;
		}
	}
	
	private static String finalPartOfTaskName( String taskName ) {
		Path path = Paths.get(taskName);
		return path.getName( path.getNameCount() - 1 ).toString();
	}
	
	private static String removeSpecialChars(String str) {
		return str.replaceAll("[^a-zA-Z]+","");
	}
}
