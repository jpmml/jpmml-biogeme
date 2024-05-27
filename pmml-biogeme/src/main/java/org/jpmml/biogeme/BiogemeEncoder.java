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

import java.util.Arrays;
import java.util.List;

import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.OpType;
import org.jpmml.converter.TypeUtil;
import org.jpmml.python.PickleUtil;
import org.jpmml.python.PythonEncoder;

public class BiogemeEncoder extends PythonEncoder {

	public BiogemeEncoder(){
	}

	public DataField createChoiceField(String name, List<?> values){
		DataType dataType = TypeUtil.getDataType(values, DataType.STRING);

		return createDataField(name, OpType.CATEGORICAL, dataType, values);
	}

	public DataField createAvailabilityField(String name){
		return createDataField(name, OpType.CONTINUOUS, DataType.INTEGER, Arrays.asList(0, 1));
	}

	static {
		ClassLoader clazzLoader = BiogemeEncoder.class.getClassLoader();

		PickleUtil.init(clazzLoader, "biogeme2pmml.properties");
	}
}