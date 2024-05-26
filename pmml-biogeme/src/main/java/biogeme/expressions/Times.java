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
import org.jpmml.converter.ExpressionUtil;

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

		org.dmg.pmml.Expression result;

		result = toPMML(left, right);
		if(result != null){
			return result;
		}

		result = toPMML(right, left);
		if(result != null){
			return result;
		}

		return super.toPMML();
	}

	static
	private org.dmg.pmml.Expression toPMML(Expression left, Expression right){

		if(left instanceof Beta){
			Beta beta = (Beta)left;

			if(!beta.getEnabled()){
				return right.toPMML();
			}

			return null;
		} else

		if(left instanceof ComparisonExpression){

			if(right instanceof ComparisonExpression){

				return ExpressionUtil.createApply(PMMLFunctions.IF,
					ExpressionUtil.createApply(PMMLFunctions.AND,
						left.toPMML(),
						right.toPMML()
					),
					ExpressionUtil.createConstant(1d),
					ExpressionUtil.createConstant(0d)
				);
			} else

			{
				return ExpressionUtil.createApply(PMMLFunctions.IF,
					left.toPMML(),
					right.toPMML(),
					ExpressionUtil.createConstant(0d)
				);
			}
		}

		return null;
	}
}