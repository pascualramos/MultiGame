/*
* Copyright (C) 2009 ECOSUR, Andrew Waterman
*
* Licensed under the Academic Free License v. 3.2.
* http://www.opensource.org/licenses/afl-3.0.php
*/

/**
 * @author awaterma@ecosur.mx
 */
package mx.ecosur.multigame.impl.entity.tablon;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.apache.commons.lang.builder.HashCodeBuilder;

import mx.ecosur.multigame.impl.Color;
import mx.ecosur.multigame.impl.model.GridCell;
import mx.ecosur.multigame.impl.enums.tablon.*;

@Entity ()
public class TablonFicha extends GridCell {

	private static final long serialVersionUID = -8048552960014554186L;
	private TokenType type;
	
	public TablonFicha () {
		super(); 
	}

	public TablonFicha (int column, int row, Color color, TokenType type) {
		super(column, row, color);
		this.type = type;
	}

	@Enumerated (EnumType.STRING)
	public TokenType getType() {
		return type;
	}

	@Enumerated (EnumType.STRING)
	public void setType(TokenType type) {
		this.type = type;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj instanceof TablonFicha) {
			TablonFicha comp = (TablonFicha) obj;
			ret = (comp.type.equals(this.type) &&
					comp.getRow() == getRow() &&
					comp.getColumn() == getColumn() &&
					comp.getColor() == getColor());
		} 
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder() 
        .append(getColumn()) 
        .append(getRow())
        .append(getType())
        .append(getColor())
        .toHashCode(); 
	}
	
	/* (non-Javadoc)
	 * @see mx.ecosur.multigame.model.Cell#toString()
	 */
	@Override
	public String toString() {
		return "[" + this.getClass().getName() + "], type = " + type + ", " + super.toString();		
	}	
}
