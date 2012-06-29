package com.wwidesigner.optimization;

import com.wwidesigner.util.PhysicalParameters;

//import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.complex.Complex;

import com.wwidesigner.math.StateVector;
import com.wwidesigner.math.TransferMatrix;

import com.wwidesigner.util.Constants.TemperatureType;

public class OptimizableInstrument
{
	private PhysicalParameters mParameters;
	
	private List<Section> mSections;
	private List<Hole> mHoles;
	
	private MouthpieceInterface mMouthpiece;
	
	private TerminationInterface mTermination;
	
    public OptimizableInstrument(List<Section> sections, List<Hole> holes)
    {
    	mMouthpiece = new FluteMouthpiece();
    	
    	mSections = sections;
    	mTermination = new IdealOpenEnd();
    	mHoles = holes;

    	mParameters = new PhysicalParameters(25., TemperatureType.C);
    }

	public Complex calculateReflectionCoefficient(Fingering fingering, double wave_number)
	{	
		TransferMatrix M = TransferMatrix.makeIdentity();

		Iterator<Hole> holesIterator = mHoles.iterator();
		
		int i = -1;
		Hole nextHole = null;
		
		if (holesIterator.hasNext())
		{
			nextHole = holesIterator.next();
			i = 0;
		}
	
		double xi = 0.;
		
		for (Section s: mSections)
		{
			double x0 = xi;
			double x1 = 0.;
			double xf = xi + s.getLength();
			
			while (nextHole != null && (nextHole.getPosition() >= xi && nextHole.getPosition() < xf))
			{
			    x1 = nextHole.getPosition();
			    Section subSection = s.makeSubSection(x0-xi, x1-xi);
				M = TransferMatrix.multiply(M, subSection.transferMatrix(wave_number, mParameters));
			    M = TransferMatrix.multiply(M, nextHole.transferMatrix(fingering.getPattern()[i], s.radiusAt(x1-xi), wave_number, mParameters));
			    x0 = x1;
			    if (holesIterator.hasNext())
			    {
			    	nextHole = holesIterator.next();
			        i++;
			    }
			    else
			    {
			    	nextHole = null;
			    }
			}
		    Section subSection = s.makeSubSection(x0-xi, xf-xi);
			
			M = TransferMatrix.multiply(M, subSection.transferMatrix(wave_number, mParameters));
		    xi = xf;
		}
		
		StateVector sv = TransferMatrix.multiply(M, mTermination.stateVector(wave_number, mParameters));
		
		return sv.Reflectance( mParameters.calcZ0(mSections.get(0).getLeftRadius() )).multiply( mMouthpiece.reflectanceMultiplier() );
	}	

	public double[] getStateVector()
    {
    	 int len = 1 + mHoles.size();
    	 
         double[] state_vector = new double[len];
         state_vector[0] = mSections.get(0).getLength();
         
         double accumulatedDistance = 0.;
         for (int i = mHoles.size(); i > 0; --i)
         {
        	 Hole hole = mHoles.get(i-1);
             state_vector[i] = state_vector[0]-hole.getPosition()-accumulatedDistance;
             accumulatedDistance += state_vector[i];
         }
         return state_vector;
    }
         	 
    public void updateGeometry(double[] state_vector)
    {
    	mSections.get(0).setLength(state_vector[0]);
    	
        double accumulatedDistance = 0.;
        for (int i = mHoles.size(); i > 0; --i)
        {
            Hole hole = mHoles.get(i-1);
    	    hole.setPosition(state_vector[0]-state_vector[i]-accumulatedDistance);
            accumulatedDistance += state_vector[i];
        }
    }
}