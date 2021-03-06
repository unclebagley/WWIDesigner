package com.wwidesigner.optimization;

import com.wwidesigner.geometry.Hole;
import com.wwidesigner.geometry.Instrument;
import com.wwidesigner.geometry.PositionInterface;
import com.wwidesigner.modelling.EvaluatorInterface;
import com.wwidesigner.modelling.InstrumentCalculator;
import com.wwidesigner.note.TuningInterface;

/**
 * Optimization objective function for bore length and hole positions:
 * <ul>
 * <li>Position of end bore point.</li>
 * <li>Position of top hole as fraction of bore length.</li>
 * <li>For each hole, spacing below the hole to the next hole.</li>
 * </ul>
 * Assumes that total spacing is less than the bore length. (In practice, it
 * will be significantly less.)
 * 
 * @author Edward Kort, Burton Patkau
 * 
 */
public class HolePositionFromTopObjectiveFunction extends
		HolePositionObjectiveFunction
{

	public HolePositionFromTopObjectiveFunction(
			InstrumentCalculator calculator, TuningInterface tuning,
			EvaluatorInterface evaluator)
	{
		super(calculator, tuning, evaluator);
	}

	@Override
	public double[] getGeometryPoint()
	{
		// Geometry dimensions are distances between holes.
		// First dimension is bore length.
		// Second dimension is distance between first hole and top of bore,
		// expressed as a fraction of the bore length.

		PositionInterface[] sortedHoles = Instrument.sortList(calculator
				.getInstrument().getHole());

		double[] geometry = new double[nrDimensions];

		geometry[0] = getEndOfBore();
		double priorHolePosition = 0.;

		for (int i = 0; i < sortedHoles.length; i++)
		{
			Hole hole = (Hole) sortedHoles[i];
			geometry[i + 1] = hole.getBorePosition() - priorHolePosition;
			if (i == 0)
			{
				geometry[i + 1] /= geometry[0];
			}
			priorHolePosition = hole.getBorePosition();
		}
		return geometry;
	}

	@Override
	public void setGeometryPoint(double[] point)
	{
		setBore(point);

		PositionInterface[] sortedHoles = Instrument.sortList(calculator
				.getInstrument().getHole());

		// Geometry dimensions are distances between holes.
		// First dimension is bore length.
		// Second dimension is distance between first hole and top of bore, as
		// fraction of bore length.
		double priorHolePosition = 0.;

		for (int i = 0; i < sortedHoles.length; i++)
		{
			Hole hole = (Hole) sortedHoles[i];
			double holePosition = priorHolePosition + point[i + 1];
			if (i == 0)
			{
				holePosition *= point[0];
			}
			hole.setBorePosition(holePosition);
			priorHolePosition = holePosition;
		}

		calculator.getInstrument().updateComponents();
	}

	protected void setConstraints()
	{
		constraints.addConstraint(new Constraint(CONSTR_CAT, "Bore length",
				CONSTR_TYPE));

		PositionInterface[] sortedHoles = Instrument.sortList(calculator
				.getInstrument().getHole());
		int lastIdx = sortedHoles.length;
		for (int i = lastIdx, idx = 0; i > 0; i--, idx++)
		{
			String name = Constraint.getHoleName((Hole) sortedHoles[idx], i, 1,
					lastIdx);
			String constraintName = "";
			Constraint.ConstraintType constraintType;
			if (idx == 0)
			{
				constraintName = "Bore top to " + name
						+ ", bore-length fraction";
				constraintType = Constraint.ConstraintType.DIMENSIONLESS;
			}
			else
			{
				String priorName = Constraint.getHoleName(
						(Hole) sortedHoles[idx - 1], i + 1, 1, lastIdx);
				constraintName = priorName + " to " + name + " distance";
				constraintType = Constraint.ConstraintType.DIMENSIONAL;
			}

			constraints.addConstraint(new Constraint(CONSTR_CAT,
					constraintName, constraintType));
		}

		constraints.setNumberOfHoles(calculator.getInstrument().getHole()
				.size());
		constraints.setObjectiveDisplayName("Hole position optimizer");
	}

}
