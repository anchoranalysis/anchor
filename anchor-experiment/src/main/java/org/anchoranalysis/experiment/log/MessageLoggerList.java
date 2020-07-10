package org.anchoranalysis.experiment.log;

/*
 * #%L
 * anchor-core
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


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.experiment.log.reporter.StatefulMessageLogger;

public class MessageLoggerList implements StatefulMessageLogger {

	private final List<StatefulMessageLogger> list;
	
	public MessageLoggerList(Stream<StatefulMessageLogger> stream) {
		list = stream.collect(Collectors.toList());
	}
	
	@Override
	public void start() {
		for (StatefulMessageLogger logger : list) {
			assert logger!=null;
			logger.start();
		}
		
	}

	@Override
	public void log(String message) {
		for (StatefulMessageLogger logger : list) {
			assert logger!=null;
			logger.log(message);
		}
	}

	@Override
	public void close(boolean successful) {
		for (StatefulMessageLogger logger : list) {
			logger.close(successful);
		}
	}

	@Override
	public void logFormatted(String formatString, Object... args) {
		for (MessageLogger logger : list) {
			logger.logFormatted(formatString, args);
		}
		
	}

	public boolean add(StatefulMessageLogger arg0) {
		return list.add(arg0);
	}

	public List<StatefulMessageLogger> getList() {
		return list;
	}
	
}