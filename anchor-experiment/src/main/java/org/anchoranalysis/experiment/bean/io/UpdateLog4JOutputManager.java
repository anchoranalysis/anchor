package org.anchoranalysis.experiment.bean.io;

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

import java.util.Enumeration;

import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
class UpdateLog4JOutputManager {
	
	@SuppressWarnings("unchecked")	
	public static void updateLog4J( BoundOutputManagerRouteErrors outputManager ) {
		// Bit of reflection trickery involved
		
		Enumeration<Appender> enumApp = LogManager.getRootLogger().getAllAppenders();
		while (enumApp.hasMoreElements()) {
	    
			try {
				
				Appender appender = enumApp.nextElement();
				
				if (appender instanceof Log4JFileAppender) {
					Log4JFileAppender logjApp = (Log4JFileAppender) appender;
										
					String outputName = logjApp.getOutputName();
					if (!outputManager.isOutputAllowed(outputName)) {
						LogManager.getRootLogger().removeAppender(appender);
						continue;
					}
					
					logjApp.init( outputManager );
					logjApp.activateOptions();
				}
				
			} catch (Exception e) {
				// Do nothing, and continue in loop
			}
	    }
	}
}
