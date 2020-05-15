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

import org.anchoranalysis.bean.annotation.BeanField;

/**
 * Automatically populates a experiment-name and version number
 * @author owen
 *
 */
public class ExperimentIdentifierAuto extends ExperimentIdentifier {

	// START BEAN FIELDS
	/** If there's no task-name, then this constant is used as a fallback name */
	@BeanField
	private String fallbackName = "experiment";
	
	/** If true, the current day (yyyy.MM.dd) is included in the output */
	@BeanField
	private boolean day = true;
	
	/** If true, the current time (HH.mm.ss) is included in the output */
	@BeanField
	private boolean time = true;
	
	/** Only relevant if time==true. Whether the second is included in the time */
	@BeanField
	private boolean second = true;
	// END BEAN FIELDS

	private static String FORMAT_DAY = "yyyy.MM.dd";
	private static String FORMAT_TIME_BASE = "HH.mm";
	private static String FORMAT_TIME_SECOND = "ss";
	
	@Override
	public String identifier(String taskName) {
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
	private String determineExperimentName(String taskName) {
		if (taskName!=null) {
			return removeSpecialChars(
				finalPartOfTaskName(taskName)
			);
		} else {
			return fallbackName;
		}
	}
	
	/**
	 * Calculates the version part of the identifier
	 * 
	 * @return a string describing the version or null if version should be omitted
	 */
	private String determineVersion() {
		DateTimeFormatter dtf = createFormatter();
		if (dtf!=null) {
			return dtf.format( LocalDateTime.now() );
		} else {
			return null;
		}
	}
	
	/**
	 * Creates a date-time formatter
	 * 
	 * @return the formatter or null if neither the day or time should be included
	 */
	private DateTimeFormatter createFormatter() {
		String formatterStr = createFormatterPatternStr();
		if (formatterStr!=null) {
			return DateTimeFormatter.ofPattern(formatterStr);
		} else {
			return null;
		}
	}
	
	private String createFormatterPatternStr() {
		if (day) {
			if (time) {
				return FORMAT_DAY + "." + createPatternStrTime();
			} else {
				return FORMAT_DAY;
			}
		} else if (time) {
			return createPatternStrTime();
		} else {
			return null;
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

	public String getFallbackName() {
		return fallbackName;
	}

	public void setFallbackName(String fallbackName) {
		this.fallbackName = fallbackName;
	}

	public boolean isDay() {
		return day;
	}

	public void setDay(boolean day) {
		this.day = day;
	}

	public boolean isTime() {
		return time;
	}

	public void setTime(boolean time) {
		this.time = time;
	}

	public boolean isSecond() {
		return second;
	}

	public void setSecond(boolean second) {
		this.second = second;
	}


}
