package com.comp4903.AI;

public enum AIState {
	Aggressive(0),
	Cautious(1),
	Defensive(2),
	Retreat(3);
	
	private int code;
	
	private AIState(int c) { code = c; }
	
	public int getCode() { return code; }
}