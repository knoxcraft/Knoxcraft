package org.knoxcraft.turtle3d;

import java.util.Stack;

import org.knoxcraft.serverturtle.SpongeTurtle;
import org.slf4j.Logger;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;

public class KCTUndoScript {
	private KCTScript script;
	private Vector3i startLocation;
	private TurtleDirection direction;
	private World world;
	private Stack<KCTWorldBlockInfo> undoStack;
	private Logger log;
	
	public KCTUndoScript(KCTScript script, Vector3i startLocation, TurtleDirection direction, World world, Stack<KCTWorldBlockInfo> undoStack, Logger log) {
		this.script = script;
		this.startLocation = startLocation;
		this.direction = direction;
		this.world = world;
		this.undoStack = undoStack;
		this.log = log;
	}
	
	public void executeUndo() {
		SpongeTurtle turtle = new SpongeTurtle(log);
		turtle.setLoc(startLocation);
		turtle.setTurtleDirection(direction);
		turtle.setWorld(world);
		turtle.executeUndoScript(script, undoStack);
	}
}
