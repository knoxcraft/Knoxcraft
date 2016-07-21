package org.knoxcraft.turtle3d;

import org.spongepowered.api.block.BlockState;

import com.flowpowered.math.vector.Vector3i;

public class KCTWorldBlockInfo {
	private int x;
	private int y;
	private int z;
	private BlockState block;
	
	public KCTWorldBlockInfo(int x, int y, int z, BlockState block) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.block = block;
	}
	
	public Vector3i getLoc() {
		return new Vector3i(x, y, z);
	}
	
	public BlockState getBlock() {
		return block;
	}
}