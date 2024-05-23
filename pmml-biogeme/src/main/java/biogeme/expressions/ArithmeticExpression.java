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

import org.jpmml.converter.ExpressionUtil;

abstract
public class ArithmeticExpression extends BinaryExpression {

	public ArithmeticExpression(String module, String name){
		super(module, name);
	}

	abstract
	public String getPMMLFunction();

	@Override
	public org.dmg.pmml.Expression toPMML(){
		String pmmlFunction = getPMMLFunction();

		Expression left = getLeft();
		Expression right = getRight();

		return ExpressionUtil.createApply(pmmlFunction,
			left.toPMML(),
			right.toPMML()
		);
	}
}