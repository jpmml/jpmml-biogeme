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

import java.util.function.Predicate;

import com.google.common.base.Equivalence;
import org.jpmml.evaluator.ResultField;
import org.jpmml.evaluator.testing.PMMLEquivalence;
import org.jpmml.python.testing.PythonEncoderBatchTest;

abstract
public class BiogemeEncoderBatchTest extends PythonEncoderBatchTest {

	public BiogemeEncoderBatchTest(){
		super(new PMMLEquivalence(1e-13, 1e-13));
	}

	@Override
	public BiogemeEncoderBatch createBatch(String algorithm, String dataset, Predicate<ResultField> predicate, Equivalence<Object> equivalence){
		BiogemeEncoderBatch result = new BiogemeEncoderBatch(algorithm, dataset, predicate, equivalence){

			@Override
			public BiogemeEncoderBatchTest getArchiveBatchTest(){
				return BiogemeEncoderBatchTest.this;
			}
		};

		return result;
	}
}