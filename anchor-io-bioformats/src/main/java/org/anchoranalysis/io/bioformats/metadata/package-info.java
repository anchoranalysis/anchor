/**
 * Reading metadata information from files using EXIF and/or other metadata headers.
 *
 * <p>Note that this doesn't use the Bioformats library directory, rather the <a
 * href="https://github.com/drewnoakes/metadata-extractor">metadata-extractor</a> library. However,
 * this dependency exists due to the Bioformats library, so the routines are included in this
 * package for convenience, as they need to be available to the bioformats library, as well as to
 * plugins.
 */
package org.anchoranalysis.io.bioformats.metadata;
