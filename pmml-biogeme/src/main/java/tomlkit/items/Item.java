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
package tomlkit.items;

import org.jpmml.python.CythonObject;

abstract
public class Item extends CythonObject {

	public Item(String module, String name){
		super(module, name);
	}

	abstract
	public Object getValue();

	@Override
	public void __init__(Object[] args){
		super.__setstate__(INIT_ARGS, args);
	}

	public String getRaw(){
		return getString("raw");
	}

	private static final String[] INIT_ARGS = {
		"value",
		"trivia",
		"raw"
	};
}