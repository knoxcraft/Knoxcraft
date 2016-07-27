package org.knoxcraft.turtle3d;

import org.spongepowered.api.block.BlockState;

import com.flowpowered.math.vector.Vector3i;

public class KCTWorldBlockInfo {
	private Vector3i location;
	private BlockState block;
	
	public KCTWorldBlockInfo(int x, int y, int z, BlockState block) {
	    location = new Vector3i(x, y, z);
		this.block = block;
	}
	
	public KCTWorldBlockInfo(Vector3i location, BlockState block) {
        this.location = location;
        this.block = block;
    }
	
	public Vector3i getLoc() {
		return location;
	}
	
	public BlockState getBlock() {
		return block;
	}
}