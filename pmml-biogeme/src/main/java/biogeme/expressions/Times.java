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
package biogeme.expressions;

import org.dmg.pmml.PMMLFunctions;

public class Times extends ArithmeticExpression {

	public Times(String module, String name){
		super(module, name);
	}

	@Override
	public String getPMMLFunction(){
		return PMMLFunctions.MULTIPLY;
	}

	@Override
	public org.dmg.pmml.Expression toPMML(){
		Expression left = getLeft();
		Expression right = getRight();

		if(isEnabled(left) && !isEnabled(right)){
			return left.toPMML();
		} else

		if(!isEnabled(left) && isEnabled(right)){
			return right.toPMML();
		}

		return super.toPMML();
	}

	static
	private boolean isEnabled(Expression expression){

		if(expression instanceof Beta){
			Beta beta = (Beta)expression;

			return beta.getEnabled();
		}

		return true;
	}
}