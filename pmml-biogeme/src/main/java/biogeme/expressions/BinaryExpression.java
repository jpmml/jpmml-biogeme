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

abstract
public class BinaryExpression extends Expression {

	public BinaryExpression(String module, String name){
		super(module, name);
	}

	@Override
	public String toTreeString(String indent){
		Expression left = getLeft();
		Expression right = getRight();

		return super.toTreeString(indent) + "\n" +
			left.toTreeString("\t" + indent) + "\n" +
			right.toTreeString("\t" + indent);
	}

	public Expression getLeft(){
		return get("left", Expression.class);
	}

	public Expression getRight(){
		return get("right", Expression.class);
	}
}