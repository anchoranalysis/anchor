package org.anchoranalysis.experiment.bean;

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


import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.xml.BeanXmlLoader;
import org.anchoranalysis.bean.xml.error.BeanXmlException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.bean.identifier.ExperimentIdentifier;
import org.anchoranalysis.experiment.bean.identifier.ExperimentIdentifierSimple;
import org.anchoranalysis.experiment.bean.io.InputOutputExperiment;
import org.anchoranalysis.experiment.log.ConsoleLogReporter;
import org.anchoranalysis.io.bean.descriptivename.DescriptiveNameFromFile;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.bean.provider.file.FileProvider;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.input.descriptivename.DescriptiveFile;

// Not finished
public class MultipleInputOutputExperiment<T extends InputFromManager, S> extends Experiment {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private FileProvider inputManagerBeanPathProvider;
	
	@BeanField
	private DescriptiveNameFromFile descriptiveNameFromFile;
	
	@BeanField
	private InputOutputExperiment<T,S> experiment;
	
	@BeanField
	private String version = "1.0";
	
	@BeanField
	private ExperimentIdentifier experimentIdentifier = null;
	// END BEAN PROPERTIES
	
	@Override
	public void doExperiment(ExperimentExecutionArguments expArgs)
			throws ExperimentExecutionException {
			
		Collection<File> files;
		try {
			files = inputManagerBeanPathProvider.matchingFiles(
				ProgressReporterNull.get(),
				expArgs.createInputContext(),
				new LogErrorReporter( new ConsoleLogReporter() )
			);
		} catch (AnchorIOException e) {
			throw new ExperimentExecutionException("Cannot find input manager files", e);
		} catch (IOException e) {
			throw new ExperimentExecutionException("Cannot create input context", e);
		}
		
		try {
			List<DescriptiveFile> list = descriptiveNameFromFile.descriptiveNamesFor(files, "Invalid Name");
			
			int i = 0;
			for( DescriptiveFile df : list ) {

				System.out.printf("Starting\t%03d:\t%s\tat %s%n", i, df.getDescriptiveName(), df.getPath() );
				
				InputManager<T> inputManager = BeanXmlLoader.loadBean(df.getPath(), "bean");

				experiment.setInput(inputManager);
				experiment.setExperimentIdentifier( new ExperimentIdentifierSimple(df.getDescriptiveName(),version));
				System.out.printf("Ending   \t%03d:\t%s\tat %s%n", i++, df.getDescriptiveName(), df.getPath() );

				experiment.doExperiment(expArgs);
			}
		
		} catch (BeanXmlException e ) {
			System.out.printf("Ending early due to exception" );
			throw new ExperimentExecutionException(e);
		}
	}

	@Override
	public boolean useDetailedLogging() {
		return experiment.useDetailedLogging();
	}

	public FileProvider getInputManagerBeanPathProvider() {
		return inputManagerBeanPathProvider;
	}

	public void setInputManagerBeanPathProvider(
			FileProvider inputManagerBeanPathProvider) {
		this.inputManagerBeanPathProvider = inputManagerBeanPathProvider;
	}

	public DescriptiveNameFromFile getDescriptiveNameFromFile() {
		return descriptiveNameFromFile;
	}

	public void setDescriptiveNameFromFile(
			DescriptiveNameFromFile descriptiveNameFromFile) {
		this.descriptiveNameFromFile = descriptiveNameFromFile;
	}

	public InputOutputExperiment<T,S> getExperiment() {
		return experiment;
	}

	public void setExperiment(InputOutputExperiment<T,S> experiment) {
		this.experiment = experiment;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public ExperimentIdentifier getExperimentIdentifier() {
		return experimentIdentifier;
	}

	public void setExperimentIdentifier(ExperimentIdentifier experimentIdentifier) {
		this.experimentIdentifier = experimentIdentifier;
	}
}
