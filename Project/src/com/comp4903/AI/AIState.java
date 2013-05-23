package com.comp4903.AI;

public enum AIState {
	Aggressive(0),
	Defensive(1),
	Retreat(2);
	
	private int code;
	
	private AIState(int c) { code = c; }
	
	public int getCode() { return code; }
}