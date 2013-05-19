package com.comp4903.project.gameEngine.networking;

import com.comp4903.project.gameEngine.enums.ActionType;
import com.comp4903.project.gameEngine.enums.SkillType;
import com.comp4903.project.gameEngine.enums.TypeFinder;
import com.comp4903.project.network.NetworkMessage;

public class Action {
	public ActionType action;
	public int uIDOne;
	public int uIDTwo;
	// Move
	public int x;
	public int y;
	// Headshot
	public int decisionNum;
	// Attack
	public int numOfAttacks;
	public int[] attack;
	
	public Action(){
		action = ActionType.None;
		uIDOne = -1;
		uIDTwo = -1;
		x = -1;
		y = -1;
		decisionNum = -1;
		numOfAttacks = 0;
		attack = new int[numOfAttacks];
	}
	
	public NetworkMessage getActionMessage(){
		NetworkMessage message = new NetworkMessage();
		message.append("" + action);
		message.append(uIDOne);
		message.append(uIDTwo);
		switch(action){
			case Attack:
				message.append(numOfAttacks);
				for (int i = 0; i < numOfAttacks; i++)
					message.append(attack[i]);
				break;
			case Defend:
				break;
			case Grab:
				break;
			case Headshot:
				message.append(decisionNum);
				break;
			case Heal:
				break;
			case Move:
				message.append(x);
				message.append(y);
				break;
			default:
				break;
		}
		return message;
	}
	
	public boolean decodeMessage(NetworkMessage message){
		action = TypeFinder.findActionType(message.readString());
		uIDOne = message.readInt();
		uIDTwo = message.readInt();
		switch (action){
			case Attack:
				numOfAttacks = message.readInt();
				break;
			case Defend:
				break;
			case Grab:
				break;
			case Headshot:
				decisionNum = message.readInt();
				break;
			case Heal:
				break;
			case Move:
				x = message.readInt();
				y = message.readInt();
				break;
			default:
				return false;
		}
		return true;
	}
}
