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

import java.util.Arrays;
import java.util.Map;

import numpy.core.ScalarUtil;
import org.dmg.pmml.FieldRef;

public class Beta extends ElementaryExpression {

	public Beta(String module, String name){
		super(module, name);
	}

	@Override
	public FieldRef toPMML(){
		Boolean enabled = getEnabled();

		if(!enabled){
			String name = getName();

			throw new IllegalStateException(name);
		}

		return super.toPMML();
	}

	public Number getValue(Map<?, ?> betas){
		Integer status = getStatus();

		switch(status){
			case Beta.STATUS_ESTIMATE:
				{
					String name = getName();

					Number value = (Number)ScalarUtil.decode(betas.get(name));
					if(value == null){
						throw new IllegalArgumentException(name);
					}

					return value;
				}
			case Beta.STATUS_MAINTAIN_INITIAL_VALUE:
				{
					Number value = getInitValue();

					return value;
				}
			default:
				throw new IllegalArgumentException();
		}
	}

	public Boolean getEnabled(){
		Boolean enabled = getOptionalBoolean("_enabled");

		if(enabled == null){
			enabled = Boolean.TRUE;
		}

		return enabled;
	}

	public Expression setEnabled(Boolean enabled){
		setattr("_enabled", enabled);

		return this;
	}

	public Number getInitValue(){
		return getNumber("initValue");
	}

	public Integer getStatus(){
		return getEnum("status", this::getInteger, Arrays.asList(Beta.STATUS_ESTIMATE, Beta.STATUS_MAINTAIN_INITIAL_VALUE));
	}

	private static final int STATUS_ESTIMATE = 0;
	private static final int STATUS_MAINTAIN_INITIAL_VALUE = 1;
}