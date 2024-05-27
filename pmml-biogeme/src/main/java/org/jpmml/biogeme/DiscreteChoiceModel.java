/*
 * Copyright (c) 2024 Villu Ruusmann
 *
 * This file is part of JPMML-Biogeme
 *
 * JPMML-Biogeme is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JPMML-Biogeme is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with JPMML-Biogeme.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jpmml.biogeme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import biogeme.expressions.Beta;
import biogeme.expressions.Expression;
import biogeme.expressions.Numeric;
import biogeme.expressions.Plus;
import biogeme.expressions.Times;
import biogeme.expressions.Variable;
import com.google.common.collect.Iterables;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.Field;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.Model;
import org.dmg.pmml.OpType;
import org.dmg.pmml.Output;
import org.dmg.pmml.OutputField;
import org.dmg.pmml.PMML;
import org.dmg.pmml.mining.MiningModel;
import org.dmg.pmml.mining.Segmentation;
import org.dmg.pmml.regression.RegressionModel;
import org.dmg.pmml.regression.RegressionTable;
import org.jpmml.converter.CategoricalLabel;
import org.jpmml.converter.ContinuousFeature;
import org.jpmml.converter.Feature;
import org.jpmml.converter.FieldNameUtil;
import org.jpmml.converter.InteractionFeature;
import org.jpmml.converter.ModelUtil;
import org.jpmml.converter.mining.MiningModelUtil;
import org.jpmml.converter.regression.RegressionModelUtil;
import org.jpmml.converter.transformations.ExpTransformation;
import org.jpmml.python.PythonObject;

public class DiscreteChoiceModel extends PythonObject {

	public DiscreteChoiceModel(){
		super("builtins", "dict");
	}

	public PMML encodePMML(BiogemeEncoder encoder){
		Map<?, ?> V = getV();
		Map<?, ?> betas = getBetas();
		Map<?, ?> availability = getAvailability();

		if(availability != null){

			if(!Objects.equals(V.keySet(), availability.keySet())){
				throw new IllegalArgumentException();
			}
		}

		List<Object> choices = new ArrayList<>(V.keySet());

		DataField choiceField = encoder.createChoiceField("Choice", choices);

		if(availability != null){
			(availability.values()).stream()
				.forEach((value) -> {

					if(value instanceof Variable){
						Variable variable = (Variable)value;

						String name = variable.getName();

						encoder.createAvailabilityField(name);
					}
				});
		}

		CategoricalLabel categoricalLabel = new CategoricalLabel(choiceField);

		List<Model> models = new ArrayList<>();

		List<RegressionTable> regressionTables = new ArrayList<>();

		for(Object choice : choices){
			Expression utilityFunctionExpr = (Expression)V.get(choice);

			Model model = encodeUtilityFunction(choice, utilityFunctionExpr, betas, encoder);

			models.add(model);

			Feature feature = getPredictionFeature(model, encoder);

			if(availability != null){
				Expression availabilityExpr = (Expression)availability.get(choice);

				Field<?> availabilityField = encodeAvailability(choice, availabilityExpr, encoder);
				if(availabilityField != null){
					Feature availabilityFeature = new ContinuousFeature(encoder, availabilityField);

					feature = new InteractionFeature(encoder, FieldNameUtil.create("interaction", availabilityFeature, feature), DataType.DOUBLE, Arrays.asList(availabilityFeature, feature));
				}
			}

			RegressionTable regressionTable = RegressionModelUtil.createRegressionTable(Collections.singletonList(feature), Collections.singletonList(1d), null)
				.setTargetCategory(choice);

			regressionTables.add(regressionTable);
		}

		RegressionModel regressionModel = new RegressionModel(MiningFunction.CLASSIFICATION, ModelUtil.createMiningSchema(categoricalLabel), regressionTables)
			.setNormalizationMethod(RegressionModel.NormalizationMethod.SIMPLEMAX)
			.setOutput(ModelUtil.createProbabilityOutput(DataType.DOUBLE, categoricalLabel));

		models.add(regressionModel);

		MiningModel miningModel = MiningModelUtil.createModelChain(models, Segmentation.MissingPredictionTreatment.RETURN_MISSING);

		return encoder.encodePMML(miningModel);
	}

	public Map<?, ?> getV(){
		return getDict("V");
	}

	public Map<?, ?> getBetas(){
		return getDict("betas");
	}

	public Map<?, ?> getAvailability(){
		return getOptionalDict("availability");
	}

	static
	private RegressionModel encodeUtilityFunction(Object choice, Expression expression, Map<?, ?> betas, BiogemeEncoder encoder){
		declareVariables(expression, encoder);

		List<Feature> features = new ArrayList<>();
		List<Number> coefficients = new ArrayList<>();
		Number intercept = null;

		List<Expression> terms = extractTerms(expression);
		for(int i = 0; i < terms.size(); i++){
			Expression term = terms.get(i);

			if(term instanceof Beta){
				Beta beta = (Beta)term;

				if(intercept != null){
					throw new IllegalArgumentException();
				}

				intercept = beta.getValue(betas);
			} else

			{
				Beta beta = extractBeta(term);

				Feature feature;

				org.dmg.pmml.Expression pmmlExpression = term.toPMML();

				if(pmmlExpression instanceof FieldRef){
					FieldRef fieldRef = (FieldRef)pmmlExpression;

					Field<?> field = encoder.getField(fieldRef.requireField());

					feature = new ContinuousFeature(encoder, field);
				} else

				{
					String name = FieldNameUtil.create("term", choice, i);

					DerivedField derivedField = encoder.createDerivedField(name, OpType.CONTINUOUS, DataType.DOUBLE, pmmlExpression);

					feature = new ContinuousFeature(encoder, derivedField);
				}

				features.add(feature);

				Number coefficient;

				if(beta != null){
					coefficient = beta.getValue(betas);
				} else

				{
					coefficient = 1d;
				}

				coefficients.add(coefficient);
			}
		}

		RegressionModel regressionModel = new RegressionModel(MiningFunction.REGRESSION, ModelUtil.createMiningSchema(null), null)
			.setNormalizationMethod(RegressionModel.NormalizationMethod.NONE)
			.addRegressionTables(RegressionModelUtil.createRegressionTable(features, coefficients, intercept))
			.setOutput(ModelUtil.createPredictedOutput(FieldNameUtil.create("utilityFunction", choice), OpType.CONTINUOUS, DataType.DOUBLE, new ExpTransformation()));

		return regressionModel;
	}

	static
	public Field<?> encodeAvailability(Object choice, Expression expression, BiogemeEncoder encoder){

		if(expression instanceof Numeric){
			Numeric numeric = (Numeric)expression;

			Number value = numeric.getValue();
			if(value.doubleValue() != 1d){
				throw new IllegalArgumentException();
			}

			return null;
		} else

		if(expression instanceof Variable){
			Variable variable = (Variable)expression;

			String name = variable.getName();

			return encoder.getField(name);
		} else

		{
			throw new IllegalArgumentException();
		}
	}

	static
	private void declareVariables(Expression expression, BiogemeEncoder encoder){
		Function<Expression, Void> function = new Function<Expression, Void>(){

			@Override
			public Void apply(Expression expression){

				if(expression instanceof Variable){
					Variable variable = (Variable)expression;

					DataField dataField = encoder.getDataField(variable.getName());
					if(dataField == null){
						dataField = encoder.createDataField(variable.getName(), OpType.CONTINUOUS, DataType.DOUBLE);
					}
				}

				return null;
			}
		};

		expression.traverse(function);
	}

	static
	public Feature getPredictionFeature(Model model, BiogemeEncoder encoder){
		Output output = model.getOutput();

		if(output != null && output.hasOutputFields()){
			List<OutputField> outputFields = output.getOutputFields();

			OutputField outputField = Iterables.getLast(outputFields);

			return new ContinuousFeature(encoder, outputField);
		} else

		{
			throw new IllegalArgumentException();
		}
	}

	static
	private List<Expression> extractTerms(Expression expression){

		if(expression instanceof Plus){
			Plus plus = (Plus)expression;

			Expression left = plus.getLeft();
			Expression right = plus.getRight();

			List<Expression> result = new ArrayList<>();

			result.addAll(extractTerms(left));
			result.addAll(extractTerms(right));

			return result;
		}

		return Collections.singletonList(expression);
	}

	static
	private Beta extractBeta(Expression expression){

		if(expression instanceof Times){
			Times times = (Times)expression;

			Expression left = times.getLeft();
			Expression right = times.getRight();

			if(left instanceof Beta){
				Beta beta = (Beta)left;
				beta.setEnabled(false);

				return beta;
			} else

			if(right instanceof Beta){
				Beta beta = (Beta)right;
				beta.setEnabled(false);

				return beta;
			} else

			{
				Beta beta = extractBeta(left);
				if(beta != null){
					return beta;
				}

				beta = extractBeta(right);
				if(beta != null){
					return beta;
				}
			}
		}

		return null;
	}
}