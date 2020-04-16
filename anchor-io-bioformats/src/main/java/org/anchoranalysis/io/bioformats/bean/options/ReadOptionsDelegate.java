package org.anchoranalysis.io.bioformats.bean.options;

import java.util.List;

import org.anchoranalysis.bean.annotation.BeanField;

import loci.formats.IFormatReader;

/**
 * Forces a particular settings, but otherwise uses settings from a delegate
 * 
 * <p>Sub-classes of this deliberately break the Liskov substitution principle by replacing existing behaviour</p>
 * 
 * @author Owen Feehan
 *
 */
public abstract class ReadOptionsDelegate extends ReadOptions {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	// START BEAN PROPERTIES
	@BeanField
	private ReadOptions options = new Default();
	// END BEAN PROPERTIES
	
	@Override
	public List<String> determineChannelNames(IFormatReader reader) {
		return options.determineChannelNames(reader);
	}
		
	@Override
	public int sizeT(IFormatReader reader) {
		return options.sizeT(reader);
	}

	@Override
	public int sizeZ(IFormatReader reader) {
		return options.sizeZ(reader);
	}

	@Override
	public int sizeC(IFormatReader reader) {
		return options.sizeC(reader);
	}
	
	@Override
	public boolean isRGB(IFormatReader reader) {
		return options.isRGB(reader);
	}
	
	@Override
	public int effectiveBitsPerPixel(IFormatReader reader) {
		return options.effectiveBitsPerPixel(reader);
	}
	
	@Override
	public int chnlsPerByteArray(IFormatReader reader) {
		return options.chnlsPerByteArray(reader);
	}
		
	protected ReadOptions delegate() {
		return options;
	}
	
	public ReadOptions getOptions() {
		return options;
	}

	public void setOptions(ReadOptions options) {
		this.options = options;
	}
}
