package org.anchoranalysis.experiment.bean.processor;

/*
 * #%L
 * anchor-experiment
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


import java.util.function.DoubleSupplier;

import org.anchoranalysis.core.log.LogReporter;
import org.anchoranalysis.core.text.LanguageUtilities;
import org.anchoranalysis.experiment.task.TaskStatistics;

class StatisticsLogger {

	private static final String JOB_WORD = "job";
	
	private LogReporter logReporter;
		
	public StatisticsLogger(LogReporter logReporter) {
		super();
		this.logReporter = logReporter;
	}

	/**
	 * Logs a textual message describing the statistics
	 * 
	 * @param stats the statistics
	 */
	public void logTextualMessage( TaskStatistics stats ) {
		
		if (stats.isAllSuccessful()) {
			logMessageAboutTasks(
				"All ",
				stats.numCompletedSuccess(),
				" completed successfully.",
				() -> stats.meanExecutionTimeSuccess(),
				true
			);
		} else {
			
			logMessageAboutTasks(
				"",
				stats.numCompletedSuccess(),
				String.format(" out of %d completed successfully.", stats.numTotalScheduledJobs()),
				() -> stats.meanExecutionTimeSuccess(),
				false
			);
			
			logMessageAboutTasks(
				"",
				stats.numCompletedFailed(),
				" failed.",
				() -> stats.meanExecutionTimeFailed(),
				false
			);
			
			long numNotCompleted = stats.numNotCompleted();
			if (numNotCompleted>0) {
				logReporter.logFormatted("%s were never submitted.", maybePluralizeJobs(numNotCompleted) );
			}
		}
	}
	
	/**
	 * Logs a message in the form (treating the + as concatenation, and the %d as a placeholder for number
	 * 
	 *    prefix + %d tasks + suffix + messageAboutAverageExecutionTime
	 * 
	 * The word 'tasks' is smart about pluralization.
	 * 
	 * @param prefix prefix
	 * @param numTasks number of tasks
	 * @param suffix suffix
	 * @param executionTimeMs a function for calculating the mean-execution time
	 * @param alwaysPluralize always pluralize tasks
	 */
	private void logMessageAboutTasks( String prefix, long numTasks, String suffix, DoubleSupplier executionTimeMs, boolean alwaysPluralize ) {
		String tasksDscr = alwaysPluralize ? alwaysPluralizeJobs(numTasks) : maybePluralizeJobs(numTasks); 
		logMessageWithExecutionTime(
			prefix + tasksDscr + suffix,
			numTasks > 0,
			executionTimeMs
		);
		
	}
	
	
	/**
	 * Logs a message with optional additional message about average-execution time
	 *  
	 * @param msg the message to always log
	 * @param showExecutionTime indicates whether to include the optional additional message or not
	 * @param executionTimeMs function for calculating the average-execution time in millisecond
	 */
	private void logMessageWithExecutionTime( String msg, boolean showExecutionTime, DoubleSupplier executionTimeMs ) {
		if (showExecutionTime) {
			double execTimeSeconds = executionTimeMs.getAsDouble() / 1000;
			msg = msg + String.format(" The average execution time was %.3f ms.", execTimeSeconds );
		}
		logReporter.log(msg);
	}
	
	/**
	 * Returns 1 task or n tasks as is appropriate
	 * 
	 * @param number the number n
	 * @return the string as above
	 */
	private static String maybePluralizeJobs( long number ) {
		return LanguageUtilities.maybePluralize(number, JOB_WORD);
	}
	
	/**
	 * Returns 1 task or n tasks as is appropriate
	 * 
	 * @param number the number n
	 * @return the string as above
	 */
	private static String alwaysPluralizeJobs( long number ) {
		return LanguageUtilities.pluralize(number, JOB_WORD);
	}
}
