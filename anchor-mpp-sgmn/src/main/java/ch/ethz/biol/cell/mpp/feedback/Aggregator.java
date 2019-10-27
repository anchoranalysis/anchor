package ch.ethz.biol.cell.mpp.feedback;

/*
 * #%L
 * anchor-mpp-sgmn
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


import java.io.PrintWriter;

public class Aggregator {

	private double nrg;
	private double size;
	private double temperature;
	private double accptAll;
	
	private int lastDivider;

	private AggIDCounter kernelAccpt;
	private AggIDCounter kernelProp;	
	
	public Aggregator() {
		
	}
	
	public Aggregator( int maxID ) {
		kernelAccpt = new AggIDCounter( maxID );
		kernelProp = new AggIDCounter( maxID );
		reset();
	}
	
	public Aggregator deepCopy() {
		Aggregator agg = new Aggregator();
		agg.kernelAccpt = kernelAccpt.deepCopy();
		agg.kernelProp = kernelProp.deepCopy();
		agg.nrg = nrg;
		agg.size = size;
		agg.temperature = temperature;
		agg.accptAll = accptAll;
		agg.lastDivider = lastDivider;
		return agg;
	}
	
	public void reset() {
		nrg = 0;
		size = 0;
		temperature = 0;
		accptAll = 0;
		kernelAccpt.reset();
		kernelProp.reset();
		lastDivider = -1;
	}
	
	public void div( int divider ) {
		nrg /= divider;
		size /= divider;
		temperature /= divider;
		accptAll /= divider;
		kernelAccpt.div( divider );
		kernelProp.div( divider );
		kernelAccpt.div( kernelProp );
		this.lastDivider = divider;
	}
	
	@Override
	public String toString() {
		return String.format(
				"agg(nrg=%e  size=%3.3f  aa=%3.6f temp=%e  ka=%s)",
				nrg,
				size,
				accptAll,
				temperature,
				kernelAccpt.toString()
		);
	}
	
	public void outputToWriter( PrintWriter writer ) {
		writer.printf("%e,%f,%e,%e,", nrg, size, accptAll, temperature );
		kernelProp.outputToWriter( writer );
		writer.print(",");
		kernelAccpt.outputToWriter( writer );
	}
	
	public void outputHeaderToWriter( PrintWriter writer ) {
		writer.print("Nrg,Size,AccptAll,Temperature," );
		kernelProp.outputHeaderToWriter( writer, "Prop" );
		writer.print(",");
		kernelAccpt.outputHeaderToWriter( writer, "Accpt" );
	}
	
	public void incrNRG( double incr ) {
		nrg += incr;
	}
	
	public void incrSize( double incr ) {
		size += incr;
	}
	
	public void incrTemperature( double incr ) {
		temperature += incr;
	}

	public void incrKernelProp( int kernelID ) {
		kernelProp.incr( kernelID );
	}
	
	public void incrKernelAccpt( int kernelID ) {
		kernelAccpt.incr( kernelID );
		accptAll++;
	}

	public double getNrg() {
		return nrg;
	}

	public void setNrg(double nrg) {
		this.nrg = nrg;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}
	
	public boolean hasLastDivider() {
		return (lastDivider!=-1);
	}

	public int getLastDivider() {
		return lastDivider;
	}

	public double getTemp() {
		return temperature;
	}

	public AggIDCounter getKernelAccpt() {
		return kernelAccpt;
	}

	public AggIDCounter getKernelProp() {
		return kernelProp;
	}
	
	public double getAccptAll() {
		return accptAll;
	}
}

