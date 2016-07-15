package org.knoxcraft.turtle3d;

import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.Direction.Division;

import com.flowpowered.math.vector.Vector3d;

public enum TurtleDirection {
	NORTH(new Vector3d(0, 0, -1), Division.CARDINAL),    //North(0, 0, -1) = 0
    NORTHEAST(new Vector3d(1, 0, -1), Division.ORDINAL), //NorthEast(1, 0, -1) = 1

    EAST(new Vector3d(1, 0, 0), Division.CARDINAL),      //East(1, 0, 0) = 2
    SOUTHEAST(new Vector3d(1, 0, 1), Division.ORDINAL),  //SouthEast(1, 0, 1) = 3

    SOUTH(new Vector3d(0, 0, 1), Division.CARDINAL),     //South(0, 0, 1) = 4
    SOUTHWEST(new Vector3d(-1, 0, 1), Division.ORDINAL), //SouthWest(-1, 0, 1) = 5

    WEST(new Vector3d(-1, 0, 0), Division.CARDINAL),     //West(-1, 0, 0) = 6
    NORTHWEST(new Vector3d(-1, 0, -1), Division.ORDINAL),//NorthWest(-1, 0, -1) = 7

    NONE(new Vector3d(0, 0, 0), Division.NONE);          //None(0, 0, 0) = 8

	private final Vector3d direction;
	private final Division division;
	
	TurtleDirection(Vector3d vector3d, Division division) {
	    if (vector3d.lengthSquared() == 0) {
	        // Prevent normalization of the zero direction
	        this.direction = vector3d;
	    } else {
	        this.direction = vector3d.normalize();
	    }
	    this.division = division;
	}
	
	public int getIntValue() {
		int x, y, z;
		x = (int) direction.getX();
		y = (int) direction.getY();
		z = (int) direction.getZ();
		
		if (y == 0) {
			if (x == 0 && z == -1)  //North(0, 0, -1) = 0     
				return 0;            
			if (x == 1 && z == -1)  //NorthEast(1, 0, -1) = 1                           
				return 1;           
			if (x == 1 && z == 0)   //East(1, 0, 0) = 2        
				return 2;           
			if (x == 1 && z == 1)   //SouthEast(1, 0, 1) = 3   
				return 3;                                      
			if (x == 0 && z == 1)   //South(0, 0, 1) = 4       
				return 4;           
			if (x == 1 && z == 1)   //SouthWest(-1, 0, 1) = 5  
				return 5;                                      
			if (x == -1 && z == 0)  //West(-1, 0, 0) = 6        
				return 6;           
			if (x == -1 && z == -1) //NorthWest(-1, 0, -1) = 7   
				return 7;                                        
		}                                    
		                                                       
		return 8;                   //None(0, 0, 0) = 8                              
	}
	
    private interface C {
        double C8 = Math.cos(Math.PI / 8);
        double S8 = Math.sin(Math.PI / 8);
    }
}
