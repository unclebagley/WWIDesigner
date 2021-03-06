/**
 * 
 */
package com.wwidesigner.gui;

import com.wwidesigner.geometry.Instrument;
import com.wwidesigner.modelling.EvaluatorInterface;
import com.wwidesigner.modelling.GordonCalculator;
import com.wwidesigner.modelling.InstrumentCalculator;
import com.wwidesigner.modelling.InstrumentTuner;
import com.wwidesigner.modelling.NAFCalculator;
import com.wwidesigner.modelling.ReflectionEvaluator;
import com.wwidesigner.modelling.SimpleInstrumentTuner;
import com.wwidesigner.note.Tuning;
import com.wwidesigner.optimization.BaseObjectiveFunction;
import com.wwidesigner.optimization.FippleFactorObjectiveFunction;
import com.wwidesigner.optimization.HoleFromTopObjectiveFunction;
import com.wwidesigner.optimization.HoleGroupObjectiveFunction;
import com.wwidesigner.optimization.HoleSizeObjectiveFunction;
import com.wwidesigner.optimization.SingleTaperHoleGroupObjectiveFunction;
import com.wwidesigner.optimization.SingleTaperNoHoleGroupingFromTopObjectiveFunction;
import com.wwidesigner.optimization.multistart.GridRangeProcessor;
import com.wwidesigner.util.Constants.TemperatureType;
import com.wwidesigner.util.PhysicalParameters;

/**
 * @author kort
 * 
 */
public class NafStudyModel extends StudyModel
{
	public static final String GORDON_CALC_SUB_CATEGORY_ID = "Gordon calculator";
	public static final String NAF_CALC_SUB_CATEGORY_ID = "NAF calculator";

	public static final String FIPPLE_OPT_SUB_CATEGORY_ID = "Fipple-factor Optimizer";
	public static final String HOLESIZE_OPT_SUB_CATEGORY_ID = "Hole-size Optimizer";
	public static final String NO_GROUP_OPT_SUB_CATEGORY_ID = "No-grouping Hole Optimizer";
	public static final String GROUP_OPT_SUB_CATEGORY_ID = "Hole-grouping Optimizer";
	public static final String TAPER_GROUP_OPT_SUB_CATEGORY_ID = "Taper, hole-grouping Optimizer";
	public static final String TAPER_NO_GROUP_OPT_SUB_CATEGORY_ID = "Taper, no-hole-grouping Optimizer";

	public static final String HOLE_0_CONS_SUB_CATEGORY_ID = "0 holes";
	public static final String HOLE_6_1_125_SPACING_CONS_SUB_CATEGORY_ID = "6 holes, 1-1/8\" max spacing";
	public static final String HOLE_6_1_25_SPACING_CONS_SUB_CATEGORY_ID = "6 holes, 1-1/4\" max spacing";
	public static final String HOLE_6_40_SPACING_CONS_SUB_CATEGORY_ID = "6 holes, 1.40\" max spacing";
	public static final String HOLE_6_1_5_SPACING_CONS_SUB_CATEGORY_ID = "6 holes, 1-1/2\" max spacing";
	public static final String HOLE_7_CONS_SUB_CATEGORY_ID = "7 holes";

	public static final String NO_MULTI_START_SUB_CATEGORY_ID = "No multi-start optimization";
	public static final String VARY_FIRST_MULTI_START_SUB_CATEGORY_ID = "Vary first bound variable";
	public static final String VARY_ALL_MULTI_START_SUB_CATEGORY_ID = "Vary all dimensions";

	protected int numberOfStarts = 30;

	public NafStudyModel()
	{
		super();
		setLocalCategories();
	}

	protected void setLocalCategories()
	{
		setParams(new PhysicalParameters(72.0, TemperatureType.F));
		Category calculators = new Category(CALCULATOR_CATEGORY_ID);
		calculators.addSub(GORDON_CALC_SUB_CATEGORY_ID, null);
		calculators.addSub(NAF_CALC_SUB_CATEGORY_ID, null);
		calculators.setSelectedSub(NAF_CALC_SUB_CATEGORY_ID);
		categories.add(calculators);
		Category multiStart = new Category(MULTI_START_CATEGORY_ID);
		multiStart.addSub(NO_MULTI_START_SUB_CATEGORY_ID, null);
		multiStart.addSub(VARY_FIRST_MULTI_START_SUB_CATEGORY_ID, null);
		multiStart.addSub(VARY_ALL_MULTI_START_SUB_CATEGORY_ID, null);
		// Default to no multi-start
		multiStart.setSelectedSub(NO_MULTI_START_SUB_CATEGORY_ID);
		categories.add(multiStart);
		Category optimizers = new Category(OPTIMIZER_CATEGORY_ID);
		optimizers.addSub(FIPPLE_OPT_SUB_CATEGORY_ID, null);
		optimizers.addSub(NO_GROUP_OPT_SUB_CATEGORY_ID, null);
		optimizers.addSub(HOLESIZE_OPT_SUB_CATEGORY_ID, null);
		optimizers.addSub(GROUP_OPT_SUB_CATEGORY_ID, null);
		optimizers.addSub(TAPER_NO_GROUP_OPT_SUB_CATEGORY_ID, null);
		optimizers.addSub(TAPER_GROUP_OPT_SUB_CATEGORY_ID, null);
		categories.add(optimizers);
		Category constraints = new Category(CONSTRAINT_CATEGORY_ID);
		constraints.addSub(HOLE_0_CONS_SUB_CATEGORY_ID, null);
		constraints.addSub(HOLE_6_1_125_SPACING_CONS_SUB_CATEGORY_ID, null);
		constraints.addSub(HOLE_6_1_25_SPACING_CONS_SUB_CATEGORY_ID, null);
		constraints.addSub(HOLE_6_40_SPACING_CONS_SUB_CATEGORY_ID, null);
		constraints.addSub(HOLE_6_1_5_SPACING_CONS_SUB_CATEGORY_ID, null);
		constraints.addSub(HOLE_7_CONS_SUB_CATEGORY_ID, null);
		categories.add(constraints);
	}

	@Override
	public boolean canTune()
	{
		Category tuningCategory = getCategory(TUNING_CATEGORY_ID);
		String tuningSelected = tuningCategory.getSelectedSub();

		Category instrumentCategory = getCategory(INSTRUMENT_CATEGORY_ID);
		String instrumentSelected = instrumentCategory.getSelectedSub();

		Category calculatorCategory = getCategory(CALCULATOR_CATEGORY_ID);
		String calculatorSelected = calculatorCategory.getSelectedSub();

		return tuningSelected != null && instrumentSelected != null
				&& calculatorSelected != null;
	}

	@Override
	public boolean canOptimize()
	{
		Category category = getCategory(OPTIMIZER_CATEGORY_ID);
		String optimizerSelected = category.getSelectedSub();

		category = getCategory(CONSTRAINT_CATEGORY_ID);
		String constraintsSelected = category.getSelectedSub();

		return optimizerSelected != null && constraintsSelected != null
				&& canTune();
	}

	@Override
	protected InstrumentCalculator getCalculator()
	{
		Category calculatorCategory = getCategory(CALCULATOR_CATEGORY_ID);
		String calculatorSelected = calculatorCategory.getSelectedSub();
		InstrumentCalculator calculator = null;

		switch (calculatorSelected)
		{
			case GORDON_CALC_SUB_CATEGORY_ID:
				calculator = new GordonCalculator();
				break;
			case NAF_CALC_SUB_CATEGORY_ID:
				calculator = new NAFCalculator();
				break;
		}

		if (calculator != null)
		{
			calculator.setPhysicalParameters(params);
		}
		return calculator;
	}

	@Override
	protected InstrumentTuner getInstrumentTuner()
	{
		InstrumentTuner tuner = new SimpleInstrumentTuner();
		tuner.setParams(params);
		return tuner;
	}

	@Override
	protected BaseObjectiveFunction getObjectiveFunction() throws Exception
	{
		Category optimizerCategory = getCategory(OPTIMIZER_CATEGORY_ID);
		String optimizer = optimizerCategory.getSelectedSub();
		Category constraintCategory = getCategory(CONSTRAINT_CATEGORY_ID);
		String constraint = constraintCategory.getSelectedSub();

		Instrument instrument = getInstrument();
		Tuning tuning = getTuning();
		InstrumentCalculator calculator = getCalculator();
		calculator.setInstrument(instrument);
		EvaluatorInterface evaluator;
		int numberOfHoles = instrument.getHole().size();

		BaseObjectiveFunction objective = null;
		double[] lowerBound = null;
		double[] upperBound = null;
		int[][] holeGroups = null;

		switch (optimizer)
		{
			case FIPPLE_OPT_SUB_CATEGORY_ID:
				evaluator = new ReflectionEvaluator(calculator);
				objective = new FippleFactorObjectiveFunction(calculator,
						tuning, evaluator);
				lowerBound = new double[] { 0.2 };
				upperBound = new double[] { 1.5 };
				break;
			case HOLESIZE_OPT_SUB_CATEGORY_ID:
				evaluator = new ReflectionEvaluator(calculator);
				objective = new HoleSizeObjectiveFunction(calculator, tuning,
						evaluator);
				// Bounds are hole diameters expressed in meters.
				if (numberOfHoles == 0)
				{
					lowerBound = new double[0];
					upperBound = new double[0];
				}
				else if (numberOfHoles == 7)
				{
					lowerBound = new double[] { 0.002, 0.002, 0.002, 0.002,
							0.002, 0.002, 0.002 };
					upperBound = new double[] { 0.014, 0.014, 0.014, 0.014,
							0.014, 0.008, 0.008 };
				}
				else
				// Assume 6 holes.
				{
					lowerBound = new double[] { 0.002, 0.003, 0.003, 0.003,
							0.003, 0.003 };
					upperBound = new double[] { 0.0102, 0.0102, 0.010, 0.010,
							0.010, 0.012 };
				}
				break;
			case NO_GROUP_OPT_SUB_CATEGORY_ID:
				evaluator = new ReflectionEvaluator(calculator);
				// Length bounds are expressed in meters, diameter bounds as
				// ratios.
				if (numberOfHoles == 0)
				{
					lowerBound = new double[] { 0.2 };
					upperBound = new double[] { 0.6 };
				}
				else if (numberOfHoles == 7)
				{
					lowerBound = new double[] { 0.2, 0.28, 0.0203, 0.0203,
							0.0203, 0.0203, 0.0203, 0.0005, 0.012, 0.002,
							0.002, 0.002, 0.002, 0.002, 0.002, 0.002 };
					upperBound = new double[] { 0.7, 0.50, 0.05, 0.05, 0.1,
							0.05, 0.05, 0.003, 0.014, 0.014, 0.014, 0.014,
							0.014, 0.008, 0.008 };
				}
				else if (constraint == HOLE_6_1_125_SPACING_CONS_SUB_CATEGORY_ID)
				{
					lowerBound = new double[] { 0.2, 0.28, 0.0203, 0.0203,
							0.0203, 0.0203, 0.0203, 0.002, 0.003, 0.003, 0.003,
							0.003, 0.003 };
					upperBound = new double[] { 0.7, 0.50, 0.029, 0.029, 0.07,
							0.029, 0.029, 0.0102, 0.0102, 0.010, 0.010, 0.010,
							0.012 };
				}
				else if (constraint == HOLE_6_1_25_SPACING_CONS_SUB_CATEGORY_ID)
				{
					lowerBound = new double[] { 0.2, 0.28, 0.0203, 0.0203,
							0.0203, 0.0203, 0.0203, 0.002, 0.003, 0.003, 0.003,
							0.003, 0.003 };
					upperBound = new double[] { 0.7, 0.50, 0.032, 0.032, 0.07,
							0.032, 0.032, 0.30, 0.0102, 0.0102, 0.010, 0.010,
							0.010, 0.012 };
				}
				else if (constraint == HOLE_6_40_SPACING_CONS_SUB_CATEGORY_ID)
				{
					lowerBound = new double[] { 0.2, 0.28, 0.0203, 0.0203,
							0.0203, 0.0203, 0.0203, 0.002, 0.003, 0.003, 0.003,
							0.003, 0.003 };
					upperBound = new double[] { 0.7, 0.50, 0.0356, 0.0356,
							0.07, 0.0356, 0.0336, 0.0102, 0.0102, 0.010, 0.010,
							0.010, 0.012 };
				}
				else
				// 6 holes, 1.5 inch spacing.
				{
					lowerBound = new double[] { 0.2, 0.28, 0.0203, 0.0203,
							0.0203, 0.0203, 0.0203, 0.002, 0.003, 0.003, 0.003,
							0.003, 0.003 };
					upperBound = new double[] { 0.7, 0.50, 0.038, 0.038, 0.07,
							0.038, 0.038, 0.0102, 0.0102, 0.010, 0.010, 0.010,
							0.012 };
				}
				objective = new HoleFromTopObjectiveFunction(calculator,
						tuning, evaluator);
				break;
			case GROUP_OPT_SUB_CATEGORY_ID:
				evaluator = new ReflectionEvaluator(calculator);
				// Length bounds are expressed in meters, diameter bounds as
				// ratios.
				if (numberOfHoles == 0)
				{
					lowerBound = new double[] { 0.2 };
					upperBound = new double[] { 0.6 };
				}
				else if (numberOfHoles == 7)
				{
					holeGroups = new int[][] { { 0, 1, 2 }, { 3, 4, 5 }, { 6 } };
					lowerBound = new double[] { 0.2, 0.0203, 0.0203, 0.0203,
							0.0005, 0.012, 0.002, 0.002, 0.002, 0.002, 0.002,
							0.002, 0.002 };
					upperBound = new double[] { 0.7, 0.05, 0.05, 0.1, 0.003,
							0.20, 0.014, 0.014, 0.014, 0.014, 0.014, 0.008,
							0.008 };
				}
				else
				{
					holeGroups = new int[][] { { 0, 1, 2 }, { 3, 4, 5 } };
					lowerBound = new double[] { 0.2, 0.0203, 0.0203, 0.0203,
							0.01, 0.002, 0.003, 0.003, 0.003, 0.003, 0.003 };
					upperBound = new double[] { 0.7, 0.038, 0.07, 0.038, 0.30,
							0.0102, 0.0102, 0.010, 0.010, 0.010, 0.012 };
					if (constraint == HOLE_6_1_125_SPACING_CONS_SUB_CATEGORY_ID)
					{
						upperBound[1] = 0.029;
						upperBound[3] = 0.029;
					}
					else if (constraint == HOLE_6_1_25_SPACING_CONS_SUB_CATEGORY_ID)
					{
						upperBound[1] = 0.032;
						upperBound[3] = 0.032;
					}
					else if (constraint == HOLE_6_40_SPACING_CONS_SUB_CATEGORY_ID)
					{
						upperBound[1] = 0.0356;
						upperBound[3] = 0.0356;
					}
				}
				objective = new HoleGroupObjectiveFunction(calculator, tuning,
						evaluator, holeGroups);
				break;
			case TAPER_NO_GROUP_OPT_SUB_CATEGORY_ID:
				evaluator = new ReflectionEvaluator(calculator);
				// Length bounds are expressed in meters, diameter bounds as
				// ratios,
				// taper bounds as ratios.
				if (numberOfHoles == 0)
				{
					lowerBound = new double[] { 0.2, 0.8, 0.2, 0.0 };
					upperBound = new double[] { 0.7, 1.2, 1.0, 1.0 };
				}
				else if (numberOfHoles == 7)
				{
					lowerBound = new double[] { 0.2, 0.28, 0.0203, 0.0203, 0.0203,
							0.0203, 0.0203, 0.0005, 0.002, 0.002, 0.002,
							0.002, 0.002, 0.002, 0.002, 0.8, 0.2, 0.0 };
					upperBound = new double[] { 0.7, 0.50, 0.05, 0.05, 0.1, 0.05,
							0.05, 0.003, 0.014, 0.014, 0.014, 0.014,
							0.014, 0.008, 0.008, 1.2, 1.0, 1.0 };
				}
				else if (constraint == HOLE_6_1_125_SPACING_CONS_SUB_CATEGORY_ID)
				{
					lowerBound = new double[] { 0.2, 0.28, 0.0203, 0.0203, 0.0203,
							0.0203, 0.0203, 0.002, 0.003, 0.003, 0.003,
							0.003, 0.003, 0.8, 0.2, 0.0 };
					upperBound = new double[] { 0.7, 0.50, 0.029, 0.029, 0.07, 0.029,
							0.029, 0.0102, 0.0102, 0.010, 0.010, 0.010,
							0.012, 1.2, 1.0, 1.0 };
				}
				else if (constraint == HOLE_6_1_25_SPACING_CONS_SUB_CATEGORY_ID)
				{
					lowerBound = new double[] { 0.2, 0.28, 0.0203, 0.0203, 0.0203,
							0.0203, 0.0203, 0.002, 0.003, 0.003, 0.003,
							0.003, 0.003, 0.8, 0.2, 0.0 };
					upperBound = new double[] { 0.7, 0.50, 0.032, 0.032, 0.07, 0.032,
							0.032, 0.0102, 0.0102, 0.010, 0.010, 0.010,
							0.012, 1.15, 1.0, 1.0 };
				}
				else if (constraint == HOLE_6_40_SPACING_CONS_SUB_CATEGORY_ID)
				{
					lowerBound = new double[] { 0.2, 0.28, 0.0203, 0.0203, 0.0203,
							0.0203, 0.0203, 0.002, 0.003, 0.003, 0.003,
							0.003, 0.003, 0.8, 0.2, 0.0 };
					upperBound = new double[] { 0.7, 0.50, 0.0356, 0.0356, 0.07,
							0.0356, 0.0336, 0.0102, 0.0102, 0.010, 0.010,
							0.010, 0.012, 1.15, 1.0, 1.0 };
				}
				else
				// 6 holes, 1.5 inch spacing.
				{
					lowerBound = new double[] { 0.2, 0.28, 0.0203, 0.0203, 0.0203,
							0.0203, 0.0203, 0.002, 0.003, 0.003, 0.003,
							0.003, 0.003, 0.8, 0.2, 0.0 };
					upperBound = new double[] { 0.7, 0.50, 0.038, 0.038, 0.07, 0.038,
							0.038, 0.0102, 0.0102, 0.010, 0.010, 0.010,
							0.012, 1.2, 1.0, 1.0 };
				}
				objective = new SingleTaperNoHoleGroupingFromTopObjectiveFunction(
						calculator, tuning, evaluator);
				break;
			case TAPER_GROUP_OPT_SUB_CATEGORY_ID:
				evaluator = new ReflectionEvaluator(calculator);
				// Length bounds are expressed in meters, diameter bounds as
				// ratios,
				// taper bounds as ratios.
				if (numberOfHoles == 0)
				{
					holeGroups = new int[][] { {} };
					lowerBound = new double[] { 0.2, 0.8, 0.2, 0.0 };
					upperBound = new double[] { 0.7, 1.2, 1.0, 1.0 };
				}
				else if (numberOfHoles == 7)
				{
					holeGroups = new int[][] { { 0, 1, 2 }, { 3, 4, 5 }, { 6 } };
					lowerBound = new double[] { 0.2, 0.0203, 0.0203, 0.0203,
							0.0203, 0.012, 0.002, 0.002, 0.002, 0.002, 0.002,
							0.002, 0.002, 0.8, 0.2, 0.0 };
					upperBound = new double[] { 0.7, 0.003, 0.05, 0.05, 0.1,
							0.30, 0.014, 0.014, 0.014, 0.014, 0.014, 0.008,
							0.008, 1.2, 1.0, 1.0 };
				}
				else
				{
					holeGroups = new int[][] { { 0, 1, 2 }, { 3, 4, 5 } };
					lowerBound = new double[] { 0.2, 0.0203, 0.0203, 0.0203,
							0.01, 0.002, 0.003, 0.003, 0.003, 0.003, 0.003,
							0.8, 0.2, 0.0 };
					upperBound = new double[] { 0.7, 0.038, 0.07, 0.038, 0.20,
							0.0102, 0.0102, 0.010, 0.010, 0.010, 0.012, 1.2,
							1.0, 1.0 };
					if (constraint == HOLE_6_1_125_SPACING_CONS_SUB_CATEGORY_ID)
					{
						upperBound[1] = 0.029;
						upperBound[3] = 0.029;
					}
					else if (constraint == HOLE_6_1_25_SPACING_CONS_SUB_CATEGORY_ID)
					{
						upperBound[1] = 0.032;
						upperBound[3] = 0.032;
					}
					else if (constraint == HOLE_6_40_SPACING_CONS_SUB_CATEGORY_ID)
					{
						upperBound[1] = 0.0356;
						upperBound[3] = 0.0356;
					}
				}
				objective = new SingleTaperHoleGroupObjectiveFunction(
						calculator, tuning, evaluator, holeGroups);
				break;
		}

		objective.setLowerBounds(lowerBound);
		objective.setUpperBounds(upperBound);

		Category multiStartCategory = getCategory(MULTI_START_CATEGORY_ID);
		String multiStartSelected = multiStartCategory.getSelectedSub();
		if (multiStartSelected == VARY_FIRST_MULTI_START_SUB_CATEGORY_ID)
		{
			GridRangeProcessor rangeProcessor = new GridRangeProcessor(
					lowerBound, upperBound, new int[] { 0 }, 30);
			objective.setRangeProcessor(rangeProcessor);
			objective.setMaxEvaluations(30 * objective.getMaxEvaluations());
		}
		else if (multiStartSelected == VARY_ALL_MULTI_START_SUB_CATEGORY_ID)
		{
			GridRangeProcessor rangeProcessor = new GridRangeProcessor(
					lowerBound, upperBound, null, 30);
			objective.setRangeProcessor(rangeProcessor);
			objective.setMaxEvaluations(30 * objective.getMaxEvaluations());
		}

		return objective;
	} // getObjectiveFunction

}
