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
package biogeme.results;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.razorvine.pickle.objects.ClassDict;
import org.jpmml.python.HasArray;
import org.jpmml.python.PythonObject;

public class Results extends PythonObject {

	public Results(String module, String name){
		super(module, name);
	}

	public Map<String, Number> getBetas(){
		ClassDict data = getData();

		List<String> betaNames = (List)data.get("betaNames");
		List<Number> betaValues = (List)((HasArray)data.get("betaValues")).getArrayContent();

		Map<String, Number> result = new LinkedHashMap<>();

		for(int i = 0; i < betaNames.size(); i++){
			String betaName = betaNames.get(i);
			Number betaValue = betaValues.get(i);

			result.put(betaName, betaValue);
		}

		return result;
	}

	public ClassDict getData(){
		return get("data", ClassDict.class);
	}
}