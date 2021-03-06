/**
 * Test instrument impedance calculation on whistle BP7.
 */
package com.wwidesigner.modelling;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.math3.complex.Complex;
import org.junit.Test;

import com.wwidesigner.geometry.Instrument;
import com.wwidesigner.geometry.bind.GeometryBindFactory;
import com.wwidesigner.modelling.WhistleCalculator;
import com.wwidesigner.note.Note;
import com.wwidesigner.note.Tuning;
import com.wwidesigner.note.Fingering;
import com.wwidesigner.note.bind.NoteBindFactory;
import com.wwidesigner.util.BindFactory;
import com.wwidesigner.util.Constants.TemperatureType;
import com.wwidesigner.util.PhysicalParameters;

/**
 * @author Burton Patkau
 * 
 */
public class InstrumentImpedanceTest
{
	// Standard instrument, and its measured tuning.

	protected String inputInstrumentXML = "com/wwidesigner/optimization/example/BP7.xml";
	protected String inputTuningXML = "com/wwidesigner/optimization/example/BP7-tuning.xml";

	/**
	 * For the standard instrument, test the calculation of impedance against known zeros,
	 * and compare predicted fmax to measured values.
	 */
	@Test
	public final void testInstrumentImpedance()
	{
		try
		{
			double temperature = 28.2;
			PhysicalParameters params = new PhysicalParameters(temperature, TemperatureType.C);
			Instrument instrument = getInstrumentFromXml(inputInstrumentXML);
			InstrumentCalculator calculator = new WhistleCalculator(instrument,params);
			Tuning tuning = getTuningFromXml(inputTuningXML);
			List<Fingering>  noteList = tuning.getFingering();

			Double fmax[]
					  = { 588.,   665.,   741.,   791.,   899.,  1005.,  1087.,  1147.,
				         1202.,  1333.,  1485.,  1586.,  1787.,  1997.,  2048.,  2245.,
				         2437.,   908.};

			instrument.convertToMetres();
			double Z0 = params.calcZ0(instrument.getMouthpiece()
					.getBoreDiameter() / 2.0);

			// Test that impedance is zero at known zeros in the calculated impedance.

			for (int i = 0; i < fmax.length; ++i)
			{
				Fingering fingering = noteList.get(i);
				Complex Z = calculator.calcZ(fmax[i],fingering);
				Z = Z.divide(Z0);
				assertEquals("Imag(Z) is non-zero at known resonance.", 0.0,
						Z.getImaginary(), 0.035);
			}

			// Test that zeros of the calculated impedance are close to the measured values of fmax.
			double totalError = 0.0;
			int nrPredictions = 0;

			for ( int i = 0; i < noteList.size(); ++ i )
			{
				Fingering fingering = noteList.get(i);
				double actual = 0.0;
				double cents;
				if ( fingering.getNote().getFrequencyMax() != null )
				{
					actual = fingering.getNote().getFrequencyMax();
				}
				else if ( fingering.getNote().getFrequency() != null )
				{
					actual = fingering.getNote().getFrequency();
				}
				if ( actual != 0.0 )
				{
					PlayingRange range = new PlayingRange(calculator, fingering);
					double predicted = range.findXZero(actual);
					assertTrue("No prediction for note " + i, predicted > 0.0 );
					cents = Note.cents(actual, predicted);
					assertEquals("Predicted fmax does not agree with actual at note " + i, 0.0,
								cents, 20.0 );
					totalError += cents;
					nrPredictions += 1;
				}
			}
			
			// Test that the average prediction error is close to zero.
			
			assertEquals("Average prediction error is not small.", 0.0, totalError/nrPredictions, 0.50 );
		}
		catch (Exception e)
		{
			fail("Exception: " + e.getMessage());
		}
	}

	protected Instrument getInstrumentFromXml(String instrumentXML)
			throws Exception
	{
		BindFactory geometryBindFactory = GeometryBindFactory.getInstance();
		File inputFile = getInputFile(instrumentXML, geometryBindFactory);
		Instrument instrument = (Instrument) geometryBindFactory.unmarshalXml(
				inputFile, true);
		instrument.updateComponents();

		return instrument;
	}

	protected Tuning getTuningFromXml(String tuningXML) throws Exception
	{
		BindFactory noteBindFactory = NoteBindFactory.getInstance();
		File inputFile = getInputFile(tuningXML, noteBindFactory);
		Tuning tuning = (Tuning) noteBindFactory.unmarshalXml(inputFile, true);

		return tuning;
	}

	/**
	 * This approach for get the input File is based on finding it in the
	 * classpath. The actual application will use an explicit file path - this
	 * approach will be unnecessary.
	 * 
	 * @param fileName
	 *            expressed as a package path.
	 * @param bindFactory
	 *            that manages the elements in the file.
	 * @return A file representation of the fileName, as found somewhere in the
	 *         classpath.
	 * @throws FileNotFoundException
	 */
	protected File getInputFile(String fileName, BindFactory bindFactory)
			throws FileNotFoundException
	{
		String inputPath = BindFactory.getPathFromName(fileName);
		File inputFile = new File(inputPath);

		return inputFile;
	}
}
