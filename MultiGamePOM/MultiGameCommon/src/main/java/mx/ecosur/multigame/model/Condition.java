/*
* Copyright (C) 2008, 2009 ECOSUR, Andrew Waterman
*
* Licensed under the Academic Free License v. 3.2.
* http://www.opensource.org/licenses/afl-3.0.php
*/

/**
 * @author awaterma@ecosur.mx
 */
package mx.ecosur.multigame.model;

import mx.ecosur.multigame.model.implementation.ConditionImpl;

public class Condition implements Model {

	private static final long serialVersionUID = -5295777444155230289L;
	
	private ConditionImpl conditionImpl;
	
	public Condition () {
		super();
	}

	public Condition (ConditionImpl conditionImpl) {
		this.conditionImpl = conditionImpl;
	}
	
	public String getReason() {
		return conditionImpl.getReason();		
	}
	
	public Object[] getTriggers () {
		return conditionImpl.getTriggers();
	}

	/**
	 * @return
	 */
	public ConditionImpl getImplementation() {
		return conditionImpl;
	}
	
	public void setImplementation (ConditionImpl conditionImpl) {
		this.conditionImpl = conditionImpl;
	}

}
