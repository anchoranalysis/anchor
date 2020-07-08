package org.anchoranalysis.experiment.task.processor;

import java.util.Optional;

/*-
 * #%L
 * anchor-experiment
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.core.log.MessageLogger;
import org.apache.commons.lang3.StringUtils;

/**
 * Logs when jobs start and stop
 * 
 * We keep it thread-safe as there might be multiple calls in parallel
 * 
 * @author Owen Feehan
 *
 */
public class JobStartStopLogger {
	
	// Assume terminal has at least 80 characters in width
	private static final String HASH_SEPERATOR = StringUtils.repeat('#', 80);
	
	private final String jobDscrText;
	private final Optional<MessageLogger> logger;
	private final ConcurrentJobMonitor monitor;
	private final boolean showHashSeperators;
	private final boolean disableLogMessages;
	private final int showOngoingJobsLessThan;
	
	/**
	 * 
	 * @param jobDscrText A noun describing the job that appears in the log e.g. "Job"
	 * @param logger if non-NULL, write messages to logger. If null, no messages are written
	 * @param showHashSeperators indicates if lines of hashes should be placed before and after each log message (adds emphasis)
	 * @param showOngoingJobsLess When the number of ongoing jobs is less than this threshold, they are shown in event logs. 0 disables. 
	 * @param monitor
	 */
	public JobStartStopLogger(String jobDscrText, Optional<MessageLogger> logger, ConcurrentJobMonitor monitor, boolean showHashSeperators, int showOngoingJobsLessThan ) {
		super();
		this.jobDscrText = jobDscrText;
		this.logger = logger;
		this.monitor = monitor;
		this.showHashSeperators = showHashSeperators;
		this.showOngoingJobsLessThan = showOngoingJobsLessThan;

		this.disableLogMessages = monitor.getTotalNumTasks()<=1 || !logger.isPresent();
	}

	public synchronized void logStart(JobDescription job) {
		
		if (disableLogMessages) {
			return;
		}
		logEvent("start", job, "");
	}
	
	public synchronized void logEnd(JobDescription job, JobState jobState, boolean success) {
		
		if (disableLogMessages) {
			return;
		}
		
		logEvent(
			success ? "end  " : "ERROR",
			job,
			timeStr(jobState)
		);
	}
	
	private void logEvent( String eventWord, JobDescription job, String timeStr ) {
		
		if (!logger.isPresent()) {
			return;
		}
		
		logWithDecoration( () ->
			logger.get().logFormatted(
				"%s %4d:\t%s\t[%s]\t%s\t%s  %s",
				jobDscrText,
				job.getJobNumber(),
				eventWord,
				monitor.currentStateDescription(),
				timeStr,
				job.getJobShortName(),
				ongoingJobStr()
			)
		);
	}
	
	private String ongoingJobStr() {
		return showOngoingJobsLessThan > 0 ? monitor.ongoingTasksLessThan(showOngoingJobsLessThan) : "";
	}

	private void logWithDecoration( Runnable logFunc ) {
		logMaybeHashSeperator();
		logFunc.run();
		logMaybeHashSeperator();
	}
	
	private void logMaybeHashSeperator() {
		if (showHashSeperators) {
			logger.get().log(HASH_SEPERATOR);
		}
	}
		
	private static String timeStr( JobState jobState ) {
		return String.format("(%ds)", jobState.getTime()/1000);
	}
}
