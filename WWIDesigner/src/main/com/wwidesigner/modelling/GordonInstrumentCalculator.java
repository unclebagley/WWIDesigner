/**
 * 
 */
package com.wwidesigner.modelling;

import org.apache.commons.math3.complex.Complex;

import com.wwidesigner.geometry.BoreSection;
import com.wwidesigner.geometry.ComponentInterface;
import com.wwidesigner.geometry.Hole;
import com.wwidesigner.geometry.Instrument;
import com.wwidesigner.geometry.Mouthpiece;
import com.wwidesigner.modelling.InstrumentCalculator;
import com.wwidesigner.math.StateVector;
import com.wwidesigner.math.TransferMatrix;
import com.wwidesigner.note.Fingering;
import com.wwidesigner.util.PhysicalParameters;

/**
 * @author kort
 * 
 */
public class GordonInstrumentCalculator extends InstrumentCalculator
{

	public GordonInstrumentCalculator()
	{
		super();
	}

	/**
	 * @param instrument
	 */
	public GordonInstrumentCalculator(Instrument instrument,
			PhysicalParameters physicalParams)
	{
		super(instrument, physicalParams);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wwidesigner.geometry.InstrumentCalculator#calcRefOrImpCoefficent(
	 * double, com.wwidesigner.note.Fingering,
	 * com.wwidesigner.util.PhysicalParameters)
	 */
	@Override
	public Complex calcZ(double freq)
	{
		TransferMatrix fluteTM = new TransferMatrix(Complex.ONE, Complex.ZERO,
				Complex.ZERO, Complex.ONE);

		double waveNumber = params.calcWaveNumber(freq);

		for (ComponentInterface component : instrument.getComponents())
		{
			TransferMatrix compTM = null;
			if (component instanceof BoreSection)
			{
				if (((BoreSection)component).getLength() > 0)
				{
					compTM = boreSectionCalculator.calcTransferMatrix(
						(BoreSection) component, waveNumber, params);
				}
			}
			else if (component instanceof Hole)
			{
				compTM = holeCalculator.calcTransferMatrix((Hole) component,
						waveNumber, params);
			}
			else if (component instanceof Mouthpiece)
			{
				compTM = mouthpieceCalculator.calcTransferMatrix(
						(Mouthpiece) component, waveNumber, params);
			}

			if (compTM != null)
			{
				fluteTM = fluteTM.multiply(compTM);
			}
		}

		StateVector sv = terminationCalculator.calcStateVector(
				instrument.getTermination(), waveNumber, params);
		Complex termImp = sv.Impedance();

		Complex result = termImp.multiply(fluteTM.getPP()).add(fluteTM.getPU())
				.divide(termImp.multiply(fluteTM.getUP()).add(fluteTM.getUU()));
		return result.conjugate();
	}

	@Override
	public Complex calcReflectionCoefficient(double freq)
	{
		// TODO Auto-generated method stub
		return Complex.ONE;
	}

	@Override
	public Double getPlayedFrequency(Fingering fingering, double freqRange,
			int numberOfFrequencies)
	{
		Double playedFreq = null;
		double targetFreq = fingering.getNote().getFrequency();
		double freqStart = targetFreq / freqRange;
		double freqEnd = targetFreq * freqRange;
		ImpedanceSpectrum spectrum = new ImpedanceSpectrum();

		spectrum.calcImpedance(this.instrument, this, freqStart, freqEnd,
				numberOfFrequencies, fingering, params);
		playedFreq = spectrum.getClosestMinimumFrequency(targetFreq);

		return playedFreq;
	}

	@Override
	public double calcGain(double freq, Complex Z)
	{
		// Magnitude of loop gain for a given note, after Auvray, 2012.
		if (instrument.getMouthpiece().getGainFactor() == null)
		{
			return 0.0;
		}
		return (freq * instrument.getMouthpiece().getGainFactor()) / Z.abs();
	}
}
