package org.knoxcraft;

import static org.junit.Assert.*;

import org.junit.Test;
import org.knoxcraft.turtle3d.KCTBlockTypes;
import org.knoxcraft.turtle3d.KCTBlockTypesBuilder;
import org.knoxcraft.turtle3d.Turtle3D;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;

public class TurtleTest {

	@Test
	public void test() {
		SpongeTurtleTest.main(new String[]{});
		System.out.println(Turtle3D.turtleMap.get(Thread.currentThread()).get("cube").getScript().toJSONString());
	}
	
	@Test
	public void testKCTBlockTypesBuilder() {
		//System.out.println(KCTBlockTypesBuilder.getName(KCTBlockTypes.BEDROCK));
		BlockType t=BlockTypes.AIR;
		t.getDefaultState();
	}

}
