package org.knoxcraft.serverturtle;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.GeneratorTypes;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.gen.GenerationPopulator;
import org.spongepowered.api.world.gen.WorldGenerator;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

public class KnoxCraftWorldModifier implements WorldGeneratorModifier  {

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return "TurtlePlugin:KnoxCraftFlatLands";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Knox Craft Flat Lands";
	}

	@Override
	public void modifyWorldGenerator(WorldCreationSettings world, DataContainer settings,
			WorldGenerator worldGenerator) {
		// TODO MAKE A FLAT WORLD MF
		
		
	}
	
	

}
