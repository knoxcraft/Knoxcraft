/**
*  Blocks for Knoxcraft Turtles.
*/
var helpURL = 'https://sites.google.com/a/knox.edu/knoxcraft/student-guide-blockly#help';

/**
*  Initialize the turtle.  
*  Edit at https://blockly-demo.appspot.com/static/demos/blockfactory/index.html#z9w74y
*/
Blockly.Blocks['turtle_init'] = {
  init: function() {
    this.appendDummyInput()
        .appendField("Create a turtle named")
        .appendField(new Blockly.FieldTextInput("Turtle"), "name")
        .appendField("that does this:");
    this.appendStatementInput("script");
    this.setColour(260);
    this.setTooltip('Create a turtle and define its script.');
    this.setHelpUrl(helpURL);
  }
};

/** 
*  Move the turtle forward, back, left, right, up, down.  
*  Edit at https://blockly-demo.appspot.com/static/demos/blockfactory/index.html#fnnmk8
*/
Blockly.Blocks['turtle_move'] = {
  init: function() {
    this.appendDummyInput()
        .appendField("move")
        .appendField(new Blockly.FieldTextInput("1"), "dist")
        .appendField("unit(s)");
    this.appendDummyInput()
        .appendField(new Blockly.FieldDropdown([["forward", "FORWARD"], ["backward", "BACKWARD"], ["left", "LEFT"], ["right", "RIGHT"], ["up", "UP"], ["down", "DOWN"]]), "dir");
    this.setInputsInline(true);
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setColour(260);
    this.setTooltip('Move the turtle forward, backward, left, right, up, or down.');
    this.setHelpUrl(helpURL);
  }
};

/**
*  Turn the turtle left or right.
*  Edit at https://blockly-demo.appspot.com/static/demos/blockfactory/index.html#mabktu
*/
Blockly.Blocks['turtle_turn'] = {
  init: function() {
    this.appendDummyInput()
        .appendField("turn")
        .appendField(new Blockly.FieldTextInput("0"), "deg")
        .appendField("degree(s)");
    this.appendDummyInput()
        .appendField(new Blockly.FieldDropdown([["left", "LEFT"], ["right", "RIGHT"]]), "turnDir");
    this.setInputsInline(true);
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setColour(260);
    this.setTooltip('Turn the turtle left or right.  Note that Knoxcraft rounds to multiples of 45 degrees.');
    this.setHelpUrl(helpURL);
  }
};

/**
*  Turn block placement on/off.
*  Edit at https://blockly-demo.appspot.com/static/demos/blockfactory/index.html#av7h89
*/
Blockly.Blocks['turtle_setblockplace'] = {
  init: function() {
    this.appendDummyInput()
        .appendField("turn block placement")
        .appendField(new Blockly.FieldDropdown([["on", "TRUE"], ["off", "FALSE"]]), "mode");
    this.setInputsInline(true);
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setColour(260);
    this.setTooltip('Turn block placement mode on and off.');
    this.setHelpUrl(helpURL);
  }
};

/**
*  Set block type by name/id.
*  Edit at https://blockly-demo.appspot.com/static/demos/blockfactory/index.html#xhvx9n
*/
Blockly.Blocks['turtle_setblocktype'] = {
  init: function() {
    this.appendDummyInput()
        .appendField("set block type to")
        .appendField(new Blockly.FieldTextInput("name or id number"), "blockType");
    this.setInputsInline(true);
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setColour(260);
    this.setTooltip('If you know the name or id number of the Minecraft block type you want, use this block.');
    this.setHelpUrl(helpURL);
  }
};

/**
*  Set block type with drop down.
*/
function blockOptions() {
    
    var options = [];
    
    //types copied from Python version (pykc.py)
    //there are some duplicates in here (same id number, different name) that we may want to remove...
    options.push(['air', '"AIR"']);
    options.push(['stone', '"STONE"']);
    options.push(['granite', '"GRANITE"']);
    options.push(['polished_granite', '"POLISHED_GRANITE"']);
    options.push(['diorite', '"DIORITE"']);
    options.push(['polished_diorite', '"POLISHED_DIORITE"']);
    options.push(['andesite', '"ANDESITE"']);
    options.push(['polished_andesite', '"POLISHED_ANDESITE"']);
    options.push(['grass', '"GRASS"']);
    options.push(['dirt', '"DIRT"']);
    options.push(['coarse_dirt', '"COARSE_DIRT"']);
    options.push(['podzol', '"PODZOL"']);
    options.push(['cobblestone', '"COBBLESTONE"']);
    options.push(['oak_wood_plank', '"OAK_WOOD_PLANK"']);
    options.push(['spruce_wood_plank', '"SPRUCE_WOOD_PLANK"']);
    options.push(['birch_wood_plank', '"BIRCH_WOOD_PLANK"']);
    options.push(['jungle_wood_plank', '"JUNGLE_WOOD_PLANK"']);
    options.push(['acacia_wood_plank', '"ACACIA_WOOD_PLANK"']);
    options.push(['dark_oak_wood_plank', '"DARK_OAK_WOOD_PLANK"']);
    options.push(['oak_sapling', '"OAK_SAPLING"']);
    options.push(['spruce_sapling', '"SPRUCE_SAPLING"']);
    options.push(['birch_sapling', '"BIRCH_SAPLING"']);
    options.push(['jungle_sapling', '"JUNGLE_SAPLING"']);
    options.push(['acacia_sapling', '"ACACIA_SAPLING"']);
    options.push(['dark_oak_sapling', '"DARK_OAK_SAPLING"']);
    options.push(['bedrock', '"BEDROCK"']);
    options.push(['flowing_water', '"FLOWING_WATER"']);
    options.push(['still_water', '"STILL_WATER"']);
    options.push(['flowing_lava', '"FLOWING_LAVA"']);
    options.push(['still_lava', '"STILL_LAVA"']);
    options.push(['sand', '"SAND"']);
    options.push(['red_sand', '"RED_SAND"']);
    options.push(['gravel', '"GRAVEL"']);
    options.push(['gold_ore', '"GOLD_ORE"']);
    options.push(['iron_ore', '"IRON_ORE"']);
    options.push(['coal_ore', '"COAL_ORE"']);
    options.push(['oak_wood', '"OAK_WOOD"']);
    options.push(['spruce_wood', '"SPRUCE_WOOD"']);
    options.push(['birch_wood', '"BIRCH_WOOD"']);
    options.push(['jungle_wood', '"JUNGLE_WOOD"']);
    options.push(['oak_leaves', '"OAK_LEAVES"']);
    options.push(['spruce_leaves', '"SPRUCE_LEAVES"']);
    options.push(['birch_leaves', '"BIRCH_LEAVES"']);
    options.push(['jungle_leaves', '"JUNGLE_LEAVES"']);
    options.push(['sponge', '"SPONGE"']);
    options.push(['wet_sponge', '"WET_SPONGE"']);
    options.push(['glass', '"GLASS"']);
    options.push(['lapis_lazuli_ore', '"LAPIS_LAZULI_ORE"']);
    options.push(['lapis_lazuli_block', '"LAPIS_LAZULI_BLOCK"']);
    options.push(['dispenser', '"DISPENSER"']);
    options.push(['sandstone', '"SANDSTONE"']);
    options.push(['chiseled_sandstone', '"CHISELED_SANDSTONE"']);
    options.push(['smooth_sandstone', '"SMOOTH_SANDSTONE"']);
    options.push(['note_block', '"NOTE_BLOCK"']);
    options.push(['bed', '"BED"']);
    options.push(['powered_rail', '"POWERED_RAIL"']);
    options.push(['detector_rail', '"DETECTOR_RAIL"']);
    options.push(['sticky_piston', '"STICKY_PISTON"']);
    options.push(['cobweb', '"COBWEB"']);
    options.push(['dead_shrub', '"DEAD_SHRUB"']);
    options.push(['grass_tallgrass', '"GRASS_TALLGRASS"']);
    options.push(['fern', '"FERN"']);
    options.push(['dead_bush', '"DEAD_BUSH"']);
    options.push(['piston', '"PISTON"']);
    options.push(['piston_head', '"PISTON_HEAD"']);
    options.push(['white_wool', '"WHITE_WOOL"']);
    options.push(['orange_wool', '"ORANGE_WOOL"']);
    options.push(['magenta_wool', '"MAGENTA_WOOL"']);
    options.push(['light_blue_wool', '"LIGHT_BLUE_WOOL"']);
    options.push(['yellow_wool', '"YELLOW_WOOL"']);
    options.push(['lime_wool', '"LIME_WOOL"']);
    options.push(['pink_wool', '"PINK_WOOL"']);
    options.push(['gray_wool', '"GRAY_WOOL"']);
    options.push(['light_gray_wool', '"LIGHT_GRAY_WOOL"']);
    options.push(['cyan_wool', '"CYAN_WOOL"']);
    options.push(['purple_wool', '"PURPLE_WOOL"']);
    options.push(['blue_wool', '"BLUE_WOOL"']);
    options.push(['brown_wool', '"BROWN_WOOL"']);
    options.push(['green_wool', '"GREEN_WOOL"']);
    options.push(['red_wool', '"RED_WOOL"']);
    options.push(['black_wool', '"BLACK_WOOL"']);
    options.push(['dandelion', '"DANDELION"']);
    options.push(['poppy', '"POPPY"']);
    options.push(['blue_orchid', '"BLUE_ORCHID"']);
    options.push(['allium', '"ALLIUM"']);
    options.push(['azure_bluet', '"AZURE_BLUET"']);
    options.push(['red_tulip', '"RED_TULIP"']);
    options.push(['orange_tulip', '"ORANGE_TULIP"']);
    options.push(['white_tulip', '"WHITE_TULIP"']);
    options.push(['pink_tulip', '"PINK_TULIP"']);
    options.push(['oxeye_daisy', '"OXEYE_DAISY"']);
    options.push(['brown_mushroom', '"BROWN_MUSHROOM"']);
    options.push(['red_mushroom', '"RED_MUSHROOM"']);
    options.push(['gold_block', '"GOLD_BLOCK"']);
    options.push(['iron_block', '"IRON_BLOCK"']);
    options.push(['double_stone_slab', '"DOUBLE_STONE_SLAB"']);
    options.push(['double_sandstone_slab', '"DOUBLE_SANDSTONE_SLAB"']);
    options.push(['double_wooden_slab', '"DOUBLE_WOODEN_SLAB"']);
    options.push(['double_cobblestone_slab', '"DOUBLE_COBBLESTONE_SLAB"']);
    options.push(['double_brick_slab', '"DOUBLE_BRICK_SLAB"']);
    options.push(['double_stone_brick_slab', '"DOUBLE_STONE_BRICK_SLAB"']);
    options.push(['double_nether_brick_slab', '"DOUBLE_NETHER_BRICK_SLAB"']);
    options.push(['double_quartz_slab', '"DOUBLE_QUARTZ_SLAB"']);
    options.push(['stone_slab', '"STONE_SLAB"']);
    options.push(['sandstone_slab', '"SANDSTONE_SLAB"']);
    options.push(['wooden_slab', '"WOODEN_SLAB"']);
    options.push(['cobblestone_slab', '"COBBLESTONE_SLAB"']);
    options.push(['brick_slab', '"BRICK_SLAB"']);
    options.push(['stone_brick_slab', '"STONE_BRICK_SLAB"']);
    options.push(['nether_brick_slab', '"NETHER_BRICK_SLAB"']);
    options.push(['quartz_slab', '"QUARTZ_SLAB"']);
    options.push(['bricks', '"BRICKS"']);
    options.push(['tnt', '"TNT"']);
    options.push(['bookshelf', '"BOOKSHELF"']);
    options.push(['moss_stone', '"MOSS_STONE"']);
    options.push(['obsidian', '"OBSIDIAN"']);
    options.push(['torch', '"TORCH"']);
    options.push(['fire', '"FIRE"']);
    options.push(['monster_spawner', '"MONSTER_SPAWNER"']);
    options.push(['oak_wood_stairs', '"OAK_WOOD_STAIRS"']);
    options.push(['chest', '"CHEST"']);
    options.push(['redstone_wire', '"REDSTONE_WIRE"']);
    options.push(['diamond_ore', '"DIAMOND_ORE"']);
    options.push(['diamond_block', '"DIAMOND_BLOCK"']);
    options.push(['crafting_table', '"CRAFTING_TABLE"']);
    options.push(['wheat_crops', '"WHEAT_CROPS"']);
    options.push(['farmland', '"FARMLAND"']);
    options.push(['furnace', '"FURNACE"']);
    options.push(['burning_furnace', '"BURNING_FURNACE"']);
    options.push(['standing_sign_block', '"STANDING_SIGN_BLOCK"']);
    options.push(['oak_door_block', '"OAK_DOOR_BLOCK"']);
    options.push(['ladder', '"LADDER"']);
    options.push(['rail', '"RAIL"']);
    options.push(['cobblestone_stairs', '"COBBLESTONE_STAIRS"']);
    options.push(['wall_mounted_sign_block', '"WALL_MOUNTED_SIGN_BLOCK"']);
    options.push(['lever', '"LEVER"']);
    options.push(['stone_pressure_plate', '"STONE_PRESSURE_PLATE"']);
    options.push(['iron_door_block', '"IRON_DOOR_BLOCK"']);
    options.push(['wooden_pressure_plate', '"WOODEN_PRESSURE_PLATE"']);
    options.push(['redstone_ore', '"REDSTONE_ORE"']);
    options.push(['glowing_redstone_ore', '"GLOWING_REDSTONE_ORE"']);
    options.push(['redstone_torch_off', '"REDSTONE_TORCH_OFF"']);
    options.push(['redstone_torch_on', '"REDSTONE_TORCH_ON"']);
    options.push(['stone_button', '"STONE_BUTTON"']);
    options.push(['snow', '"SNOW"']);
    options.push(['ice', '"ICE"']);
    options.push(['snow_block', '"SNOW_BLOCK"']);
    options.push(['cactus', '"CACTUS"']);
    options.push(['clay', '"CLAY"']);
    options.push(['sugar_canes', '"SUGAR_CANES"']);
    options.push(['jukebox', '"JUKEBOX"']);
    options.push(['oak_fence', '"OAK_FENCE"']);
    options.push(['pumpkin', '"PUMPKIN"']);
    options.push(['netherrack', '"NETHERRACK"']);
    options.push(['soul_sand', '"SOUL_SAND"']);
    options.push(['glowstone', '"GLOWSTONE"']);
    options.push(['nether_portal', '"NETHER_PORTAL"']);
    options.push(['jack_olantern', '"JACK_OLANTERN"']);
    options.push(['cake_block', '"CAKE_BLOCK"']);
    options.push(['redstone_repeater_block_off', '"REDSTONE_REPEATER_BLOCK_OFF"']);
    options.push(['redstone_repeater_block_on', '"REDSTONE_REPEATER_BLOCK_ON"']);
    options.push(['white_stained_glass', '"WHITE_STAINED_GLASS"']);
    options.push(['orange_stained_glass', '"ORANGE_STAINED_GLASS"']);
    options.push(['magenta_stained_glass', '"MAGENTA_STAINED_GLASS"']);
    options.push(['light_blue_stained_glass', '"LIGHT_BLUE_STAINED_GLASS"']);
    options.push(['yellow_stained_glass', '"YELLOW_STAINED_GLASS"']);
    options.push(['lime_stained_glass', '"LIME_STAINED_GLASS"']);
    options.push(['pink_stained_glass', '"PINK_STAINED_GLASS"']);
    options.push(['gray_stained_glass', '"GRAY_STAINED_GLASS"']);
    options.push(['light_gray_stained_glass', '"LIGHT_GRAY_STAINED_GLASS"']);
    options.push(['cyan_stained_glass', '"CYAN_STAINED_GLASS"']);
    options.push(['purple_stained_glass', '"PURPLE_STAINED_GLASS"']);
    options.push(['blue_stained_glass', '"BLUE_STAINED_GLASS"']);
    options.push(['brown_stained_glass', '"BROWN_STAINED_GLASS"']);
    options.push(['green_stained_glass', '"GREEN_STAINED_GLASS"']);
    options.push(['red_stained_glass', '"RED_STAINED_GLASS"']);
    options.push(['black_stained_glass', '"BLACK_STAINED_GLASS"']);
    options.push(['wooden_trapdoor', '"WOODEN_TRAPDOOR"']);
    options.push(['stone_monster_egg', '"STONE_MONSTER_EGG"']);
    options.push(['cobblestone_monster_egg', '"COBBLESTONE_MONSTER_EGG"']);
    options.push(['stone_brick_monster_egg', '"STONE_BRICK_MONSTER_EGG"']);
    options.push(['mossy_stone_brick_monster_egg', '"MOSSY_STONE_BRICK_MONSTER_EGG"']);
    options.push(['cracked_stone_brick_monster_egg', '"CRACKED_STONE_BRICK_MONSTER_EGG"']);
    options.push(['chiseled_stone_brick_monster_egg', '"CHISELED_STONE_BRICK_MONSTER_EGG"']);
    options.push(['stone_bricks', '"STONE_BRICKS"']);
    options.push(['mossy_stone_bricks', '"MOSSY_STONE_BRICKS"']);
    options.push(['cracked_stone_bricks', '"CRACKED_STONE_BRICKS"']);
    options.push(['chiseled_stone_bricks', '"CHISELED_STONE_BRICKS"']);
    options.push(['brown_mushroom_block', '"BROWN_MUSHROOM_BLOCK"']);
    options.push(['red_mushroom_block', '"RED_MUSHROOM_BLOCK"']);
    options.push(['iron_bars', '"IRON_BARS"']);
    options.push(['glass_pane', '"GLASS_PANE"']);
    options.push(['melon_block', '"MELON_BLOCK"']);
    options.push(['pumpkin_stem', '"PUMPKIN_STEM"']);
    options.push(['melon_stem', '"MELON_STEM"']);
    options.push(['vines', '"VINES"']);
    options.push(['oak_fence_gate', '"OAK_FENCE_GATE"']);
    options.push(['brick_stairs', '"BRICK_STAIRS"']);
    options.push(['stone_brick_stairs', '"STONE_BRICK_STAIRS"']);
    options.push(['mycelium', '"MYCELIUM"']);
    options.push(['lily_pad', '"LILY_PAD"']);
    options.push(['nether_brick', '"NETHER_BRICK"']);
    options.push(['nether_brick_fence', '"NETHER_BRICK_FENCE"']);
    options.push(['nether_brick_stairs', '"NETHER_BRICK_STAIRS"']);
    options.push(['nether_wart', '"NETHER_WART"']);
    options.push(['enchantment_table', '"ENCHANTMENT_TABLE"']);
    options.push(['brewing_stand', '"BREWING_STAND"']);
    options.push(['cauldron', '"CAULDRON"']);
    options.push(['end_portal', '"END_PORTAL"']);
    options.push(['end_portal_frame', '"END_PORTAL_FRAME"']);
    options.push(['end_stone', '"END_STONE"']);
    options.push(['dragon_egg', '"DRAGON_EGG"']);
    options.push(['redstone_lamp_inactive', '"REDSTONE_LAMP_INACTIVE"']);
    options.push(['redstone_lamp_active', '"REDSTONE_LAMP_ACTIVE"']);
    options.push(['double_oak_wood_slab', '"DOUBLE_OAK_WOOD_SLAB"']);
    options.push(['double_spruce_wood_slab', '"DOUBLE_SPRUCE_WOOD_SLAB"']);
    options.push(['double_birch_wood_slab', '"DOUBLE_BIRCH_WOOD_SLAB"']);
    options.push(['double_jungle_wood_slab', '"DOUBLE_JUNGLE_WOOD_SLAB"']);
    options.push(['double_acacia_wood_slab', '"DOUBLE_ACACIA_WOOD_SLAB"']);
    options.push(['double_dark_oak_wood_slab', '"DOUBLE_DARK_OAK_WOOD_SLAB"']);
    options.push(['oak_wood_slab', '"OAK_WOOD_SLAB"']);
    options.push(['spruce_wood_slab', '"SPRUCE_WOOD_SLAB"']);
    options.push(['birch_wood_slab', '"BIRCH_WOOD_SLAB"']);
    options.push(['jungle_wood_slab', '"JUNGLE_WOOD_SLAB"']);
    options.push(['acacia_wood_slab', '"ACACIA_WOOD_SLAB"']);
    options.push(['dark_oak_wood_slab', '"DARK_OAK_WOOD_SLAB"']);
    options.push(['cocoa', '"COCOA"']);
    options.push(['sandstone_stairs', '"SANDSTONE_STAIRS"']);
    options.push(['emerald_ore', '"EMERALD_ORE"']);
    options.push(['ender_chest', '"ENDER_CHEST"']);
    options.push(['tripwire_hook', '"TRIPWIRE_HOOK"']);
    options.push(['tripwire', '"TRIPWIRE"']);
    options.push(['emerald_block', '"EMERALD_BLOCK"']);
    options.push(['spruce_wood_stairs', '"SPRUCE_WOOD_STAIRS"']);
    options.push(['birch_wood_stairs', '"BIRCH_WOOD_STAIRS"']);
    options.push(['jungle_wood_stairs', '"JUNGLE_WOOD_STAIRS"']);
    options.push(['command_block', '"COMMAND_BLOCK"']);
    options.push(['beacon', '"BEACON"']);
    options.push(['cobblestone_wall', '"COBBLESTONE_WALL"']);
    options.push(['mossy_cobblestone_wall', '"MOSSY_COBBLESTONE_WALL"']);
    options.push(['flower_pot', '"FLOWER_POT"']);
    options.push(['carrots', '"CARROTS"']);
    options.push(['potatoes', '"POTATOES"']);
    options.push(['wooden_button', '"WOODEN_BUTTON"']);
    options.push(['mob_head', '"MOB_HEAD"']);
    options.push(['anvil', '"ANVIL"']);
    options.push(['trapped_chest', '"TRAPPED_CHEST"']);
    options.push(['weighted_pressure_plate_light', '"WEIGHTED_PRESSURE_PLATE_LIGHT"']);
    options.push(['weighted_pressure_plate_heavy', '"WEIGHTED_PRESSURE_PLATE_HEAVY"']);
    options.push(['redstone_comparator_inactive', '"REDSTONE_COMPARATOR_INACTIVE"']);
    options.push(['redstone_comparator_active', '"REDSTONE_COMPARATOR_ACTIVE"']);
    options.push(['daylight_sensor', '"DAYLIGHT_SENSOR"']);
    options.push(['redstone_block', '"REDSTONE_BLOCK"']);
    options.push(['nether_quartz_ore', '"NETHER_QUARTZ_ORE"']);
    options.push(['hopper', '"HOPPER"']);
    options.push(['quartz_block', '"QUARTZ_BLOCK"']);
    options.push(['chiseled_quartz_block', '"CHISELED_QUARTZ_BLOCK"']);
    options.push(['pillar_quartz_block', '"PILLAR_QUARTZ_BLOCK"']);
    options.push(['quartz_stairs', '"QUARTZ_STAIRS"']);
    options.push(['activator_rail', '"ACTIVATOR_RAIL"']);
    options.push(['dropper', '"DROPPER"']);
    options.push(['white_stained_clay', '"WHITE_STAINED_CLAY"']);
    options.push(['orange_stained_clay', '"ORANGE_STAINED_CLAY"']);
    options.push(['magenta_stained_clay', '"MAGENTA_STAINED_CLAY"']);
    options.push(['light_blue_stained_clay', '"LIGHT_BLUE_STAINED_CLAY"']);
    options.push(['yellow_stained_clay', '"YELLOW_STAINED_CLAY"']);
    options.push(['lime_stained_clay', '"LIME_STAINED_CLAY"']);
    options.push(['pink_stained_clay', '"PINK_STAINED_CLAY"']);
    options.push(['gray_stained_clay', '"GRAY_STAINED_CLAY"']);
    options.push(['light_gray_stained_clay', '"LIGHT_GRAY_STAINED_CLAY"']);
    options.push(['cyan_stained_clay', '"CYAN_STAINED_CLAY"']);
    options.push(['purple_stained_clay', '"PURPLE_STAINED_CLAY"']);
    options.push(['blue_stained_clay', '"BLUE_STAINED_CLAY"']);
    options.push(['brown_stained_clay', '"BROWN_STAINED_CLAY"']);
    options.push(['green_stained_clay', '"GREEN_STAINED_CLAY"']);
    options.push(['red_stained_clay', '"RED_STAINED_CLAY"']);
    options.push(['black_stained_clay', '"BLACK_STAINED_CLAY"']);
    options.push(['white_stained_glass_pane', '"WHITE_STAINED_GLASS_PANE"']);
    options.push(['orange_stained_glass_pane', '"ORANGE_STAINED_GLASS_PANE"']);
    options.push(['magenta_stained_glass_pane', '"MAGENTA_STAINED_GLASS_PANE"']);
    options.push(['light_blue_stained_glass_pane', '"LIGHT_BLUE_STAINED_GLASS_PANE"']);
    options.push(['yellow_stained_glass_pane', '"YELLOW_STAINED_GLASS_PANE"']);
    options.push(['lime_stained_glass_pane', '"LIME_STAINED_GLASS_PANE"']);
    options.push(['pink_stained_glass_pane', '"PINK_STAINED_GLASS_PANE"']);
    options.push(['gray_stained_glass_pane', '"GRAY_STAINED_GLASS_PANE"']);
    options.push(['light_gray_stained_glass_pane', '"LIGHT_GRAY_STAINED_GLASS_PANE"']);
    options.push(['cyan_stained_glass_pane', '"CYAN_STAINED_GLASS_PANE"']);
    options.push(['purple_stained_glass_pane', '"PURPLE_STAINED_GLASS_PANE"']);
    options.push(['blue_stained_glass_pane', '"BLUE_STAINED_GLASS_PANE"']);
    options.push(['brown_stained_glass_pane', '"BROWN_STAINED_GLASS_PANE"']);
    options.push(['green_stained_glass_pane', '"GREEN_STAINED_GLASS_PANE"']);
    options.push(['red_stained_glass_pane', '"RED_STAINED_GLASS_PANE"']);
    options.push(['black_stained_glass_pane', '"BLACK_STAINED_GLASS_PANE"']);
    options.push(['acacia_leaves', '"ACACIA_LEAVES"']);
    options.push(['dark_oak_leaves', '"DARK_OAK_LEAVES"']);
    options.push(['acacia_wood', '"ACACIA_WOOD"']);
    options.push(['dark_oak_wood', '"DARK_OAK_WOOD"']);
    options.push(['acacia_wood_stairs', '"ACACIA_WOOD_STAIRS"']);
    options.push(['dark_oak_wood_stairs', '"DARK_OAK_WOOD_STAIRS"']);
    options.push(['slime_block', '"SLIME_BLOCK"']);
    options.push(['barrier', '"BARRIER"']);
    options.push(['iron_trapdoor', '"IRON_TRAPDOOR"']);
    options.push(['prismarine', '"PRISMARINE"']);
    options.push(['prismarine_bricks', '"PRISMARINE_BRICKS"']);
    options.push(['dark_prismarine', '"DARK_PRISMARINE"']);
    options.push(['sea_lantern', '"SEA_LANTERN"']);
    options.push(['hay_bale', '"HAY_BALE"']);
    options.push(['white_carpet', '"WHITE_CARPET"']);
    options.push(['orange_carpet', '"ORANGE_CARPET"']);
    options.push(['magenta_carpet', '"MAGENTA_CARPET"']);
    options.push(['light_blue_carpet', '"LIGHT_BLUE_CARPET"']);
    options.push(['yellow_carpet', '"YELLOW_CARPET"']);
    options.push(['lime_carpet', '"LIME_CARPET"']);
    options.push(['pink_carpet', '"PINK_CARPET"']);
    options.push(['gray_carpet', '"GRAY_CARPET"']);
    options.push(['light_gray_carpet', '"LIGHT_GRAY_CARPET"']);
    options.push(['cyan_carpet', '"CYAN_CARPET"']);
    options.push(['purple_carpet', '"PURPLE_CARPET"']);
    options.push(['blue_carpet', '"BLUE_CARPET"']);
    options.push(['brown_carpet', '"BROWN_CARPET"']);
    options.push(['green_carpet', '"GREEN_CARPET"']);
    options.push(['red_carpet', '"RED_CARPET"']);
    options.push(['black_carpet', '"BLACK_CARPET"']);
    options.push(['hardened_clay', '"HARDENED_CLAY"']);
    options.push(['block_of_coal', '"BLOCK_OF_COAL"']);
    options.push(['packed_ice', '"PACKED_ICE"']);
    options.push(['sunflower', '"SUNFLOWER"']);
    options.push(['lilac', '"LILAC"']);
    options.push(['double_tallgrass', '"DOUBLE_TALLGRASS"']);
    options.push(['large_fern', '"LARGE_FERN"']);
    options.push(['rose_bush', '"ROSE_BUSH"']);
    options.push(['peony', '"PEONY"']);
    options.push(['free_standing_banner', '"FREE_STANDING_BANNER"']);
    options.push(['wall_mounted_banner', '"WALL_MOUNTED_BANNER"']);
    options.push(['inverted_daylight_sensor', '"INVERTED_DAYLIGHT_SENSOR"']);
    options.push(['red_sandstone', '"RED_SANDSTONE"']);
    options.push(['chiseled_red_sandstone', '"CHISELED_RED_SANDSTONE"']);
    options.push(['smooth_red_sandstone', '"SMOOTH_RED_SANDSTONE"']);
    options.push(['red_sandstone_stairs', '"RED_SANDSTONE_STAIRS"']);
    options.push(['double_red_sandstone_slab', '"DOUBLE_RED_SANDSTONE_SLAB"']);
    options.push(['red_sandstone_slab', '"RED_SANDSTONE_SLAB"']);
    options.push(['spruce_fence_gate', '"SPRUCE_FENCE_GATE"']);
    options.push(['birch_fence_gate', '"BIRCH_FENCE_GATE"']);
    options.push(['jungle_fence_gate', '"JUNGLE_FENCE_GATE"']);
    options.push(['dark_oak_fence_gate', '"DARK_OAK_FENCE_GATE"']);
    options.push(['acacia_fence_gate', '"ACACIA_FENCE_GATE"']);
    options.push(['spruce_fence', '"SPRUCE_FENCE"']);
    options.push(['birch_fence', '"BIRCH_FENCE"']);
    options.push(['jungle_fence', '"JUNGLE_FENCE"']);
    options.push(['dark_oak_fence', '"DARK_OAK_FENCE"']);
    options.push(['acacia_fence', '"ACACIA_FENCE"']);
    options.push(['spruce_door_block', '"SPRUCE_DOOR_BLOCK"']);
    options.push(['birch_door_block', '"BIRCH_DOOR_BLOCK"']);
    options.push(['jungle_door_block', '"JUNGLE_DOOR_BLOCK"']);
    options.push(['acacia_door_block', '"ACACIA_DOOR_BLOCK"']);
    options.push(['dark_oak_door_block', '"DARK_OAK_DOOR_BLOCK"']);
    options.push(['end_rod', '"END_ROD"']);
    options.push(['chorus_plant', '"CHORUS_PLANT"']);
    options.push(['chorus_flower', '"CHORUS_FLOWER"']);
    options.push(['purpur_block', '"PURPUR_BLOCK"']);
    options.push(['purpur_pillar', '"PURPUR_PILLAR"']);
    options.push(['purpur_stairs', '"PURPUR_STAIRS"']);
    options.push(['purpur_double_slab', '"PURPUR_DOUBLE_SLAB"']);
    options.push(['purpur_slab', '"PURPUR_SLAB"']);
    options.push(['end_stone_bricks', '"END_STONE_BRICKS"']);
    options.push(['beetroot_block', '"BEETROOT_BLOCK"']);
    options.push(['grass_path', '"GRASS_PATH"']);
    options.push(['end_gateway', '"END_GATEWAY"']);
    options.push(['repeating_command_block', '"REPEATING_COMMAND_BLOCK"']);
    options.push(['chain_command_block', '"CHAIN_COMMAND_BLOCK"']);
    options.push(['frosted_ice', '"FROSTED_ICE"']);
    options.push(['structure_block', '"STRUCTURE_BLOCK"']);
    return options;
}
Blockly.Blocks['turtle_setblocktype2'] = {
  init: function() {
    var dropdown = new Blockly.FieldDropdown(blockOptions);
    this.appendDummyInput()
        .appendField("set block type to")
        .appendField(dropdown, 'blockType');
    this.setInputsInline(true);
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setColour(260);
    this.setTooltip('Choose a Minecraft block type from the list.');
    this.setHelpUrl(helpURL);
  }
};

/**
*  Set turtle relative position.
*  Edit at https://blockly-demo.appspot.com/static/demos/blockfactory/index.html#yr2tpn
*/
Blockly.Blocks['turtle_setpos'] = {
  init: function() {
    this.appendDummyInput()
        .appendField("set position to")
        .appendField(new Blockly.FieldTextInput("x"), "X")
        .appendField(",")
        .appendField(new Blockly.FieldTextInput("y"), "Y")
        .appendField(",")
        .appendField(new Blockly.FieldTextInput("z"), "Z");
    this.setInputsInline(true);
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setColour(260);
    this.setTooltip('Set the turtle\'s position relative to its initial position.');
    this.setHelpUrl(helpURL);
  }
};

/**
*  Set turtle direction.
*  Edit at https://blockly-demo.appspot.com/static/demos/blockfactory/index.html#scshk8
*/
Blockly.Blocks['turtle_setdir'] = {
  init: function() {
    this.appendDummyInput()
        .appendField("set direction to")
        .appendField(new Blockly.FieldDropdown([["north", "NORTH"], ["northeast", "NORTHEAST"], ["east", "EAST"], ["southeast", "SOUTHEAST"], ["south", "SOUTH"], ["southwest", "SOUTHWEST"], ["west", "WEST"], ["northwest", "NORTHWEST"]]), "dir");
    this.setInputsInline(true);
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setColour(260);
    this.setTooltip('Set the turtle\'s heading.');
    this.setHelpUrl(helpURL);
  }
};
