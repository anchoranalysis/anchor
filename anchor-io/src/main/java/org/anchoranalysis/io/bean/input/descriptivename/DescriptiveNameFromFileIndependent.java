package org.anchoranalysis.io.bean.input.descriptivename;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.anchoranalysis.core.error.CreateException;

/**
 * Calculates the descriptive-name independently for each file
 * 
 * @author owen
 *
 */
public abstract class DescriptiveNameFromFileIndependent extends DescriptiveNameFromFile {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Logger log = Logger.getLogger(DescriptiveNameFromFile.class.getName());

	@Override
	public List<DescriptiveFile> descriptiveNamesFor( Collection<File> files, String elseName ) {
		
		List<DescriptiveFile> out = new ArrayList<>();
		
		int i =0;
		for (File f : files) {
			String descriptiveName = createDescriptiveNameOrElse( f, i++, elseName);
			out.add( new DescriptiveFile(f, descriptiveName) );
		}
		
		return out;
	}
	
	protected abstract String createDescriptiveName( File file, int index ) throws CreateException;
	
	private String createDescriptiveNameOrElse( File file, int index, String elseName ) {
		try {
			return createDescriptiveName(file, index);
		} catch (CreateException e) {
			String msg = String.format(
				"Cannot create a descriptive-name for file %s and index %d. Using '%s' instead.",
				file.getPath(),
				index,
				elseName
			);
			log.log( Level.WARNING, msg, e );
			return elseName;
		}
	}
}
