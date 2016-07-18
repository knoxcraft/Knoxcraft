 package org.knoxcraft.turtle3d;

import java.util.HashMap;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.CatalogTypes;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.BrickTypes;
import org.spongepowered.api.data.type.DirtTypes;
import org.spongepowered.api.data.type.DisguisedBlockTypes;
import org.spongepowered.api.data.type.DoublePlantTypes;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.data.type.PlantTypes;
import org.spongepowered.api.data.type.PrismarineTypes;
import org.spongepowered.api.data.type.QuartzTypes;
import org.spongepowered.api.data.type.SandTypes;
import org.spongepowered.api.data.type.SandstoneTypes;
import org.spongepowered.api.data.type.ShrubTypes;
import org.spongepowered.api.data.type.SlabTypes;
import org.spongepowered.api.data.type.StoneTypes;
import org.spongepowered.api.data.type.TreeTypes;
import org.spongepowered.api.data.type.WallTypes;

public final class KCTBlockTypesBuilder {
	private static class Metadata {
		private BlockState block;
		private int numID; 
		private int meta;
		private String name;
		private String textID;
		
		public Metadata(int numID, int meta, String name, String textID, BlockState block) {
			this.numID = numID;
			this.meta = meta;
			this.name = name;
			this.textID = textID;
			this.block = block;
		}
		
		public int getNumID() {
			return numID;
		}
		
		public int getMetadata() {
			return meta;
		}
		
		public String getName() {
			return name;
		}
		
		public String getTextID() {
			return textID;
		}
		
		public BlockState getBlock() {
			return block;
		}
	}
	
	private static final HashMap<KCTBlockTypes, Metadata> blocks = new HashMap<KCTBlockTypes, Metadata>();
	
	/**
	 * Return the {@link BlockState} that corresponds to the given {@link KCTBlockType}.
	 * Basically, this converts between our own internal <tt>enum</tt> and a Sponge
	 * {@link BlockState}.
	 * 
	 * <b>NOTE:</b>
	 * 
	 * Using an initialize() method that gets called once, rather than a static initializer.
     * The {@link BlockTypes} class dynamically creates placeholders where all methods throw
     * UnsupportedOperationException. If you use any methods of a {@link BlockType} in a static
     * initializer it will throw that UnsupportedOperationException. So we have to delay actually
     * calling any methods on a {@link BlockType} until Sponge has actually initialized everything
     * in {@link BlockTypes} with its real value.
     * 
     * Sponge does eventually fill in the real {@link BlockType} instances in {@link BlockTypes}, probably
     * using some design pattern that is related to {@link CatalogTypes} and {@link CatalogType}.
     * 
     * We honestly don't understand how this process works, so we are just using the static 
     * initialize() method because it gets called late enough that it doesn't generate an exception.
     * We don't know why this works.
	 * 
	 * @param type
	 * @return
	 */
	public static int getNumID(KCTBlockTypes type) {
		initialize();
		return blocks.get(type).getNumID();
	}
	
	public static int getMetadata(KCTBlockTypes type) {
		initialize();
		return blocks.get(type).getMetadata();
	}
	
	public static String getName(KCTBlockTypes type) {
		initialize();
		return blocks.get(type).getName();
	}
	
	public static String getTextID(KCTBlockTypes type) {
		initialize();
		return blocks.get(type).getTextID();
	}
	
	public static BlockState getBlockState(KCTBlockTypes type) {
	    initialize();
	    return blocks.get(type).getBlock();
	}
	
	private static boolean isInitialized=false;
	
	/**
	 * See {@link #getBlockState(KCTBlockTypes)} for more details about why we have this method
	 * instead of a static initializer.
	 */
	private static void initialize() {
	    // Ensure that we only call initialize once!
	    if (isInitialized){
	        return;
	    }
	    isInitialized=true;
	    
		blocks.put(KCTBlockTypes.AIR, new Metadata(0, 0, "AIR", "AIR",
				BlockTypes.AIR.getDefaultState()));

		blocks.put(KCTBlockTypes.STONE, new Metadata(1, 0, "STONE", "STONE",
				BlockTypes.STONE.getDefaultState()));

		blocks.put(KCTBlockTypes.GRANITE, new Metadata(1, 1, "GRANITE", "STONE",
				BlockTypes.STONE.getDefaultState().with(Keys.STONE_TYPE, StoneTypes.GRANITE).get()));

		blocks.put(KCTBlockTypes.POLISHED_GRANITE, new Metadata(1, 2, "POLISHED_GRANITE", "STONE",
				BlockTypes.STONE.getDefaultState().with(Keys.STONE_TYPE, StoneTypes.SMOOTH_GRANITE).get()));

		blocks.put(KCTBlockTypes.DIORITE, new Metadata(1, 3, "DIORITE", "STONE",
				BlockTypes.STONE.getDefaultState().with(Keys.STONE_TYPE, StoneTypes.DIORITE).get()));

		blocks.put(KCTBlockTypes.POLISHED_DIORITE, new Metadata(1, 4, "POLISHED_DIORITE", "STONE",
				BlockTypes.STONE.getDefaultState().with(Keys.STONE_TYPE, StoneTypes.SMOOTH_DIORITE).get()));

		blocks.put(KCTBlockTypes.ANDESITE, new Metadata(1, 5, "ANDESITE", "STONE",
				BlockTypes.STONE.getDefaultState().with(Keys.STONE_TYPE, StoneTypes.ANDESITE).get()));

		blocks.put(KCTBlockTypes.POLISHED_ANDESITE, new Metadata(1, 6, "POLISHED_ANDESITE", "STONE",
				BlockTypes.STONE.getDefaultState().with(Keys.STONE_TYPE, StoneTypes.SMOOTH_ANDESITE).get()));

		blocks.put(KCTBlockTypes.GRASS, new Metadata(2, 0, "GRASS", "GRASS",
				BlockTypes.GRASS.getDefaultState()));

		blocks.put(KCTBlockTypes.DIRT, new Metadata(3, 0, "DIRT", "DIRT",
				BlockTypes.DIRT.getDefaultState()));

		blocks.put(KCTBlockTypes.COARSE_DIRT, new Metadata(3, 1, "COARSE_DIRT", "DIRT",
				BlockTypes.DIRT.getDefaultState().with(Keys.DIRT_TYPE, DirtTypes.COARSE_DIRT).get()));

		blocks.put(KCTBlockTypes.PODZOL, new Metadata(3, 2, "PODZOL", "DIRT",
				BlockTypes.DIRT.getDefaultState().with(Keys.DIRT_TYPE, DirtTypes.PODZOL).get()));

		blocks.put(KCTBlockTypes.COBBLESTONE, new Metadata(4, 0, "COBBLESTONE", "COBBLESTONE",
				BlockTypes.COBBLESTONE.getDefaultState()));

		blocks.put(KCTBlockTypes.OAK_WOOD_PLANK, new Metadata(5, 0, "OAK_WOOD_PLANK", "PLANKS",
				BlockTypes.PLANKS.getDefaultState()));

		blocks.put(KCTBlockTypes.SPRUCE_WOOD_PLANK, new Metadata(5, 1, "SPRUCE_WOOD_PLANK", "PLANKS",
				BlockTypes.PLANKS.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.SPRUCE).get()));

		blocks.put(KCTBlockTypes.BIRCH_WOOD_PLANK, new Metadata(5, 2, "BIRCH_WOOD_PLANK", "PLANKS",
				BlockTypes.PLANKS.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.BIRCH).get()));

		blocks.put(KCTBlockTypes.JUNGLE_WOOD_PLANK, new Metadata(5, 3, "JUNGLE_WOOD_PLANK", "PLANKS",
				BlockTypes.PLANKS.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.JUNGLE).get()));

		blocks.put(KCTBlockTypes.ACACIA_WOOD_PLANK, new Metadata(5, 4, "ACACIA_WOOD_PLANK", "PLANKS",
				BlockTypes.PLANKS.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.ACACIA).get()));

		blocks.put(KCTBlockTypes.DARK_OAK_WOOD_PLANK, new Metadata(5, 5, "DARK_OAK_WOOD_PLANK", "PLANKS",
				BlockTypes.PLANKS.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.DARK_OAK).get()));

		blocks.put(KCTBlockTypes.OAK_SAPLING, new Metadata(6, 0, "OAK_SAPLING", "SAPLING",
				BlockTypes.SAPLING.getDefaultState()));

		blocks.put(KCTBlockTypes.SPRUCE_SAPLING, new Metadata(6, 1, "SPRUCE_SAPLING", "SAPLING",
				BlockTypes.SAPLING.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.SPRUCE).get()));

		blocks.put(KCTBlockTypes.BIRCH_SAPLING, new Metadata(6, 2, "BIRCH_SAPLING", "SAPLING",
				BlockTypes.SAPLING.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.BIRCH).get()));

		blocks.put(KCTBlockTypes.JUNGLE_SAPLING, new Metadata(6, 3, "JUNGLE_SAPLING", "SAPLING",
				BlockTypes.SAPLING.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.JUNGLE).get()));

		blocks.put(KCTBlockTypes.ACACIA_SAPLING, new Metadata(6, 4, "ACACIA_SAPLING", "SAPLING",
				BlockTypes.SAPLING.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.ACACIA).get()));

		blocks.put(KCTBlockTypes.DARK_OAK_SAPLING, new Metadata(6, 5, "DARK_OAK_SAPLING", "SAPLING",
				BlockTypes.SAPLING.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.DARK_OAK).get()));

		blocks.put(KCTBlockTypes.BEDROCK, new Metadata(7, 0, "BEDROCK", "BEDROCK",
				BlockTypes.BEDROCK.getDefaultState()));

		blocks.put(KCTBlockTypes.FLOWING_WATER, new Metadata(8, 0, "FLOWING_WATER", "FLOWING_WATER",
				BlockTypes.FLOWING_WATER.getDefaultState()));

		blocks.put(KCTBlockTypes.STILL_WATER, new Metadata(9, 0, "STILL_WATER", "WATER",
				BlockTypes.WATER.getDefaultState()));

		blocks.put(KCTBlockTypes.FLOWING_LAVA, new Metadata(10, 0, "FLOWING_LAVA", "FLOWING_LAVA",
				BlockTypes.FLOWING_LAVA.getDefaultState()));

		blocks.put(KCTBlockTypes.STILL_LAVA, new Metadata(11, 0, "STILL_LAVA", "LAVA",
				BlockTypes.LAVA.getDefaultState()));

		blocks.put(KCTBlockTypes.SAND, new Metadata(12, 0, "SAND", "SAND",
				BlockTypes.SAND.getDefaultState()));

		blocks.put(KCTBlockTypes.RED_SAND, new Metadata(12, 1, "RED_SAND", "SAND",
				BlockTypes.SAND.getDefaultState().with(Keys.SAND_TYPE, SandTypes.RED).get()));

		blocks.put(KCTBlockTypes.GRAVEL, new Metadata(13, 0, "GRAVEL", "GRAVEL",
				BlockTypes.GRAVEL.getDefaultState()));

		blocks.put(KCTBlockTypes.GOLD_ORE, new Metadata(14, 0, "GOLD_ORE", "GOLD_ORE",
				BlockTypes.GOLD_ORE.getDefaultState()));

		blocks.put(KCTBlockTypes.IRON_ORE, new Metadata(15, 0, "IRON_ORE", "IRON_ORE",
				BlockTypes.IRON_ORE.getDefaultState()));

		blocks.put(KCTBlockTypes.COAL_ORE, new Metadata(16, 0, "COAL_ORE", "COAL_ORE",
				BlockTypes.COAL_ORE.getDefaultState()));

		blocks.put(KCTBlockTypes.OAK_WOOD, new Metadata(17, 0, "OAK_WOOD", "LOG",
				BlockTypes.LOG.getDefaultState()));

		blocks.put(KCTBlockTypes.SPRUCE_WOOD, new Metadata(17, 1, "SPRUCE_WOOD", "LOG",
				BlockTypes.LOG.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.SPRUCE).get()));

		blocks.put(KCTBlockTypes.BIRCH_WOOD, new Metadata(17, 2, "BIRCH_WOOD", "LOG",
				BlockTypes.LOG.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.BIRCH).get()));

		blocks.put(KCTBlockTypes.JUNGLE_WOOD, new Metadata(17, 3, "JUNGLE_WOOD", "LOG",
				BlockTypes.LOG.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.JUNGLE).get()));

		blocks.put(KCTBlockTypes.OAK_LEAVES, new Metadata(18, 0, "OAK_LEAVES", "LEAVES",
				BlockTypes.LEAVES.getDefaultState()));

		blocks.put(KCTBlockTypes.SPRUCE_LEAVES, new Metadata(18, 1, "SPRUCE_LEAVES", "LEAVES",
				BlockTypes.LEAVES.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.SPRUCE).get()));

		blocks.put(KCTBlockTypes.BIRCH_LEAVES, new Metadata(18, 2, "BIRCH_LEAVES", "LEAVES",
				BlockTypes.LEAVES.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.BIRCH).get()));

		blocks.put(KCTBlockTypes.JUNGLE_LEAVES, new Metadata(18, 3, "JUNGLE_LEAVES", "LEAVES",
				BlockTypes.LEAVES.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.JUNGLE).get()));

		blocks.put(KCTBlockTypes.SPONGE, new Metadata(19, 0, "SPONGE", "SPONGE",
				BlockTypes.SPONGE.getDefaultState()));

		blocks.put(KCTBlockTypes.WET_SPONGE, new Metadata(19, 1, "WET_SPONGE", "SPONGE",
				BlockTypes.SPONGE.getDefaultState().with(Keys.IS_WET, true).get()));

		blocks.put(KCTBlockTypes.GLASS, new Metadata(20, 0, "GLASS", "GLASS",
				BlockTypes.GLASS.getDefaultState()));

		blocks.put(KCTBlockTypes.LAPIS_LAZULI_ORE, new Metadata(21, 0, "LAPIS_LAZULI_ORE", "LAPIS_ORE",
				BlockTypes.LAPIS_ORE.getDefaultState()));

		blocks.put(KCTBlockTypes.LAPIS_LAZULI_BLOCK, new Metadata(22, 0, "LAPIS_LAZULI_BLOCK", "LAPIS_BLOCK",
				BlockTypes.LAPIS_BLOCK.getDefaultState()));

		blocks.put(KCTBlockTypes.DISPENSER, new Metadata(23, 0, "DISPENSER", "DISPENSER",
				BlockTypes.DISPENSER.getDefaultState()));

		blocks.put(KCTBlockTypes.SANDSTONE, new Metadata(24, 0, "SANDSTONE", "SANDSTONE",
				BlockTypes.SANDSTONE.getDefaultState()));

		blocks.put(KCTBlockTypes.CHISELED_SANDSTONE, new Metadata(24, 1, "CHISELED_SANDSTONE", "SANDSTONE",
				BlockTypes.SANDSTONE.getDefaultState().with(Keys.SANDSTONE_TYPE, SandstoneTypes.CHISELED).get()));

		blocks.put(KCTBlockTypes.SMOOTH_SANDSTONE, new Metadata(24, 2, "SMOOTH_SANDSTONE", "SANDSTONE",
				BlockTypes.SANDSTONE.getDefaultState().with(Keys.SANDSTONE_TYPE, SandstoneTypes.SMOOTH).get()));

		blocks.put(KCTBlockTypes.NOTE_BLOCK, new Metadata(25, 0, "NOTE_BLOCK", "NOTEBLOCK",
				BlockTypes.NOTEBLOCK.getDefaultState()));

		blocks.put(KCTBlockTypes.BED, new Metadata(26, 0, "BED", "BED",
				BlockTypes.BED.getDefaultState()));

		blocks.put(KCTBlockTypes.POWERED_RAIL, new Metadata(27, 0, "POWERED_RAIL", "GOLDEN_RAIL",
				BlockTypes.GOLDEN_RAIL.getDefaultState()));

		blocks.put(KCTBlockTypes.DETECTOR_RAIL, new Metadata(28, 0, "DETECTOR_RAIL", "DETECTOR_RAIL",
				BlockTypes.DETECTOR_RAIL.getDefaultState()));

		blocks.put(KCTBlockTypes.STICKY_PISTON, new Metadata(29, 0, "STICKY_PISTON", "STICKY_PISTON",
				BlockTypes.STICKY_PISTON.getDefaultState()));

		blocks.put(KCTBlockTypes.COBWEB, new Metadata(30, 0, "COBWEB", "WEB",
				BlockTypes.WEB.getDefaultState()));

		blocks.put(KCTBlockTypes.DEAD_SHRUB, new Metadata(31, 0, "DEAD_SHRUB", "TALLGRASS",
				BlockTypes.TALLGRASS.getDefaultState()));

		blocks.put(KCTBlockTypes.GRASS_TALLGRASS, new Metadata(31, 1, "GRASS", "TALLGRASS",
				BlockTypes.TALLGRASS.getDefaultState().with(Keys.SHRUB_TYPE, ShrubTypes.TALL_GRASS).get()));

		blocks.put(KCTBlockTypes.FERN, new Metadata(31, 2, "FERN", "TALLGRASS",
				BlockTypes.TALLGRASS.getDefaultState().with(Keys.SHRUB_TYPE, ShrubTypes.FERN).get()));

		blocks.put(KCTBlockTypes.DEAD_BUSH, new Metadata(32, 0, "DEAD_BUSH", "DEADBUSH",
				BlockTypes.DEADBUSH.getDefaultState()));

		blocks.put(KCTBlockTypes.PISTON, new Metadata(33, 0, "PISTON", "PISTON",
				BlockTypes.PISTON.getDefaultState()));

		blocks.put(KCTBlockTypes.PISTON_HEAD, new Metadata(34, 0, "PISTON_HEAD", "PISTON_HEAD",
				BlockTypes.PISTON_HEAD.getDefaultState()));

		blocks.put(KCTBlockTypes.WHITE_WOOL, new Metadata(35, 0, "WHITE_WOOL", "WOOL",
				BlockTypes.WOOL.getDefaultState()));

		blocks.put(KCTBlockTypes.ORANGE_WOOL, new Metadata(35, 1, "ORANGE_WOOL", "WOOL",
				BlockTypes.WOOL.getDefaultState().with(Keys.DYE_COLOR, DyeColors.ORANGE).get()));

		blocks.put(KCTBlockTypes.MAGENTA_WOOL, new Metadata(35, 2, "MAGENTA_WOOL", "WOOL",
				BlockTypes.WOOL.getDefaultState().with(Keys.DYE_COLOR, DyeColors.MAGENTA).get()));

		blocks.put(KCTBlockTypes.LIGHT_BLUE_WOOL, new Metadata(35, 3, "LIGHT_BLUE_WOOL", "WOOL",
				BlockTypes.WOOL.getDefaultState().with(Keys.DYE_COLOR, DyeColors.LIGHT_BLUE).get()));

		blocks.put(KCTBlockTypes.YELLOW_WOOL, new Metadata(35, 4, "YELLOW_WOOL", "WOOL",
				BlockTypes.WOOL.getDefaultState().with(Keys.DYE_COLOR, DyeColors.YELLOW).get()));

		blocks.put(KCTBlockTypes.LIME_WOOL, new Metadata(35, 5, "LIME_WOOL", "WOOL",
				BlockTypes.WOOL.getDefaultState().with(Keys.DYE_COLOR, DyeColors.LIME).get()));

		blocks.put(KCTBlockTypes.PINK_WOOL, new Metadata(35, 6, "PINK_WOOL", "WOOL",
				BlockTypes.WOOL.getDefaultState().with(Keys.DYE_COLOR, DyeColors.PINK).get()));

		blocks.put(KCTBlockTypes.GRAY_WOOL, new Metadata(35, 7, "GRAY_WOOL", "WOOL",
				BlockTypes.WOOL.getDefaultState().with(Keys.DYE_COLOR, DyeColors.GRAY).get()));

		blocks.put(KCTBlockTypes.LIGHT_GRAY_WOOL, new Metadata(35, 8, "LIGHT_GRAY_WOOL", "WOOL",
				BlockTypes.WOOL.getDefaultState().with(Keys.DYE_COLOR, DyeColors.GRAY).get()));

		blocks.put(KCTBlockTypes.CYAN_WOOL, new Metadata(35, 9, "CYAN_WOOL", "WOOL",
				BlockTypes.WOOL.getDefaultState().with(Keys.DYE_COLOR, DyeColors.CYAN).get()));

		blocks.put(KCTBlockTypes.PURPLE_WOOL, new Metadata(35, 10, "PURPLE_WOOL", "WOOL",
				BlockTypes.WOOL.getDefaultState().with(Keys.DYE_COLOR, DyeColors.PURPLE).get()));

		blocks.put(KCTBlockTypes.BLUE_WOOL, new Metadata(35, 11, "BLUE_WOOL", "WOOL",
				BlockTypes.WOOL.getDefaultState().with(Keys.DYE_COLOR, DyeColors.BLUE).get()));

		blocks.put(KCTBlockTypes.BROWN_WOOL, new Metadata(35, 12, "BROWN_WOOL", "WOOL",
				BlockTypes.WOOL.getDefaultState().with(Keys.DYE_COLOR, DyeColors.BROWN).get()));

		blocks.put(KCTBlockTypes.GREEN_WOOL, new Metadata(35, 13, "GREEN_WOOL", "WOOL",
				BlockTypes.WOOL.getDefaultState().with(Keys.DYE_COLOR, DyeColors.GREEN).get()));

		blocks.put(KCTBlockTypes.RED_WOOL, new Metadata(35, 14, "RED_WOOL", "WOOL",
				BlockTypes.WOOL.getDefaultState().with(Keys.DYE_COLOR, DyeColors.RED).get()));

		blocks.put(KCTBlockTypes.BLACK_WOOL, new Metadata(35, 15, "BLACK_WOOL", "WOOL",
				BlockTypes.WOOL.getDefaultState().with(Keys.DYE_COLOR, DyeColors.BLACK).get()));

		blocks.put(KCTBlockTypes.DANDELION, new Metadata(37, 0, "DANDELION", "YELLOW_FLOWER",
				BlockTypes.YELLOW_FLOWER.getDefaultState()));

		blocks.put(KCTBlockTypes.POPPY, new Metadata(38, 0, "POPPY", "RED_FLOWER",
				BlockTypes.RED_FLOWER.getDefaultState()));

		blocks.put(KCTBlockTypes.BLUE_ORCHID, new Metadata(38, 1, "BLUE_ORCHID", "RED_FLOWER",
				BlockTypes.RED_FLOWER.getDefaultState().with(Keys.PLANT_TYPE, PlantTypes.BLUE_ORCHID).get()));

		blocks.put(KCTBlockTypes.ALLIUM, new Metadata(38, 2, "ALLIUM", "RED_FLOWER",
				BlockTypes.RED_FLOWER.getDefaultState().with(Keys.PLANT_TYPE, PlantTypes.ALLIUM).get()));

		blocks.put(KCTBlockTypes.AZURE_BLUET, new Metadata(38, 3, "AZURE_BLUET", "RED_FLOWER",
				BlockTypes.RED_FLOWER.getDefaultState().with(Keys.PLANT_TYPE, PlantTypes.POPPY).get()));

		blocks.put(KCTBlockTypes.RED_TULIP, new Metadata(38, 4, "RED_TULIP", "RED_FLOWER",
				BlockTypes.RED_FLOWER.getDefaultState().with(Keys.PLANT_TYPE, PlantTypes.RED_TULIP).get()));

		blocks.put(KCTBlockTypes.ORANGE_TULIP, new Metadata(38, 5, "ORANGE_TULIP", "RED_FLOWER",
				BlockTypes.RED_FLOWER.getDefaultState().with(Keys.PLANT_TYPE, PlantTypes.ORANGE_TULIP).get()));

		blocks.put(KCTBlockTypes.WHITE_TULIP, new Metadata(38, 6, "WHITE_TULIP", "RED_FLOWER",
				BlockTypes.RED_FLOWER.getDefaultState().with(Keys.PLANT_TYPE, PlantTypes.WHITE_TULIP).get()));

		blocks.put(KCTBlockTypes.PINK_TULIP, new Metadata(38, 7, "PINK_TULIP", "RED_FLOWER",
				BlockTypes.RED_FLOWER.getDefaultState().with(Keys.PLANT_TYPE, PlantTypes.PINK_TULIP).get()));

		blocks.put(KCTBlockTypes.OXEYE_DAISY, new Metadata(38, 8, "OXEYE_DAISY", "RED_FLOWER",
				BlockTypes.RED_FLOWER.getDefaultState().with(Keys.PLANT_TYPE, PlantTypes.OXEYE_DAISY).get()));

		blocks.put(KCTBlockTypes.BROWN_MUSHROOM, new Metadata(39, 0, "BROWN_MUSHROOM", "BROWN_MUSHROOM",
				BlockTypes.BROWN_MUSHROOM.getDefaultState()));

		blocks.put(KCTBlockTypes.RED_MUSHROOM, new Metadata(40, 0, "RED_MUSHROOM", "RED_MUSHROOM",
				BlockTypes.RED_MUSHROOM.getDefaultState()));

		blocks.put(KCTBlockTypes.GOLD_BLOCK, new Metadata(41, 0, "GOLD_BLOCK", "GOLD_BLOCK",
				BlockTypes.GOLD_BLOCK.getDefaultState()));

		blocks.put(KCTBlockTypes.IRON_BLOCK, new Metadata(42, 0, "IRON_BLOCK", "IRON_BLOCK",
				BlockTypes.IRON_BLOCK.getDefaultState()));

		blocks.put(KCTBlockTypes.DOUBLE_STONE_SLAB, new Metadata(43, 0, "DOUBLE_STONE_SLAB", "DOUBLE_STONE_SLAB",
				BlockTypes.DOUBLE_STONE_SLAB.getDefaultState()));

		blocks.put(KCTBlockTypes.DOUBLE_SANDSTONE_SLAB, new Metadata(43, 1, "DOUBLE_SANDSTONE_SLAB", "DOUBLE_STONE_SLAB",
				BlockTypes.DOUBLE_STONE_SLAB.getDefaultState().with(Keys.SLAB_TYPE, SlabTypes.SAND).get()));

		blocks.put(KCTBlockTypes.DOUBLE_WOODEN_SLAB, new Metadata(43, 2, "DOUBLE_WOODEN_SLAB", "DOUBLE_STONE_SLAB",
				BlockTypes.DOUBLE_STONE_SLAB.getDefaultState().with(Keys.SLAB_TYPE, SlabTypes.WOOD).get()));

		blocks.put(KCTBlockTypes.DOUBLE_COBBLESTONE_SLAB, new Metadata(43, 3, "DOUBLE_COBBLESTONE_SLAB", "DOUBLE_STONE_SLAB",
				BlockTypes.DOUBLE_STONE_SLAB.getDefaultState().with(Keys.SLAB_TYPE, SlabTypes.COBBLESTONE).get()));

		blocks.put(KCTBlockTypes.DOUBLE_BRICK_SLAB, new Metadata(43, 4, "DOUBLE_BRICK_SLAB", "DOUBLE_STONE_SLAB",
				BlockTypes.DOUBLE_STONE_SLAB.getDefaultState().with(Keys.SLAB_TYPE, SlabTypes.BRICK).get()));

		blocks.put(KCTBlockTypes.DOUBLE_STONE_BRICK_SLAB, new Metadata(43, 5, "DOUBLE_STONE_BRICK_SLAB", "DOUBLE_STONE_SLAB",
				BlockTypes.DOUBLE_STONE_SLAB.getDefaultState().with(Keys.SLAB_TYPE, SlabTypes.SMOOTH_BRICK).get()));

		blocks.put(KCTBlockTypes.DOUBLE_NETHER_BRICK_SLAB, new Metadata(43, 6, "DOUBLE_NETHER_BRICK_SLAB", "DOUBLE_STONE_SLAB",
				BlockTypes.DOUBLE_STONE_SLAB.getDefaultState().with(Keys.SLAB_TYPE, SlabTypes.NETHERBRICK).get()));

		blocks.put(KCTBlockTypes.DOUBLE_QUARTZ_SLAB, new Metadata(43, 7, "DOUBLE_QUARTZ_SLAB", "DOUBLE_STONE_SLAB",
				BlockTypes.DOUBLE_STONE_SLAB.getDefaultState().with(Keys.SLAB_TYPE, SlabTypes.QUARTZ).get()));

		blocks.put(KCTBlockTypes.STONE_SLAB, new Metadata(44, 0, "STONE_SLAB", "STONE_SLAB",
				BlockTypes.STONE_SLAB.getDefaultState()));

		blocks.put(KCTBlockTypes.SANDSTONE_SLAB, new Metadata(44, 1, "SANDSTONE_SLAB", "STONE_SLAB",
				BlockTypes.STONE_SLAB.getDefaultState().with(Keys.SLAB_TYPE, SlabTypes.SAND).get()));

		blocks.put(KCTBlockTypes.WOODEN_SLAB, new Metadata(44, 2, "WOODEN_SLAB", "STONE_SLAB",
				BlockTypes.STONE_SLAB.getDefaultState().with(Keys.SLAB_TYPE, SlabTypes.WOOD).get()));

		blocks.put(KCTBlockTypes.COBBLESTONE_SLAB, new Metadata(44, 3, "COBBLESTONE_SLAB", "STONE_SLAB",
				BlockTypes.STONE_SLAB.getDefaultState().with(Keys.SLAB_TYPE, SlabTypes.STONE).get()));

		blocks.put(KCTBlockTypes.BRICK_SLAB, new Metadata(44, 4, "BRICK_SLAB", "STONE_SLAB",
				BlockTypes.STONE_SLAB.getDefaultState().with(Keys.SLAB_TYPE, SlabTypes.BRICK).get()));

		blocks.put(KCTBlockTypes.STONE_BRICK_SLAB, new Metadata(44, 5, "STONE_BRICK_SLAB", "STONE_SLAB",
				BlockTypes.STONE_SLAB.getDefaultState().with(Keys.SLAB_TYPE, SlabTypes.SMOOTH_BRICK).get()));

		blocks.put(KCTBlockTypes.NETHER_BRICK_SLAB, new Metadata(44, 6, "NETHER_BRICK_SLAB", "STONE_SLAB",
				BlockTypes.STONE_SLAB.getDefaultState().with(Keys.SLAB_TYPE, SlabTypes.STONE).get()));

		blocks.put(KCTBlockTypes.QUARTZ_SLAB, new Metadata(44, 7, "QUARTZ_SLAB", "STONE_SLAB",
				BlockTypes.STONE_SLAB.getDefaultState().with(Keys.SLAB_TYPE, SlabTypes.QUARTZ).get()));

		blocks.put(KCTBlockTypes.BRICKS, new Metadata(45, 0, "BRICKS", "BRICK_BLOCK",
				BlockTypes.BRICK_BLOCK.getDefaultState()));

		blocks.put(KCTBlockTypes.TNT, new Metadata(46, 0, "TNT", "TNT",
				BlockTypes.TNT.getDefaultState()));

		blocks.put(KCTBlockTypes.BOOKSHELF, new Metadata(47, 0, "BOOKSHELF", "BOOKSHELF",
				BlockTypes.BOOKSHELF.getDefaultState()));

		blocks.put(KCTBlockTypes.MOSS_STONE, new Metadata(48, 0, "MOSS_STONE", "MOSSY_COBBLESTONE",
				BlockTypes.MOSSY_COBBLESTONE.getDefaultState()));

		blocks.put(KCTBlockTypes.OBSIDIAN, new Metadata(49, 0, "OBSIDIAN", "OBSIDIAN",
				BlockTypes.OBSIDIAN.getDefaultState()));

		blocks.put(KCTBlockTypes.TORCH, new Metadata(50, 0, "TORCH", "TORCH",
				BlockTypes.TORCH.getDefaultState()));

		blocks.put(KCTBlockTypes.FIRE, new Metadata(51, 0, "FIRE", "FIRE",
				BlockTypes.FIRE.getDefaultState()));

		blocks.put(KCTBlockTypes.MONSTER_SPAWNER, new Metadata(52, 0, "MONSTER_SPAWNER", "MOB_SPAWNER",
				BlockTypes.MOB_SPAWNER.getDefaultState()));

		blocks.put(KCTBlockTypes.OAK_WOOD_STAIRS, new Metadata(53, 0, "OAK_WOOD_STAIRS", "OAK_STAIRS",
				BlockTypes.OAK_STAIRS.getDefaultState()));

		blocks.put(KCTBlockTypes.CHEST, new Metadata(54, 0, "CHEST", "CHEST",
				BlockTypes.CHEST.getDefaultState()));

		blocks.put(KCTBlockTypes.REDSTONE_WIRE, new Metadata(55, 0, "REDSTONE_WIRE", "REDSTONE_WIRE",
				BlockTypes.REDSTONE_WIRE.getDefaultState()));

		blocks.put(KCTBlockTypes.DIAMOND_ORE, new Metadata(56, 0, "DIAMOND_ORE", "DIAMOND_ORE",
				BlockTypes.DIAMOND_ORE.getDefaultState()));

		blocks.put(KCTBlockTypes.DIAMOND_BLOCK, new Metadata(57, 0, "DIAMOND_BLOCK", "DIAMOND_BLOCK",
				BlockTypes.DIAMOND_BLOCK.getDefaultState()));

		blocks.put(KCTBlockTypes.CRAFTING_TABLE, new Metadata(58, 0, "CRAFTING_TABLE", "CRAFTING_TABLE",
				BlockTypes.CRAFTING_TABLE.getDefaultState()));

		blocks.put(KCTBlockTypes.WHEAT_CROPS, new Metadata(59, 0, "WHEAT_CROPS", "WHEAT",
				BlockTypes.WHEAT.getDefaultState()));

		blocks.put(KCTBlockTypes.FARMLAND, new Metadata(60, 0, "FARMLAND", "FARMLAND",
				BlockTypes.FARMLAND.getDefaultState()));

		blocks.put(KCTBlockTypes.FURNACE, new Metadata(61, 0, "FURNACE", "FURNACE",
				BlockTypes.FURNACE.getDefaultState()));

		blocks.put(KCTBlockTypes.BURNING_FURNACE, new Metadata(62, 0, "BURNING_FURNACE", "LIT_FURNACE",
				BlockTypes.LIT_FURNACE.getDefaultState()));

		blocks.put(KCTBlockTypes.STANDING_SIGN_BLOCK, new Metadata(63, 0, "STANDING_SIGN_BLOCK", "STANDING_SIGN",
				BlockTypes.STANDING_SIGN.getDefaultState()));

		blocks.put(KCTBlockTypes.OAK_DOOR_BLOCK, new Metadata(64, 0, "OAK_DOOR_BLOCK", "WOODEN_DOOR",
				BlockTypes.WOODEN_DOOR.getDefaultState()));

		blocks.put(KCTBlockTypes.LADDER, new Metadata(65, 0, "LADDER", "LADDER",
				BlockTypes.LADDER.getDefaultState()));

		blocks.put(KCTBlockTypes.RAIL, new Metadata(66, 0, "RAIL", "RAIL",
				BlockTypes.RAIL.getDefaultState()));

		blocks.put(KCTBlockTypes.COBBLESTONE_STAIRS, new Metadata(67, 0, "COBBLESTONE_STAIRS", "STONE_STAIRS",
				BlockTypes.STONE_STAIRS.getDefaultState()));

		blocks.put(KCTBlockTypes.WALL_MOUNTED_SIGN_BLOCK, new Metadata(68, 0, "WALL_MOUNTED_SIGN_BLOCK", "WALL_SIGN",
				BlockTypes.WALL_SIGN.getDefaultState()));

		blocks.put(KCTBlockTypes.LEVER, new Metadata(69, 0, "LEVER", "LEVER",
				BlockTypes.LEVER.getDefaultState()));

		blocks.put(KCTBlockTypes.STONE_PRESSURE_PLATE, new Metadata(70, 0, "STONE_PRESSURE_PLATE", "STONE_PRESSURE_PLATE",
				BlockTypes.STONE_PRESSURE_PLATE.getDefaultState()));

		blocks.put(KCTBlockTypes.IRON_DOOR_BLOCK, new Metadata(71, 0, "IRON_DOOR_BLOCK", "IRON_DOOR",
				BlockTypes.IRON_DOOR.getDefaultState()));

		blocks.put(KCTBlockTypes.WOODEN_PRESSURE_PLATE, new Metadata(72, 0, "WOODEN_PRESSURE_PLATE", "WOODEN_PRESSURE_PLATE",
				BlockTypes.WOODEN_PRESSURE_PLATE.getDefaultState()));

		blocks.put(KCTBlockTypes.REDSTONE_ORE, new Metadata(73, 0, "REDSTONE_ORE", "REDSTONE_ORE",
				BlockTypes.REDSTONE_ORE.getDefaultState()));

		blocks.put(KCTBlockTypes.GLOWING_REDSTONE_ORE, new Metadata(74, 0, "GLOWING_REDSTONE_ORE", "LIT_REDSTONE_ORE",
				BlockTypes.LIT_REDSTONE_ORE.getDefaultState()));

		blocks.put(KCTBlockTypes.REDSTONE_TORCH_OFF, new Metadata(75, 0, "REDSTONE_TORCH_OFF", "UNLIT_REDSTONE_TORCH",
				BlockTypes.UNLIT_REDSTONE_TORCH.getDefaultState()));

		blocks.put(KCTBlockTypes.REDSTONE_TORCH_ON, new Metadata(76, 0, "REDSTONE_TORCH_ON", "REDSTONE_TORCH",
				BlockTypes.REDSTONE_TORCH.getDefaultState()));

		blocks.put(KCTBlockTypes.STONE_BUTTON, new Metadata(77, 0, "STONE_BUTTON", "STONE_BUTTON",
				BlockTypes.STONE_BUTTON.getDefaultState()));

		blocks.put(KCTBlockTypes.SNOW, new Metadata(78, 0, "SNOW", "SNOW_LAYER",
				BlockTypes.SNOW_LAYER.getDefaultState()));

		blocks.put(KCTBlockTypes.ICE, new Metadata(79, 0, "ICE", "ICE",
				BlockTypes.ICE.getDefaultState()));

		blocks.put(KCTBlockTypes.SNOW_BLOCK, new Metadata(80, 0, "SNOW_BLOCK", "SNOW",
				BlockTypes.SNOW.getDefaultState()));

		blocks.put(KCTBlockTypes.CACTUS, new Metadata(81, 0, "CACTUS", "CACTUS",
				BlockTypes.CACTUS.getDefaultState()));

		blocks.put(KCTBlockTypes.CLAY, new Metadata(82, 0, "CLAY", "CLAY",
				BlockTypes.CLAY.getDefaultState()));

		blocks.put(KCTBlockTypes.SUGAR_CANES, new Metadata(83, 0, "SUGAR_CANES", "REEDS",
				BlockTypes.REEDS.getDefaultState()));

		blocks.put(KCTBlockTypes.JUKEBOX, new Metadata(84, 0, "JUKEBOX", "JUKEBOX",
				BlockTypes.JUKEBOX.getDefaultState()));

		blocks.put(KCTBlockTypes.OAK_FENCE, new Metadata(85, 0, "OAK_FENCE", "FENCE",
				BlockTypes.FENCE.getDefaultState()));

		blocks.put(KCTBlockTypes.PUMPKIN, new Metadata(86, 0, "PUMPKIN", "PUMPKIN",
				BlockTypes.PUMPKIN.getDefaultState()));

		blocks.put(KCTBlockTypes.NETHERRACK, new Metadata(87, 0, "NETHERRACK", "NETHERRACK",
				BlockTypes.NETHERRACK.getDefaultState()));

		blocks.put(KCTBlockTypes.SOUL_SAND, new Metadata(88, 0, "SOUL_SAND", "SOUL_SAND",
				BlockTypes.SOUL_SAND.getDefaultState()));

		blocks.put(KCTBlockTypes.GLOWSTONE, new Metadata(89, 0, "GLOWSTONE", "GLOWSTONE",
				BlockTypes.GLOWSTONE.getDefaultState()));

		blocks.put(KCTBlockTypes.NETHER_PORTAL, new Metadata(90, 0, "NETHER_PORTAL", "PORTAL",
				BlockTypes.PORTAL.getDefaultState()));

		blocks.put(KCTBlockTypes.JACK_OLANTERN, new Metadata(91, 0, "JACK_OLANTERN", "LIT_PUMPKIN",
				BlockTypes.LIT_PUMPKIN.getDefaultState()));

		blocks.put(KCTBlockTypes.CAKE_BLOCK, new Metadata(92, 0, "CAKE_BLOCK", "CAKE",
				BlockTypes.CAKE.getDefaultState()));

		blocks.put(KCTBlockTypes.REDSTONE_REPEATER_BLOCK_OFF, new Metadata(93, 0, "REDSTONE_REPEATER_BLOCK_OFF", "UNPOWERED_REPEATER",
				BlockTypes.UNPOWERED_REPEATER.getDefaultState()));

		blocks.put(KCTBlockTypes.REDSTONE_REPEATER_BLOCK_ON, new Metadata(94, 0, "REDSTONE_REPEATER_BLOCK_ON", "POWERED_REPEATER",
				BlockTypes.POWERED_REPEATER.getDefaultState()));

		blocks.put(KCTBlockTypes.WHITE_STAINED_GLASS, new Metadata(95, 0, "WHITE_STAINED_GLASS", "STAINED_GLASS",
				BlockTypes.STAINED_GLASS.getDefaultState()));

		blocks.put(KCTBlockTypes.ORANGE_STAINED_GLASS, new Metadata(95, 1, "ORANGE_STAINED_GLASS", "STAINED_GLASS",
				BlockTypes.STAINED_GLASS.getDefaultState().with(Keys.DYE_COLOR, DyeColors.ORANGE).get()));

		blocks.put(KCTBlockTypes.MAGENTA_STAINED_GLASS, new Metadata(95, 2, "MAGENTA_STAINED_GLASS", "STAINED_GLASS",
				BlockTypes.STAINED_GLASS.getDefaultState().with(Keys.DYE_COLOR, DyeColors.MAGENTA).get()));

		blocks.put(KCTBlockTypes.LIGHT_BLUE_STAINED_GLASS, new Metadata(95, 3, "LIGHT_BLUE_STAINED_GLASS", "STAINED_GLASS",
				BlockTypes.STAINED_GLASS.getDefaultState().with(Keys.DYE_COLOR, DyeColors.LIGHT_BLUE).get()));

		blocks.put(KCTBlockTypes.YELLOW_STAINED_GLASS, new Metadata(95, 4, "YELLOW_STAINED_GLASS", "STAINED_GLASS",
				BlockTypes.STAINED_GLASS.getDefaultState().with(Keys.DYE_COLOR, DyeColors.YELLOW).get()));

		blocks.put(KCTBlockTypes.LIME_STAINED_GLASS, new Metadata(95, 5, "LIME_STAINED_GLASS", "STAINED_GLASS",
				BlockTypes.STAINED_GLASS.getDefaultState().with(Keys.DYE_COLOR, DyeColors.LIME).get()));

		blocks.put(KCTBlockTypes.PINK_STAINED_GLASS, new Metadata(95, 6, "PINK_STAINED_GLASS", "STAINED_GLASS",
				BlockTypes.STAINED_GLASS.getDefaultState().with(Keys.DYE_COLOR, DyeColors.PINK).get()));

		blocks.put(KCTBlockTypes.GRAY_STAINED_GLASS, new Metadata(95, 7, "GRAY_STAINED_GLASS", "STAINED_GLASS",
				BlockTypes.STAINED_GLASS.getDefaultState().with(Keys.DYE_COLOR, DyeColors.GRAY).get()));

		blocks.put(KCTBlockTypes.LIGHT_GRAY_STAINED_GLASS, new Metadata(95, 8, "LIGHT_GRAY_STAINED_GLASS", "STAINED_GLASS",
				BlockTypes.STAINED_GLASS.getDefaultState().with(Keys.DYE_COLOR, DyeColors.GRAY).get()));

		blocks.put(KCTBlockTypes.CYAN_STAINED_GLASS, new Metadata(95, 9, "CYAN_STAINED_GLASS", "STAINED_GLASS",
				BlockTypes.STAINED_GLASS.getDefaultState().with(Keys.DYE_COLOR, DyeColors.CYAN).get()));

		blocks.put(KCTBlockTypes.PURPLE_STAINED_GLASS, new Metadata(95, 10, "PURPLE_STAINED_GLASS", "STAINED_GLASS",
				BlockTypes.STAINED_GLASS.getDefaultState().with(Keys.DYE_COLOR, DyeColors.PURPLE).get()));

		blocks.put(KCTBlockTypes.BLUE_STAINED_GLASS, new Metadata(95, 11, "BLUE_STAINED_GLASS", "STAINED_GLASS",
				BlockTypes.STAINED_GLASS.getDefaultState().with(Keys.DYE_COLOR, DyeColors.BLUE).get()));

		blocks.put(KCTBlockTypes.BROWN_STAINED_GLASS, new Metadata(95, 12, "BROWN_STAINED_GLASS", "STAINED_GLASS",
				BlockTypes.STAINED_GLASS.getDefaultState().with(Keys.DYE_COLOR, DyeColors.BROWN).get()));

		blocks.put(KCTBlockTypes.GREEN_STAINED_GLASS, new Metadata(95, 13, "GREEN_STAINED_GLASS", "STAINED_GLASS",
				BlockTypes.STAINED_GLASS.getDefaultState().with(Keys.DYE_COLOR, DyeColors.GREEN).get()));

		blocks.put(KCTBlockTypes.RED_STAINED_GLASS, new Metadata(95, 14, "RED_STAINED_GLASS", "STAINED_GLASS",
				BlockTypes.STAINED_GLASS.getDefaultState().with(Keys.DYE_COLOR, DyeColors.RED).get()));

		blocks.put(KCTBlockTypes.BLACK_STAINED_GLASS, new Metadata(95, 15, "BLACK_STAINED_GLASS", "STAINED_GLASS",
				BlockTypes.STAINED_GLASS.getDefaultState().with(Keys.DYE_COLOR, DyeColors.BLACK).get()));

		blocks.put(KCTBlockTypes.WOODEN_TRAPDOOR, new Metadata(96, 0, "WOODEN_TRAPDOOR", "TRAPDOOR",
				BlockTypes.TRAPDOOR.getDefaultState()));

		blocks.put(KCTBlockTypes.STONE_MONSTER_EGG, new Metadata(97, 0, "STONE_MONSTER_EGG", "MONSTER_EGG",
				BlockTypes.MONSTER_EGG.getDefaultState()));

		blocks.put(KCTBlockTypes.COBBLESTONE_MONSTER_EGG, new Metadata(97, 1, "COBBLESTONE_MONSTER_EGG", "MONSTER_EGG",
				BlockTypes.MONSTER_EGG.getDefaultState().with(Keys.DISGUISED_BLOCK_TYPE, DisguisedBlockTypes.COBBLESTONE).get()));

		blocks.put(KCTBlockTypes.STONE_BRICK_MONSTER_EGG, new Metadata(97, 2, "STONE_BRICK_MONSTER_EGG", "MONSTER_EGG",
				BlockTypes.MONSTER_EGG.getDefaultState().with(Keys.DISGUISED_BLOCK_TYPE, DisguisedBlockTypes.STONEBRICK).get()));

		blocks.put(KCTBlockTypes.MOSSY_STONE_BRICK_MONSTER_EGG, new Metadata(97, 3, "MOSSY_STONE_BRICK_MONSTER_EGG", "MONSTER_EGG",
				BlockTypes.MONSTER_EGG.getDefaultState().with(Keys.DISGUISED_BLOCK_TYPE, DisguisedBlockTypes.MOSSY_STONEBRICK).get()));

		blocks.put(KCTBlockTypes.CRACKED_STONE_BRICK_MONSTER_EGG, new Metadata(97, 4, "CRACKED_STONE_BRICK_MONSTER_EGG", "MONSTER_EGG",
				BlockTypes.MONSTER_EGG.getDefaultState().with(Keys.DISGUISED_BLOCK_TYPE, DisguisedBlockTypes.CRACKED_STONEBRICK).get()));

		blocks.put(KCTBlockTypes.CHISELED_STONE_BRICK_MONSTER_EGG, new Metadata(97, 5, "CHISELED_STONE_BRICK_MONSTER_EGG", "MONSTER_EGG",
				BlockTypes.MONSTER_EGG.getDefaultState().with(Keys.DISGUISED_BLOCK_TYPE, DisguisedBlockTypes.CHISELED_STONEBRICK).get()));

		blocks.put(KCTBlockTypes.STONE_BRICKS, new Metadata(98, 0, "STONE_BRICKS", "STONEBRICK",
				BlockTypes.STONEBRICK.getDefaultState()));

		blocks.put(KCTBlockTypes.MOSSY_STONE_BRICKS, new Metadata(98, 1, "MOSSY_STONE_BRICKS", "STONEBRICK",
				BlockTypes.STONEBRICK.getDefaultState().with(Keys.BRICK_TYPE, BrickTypes.MOSSY).get()));

		blocks.put(KCTBlockTypes.CRACKED_STONE_BRICKS, new Metadata(98, 2, "CRACKED_STONE_BRICKS", "STONEBRICK",
				BlockTypes.STONEBRICK.getDefaultState().with(Keys.BRICK_TYPE, BrickTypes.CRACKED).get()));

		blocks.put(KCTBlockTypes.CHISELED_STONE_BRICKS, new Metadata(98, 3, "CHISELED_STONE_BRICKS", "STONEBRICK",
				BlockTypes.STONEBRICK.getDefaultState().with(Keys.BRICK_TYPE, BrickTypes.CHISELED).get()));

		blocks.put(KCTBlockTypes.BROWN_MUSHROOM_BLOCK, new Metadata(99, 0, "BROWN_MUSHROOM_BLOCK", "BROWN_MUSHROOM_BLOCK",
				BlockTypes.BROWN_MUSHROOM_BLOCK.getDefaultState()));

		blocks.put(KCTBlockTypes.RED_MUSHROOM_BLOCK, new Metadata(100, 0, "RED_MUSHROOM_BLOCK", "RED_MUSHROOM_BLOCK",
				BlockTypes.RED_MUSHROOM_BLOCK.getDefaultState()));

		blocks.put(KCTBlockTypes.IRON_BARS, new Metadata(101, 0, "IRON_BARS", "IRON_BARS",
				BlockTypes.IRON_BARS.getDefaultState()));

		blocks.put(KCTBlockTypes.GLASS_PANE, new Metadata(102, 0, "GLASS_PANE", "GLASS_PANE",
				BlockTypes.GLASS_PANE.getDefaultState()));

		blocks.put(KCTBlockTypes.MELON_BLOCK, new Metadata(103, 0, "MELON_BLOCK", "MELON_BLOCK",
				BlockTypes.MELON_BLOCK.getDefaultState()));

		blocks.put(KCTBlockTypes.PUMPKIN_STEM, new Metadata(104, 0, "PUMPKIN_STEM", "PUMPKIN_STEM",
				BlockTypes.PUMPKIN_STEM.getDefaultState()));

		blocks.put(KCTBlockTypes.MELON_STEM, new Metadata(105, 0, "MELON_STEM", "MELON_STEM",
				BlockTypes.MELON_STEM.getDefaultState()));

		blocks.put(KCTBlockTypes.VINES, new Metadata(106, 0, "VINES", "VINE",
				BlockTypes.VINE.getDefaultState()));

		blocks.put(KCTBlockTypes.OAK_FENCE_GATE, new Metadata(107, 0, "OAK_FENCE_GATE", "FENCE_GATE",
				BlockTypes.FENCE_GATE.getDefaultState()));

		blocks.put(KCTBlockTypes.BRICK_STAIRS, new Metadata(108, 0, "BRICK_STAIRS", "BRICK_STAIRS",
				BlockTypes.BRICK_STAIRS.getDefaultState()));

		blocks.put(KCTBlockTypes.STONE_BRICK_STAIRS, new Metadata(109, 0, "STONE_BRICK_STAIRS", "STONE_BRICK_STAIRS",
				BlockTypes.STONE_BRICK_STAIRS.getDefaultState()));

		blocks.put(KCTBlockTypes.MYCELIUM, new Metadata(110, 0, "MYCELIUM", "MYCELIUM",
				BlockTypes.MYCELIUM.getDefaultState()));

		blocks.put(KCTBlockTypes.LILY_PAD, new Metadata(111, 0, "LILY_PAD", "WATERLILY",
				BlockTypes.WATERLILY.getDefaultState()));

		blocks.put(KCTBlockTypes.NETHER_BRICK, new Metadata(112, 0, "NETHER_BRICK", "NETHER_BRICK",
				BlockTypes.NETHER_BRICK.getDefaultState()));

		blocks.put(KCTBlockTypes.NETHER_BRICK_FENCE, new Metadata(113, 0, "NETHER_BRICK_FENCE", "NETHER_BRICK_FENCE",
				BlockTypes.NETHER_BRICK_FENCE.getDefaultState()));

		blocks.put(KCTBlockTypes.NETHER_BRICK_STAIRS, new Metadata(114, 0, "NETHER_BRICK_STAIRS", "NETHER_BRICK_STAIRS",
				BlockTypes.NETHER_BRICK_STAIRS.getDefaultState()));

		blocks.put(KCTBlockTypes.NETHER_WART, new Metadata(115, 0, "NETHER_WART", "NETHER_WART",
				BlockTypes.NETHER_WART.getDefaultState()));
		
		blocks.put(KCTBlockTypes.ENCHANTMENT_TABLE, new Metadata(116, 0, "ENCHANTMENT_TABLE", "ENCHANTING_TABLE",
				BlockTypes.ENCHANTING_TABLE.getDefaultState()));

		blocks.put(KCTBlockTypes.BREWING_STAND, new Metadata(117, 0, "BREWING_STAND", "BREWING_STAND",
				BlockTypes.BREWING_STAND.getDefaultState()));

		blocks.put(KCTBlockTypes.CAULDRON, new Metadata(118, 0, "CAULDRON", "CAULDRON",
				BlockTypes.CAULDRON.getDefaultState()));

		blocks.put(KCTBlockTypes.END_PORTAL, new Metadata(119, 0, "END_PORTAL", "END_PORTAL",
				BlockTypes.END_PORTAL.getDefaultState()));

		blocks.put(KCTBlockTypes.END_PORTAL_FRAME, new Metadata(120, 0, "END_PORTAL_FRAME", "END_PORTAL_FRAME",
				BlockTypes.END_PORTAL_FRAME.getDefaultState()));

		blocks.put(KCTBlockTypes.END_STONE, new Metadata(121, 0, "END_STONE", "END_STONE",
				BlockTypes.END_STONE.getDefaultState()));

		blocks.put(KCTBlockTypes.DRAGON_EGG, new Metadata(122, 0, "DRAGON_EGG", "DRAGON_EGG",
				BlockTypes.DRAGON_EGG.getDefaultState()));

		blocks.put(KCTBlockTypes.REDSTONE_LAMP_INACTIVE, new Metadata(123, 0, "REDSTONE_LAMP_INACTIVE", "REDSTONE_LAMP",
				BlockTypes.REDSTONE_LAMP.getDefaultState()));

		blocks.put(KCTBlockTypes.REDSTONE_LAMP_ACTIVE, new Metadata(124, 0, "REDSTONE_LAMP_ACTIVE", "LIT_REDSTONE_LAMP",
				BlockTypes.LIT_REDSTONE_LAMP.getDefaultState()));

		blocks.put(KCTBlockTypes.DOUBLE_OAK_WOOD_SLAB, new Metadata(125, 0, "DOUBLE_OAK_WOOD_SLAB", "DOUBLE_WOODEN_SLAB",
				BlockTypes.DOUBLE_WOODEN_SLAB.getDefaultState()));

		blocks.put(KCTBlockTypes.DOUBLE_SPRUCE_WOOD_SLAB, new Metadata(125, 1, "DOUBLE_SPRUCE_WOOD_SLAB", "DOUBLE_WOODEN_SLAB",
				BlockTypes.DOUBLE_WOODEN_SLAB.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.SPRUCE).get()));

		blocks.put(KCTBlockTypes.DOUBLE_BIRCH_WOOD_SLAB, new Metadata(125, 2, "DOUBLE_BIRCH_WOOD_SLAB", "DOUBLE_WOODEN_SLAB",
				BlockTypes.DOUBLE_WOODEN_SLAB.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.BIRCH).get()));

		blocks.put(KCTBlockTypes.DOUBLE_JUNGLE_WOOD_SLAB, new Metadata(125, 3, "DOUBLE_JUNGLE_WOOD_SLAB", "DOUBLE_WOODEN_SLAB",
				BlockTypes.DOUBLE_WOODEN_SLAB.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.JUNGLE).get()));

		blocks.put(KCTBlockTypes.DOUBLE_ACACIA_WOOD_SLAB, new Metadata(125, 4, "DOUBLE_ACACIA_WOOD_SLAB", "DOUBLE_WOODEN_SLAB",
				BlockTypes.DOUBLE_WOODEN_SLAB.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.ACACIA).get()));

		blocks.put(KCTBlockTypes.DOUBLE_DARK_OAK_WOOD_SLAB, new Metadata(125, 5, "DOUBLE_DARK_OAK_WOOD_SLAB", "DOUBLE_WOODEN_SLAB",
				BlockTypes.DOUBLE_WOODEN_SLAB.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.OAK).get()));

		blocks.put(KCTBlockTypes.OAK_WOOD_SLAB, new Metadata(126, 0, "OAK_WOOD_SLAB", "WOODEN_SLAB",
				BlockTypes.WOODEN_SLAB.getDefaultState()));

		blocks.put(KCTBlockTypes.SPRUCE_WOOD_SLAB, new Metadata(126, 1, "SPRUCE_WOOD_SLAB", "WOODEN_SLAB",
				BlockTypes.WOODEN_SLAB.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.SPRUCE).get()));

		blocks.put(KCTBlockTypes.BIRCH_WOOD_SLAB, new Metadata(126, 2, "BIRCH_WOOD_SLAB", "WOODEN_SLAB",
				BlockTypes.WOODEN_SLAB.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.BIRCH).get()));

		blocks.put(KCTBlockTypes.JUNGLE_WOOD_SLAB, new Metadata(126, 3, "JUNGLE_WOOD_SLAB", "WOODEN_SLAB",
				BlockTypes.WOODEN_SLAB.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.JUNGLE).get()));

		blocks.put(KCTBlockTypes.ACACIA_WOOD_SLAB, new Metadata(126, 4, "ACACIA_WOOD_SLAB", "WOODEN_SLAB",
				BlockTypes.WOODEN_SLAB.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.ACACIA).get()));

		blocks.put(KCTBlockTypes.DARK_OAK_WOOD_SLAB, new Metadata(126, 5, "DARK_OAK_WOOD_SLAB", "WOODEN_SLAB",
				BlockTypes.WOODEN_SLAB.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.DARK_OAK).get()));

		blocks.put(KCTBlockTypes.COCOA, new Metadata(127, 0, "COCOA", "COCOA",
				BlockTypes.COCOA.getDefaultState()));

		blocks.put(KCTBlockTypes.SANDSTONE_STAIRS, new Metadata(128, 0, "SANDSTONE_STAIRS", "SANDSTONE_STAIRS",
				BlockTypes.SANDSTONE_STAIRS.getDefaultState()));

		blocks.put(KCTBlockTypes.EMERALD_ORE, new Metadata(129, 0, "EMERALD_ORE", "EMERALD_ORE",
				BlockTypes.EMERALD_ORE.getDefaultState()));

		blocks.put(KCTBlockTypes.ENDER_CHEST, new Metadata(130, 0, "ENDER_CHEST", "ENDER_CHEST",
				BlockTypes.ENDER_CHEST.getDefaultState()));

		blocks.put(KCTBlockTypes.TRIPWIRE_HOOK, new Metadata(131, 0, "TRIPWIRE_HOOK", "TRIPWIRE_HOOK",
				BlockTypes.TRIPWIRE_HOOK.getDefaultState()));

		blocks.put(KCTBlockTypes.TRIPWIRE, new Metadata(132, 0, "TRIPWIRE", "TRIPWIRE_HOOK",
				BlockTypes.TRIPWIRE_HOOK.getDefaultState()));

		blocks.put(KCTBlockTypes.EMERALD_BLOCK, new Metadata(133, 0, "EMERALD_BLOCK", "EMERALD_BLOCK",
				BlockTypes.EMERALD_BLOCK.getDefaultState()));

		blocks.put(KCTBlockTypes.SPRUCE_WOOD_STAIRS, new Metadata(134, 0, "SPRUCE_WOOD_STAIRS", "SPRUCE_STAIRS",
				BlockTypes.SPRUCE_STAIRS.getDefaultState()));

		blocks.put(KCTBlockTypes.BIRCH_WOOD_STAIRS, new Metadata(135, 0, "BIRCH_WOOD_STAIRS", "BIRCH_STAIRS",
				BlockTypes.BIRCH_STAIRS.getDefaultState()));

		blocks.put(KCTBlockTypes.JUNGLE_WOOD_STAIRS, new Metadata(136, 0, "JUNGLE_WOOD_STAIRS", "JUNGLE_STAIRS",
				BlockTypes.JUNGLE_STAIRS.getDefaultState()));

		blocks.put(KCTBlockTypes.COMMAND_BLOCK, new Metadata(137, 0, "COMMAND_BLOCK", "COMMAND_BLOCK",
				BlockTypes.COMMAND_BLOCK.getDefaultState()));

		blocks.put(KCTBlockTypes.BEACON, new Metadata(138, 0, "BEACON", "BEACON",
				BlockTypes.BEACON.getDefaultState()));

		blocks.put(KCTBlockTypes.COBBLESTONE_WALL, new Metadata(139, 0, "COBBLESTONE_WALL", "COBBLESTONE_WALL",
				BlockTypes.COBBLESTONE_WALL.getDefaultState()));

		blocks.put(KCTBlockTypes.MOSSY_COBBLESTONE_WALL, new Metadata(139, 1, "MOSSY_COBBLESTONE_WALL", "COBBLESTONE_WALL",
				BlockTypes.COBBLESTONE_WALL.getDefaultState().with(Keys.WALL_TYPE, WallTypes.MOSSY).get()));

		blocks.put(KCTBlockTypes.FLOWER_POT, new Metadata(140, 0, "FLOWER_POT", "FLOWER_POT",
				BlockTypes.FLOWER_POT.getDefaultState()));

		blocks.put(KCTBlockTypes.CARROTS, new Metadata(141, 0, "CARROTS", "CARROTS",
				BlockTypes.CARROTS.getDefaultState()));

		blocks.put(KCTBlockTypes.POTATOES, new Metadata(142, 0, "POTATOES", "POTATOES",
				BlockTypes.POTATOES.getDefaultState()));

		blocks.put(KCTBlockTypes.WOODEN_BUTTON, new Metadata(143, 0, "WOODEN_BUTTON", "WOODEN_BUTTON",
				BlockTypes.WOODEN_BUTTON.getDefaultState()));

		blocks.put(KCTBlockTypes.MOB_HEAD, new Metadata(144, 0, "MOB_HEAD", "SKULL",
				BlockTypes.SKULL.getDefaultState()));

		blocks.put(KCTBlockTypes.ANVIL, new Metadata(145, 0, "ANVIL", "ANVIL",
				BlockTypes.ANVIL.getDefaultState()));

		blocks.put(KCTBlockTypes.TRAPPED_CHEST, new Metadata(146, 0, "TRAPPED_CHEST", "TRAPPED_CHEST",
				BlockTypes.TRAPPED_CHEST.getDefaultState()));

		blocks.put(KCTBlockTypes.WEIGHTED_PRESSURE_PLATE_LIGHT, new Metadata(147, 0, "WEIGHTED_PRESSURE_PLATE_LIGHT", "LIGHT_WEIGHTED_PRESSURE_PLATE",
				BlockTypes.LIGHT_WEIGHTED_PRESSURE_PLATE.getDefaultState()));

		blocks.put(KCTBlockTypes.WEIGHTED_PRESSURE_PLATE_HEAVY, new Metadata(148, 0, "WEIGHTED_PRESSURE_PLATE_HEAVY", "HEAVY_WEIGHTED_PRESSURE_PLATE",
				BlockTypes.HEAVY_WEIGHTED_PRESSURE_PLATE.getDefaultState()));

		blocks.put(KCTBlockTypes.REDSTONE_COMPARATOR_INACTIVE, new Metadata(149, 0, "REDSTONE_COMPARATOR_INACTIVE", "UNPOWERED_COMPARATOR",
				BlockTypes.UNPOWERED_COMPARATOR.getDefaultState()));

		blocks.put(KCTBlockTypes.REDSTONE_COMPARATOR_ACTIVE, new Metadata(150, 0, "REDSTONE_COMPARATOR_ACTIVE", "POWERED_COMPARATOR",
				BlockTypes.POWERED_COMPARATOR.getDefaultState()));

		blocks.put(KCTBlockTypes.DAYLIGHT_SENSOR, new Metadata(151, 0, "DAYLIGHT_SENSOR", "DAYLIGHT_DETECTOR",
				BlockTypes.DAYLIGHT_DETECTOR.getDefaultState()));

		blocks.put(KCTBlockTypes.REDSTONE_BLOCK, new Metadata(152, 0, "REDSTONE_BLOCK", "REDSTONE_BLOCK",
				BlockTypes.REDSTONE_BLOCK.getDefaultState()));

		blocks.put(KCTBlockTypes.NETHER_QUARTZ_ORE, new Metadata(153, 0, "NETHER_QUARTZ_ORE", "QUARTZ_ORE",
				BlockTypes.QUARTZ_ORE.getDefaultState()));

		blocks.put(KCTBlockTypes.HOPPER, new Metadata(154, 0, "HOPPER", "HOPPER",
				BlockTypes.HOPPER.getDefaultState()));

		blocks.put(KCTBlockTypes.QUARTZ_BLOCK, new Metadata(155, 0, "QUARTZ_BLOCK", "QUARTZ_BLOCK",
				BlockTypes.QUARTZ_BLOCK.getDefaultState()));

		blocks.put(KCTBlockTypes.CHISELED_QUARTZ_BLOCK, new Metadata(155, 1, "CHISELED_QUARTZ_BLOCK", "QUARTZ_BLOCK",
				BlockTypes.QUARTZ_BLOCK.getDefaultState().with(Keys.QUARTZ_TYPE, QuartzTypes.CHISELED).get()));

		blocks.put(KCTBlockTypes.PILLAR_QUARTZ_BLOCK, new Metadata(155, 2, "PILLAR_QUARTZ_BLOCK", "QUARTZ_BLOCK",
				BlockTypes.QUARTZ_BLOCK.getDefaultState().with(Keys.QUARTZ_TYPE, QuartzTypes.LINES_Y).get()));

		blocks.put(KCTBlockTypes.QUARTZ_STAIRS, new Metadata(156, 0, "QUARTZ_STAIRS", "QUARTZ_STAIRS",
				BlockTypes.QUARTZ_STAIRS.getDefaultState()));

		blocks.put(KCTBlockTypes.ACTIVATOR_RAIL, new Metadata(157, 0, "ACTIVATOR_RAIL", "ACTIVATOR_RAIL",
				BlockTypes.ACTIVATOR_RAIL.getDefaultState()));

		blocks.put(KCTBlockTypes.DROPPER, new Metadata(158, 0, "DROPPER", "DROPPER",
				BlockTypes.DROPPER.getDefaultState()));

		blocks.put(KCTBlockTypes.WHITE_STAINED_CLAY, new Metadata(159, 0, "WHITE_STAINED_CLAY", "STAINED_HARDENED_CLAY",
				BlockTypes.STAINED_HARDENED_CLAY.getDefaultState()));

		blocks.put(KCTBlockTypes.ORANGE_STAINED_CLAY, new Metadata(159, 1, "ORANGE_STAINED_CLAY", "STAINED_HARDENED_CLAY",
				BlockTypes.STAINED_HARDENED_CLAY.getDefaultState().with(Keys.DYE_COLOR, DyeColors.ORANGE).get()));

		blocks.put(KCTBlockTypes.MAGENTA_STAINED_CLAY, new Metadata(159, 2, "MAGENTA_STAINED_CLAY", "STAINED_HARDENED_CLAY",
				BlockTypes.STAINED_HARDENED_CLAY.getDefaultState().with(Keys.DYE_COLOR, DyeColors.MAGENTA).get()));

		blocks.put(KCTBlockTypes.LIGHT_BLUE_STAINED_CLAY, new Metadata(159, 3, "LIGHT_BLUE_STAINED_CLAY", "STAINED_HARDENED_CLAY",
				BlockTypes.STAINED_HARDENED_CLAY.getDefaultState().with(Keys.DYE_COLOR, DyeColors.LIGHT_BLUE).get()));

		blocks.put(KCTBlockTypes.YELLOW_STAINED_CLAY, new Metadata(159, 4, "YELLOW_STAINED_CLAY", "STAINED_HARDENED_CLAY",
				BlockTypes.STAINED_HARDENED_CLAY.getDefaultState().with(Keys.DYE_COLOR, DyeColors.YELLOW).get()));

		blocks.put(KCTBlockTypes.LIME_STAINED_CLAY, new Metadata(159, 5, "LIME_STAINED_CLAY", "STAINED_HARDENED_CLAY",
				BlockTypes.STAINED_HARDENED_CLAY.getDefaultState().with(Keys.DYE_COLOR, DyeColors.LIME).get()));

		blocks.put(KCTBlockTypes.PINK_STAINED_CLAY, new Metadata(159, 6, "PINK_STAINED_CLAY", "STAINED_HARDENED_CLAY",
				BlockTypes.STAINED_HARDENED_CLAY.getDefaultState().with(Keys.DYE_COLOR, DyeColors.PINK).get()));

		blocks.put(KCTBlockTypes.GRAY_STAINED_CLAY, new Metadata(159, 7, "GRAY_STAINED_CLAY", "STAINED_HARDENED_CLAY",
				BlockTypes.STAINED_HARDENED_CLAY.getDefaultState().with(Keys.DYE_COLOR, DyeColors.GRAY).get()));

		blocks.put(KCTBlockTypes.LIGHT_GRAY_STAINED_CLAY, new Metadata(159, 8, "LIGHT_GRAY_STAINED_CLAY", "STAINED_HARDENED_CLAY",
				BlockTypes.STAINED_HARDENED_CLAY.getDefaultState().with(Keys.DYE_COLOR, DyeColors.GRAY).get()));

		blocks.put(KCTBlockTypes.CYAN_STAINED_CLAY, new Metadata(159, 9, "CYAN_STAINED_CLAY", "STAINED_HARDENED_CLAY",
				BlockTypes.STAINED_HARDENED_CLAY.getDefaultState().with(Keys.DYE_COLOR, DyeColors.CYAN).get()));

		blocks.put(KCTBlockTypes.PURPLE_STAINED_CLAY, new Metadata(159, 10, "PURPLE_STAINED_CLAY", "STAINED_HARDENED_CLAY",
				BlockTypes.STAINED_HARDENED_CLAY.getDefaultState().with(Keys.DYE_COLOR, DyeColors.PURPLE).get()));

		blocks.put(KCTBlockTypes.BLUE_STAINED_CLAY, new Metadata(159, 11, "BLUE_STAINED_CLAY", "STAINED_HARDENED_CLAY",
				BlockTypes.STAINED_HARDENED_CLAY.getDefaultState().with(Keys.DYE_COLOR, DyeColors.BLUE).get()));

		blocks.put(KCTBlockTypes.BROWN_STAINED_CLAY, new Metadata(159, 12, "BROWN_STAINED_CLAY", "STAINED_HARDENED_CLAY",
				BlockTypes.STAINED_HARDENED_CLAY.getDefaultState().with(Keys.DYE_COLOR, DyeColors.BROWN).get()));

		blocks.put(KCTBlockTypes.GREEN_STAINED_CLAY, new Metadata(159, 13, "GREEN_STAINED_CLAY", "STAINED_HARDENED_CLAY",
				BlockTypes.STAINED_HARDENED_CLAY.getDefaultState().with(Keys.DYE_COLOR, DyeColors.GREEN).get()));

		blocks.put(KCTBlockTypes.RED_STAINED_CLAY, new Metadata(159, 14, "RED_STAINED_CLAY", "STAINED_HARDENED_CLAY",
				BlockTypes.STAINED_HARDENED_CLAY.getDefaultState().with(Keys.DYE_COLOR, DyeColors.RED).get()));

		blocks.put(KCTBlockTypes.BLACK_STAINED_CLAY, new Metadata(159, 15, "BLACK_STAINED_CLAY", "STAINED_HARDENED_CLAY",
				BlockTypes.STAINED_HARDENED_CLAY.getDefaultState().with(Keys.DYE_COLOR, DyeColors.BLACK).get()));

		blocks.put(KCTBlockTypes.WHITE_STAINED_GLASS_PANE, new Metadata(160, 0, "WHITE_STAINED_GLASS_PANE", "STAINED_GLASS_PANE",
				BlockTypes.STAINED_GLASS_PANE.getDefaultState()));

		blocks.put(KCTBlockTypes.ORANGE_STAINED_GLASS_PANE, new Metadata(160, 1, "ORANGE_STAINED_GLASS_PANE", "STAINED_GLASS_PANE",
				BlockTypes.STAINED_GLASS_PANE.getDefaultState().with(Keys.DYE_COLOR, DyeColors.ORANGE).get()));

		blocks.put(KCTBlockTypes.MAGENTA_STAINED_GLASS_PANE, new Metadata(160, 2, "MAGENTA_STAINED_GLASS_PANE", "STAINED_GLASS_PANE",
				BlockTypes.STAINED_GLASS_PANE.getDefaultState().with(Keys.DYE_COLOR, DyeColors.MAGENTA).get()));

		blocks.put(KCTBlockTypes.LIGHT_BLUE_STAINED_GLASS_PANE, new Metadata(160, 3, "LIGHT_BLUE_STAINED_GLASS_PANE", "STAINED_GLASS_PANE",
				BlockTypes.STAINED_GLASS_PANE.getDefaultState().with(Keys.DYE_COLOR, DyeColors.LIGHT_BLUE).get()));

		blocks.put(KCTBlockTypes.YELLOW_STAINED_GLASS_PANE, new Metadata(160, 4, "YELLOW_STAINED_GLASS_PANE", "STAINED_GLASS_PANE",
				BlockTypes.STAINED_GLASS_PANE.getDefaultState().with(Keys.DYE_COLOR, DyeColors.YELLOW).get()));

		blocks.put(KCTBlockTypes.LIME_STAINED_GLASS_PANE, new Metadata(160, 5, "LIME_STAINED_GLASS_PANE", "STAINED_GLASS_PANE",
				BlockTypes.STAINED_GLASS_PANE.getDefaultState().with(Keys.DYE_COLOR, DyeColors.LIME).get()));

		blocks.put(KCTBlockTypes.PINK_STAINED_GLASS_PANE, new Metadata(160, 6, "PINK_STAINED_GLASS_PANE", "STAINED_GLASS_PANE",
				BlockTypes.STAINED_GLASS_PANE.getDefaultState().with(Keys.DYE_COLOR, DyeColors.PINK).get()));

		blocks.put(KCTBlockTypes.GRAY_STAINED_GLASS_PANE, new Metadata(160, 7, "GRAY_STAINED_GLASS_PANE", "STAINED_GLASS_PANE",
				BlockTypes.STAINED_GLASS_PANE.getDefaultState().with(Keys.DYE_COLOR, DyeColors.GRAY).get()));

		blocks.put(KCTBlockTypes.LIGHT_GRAY_STAINED_GLASS_PANE, new Metadata(160, 8, "LIGHT_GRAY_STAINED_GLASS_PANE", "STAINED_GLASS_PANE",
				BlockTypes.STAINED_GLASS_PANE.getDefaultState().with(Keys.DYE_COLOR, DyeColors.GRAY).get()));

		blocks.put(KCTBlockTypes.CYAN_STAINED_GLASS_PANE, new Metadata(160, 9, "CYAN_STAINED_GLASS_PANE", "STAINED_GLASS_PANE",
				BlockTypes.STAINED_GLASS_PANE.getDefaultState().with(Keys.DYE_COLOR, DyeColors.CYAN).get()));

		blocks.put(KCTBlockTypes.PURPLE_STAINED_GLASS_PANE, new Metadata(160, 10, "PURPLE_STAINED_GLASS_PANE", "STAINED_GLASS_PANE",
				BlockTypes.STAINED_GLASS_PANE.getDefaultState().with(Keys.DYE_COLOR, DyeColors.PURPLE).get()));

		blocks.put(KCTBlockTypes.BLUE_STAINED_GLASS_PANE, new Metadata(160, 11, "BLUE_STAINED_GLASS_PANE", "STAINED_GLASS_PANE",
				BlockTypes.STAINED_GLASS_PANE.getDefaultState().with(Keys.DYE_COLOR, DyeColors.BLUE).get()));

		blocks.put(KCTBlockTypes.BROWN_STAINED_GLASS_PANE, new Metadata(160, 12, "BROWN_STAINED_GLASS_PANE", "STAINED_GLASS_PANE",
				BlockTypes.STAINED_GLASS_PANE.getDefaultState().with(Keys.DYE_COLOR, DyeColors.BROWN).get()));

		blocks.put(KCTBlockTypes.GREEN_STAINED_GLASS_PANE, new Metadata(160, 13, "GREEN_STAINED_GLASS_PANE", "STAINED_GLASS_PANE",
				BlockTypes.STAINED_GLASS_PANE.getDefaultState().with(Keys.DYE_COLOR, DyeColors.GREEN).get()));

		blocks.put(KCTBlockTypes.RED_STAINED_GLASS_PANE, new Metadata(160, 14, "RED_STAINED_GLASS_PANE", "STAINED_GLASS_PANE",
				BlockTypes.STAINED_GLASS_PANE.getDefaultState().with(Keys.DYE_COLOR, DyeColors.RED).get()));

		blocks.put(KCTBlockTypes.BLACK_STAINED_GLASS_PANE, new Metadata(160, 15, "BLACK_STAINED_GLASS_PANE", "STAINED_GLASS_PANE",
				BlockTypes.STAINED_GLASS_PANE.getDefaultState().with(Keys.DYE_COLOR, DyeColors.BLACK).get()));

		blocks.put(KCTBlockTypes.ACACIA_LEAVES, new Metadata(161, 0, "ACACIA_LEAVES", "LEAVES2",
				BlockTypes.LEAVES2.getDefaultState()));

		blocks.put(KCTBlockTypes.DARK_OAK_LEAVES, new Metadata(161, 1, "DARK_OAK_LEAVES", "LEAVES2",
				BlockTypes.LEAVES2.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.DARK_OAK).get()));

		blocks.put(KCTBlockTypes.ACACIA_WOOD, new Metadata(162, 0, "ACACIA_WOOD", "LOG2",
				BlockTypes.LOG2.getDefaultState()));

		blocks.put(KCTBlockTypes.DARK_OAK_WOOD, new Metadata(162, 1, "DARK_OAK_WOOD", "LOG2",
				BlockTypes.LOG2.getDefaultState().with(Keys.TREE_TYPE, TreeTypes.DARK_OAK).get()));

		blocks.put(KCTBlockTypes.ACACIA_WOOD_STAIRS, new Metadata(163, 0, "ACACIA_WOOD_STAIRS", "ACACIA_STAIRS",
				BlockTypes.ACACIA_STAIRS.getDefaultState()));

		blocks.put(KCTBlockTypes.DARK_OAK_WOOD_STAIRS, new Metadata(164, 0, "DARK_OAK_WOOD_STAIRS", "DARK_OAK_STAIRS",
				BlockTypes.DARK_OAK_STAIRS.getDefaultState()));

		blocks.put(KCTBlockTypes.SLIME_BLOCK, new Metadata(165, 0, "SLIME_BLOCK", "SLIME",
				BlockTypes.SLIME.getDefaultState()));

		blocks.put(KCTBlockTypes.BARRIER, new Metadata(166, 0, "BARRIER", "BARRIER",
				BlockTypes.BARRIER.getDefaultState()));

		blocks.put(KCTBlockTypes.IRON_TRAPDOOR, new Metadata(167, 0, "IRON_TRAPDOOR", "IRON_TRAPDOOR",
				BlockTypes.IRON_TRAPDOOR.getDefaultState()));

		blocks.put(KCTBlockTypes.PRISMARINE, new Metadata(168, 0, "PRISMARINE", "PRISMARINE",
				BlockTypes.PRISMARINE.getDefaultState()));

		blocks.put(KCTBlockTypes.PRISMARINE_BRICKS, new Metadata(168, 1, "PRISMARINE_BRICKS", "PRISMARINE",
				BlockTypes.PRISMARINE.getDefaultState().with(Keys.PRISMARINE_TYPE, PrismarineTypes.BRICKS).get()));

		blocks.put(KCTBlockTypes.DARK_PRISMARINE, new Metadata(168, 2, "DARK_PRISMARINE", "PRISMARINE",
				BlockTypes.PRISMARINE.getDefaultState().with(Keys.PRISMARINE_TYPE, PrismarineTypes.DARK).get()));

		blocks.put(KCTBlockTypes.SEA_LANTERN, new Metadata(169, 0, "SEA_LANTERN", "SEA_LANTERN",
				BlockTypes.SEA_LANTERN.getDefaultState()));

		blocks.put(KCTBlockTypes.HAY_BALE, new Metadata(170, 0, "HAY_BALE", "HAY_BLOCK",
				BlockTypes.HAY_BLOCK.getDefaultState()));

		blocks.put(KCTBlockTypes.WHITE_CARPET, new Metadata(171, 0, "WHITE_CARPET", "CARPET",
				BlockTypes.CARPET.getDefaultState()));

		blocks.put(KCTBlockTypes.ORANGE_CARPET, new Metadata(171, 1, "ORANGE_CARPET", "CARPET",
				BlockTypes.CARPET.getDefaultState().with(Keys.DYE_COLOR, DyeColors.ORANGE).get()));

		blocks.put(KCTBlockTypes.MAGENTA_CARPET, new Metadata(171, 2, "MAGENTA_CARPET", "CARPET",
				BlockTypes.CARPET.getDefaultState().with(Keys.DYE_COLOR, DyeColors.MAGENTA).get()));

		blocks.put(KCTBlockTypes.LIGHT_BLUE_CARPET, new Metadata(171, 3, "LIGHT_BLUE_CARPET", "CARPET",
				BlockTypes.CARPET.getDefaultState().with(Keys.DYE_COLOR, DyeColors.LIGHT_BLUE).get()));

		blocks.put(KCTBlockTypes.YELLOW_CARPET, new Metadata(171, 4, "YELLOW_CARPET", "CARPET",
				BlockTypes.CARPET.getDefaultState().with(Keys.DYE_COLOR, DyeColors.YELLOW).get()));

		blocks.put(KCTBlockTypes.LIME_CARPET, new Metadata(171, 5, "LIME_CARPET", "CARPET",
				BlockTypes.CARPET.getDefaultState().with(Keys.DYE_COLOR, DyeColors.LIME).get()));

		blocks.put(KCTBlockTypes.PINK_CARPET, new Metadata(171, 6, "PINK_CARPET", "CARPET",
				BlockTypes.CARPET.getDefaultState().with(Keys.DYE_COLOR, DyeColors.PINK).get()));

		blocks.put(KCTBlockTypes.GRAY_CARPET, new Metadata(171, 7, "GRAY_CARPET", "CARPET",
				BlockTypes.CARPET.getDefaultState().with(Keys.DYE_COLOR, DyeColors.GRAY).get()));

		blocks.put(KCTBlockTypes.LIGHT_GRAY_CARPET, new Metadata(171, 8, "LIGHT_GRAY_CARPET", "CARPET",
				BlockTypes.CARPET.getDefaultState().with(Keys.DYE_COLOR, DyeColors.GRAY).get()));

		blocks.put(KCTBlockTypes.CYAN_CARPET, new Metadata(171, 9, "CYAN_CARPET", "CARPET",
				BlockTypes.CARPET.getDefaultState().with(Keys.DYE_COLOR, DyeColors.CYAN).get()));

		blocks.put(KCTBlockTypes.PURPLE_CARPET, new Metadata(171, 10, "PURPLE_CARPET", "CARPET",
				BlockTypes.CARPET.getDefaultState().with(Keys.DYE_COLOR, DyeColors.PURPLE).get()));

		blocks.put(KCTBlockTypes.BLUE_CARPET, new Metadata(171, 11, "BLUE_CARPET", "CARPET",
				BlockTypes.CARPET.getDefaultState().with(Keys.DYE_COLOR, DyeColors.BLUE).get()));

		blocks.put(KCTBlockTypes.BROWN_CARPET, new Metadata(171, 12, "BROWN_CARPET", "CARPET",
				BlockTypes.CARPET.getDefaultState().with(Keys.DYE_COLOR, DyeColors.BROWN).get()));

		blocks.put(KCTBlockTypes.GREEN_CARPET, new Metadata(171, 13, "GREEN_CARPET", "CARPET",
				BlockTypes.CARPET.getDefaultState().with(Keys.DYE_COLOR, DyeColors.GREEN).get()));

		blocks.put(KCTBlockTypes.RED_CARPET, new Metadata(171, 14, "RED_CARPET", "CARPET",
				BlockTypes.CARPET.getDefaultState().with(Keys.DYE_COLOR, DyeColors.RED).get()));

		blocks.put(KCTBlockTypes.BLACK_CARPET, new Metadata(171, 15, "BLACK_CARPET", "CARPET",
				BlockTypes.CARPET.getDefaultState().with(Keys.DYE_COLOR, DyeColors.BLACK).get()));

		blocks.put(KCTBlockTypes.HARDENED_CLAY, new Metadata(172, 0, "HARDENED_CLAY", "HARDENED_CLAY",
				BlockTypes.HARDENED_CLAY.getDefaultState()));

		blocks.put(KCTBlockTypes.BLOCK_OF_COAL, new Metadata(173, 0, "BLOCK_OF_COAL", "COAL_BLOCK",
				BlockTypes.COAL_BLOCK.getDefaultState()));

		blocks.put(KCTBlockTypes.PACKED_ICE, new Metadata(174, 0, "PACKED_ICE", "PACKED_ICE",
				BlockTypes.PACKED_ICE.getDefaultState()));

		blocks.put(KCTBlockTypes.SUNFLOWER, new Metadata(175, 0, "SUNFLOWER", "DOUBLE_PLANT",
				BlockTypes.DOUBLE_PLANT.getDefaultState()));

		blocks.put(KCTBlockTypes.LILAC, new Metadata(175, 1, "LILAC", "DOUBLE_PLANT",
				BlockTypes.DOUBLE_PLANT.getDefaultState().with(Keys.DOUBLE_PLANT_TYPE, DoublePlantTypes.SYRINGA).get()));

		blocks.put(KCTBlockTypes.DOUBLE_TALLGRASS, new Metadata(175, 2, "DOUBLE_TALLGRASS", "DOUBLE_PLANT",
				BlockTypes.DOUBLE_PLANT.getDefaultState().with(Keys.DOUBLE_PLANT_TYPE, DoublePlantTypes.GRASS).get()));

		blocks.put(KCTBlockTypes.LARGE_FERN, new Metadata(175, 3, "LARGE_FERN", "DOUBLE_PLANT",
				BlockTypes.DOUBLE_PLANT.getDefaultState().with(Keys.DOUBLE_PLANT_TYPE, DoublePlantTypes.FERN).get()));

		blocks.put(KCTBlockTypes.ROSE_BUSH, new Metadata(175, 4, "ROSE_BUSH", "DOUBLE_PLANT",
				BlockTypes.DOUBLE_PLANT.getDefaultState().with(Keys.DOUBLE_PLANT_TYPE, DoublePlantTypes.ROSE).get()));

		blocks.put(KCTBlockTypes.PEONY, new Metadata(175, 5, "PEONY", "DOUBLE_PLANT",
				BlockTypes.DOUBLE_PLANT.getDefaultState().with(Keys.DOUBLE_PLANT_TYPE, DoublePlantTypes.PAEONIA).get()));

		blocks.put(KCTBlockTypes.FREE_STANDING_BANNER, new Metadata(176, 0, "FREE_STANDING_BANNER", "STANDING_BANNER",
				BlockTypes.STANDING_BANNER.getDefaultState()));

		blocks.put(KCTBlockTypes.WALL_MOUNTED_BANNER, new Metadata(177, 0, "WALL_MOUNTED_BANNER", "WALL_BANNER",
				BlockTypes.WALL_BANNER.getDefaultState()));

		blocks.put(KCTBlockTypes.INVERTED_DAYLIGHT_SENSOR, new Metadata(178, 0, "INVERTED_DAYLIGHT_SENSOR", "DAYLIGHT_DETECTOR_INVERTED",
				BlockTypes.DAYLIGHT_DETECTOR_INVERTED.getDefaultState()));

		blocks.put(KCTBlockTypes.RED_SANDSTONE, new Metadata(179, 0, "RED_SANDSTONE", "RED_SANDSTONE",
				BlockTypes.RED_SANDSTONE.getDefaultState()));

		blocks.put(KCTBlockTypes.CHISELED_RED_SANDSTONE, new Metadata(179, 1, "CHISELED_RED_SANDSTONE", "RED_SANDSTONE",
				BlockTypes.RED_SANDSTONE.getDefaultState().with(Keys.SANDSTONE_TYPE, SandstoneTypes.CHISELED).get()));

		blocks.put(KCTBlockTypes.SMOOTH_RED_SANDSTONE, new Metadata(179, 2, "SMOOTH_RED_SANDSTONE", "RED_SANDSTONE",
				BlockTypes.RED_SANDSTONE.getDefaultState().with(Keys.SANDSTONE_TYPE, SandstoneTypes.SMOOTH).get()));

		blocks.put(KCTBlockTypes.RED_SANDSTONE_STAIRS, new Metadata(180, 0, "RED_SANDSTONE_STAIRS", "RED_SANDSTONE_STAIRS",
				BlockTypes.RED_SANDSTONE_STAIRS.getDefaultState()));

		blocks.put(KCTBlockTypes.DOUBLE_RED_SANDSTONE_SLAB, new Metadata(181, 0, "DOUBLE_RED_SANDSTONE_SLAB", "DOUBLE_STONE_SLAB2",
				BlockTypes.DOUBLE_STONE_SLAB2.getDefaultState()));

		blocks.put(KCTBlockTypes.RED_SANDSTONE_SLAB, new Metadata(182, 0, "RED_SANDSTONE_SLAB", "STONE_SLAB2",
				BlockTypes.STONE_SLAB2.getDefaultState()));

		blocks.put(KCTBlockTypes.SPRUCE_FENCE_GATE, new Metadata(183, 0, "SPRUCE_FENCE_GATE", "SPRUCE_FENCE_GATE",
				BlockTypes.SPRUCE_FENCE_GATE.getDefaultState()));

		blocks.put(KCTBlockTypes.BIRCH_FENCE_GATE, new Metadata(184, 0, "BIRCH_FENCE_GATE", "BIRCH_FENCE_GATE",
				BlockTypes.BIRCH_FENCE_GATE.getDefaultState()));

		blocks.put(KCTBlockTypes.JUNGLE_FENCE_GATE, new Metadata(185, 0, "JUNGLE_FENCE_GATE", "JUNGLE_FENCE_GATE",
				BlockTypes.JUNGLE_FENCE_GATE.getDefaultState()));

		blocks.put(KCTBlockTypes.DARK_OAK_FENCE_GATE, new Metadata(186, 0, "DARK_OAK_FENCE_GATE", "DARK_OAK_FENCE_GATE",
				BlockTypes.DARK_OAK_FENCE_GATE.getDefaultState()));

		blocks.put(KCTBlockTypes.ACACIA_FENCE_GATE, new Metadata(187, 0, "ACACIA_FENCE_GATE", "ACACIA_FENCE_GATE",
				BlockTypes.ACACIA_FENCE_GATE.getDefaultState()));

		blocks.put(KCTBlockTypes.SPRUCE_FENCE, new Metadata(188, 0, "SPRUCE_FENCE", "SPRUCE_FENCE",
				BlockTypes.SPRUCE_FENCE.getDefaultState()));

		blocks.put(KCTBlockTypes.BIRCH_FENCE, new Metadata(189, 0, "BIRCH_FENCE", "BIRCH_FENCE",
				BlockTypes.BIRCH_FENCE.getDefaultState()));

		blocks.put(KCTBlockTypes.JUNGLE_FENCE, new Metadata(190, 0, "JUNGLE_FENCE", "JUNGLE_FENCE",
				BlockTypes.JUNGLE_FENCE.getDefaultState()));

		blocks.put(KCTBlockTypes.DARK_OAK_FENCE, new Metadata(191, 0, "DARK_OAK_FENCE", "DARK_OAK_FENCE",
				BlockTypes.DARK_OAK_FENCE.getDefaultState()));

		blocks.put(KCTBlockTypes.ACACIA_FENCE, new Metadata(192, 0, "ACACIA_FENCE", "ACACIA_FENCE",
				BlockTypes.ACACIA_FENCE.getDefaultState()));

		blocks.put(KCTBlockTypes.SPRUCE_DOOR_BLOCK, new Metadata(193, 0, "SPRUCE_DOOR_BLOCK", "SPRUCE_DOOR",
				BlockTypes.SPRUCE_DOOR.getDefaultState()));

		blocks.put(KCTBlockTypes.BIRCH_DOOR_BLOCK, new Metadata(194, 0, "BIRCH_DOOR_BLOCK", "BIRCH_DOOR",
				BlockTypes.BIRCH_DOOR.getDefaultState()));

		blocks.put(KCTBlockTypes.JUNGLE_DOOR_BLOCK, new Metadata(195, 0, "JUNGLE_DOOR_BLOCK", "JUNGLE_DOOR",
				BlockTypes.JUNGLE_DOOR.getDefaultState()));

		blocks.put(KCTBlockTypes.ACACIA_DOOR_BLOCK, new Metadata(196, 0, "ACACIA_DOOR_BLOCK", "ACACIA_DOOR",
				BlockTypes.ACACIA_DOOR.getDefaultState()));

		blocks.put(KCTBlockTypes.DARK_OAK_DOOR_BLOCK, new Metadata(197, 0, "DARK_OAK_DOOR_BLOCK", "DARK_OAK_DOOR",
				BlockTypes.DARK_OAK_DOOR.getDefaultState()));
		
		/* Minecraft Blocks added after Version 1.9 
		blocks.put(KCTBlockTypes.END_ROD, new Metadata(198, 0, "END_ROD", "END_ROD",
				BlockTypes.END_ROD.getDefaultState()));

		blocks.put(KCTBlockTypes.CHORUS_PLANT, new Metadata(199, 0, "CHORUS_PLANT", "CHORUS_PLANT",
				BlockTypes.CHORUS_PLANT.getDefaultState()));

		blocks.put(KCTBlockTypes.CHORUS_FLOWER, new Metadata(200, 0, "CHORUS_FLOWER", "CHORUS_FLOWER",
				BlockTypes.CHORUS_FLOWER.getDefaultState()));

		blocks.put(KCTBlockTypes.PURPUR_BLOCK, new Metadata(201, 0, "PURPUR_BLOCK", "PURPUR_BLOCK",
				BlockTypes.PURPUR_BLOCK.getDefaultState()));

		blocks.put(KCTBlockTypes.PURPUR_PILLAR, new Metadata(202, 0, "PURPUR_PILLAR", "PURPUR_PILLAR",
				BlockTypes.PURPUR_PILLAR.getDefaultState()));

		blocks.put(KCTBlockTypes.PURPUR_STAIRS, new Metadata(203, 0, "PURPUR_STAIRS", "PURPUR_STAIRS",
				BlockTypes.PURPUR_STAIRS.getDefaultState()));

		blocks.put(KCTBlockTypes.PURPUR_DOUBLE_SLAB, new Metadata(204, 0, "PURPUR_DOUBLE_SLAB", "PURPUR_DOUBLE_SLAB",
				BlockTypes.PURPUR_DOUBLE_SLAB.getDefaultState()));

		blocks.put(KCTBlockTypes.PURPUR_SLAB, new Metadata(205, 0, "PURPUR_SLAB", "PURPUR_SLAB",
				BlockTypes.PURPUR_SLAB.getDefaultState()));

		blocks.put(KCTBlockTypes.END_STONE_BRICKS, new Metadata(206, 0, "END_STONE_BRICKS", "END_BRICKS",
				BlockTypes.END_BRICKS.getDefaultState()));

		blocks.put(KCTBlockTypes.BEETROOT_BLOCK, new Metadata(207, 0, "BEETROOT_BLOCK", "BEETROOTS",
				BlockTypes.BEETROOTS.getDefaultState()));

		blocks.put(KCTBlockTypes.GRASS_PATH, new Metadata(208, 0, "GRASS_PATH", "GRASS_PATH",
				BlockTypes.GRASS_PATH.getDefaultState()));

		blocks.put(KCTBlockTypes.END_GATEWAY, new Metadata(209, 0, "END_GATEWAY", "END_GATEWAY",
				BlockTypes.END_GATEWAY.getDefaultState()));

		blocks.put(KCTBlockTypes.REPEATING_COMMAND_BLOCK, new Metadata(210, 0, "REPEATING_COMMAND_BLOCK", "REPEATING_COMMAND_BLOCK",
				BlockTypes.REPEATING_COMMAND_BLOCK.getDefaultState()));

		blocks.put(KCTBlockTypes.CHAIN_COMMAND_BLOCK, new Metadata(211, 0, "CHAIN_COMMAND_BLOCK", "CHAIN_COMMAND_BLOCK",
				BlockTypes.CHAIN_COMMAND_BLOCK.getDefaultState()));

		blocks.put(KCTBlockTypes.FROSTED_ICE, new Metadata(212, 0, "FROSTED_ICE", "FROSTED_ICE",
				BlockTypes.FROSTED_ICE.getDefaultState()));

		blocks.put(KCTBlockTypes.STRUCTURE_BLOCK, new Metadata(255, 0, "STRUCTURE_BLOCK", "STRUCTURE_BLOCK",
				BlockTypes.STRUCTURE_BLOCK.getDefaultState())); */
	}
}
