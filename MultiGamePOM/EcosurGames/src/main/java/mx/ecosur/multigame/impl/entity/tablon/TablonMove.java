/*
* Copyright (C) 2008, 2009 ECOSUR, Andrew Waterman
*
* Licensed under the Academic Free License v. 3.2.
* http://www.opensource.org/licenses/afl-3.0.php
*/

/**
 * @author awaterma@ecosur.mx
 */
package mx.ecosur.multigame.impl.entity.tablon;

import javax.persistence.Entity;

import mx.ecosur.multigame.impl.model.GridMove;
import mx.ecosur.multigame.impl.model.GridPlayer;
import mx.ecosur.multigame.impl.enums.tablon.TokenType;
import mx.ecosur.multigame.model.implementation.GamePlayerImpl;

@Entity
public class TablonMove extends GridMove {
	
	private static final long serialVersionUID = 1L;

	private TokenType type, replacementType;
	
	private boolean badYear, premium;
	
	public TablonMove() {
		super();
		badYear = false;
	}
	
	public TablonMove(GridPlayer player, TablonFicha destination) {
		super (player, destination);
		type = destination.getType();
	}
	
	public TablonMove(GridPlayer player, TablonFicha current, TablonFicha destination)
	{
		super (player, current, destination);
	}

    public TokenType getType () {
		if (getDestinationCell() == null)
			type = TokenType.UNKNOWN;
		else {
			TablonFicha destination = (TablonFicha) getDestinationCell();
			type = destination.getType();
		}
		
		return type;
	}
	
	public void setType (TokenType type) {
		this.type = type;
	}

	public TokenType getReplacementType() {
		if (replacementType == null) {
			replacementType = TokenType.UNKNOWN;
			if (getCurrentCell() instanceof TablonFicha) {
					TablonFicha current = (TablonFicha) getCurrentCell();
					replacementType = current.getType();
			}
		}

		return replacementType;
	}

	public void setReplacementType(TokenType replacementType) {
		this.replacementType = replacementType;
	}
	
	public boolean isBadYear () {
		return badYear;
	}
	
	public void setBadYear (boolean year) {
		badYear = year;
	}

	/* (non-Javadoc)
	 * @see mx.ecosur.multigame.model.implementation.MoveImpl#setPlayer(mx.ecosur.multigame.model.implementation.AgentImpl)
	 */
	public void setPlayer(GamePlayerImpl player) {
		this.player = (GridPlayer) player;
	}

    public GridPlayer getPlayer () {
        return player;
    }

    @Override
    public int hashCode() {
       int curCode = 1, destCode = 1;
       if (current != null)
        curCode = curCode - current.hashCode();
       if (destination != null)
         destCode = destCode + destination.hashCode();
       return 31 * curCode + destCode;
    }

    @Override
    public boolean equals(Object obj) {
        boolean ret = false;
        if (obj instanceof TablonMove) {
            TablonMove comparison = (TablonMove) obj;
            if (current != null && destination !=null) {
                ret = current.equals( (comparison.getCurrentCell())) &&
                      destination.equals(comparison.getDestinationCell());
            } else if (destination != null) {
                ret = destination.equals(comparison.getDestinationCell());
              }
        }

        return ret;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
       throw new CloneNotSupportedException ();
    }
}
