package transportation.GUIs;
import java.awt.Graphics2D;
import java.awt.Point;

import AnimationTools.AnimationModule;
import transportation.TransportationPanel;
import transportation.Agents.WalkerAgent;

public class WalkerGui implements Gui{

	private float xPos, yPos, xDestination, yDestination, xLast, yLast, speed;
	private AnimationModule animModule;
	boolean reachedHalfway, reachedDestination;
	boolean crashed = false;

	TransportationPanel panel;
	public WalkerAgent agent;
	boolean isPresent = true;

	public WalkerGui(float xPos, float yPos, WalkerAgent agent) {
		this.xPos = xPos * 25;
		this.yPos = yPos * 25;
		this.xDestination = xPos * 25;
		this.yDestination = yPos * 25;
		this.xLast = xPos * 25;
		this.yLast = yPos * 25;
		speed = 1.60f;
		this.agent = agent;		
		reachedHalfway = true;
		reachedDestination = true;

		animModule = new AnimationModule("Edgar", "WalkDown", 10);
	}

	public void updatePosition() {
		if(!crashed) {
			if(Math.abs(xDestination - xPos) <= speed)
				xPos = xDestination;
			else if(xPos < xDestination) {
				xPos += speed;
				animModule.changeAnimation("WalkRight");
			}
			else if(xPos > xDestination) {
				xPos -= speed;
				animModule.changeAnimation("WalkLeft");
			}

			if(Math.abs(yDestination - yPos) <= speed)
				yPos = yDestination;
			if(yPos < yDestination) {
				yPos += speed;
				animModule.changeAnimation("WalkDown");
			}
			else if(yPos > yDestination) {
				yPos -= speed;
				animModule.changeAnimation("WalkUp");
			}

			if(xDestination == xPos && Math.abs(yDestination-yPos) <= speed && !reachedHalfway) {
				agent.msgHalfway();
				reachedHalfway = true;
			}
			if(Math.abs(xDestination-xPos) <= speed && yDestination == yPos && !reachedHalfway) {
				agent.msgHalfway();
				reachedHalfway = true;
			}

			if(xPos == xDestination && yPos == yDestination && !reachedDestination) {
				xLast = xDestination;
				yLast = yDestination;
				reachedDestination = true;
				agent.msgDestination();
			}

			if(animModule.getAnimation().equals("Taunt") && animModule.getLastFrame())
				agent.msgDestination();
		}
	}

	public void draw(Graphics2D g, Point offset) {
		animModule.updateAnimation();
		if(xPos - offset.getX() < -30 || xPos - offset.getX() > panel.getWidth()+50 || yPos - offset.getY() < -30 || yPos - offset.getY() > panel.getHeight()+50) return;
		g.drawImage(animModule.getImage(), (int)xPos - (int)offset.getX(), (int)yPos - (int)offset.getY(), null);
		g.drawString(agent.getPerson().getName(), (int)xPos - (int)offset.getX(), (int)yPos - (int)offset.getY());
	}

	public void setDestination (float xDestination, float yDestination) {
		xLast = xPos;
		yLast = yPos;
		this.xDestination = xDestination * 25;
		this.yDestination = yDestination * 25;
		reachedHalfway = false;
		reachedDestination = false;
	}

	public void doTauntAndLeave() {
		animModule.changeAnimation("Taunt", 4);
	}

	public void setIgnore() {
		isPresent = false;
	}

	public void setStill() {
		animModule.setStill();
	}

	public void setMoving() {
		animModule.setMoving();
	}

	public boolean isPresent() {
		return isPresent;
	}

	public void crash() {
		//Release other semaphore if haven't already
		//Change animation
		//Change boolean to prevent position updating
		crashed = true;
		agent.crashRemoval(reachedHalfway);
		reachedHalfway = true;
		animModule.changeAnimation("Crash", 10);
		animModule.setNoLoop();
		//		agent.animSem.release();
		//		agent.stopThread();
	}
	@Override
	public void setPanel(TransportationPanel p) {
		panel = p;
	}

	@Override
	public String returnType() {
		return "Walker";
	}

	public void crashDone() {
		isPresent = false;
		animModule.setStill();
	}
}
