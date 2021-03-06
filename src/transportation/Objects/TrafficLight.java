package transportation.Objects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import transportation.TransportationPanel;
import transportation.GUIs.Gui;
import astar.astar.Position;

public class TrafficLight implements ActionListener, Gui{
	//ALWAYS PLACE TRAFFIC LIGHT IN THE TOP LEFT GRID IN THE CENTER OF THE INTERSECTION OR THE TRANSPORTATION WILL BREAK HORRIBLY
	Position location;
	MovementTile[][] grid;
	Timer timer;
	TransportationPanel panel;
	
	private enum LightState {
		UPDOWN,
		UPDOWNCAUTION,
		LEFTRIGHT,
		LEFTRIGHTCAUTION
	}
	LightState light;

	public TrafficLight(Position location, MovementTile[][] grid) {
		timer = new Timer(5000, this);
		timer.start();
		this.location = location;
		this.grid = grid;

		light = LightState.UPDOWN;

		//SET THE MOVEMENT TILES TO BE WHAT THE INTERSECTION NEEDS
		for(int i = -2; i < 4; i++) {
			grid[location.getX()][location.getY()+i].setMovement(false, true, false, false, MovementTile.MovementType.TRAFFICCROSSNONE);
			grid[location.getX()+1][location.getY()+i].setMovement(true, false, false, false, MovementTile.MovementType.TRAFFICCROSSNONE);
			grid[location.getX()+i][location.getY()].setMovement(false, false, true, false, MovementTile.MovementType.TRAFFICCROSSNONE);
			grid[location.getX()+i][location.getY()+1].setMovement(false, false, false, true, MovementTile.MovementType.TRAFFICCROSSNONE);
		}
		changeCrossWalk(0, -2, MovementTile.MovementType.TRAFFICCROSSROAD);
		changeCrossWalk(0, 2, MovementTile.MovementType.TRAFFICCROSSROAD);
		changeCrossWalk(-2, 0, MovementTile.MovementType.TRAFFICCROSSWALK);
		changeCrossWalk(2, 0, MovementTile.MovementType.TRAFFICCROSSWALK);
		
		grid[location.getX()][location.getY()].setMovement(false, true, true, false, MovementTile.MovementType.TRAFFICCROSSINTERSECTION);
		grid[location.getX()+1][location.getY()].setMovement(true, false, true, false, MovementTile.MovementType.TRAFFICCROSSINTERSECTION);
		grid[location.getX()][location.getY()+1].setMovement(false, true, false, true, MovementTile.MovementType.TRAFFICCROSSINTERSECTION);
		grid[location.getX()+1][location.getY()+1].setMovement(true, false, false, true, MovementTile.MovementType.TRAFFICCROSSINTERSECTION);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//based on state it changes the way people may walk and drive
		switch (light) {
		case UPDOWN://Change it to caution, lock out everyone from intersection
			timer.setDelay(2500);
			light = LightState.UPDOWNCAUTION;
			changeCrossWalk(0, -2, MovementTile.MovementType.TRAFFICCROSSNONE);
			changeCrossWalk(0, 2, MovementTile.MovementType.TRAFFICCROSSNONE);
			changeCrossWalk(-2, 0, MovementTile.MovementType.TRAFFICCROSSNONE);
			changeCrossWalk(2, 0, MovementTile.MovementType.TRAFFICCROSSNONE);
			break;
		case UPDOWNCAUTION://change direction
			if(checkIntersectionClear()) {
				timer.setDelay(5000);
				light = LightState.LEFTRIGHT;
				changeCrossWalk(0, -2, MovementTile.MovementType.TRAFFICCROSSWALK);
				changeCrossWalk(0, 2, MovementTile.MovementType.TRAFFICCROSSWALK);
				changeCrossWalk(-2, 0, MovementTile.MovementType.TRAFFICCROSSROAD);
				changeCrossWalk(2, 0, MovementTile.MovementType.TRAFFICCROSSROAD);
			}
			break;
		case LEFTRIGHT://Change it to caution, lock out everyone from intersection
			timer.setDelay(2500);
			light = LightState.LEFTRIGHTCAUTION;
			changeCrossWalk(0, -2, MovementTile.MovementType.TRAFFICCROSSNONE);
			changeCrossWalk(0, 2, MovementTile.MovementType.TRAFFICCROSSNONE);
			changeCrossWalk(-2, 0, MovementTile.MovementType.TRAFFICCROSSNONE);
			changeCrossWalk(2, 0, MovementTile.MovementType.TRAFFICCROSSNONE);
			break;
		case LEFTRIGHTCAUTION://change direction
			if(checkIntersectionClear()) {
				timer.setDelay(5000);
				light = LightState.UPDOWN;
				changeCrossWalk(0, -2, MovementTile.MovementType.TRAFFICCROSSROAD);
				changeCrossWalk(0, 2, MovementTile.MovementType.TRAFFICCROSSROAD);
				changeCrossWalk(-2, 0, MovementTile.MovementType.TRAFFICCROSSWALK);
				changeCrossWalk(2, 0, MovementTile.MovementType.TRAFFICCROSSWALK);
			}
			break;
		}
	}

	private void changeCrossWalk(int x, int y, MovementTile.MovementType state) {
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 2; j++) {
				grid[i+x+location.getX()][j+y+location.getY()].setMovementType(state);
			}
		}
	}
	
	private boolean checkIntersectionClear() {
		for(int i = 0; i < 2; i++) {
			for(int j = -2; j < 4; j++) {
				if(grid[i+location.getX()][j+location.getY()].availablePermits() != 1) {
					return false;
				}
			}
		}
		for(int i = -2; i < 4; i++) {
			for(int j = 0; j < 2; j++) {
				if(grid[i+location.getX()][j+location.getY()].availablePermits() != 1) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void updatePosition() {}

	@Override
	public void draw(Graphics2D g, Point offset) {
		switch (light) {
		case UPDOWN:
			g.setColor(Color.GREEN);
			g.fillOval((int)(400-offset.getX()+15), (int)(350-offset.getY()+4), 16, 16);//up
			g.fillOval((int)(400-offset.getX()+15), (int)(350-offset.getY()+30), 16, 16);//down
			g.setColor(Color.RED);
			g.fillOval((int)(400-offset.getX()+2), (int)(350-offset.getY()+17), 16, 16);//left
			g.fillOval((int)(400-offset.getX()+28), (int)(350-offset.getY()+17), 16, 16);//right
			break;
		case UPDOWNCAUTION:
			g.setColor(Color.YELLOW);
			g.fillOval((int)(400-offset.getX()+15), (int)(350-offset.getY()+4), 16, 16);//up
			g.fillOval((int)(400-offset.getX()+15), (int)(350-offset.getY()+30), 16, 16);//down
			g.setColor(Color.RED);
			g.fillOval((int)(400-offset.getX()+2), (int)(350-offset.getY()+17), 16, 16);//left
			g.fillOval((int)(400-offset.getX()+28), (int)(350-offset.getY()+17), 16, 16);//right
			break;
		case LEFTRIGHT:
			g.setColor(Color.RED);
			g.fillOval((int)(400-offset.getX()+15), (int)(350-offset.getY()+4), 16, 16);//up
			g.fillOval((int)(400-offset.getX()+15), (int)(350-offset.getY()+30), 16, 16);//down
			g.setColor(Color.GREEN);
			g.fillOval((int)(400-offset.getX()+2), (int)(350-offset.getY()+17), 16, 16);//left
			g.fillOval((int)(400-offset.getX()+28), (int)(350-offset.getY()+17), 16, 16);//right
			break;
		case LEFTRIGHTCAUTION://change direction
			g.setColor(Color.RED);
			g.fillOval((int)(400-offset.getX()+15), (int)(350-offset.getY()+4), 16, 16);//up
			g.fillOval((int)(400-offset.getX()+15), (int)(350-offset.getY()+30), 16, 16);//down
			g.setColor(Color.YELLOW);
			g.fillOval((int)(400-offset.getX()+2), (int)(350-offset.getY()+17), 16, 16);//left
			g.fillOval((int)(400-offset.getX()+28), (int)(350-offset.getY()+17), 16, 16);//right
			break;
		}
		g.setColor(Color.BLACK);
		g.drawOval((int)(400-offset.getX()+15), (int)(350-offset.getY()+4), 16, 16);//up
		g.drawOval((int)(400-offset.getX()+15), (int)(350-offset.getY()+30), 16, 16);//down
		g.drawOval((int)(400-offset.getX()+2), (int)(350-offset.getY()+17), 16, 16);//left
		g.drawOval((int)(400-offset.getX()+28), (int)(350-offset.getY()+17), 16, 16);//right
	}

	@Override
	public boolean isPresent() {
		return true;
	}

	@Override
	public void setPanel(TransportationPanel p) {
		panel = p;
	}

	@Override
	public String returnType() {
		return "Traffic Light";
	}

	public void stop() {
		timer.stop();
		light = LightState.UPDOWN;
		changeCrossWalk(0, -2, MovementTile.MovementType.TRAFFICCROSSROAD);
		changeCrossWalk(0, 2, MovementTile.MovementType.TRAFFICCROSSROAD);
		changeCrossWalk(-2, 0, MovementTile.MovementType.TRAFFICCROSSWALK);
		changeCrossWalk(2, 0, MovementTile.MovementType.TRAFFICCROSSWALK);
		
	}
}
