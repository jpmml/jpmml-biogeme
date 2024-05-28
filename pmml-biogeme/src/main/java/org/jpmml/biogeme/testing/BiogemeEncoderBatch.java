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
package org.jpmml.biogeme.testing;

import java.util.Map;
import java.util.function.Predicate;

import com.google.common.base.Equivalence;
import org.dmg.pmml.PMML;
import org.jpmml.biogeme.BiogemeEncoder;
import org.jpmml.biogeme.Experiment;
import org.jpmml.evaluator.ResultField;
import org.jpmml.python.testing.PythonEncoderBatch;

abstract
public class BiogemeEncoderBatch extends PythonEncoderBatch {

	public BiogemeEncoderBatch(String algorithm, String dataset, Predicate<ResultField> predicate, Equivalence<Object> equivalence){
		super(algorithm, dataset, predicate, equivalence);
	}

	@Override
	abstract
	public BiogemeEncoderBatchTest getArchiveBatchTest();

	@Override
	public PMML getPMML() throws Exception {
		BiogemeEncoder encoder = new BiogemeEncoder();

		Map<?, ?> dict = loadPickle(Map.class);

		Experiment experiment = new Experiment();
		experiment.update((Map)dict);

		PMML pmml = experiment.encodePMML(encoder);

		validatePMML(pmml);

		return pmml;
	}
}