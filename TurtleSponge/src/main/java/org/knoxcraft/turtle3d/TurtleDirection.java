package org.knoxcraft.turtle3d;

import org.spongepowered.api.util.Direction.Division;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

/**
 * @author kakoijohn
 *
 */
public enum TurtleDirection {
	NORTH(new Vector3i(0, 0, -1), Division.CARDINAL),    //North(0, 0, -1) = 0
    NORTHEAST(new Vector3i(1, 0, -1), Division.ORDINAL), //NorthEast(1, 0, -1) = 1

    EAST(new Vector3i(1, 0, 0), Division.CARDINAL),      //East(1, 0, 0) = 2
    SOUTHEAST(new Vector3i(1, 0, 1), Division.ORDINAL),  //SouthEast(1, 0, 1) = 3

    SOUTH(new Vector3i(0, 0, 1), Division.CARDINAL),     //South(0, 0, 1) = 4
    SOUTHWEST(new Vector3i(-1, 0, 1), Division.ORDINAL), //SouthWest(-1, 0, 1) = 5

    WEST(new Vector3i(-1, 0, 0), Division.CARDINAL),     //West(-1, 0, 0) = 6
    NORTHWEST(new Vector3i(-1, 0, -1), Division.ORDINAL),//NorthWest(-1, 0, -1) = 7

	UP(new Vector3i(0, 1, 0), Division.CARDINAL),        //Up(0, 1, 0) = 8
    DOWN(new Vector3i(0, -1, 0), Division.CARDINAL),     //Down(0, -1, 0) = 9
	
	NONE(new Vector3i(0, 0, 0), Division.NONE);          //None(0, 0, 0) = 10

	public final Vector3i direction;
	public final Division division;
	
	TurtleDirection(Vector3i vector3i, Division division) {
	    this.direction = vector3i;
	    this.division = division;
	}
	
	public static TurtleDirection valueOf(int val) {
	    return TurtleDirection.values()[val];
	}
	
	public int getIntValue() {
	    return ordinal();
	}
	
	public static TurtleDirection getTurtleDirection(Vector3d direction) {
        double d = direction.getY() / 360 * 8;
        int x = (int) Math.round(d);
        
        while (x < 0) {
            x += 8;
        }

        if (x == 0 || x == 8) {
            return TurtleDirection.SOUTH;
        } else if (x == 1) {
            return TurtleDirection.SOUTHWEST;
        } else if (x == 2) {
            return TurtleDirection.WEST;
        } else if (x == 3) {
            return TurtleDirection.NORTHWEST;
        } else if (x == 4) {
            return TurtleDirection.NORTH;
        } else if (x == 5) {
            return TurtleDirection.NORTHEAST;
        } else if (x == 6) {
            return TurtleDirection.EAST;
        } else if (x == 7) {
            return TurtleDirection.SOUTHEAST;
        } else {
            throw new RuntimeException("Direction invalid = " + direction);
        }
    }

	
	/**
	 * @return the opposite direction of the current heading.
	 */
	public TurtleDirection flip () {
		if (this == UP)
			return DOWN;
		else if (this == DOWN)
			return UP;
		
		int currentDir = this.getIntValue();
		int newDir = (currentDir+4)%8;
		return TurtleDirection.valueOf(newDir);
	}
	
	public TurtleDirection turn(boolean left, int units) {
		int currentDir = this.getIntValue();
		if (left) {
			int newDir = currentDir - units;
			while (newDir < 0) {
				newDir += 8;
			}
			return TurtleDirection.valueOf(newDir);
		} else {
			int newdir = currentDir + units;
			newdir = newdir % 8;
			return TurtleDirection.valueOf(newdir);
		}
	}
}
