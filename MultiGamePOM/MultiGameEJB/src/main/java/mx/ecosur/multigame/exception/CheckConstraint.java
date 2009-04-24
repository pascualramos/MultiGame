/*
* Copyright (C) 2008, 2009 ECOSUR, Andrew Waterman
*
* Licensed under the Academic Free License v. 3.2.
* http://www.opensource.org/licenses/afl-3.0.php
*/

/**
 * @author awaterma@ecosur.mx
 */
package mx.ecosur.multigame.exception;

import java.io.Serializable;

import mx.ecosur.multigame.ejb.entity.GamePlayer;

@SuppressWarnings("serial")
public class CheckConstraint implements Serializable {
	
	String reason;
	GamePlayer initiator;
	Object [] violators;
	
	public CheckConstraint () {
		super();
	}
	
	public CheckConstraint (String reason, GamePlayer initiator, Object...violators) 
	{
		super();
		this.reason = reason;
		this.initiator = initiator;
		this.violators = violators;
	}

	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @return the initiator
	 */
	public GamePlayer getInitiator() {
		return initiator;
	}

	/**
	 * @return the violators
	 */
	public Object[] getViolators() {
		return violators;
	}

	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * @param initiator the initiator to set
	 */
	public void setInitiator(GamePlayer initiator) {
		this.initiator = initiator;
	}

	/**
	 * @param violators the violators to set
	 */
	public void setViolators(Object[] violators) {
		this.violators = violators;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj instanceof CheckConstraint) {
			CheckConstraint test = (CheckConstraint) obj;
			ret = ret && test.getInitiator().equals(initiator);
			ret = ret && test.getReason().equals(reason);
			ret = ret && test.getViolators().equals(violators);
		}
		
		return ret;
	}
}
