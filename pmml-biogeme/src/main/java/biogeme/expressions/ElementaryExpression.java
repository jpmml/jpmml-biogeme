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

import org.dmg.pmml.FieldRef;

abstract
public class ElementaryExpression extends Expression {

	public ElementaryExpression(String module, String name){
		super(module, name);
	}

	@Override
	public FieldRef toPMML(){
		String name = getName();

		return new FieldRef(name);
	}

	@Override
	public String toTreeString(String indent){
		String name = getName();

		return super.toTreeString(indent) + " // " + name;
	}

	public String getName(){
		return getString("name");
	}
}