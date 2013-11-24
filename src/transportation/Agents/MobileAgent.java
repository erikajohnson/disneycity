package transportation.Agents;

import transportation.Objects.MovementTile;
import agent.*;

public abstract class MobileAgent extends Agent {
	
	
	protected abstract boolean pickAndExecuteAnAction();
	
	MovementTile currentSection;
	MovementTile nextSection;
}
