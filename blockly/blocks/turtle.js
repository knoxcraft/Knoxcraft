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
        .appendField(new Blockly.FieldTextInput("name or id number"), "type");
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
	  options.push(['Air', '0']);
	  options.push(['Stone', '1']);
	  options.push(['Granite','1:1']);
	  options.push(['PolishedGranite','1:2']);
	  options.push(['Diorite','1:3']);
	  options.push(['PolishedDiorite','1:4']);
	  options.push(['Andesite','1:5']);
	  options.push(['PolishedAndesite','1:6']);
	  options.push(['Grass','2']);
	  options.push(['Dirt','3']);
	  options.push(['CoarseDirt','3:1']);
	  options.push(['Podzol','3:2']);
	  options.push(['Cobble','4']);
	  options.push(['OakPlanks','5']);
	  options.push(['SprucePlanks','5:1']);
	  options.push(['BirchPlanks','5:2']);
	  options.push(['JunglePlanks','5:3']);
	  options.push(['AcaciaPlanks','5:4']);
	  options.push(['DarkOakPlanks','5:5']);
	  options.push(['OakWood','5']);
	  options.push(['SpruceWood','5:1']);
	  options.push(['BirchWood','5:2']);
	  options.push(['JungleWood','5:3']);
	  options.push(['AcaciaWood','5:4']);
	  options.push(['DarkOakWood','5:5']);
	  options.push(['OakSapling','6']);
	  options.push(['SpruceSapling','6:1']);
	  options.push(['BirchSapling','6:2']);
	  options.push(['JungleSapling','6:3']);
	  options.push(['AcaciaSapling','6:4']);
	  options.push(['DarkOakSapling','6:5']);
	  options.push(['Bedrock','7']);
	  options.push(['WaterFlowing','8']);
	  options.push(['Water','9']);
	  options.push(['LavaFlowing','10']);
	  options.push(['Lava','11']);
	  options.push(['Sand','12']);
	  options.push(['RedSand','12:1']);
	  options.push(['Gravel','13']);
	  options.push(['GoldOre','14']);
	  options.push(['IronOre','15']);
	  options.push(['CoalOre','16']);
	  options.push(['OakLog','17']);
	  options.push(['SpruceLog','17:1']);
	  options.push(['PineLog','17:1']);
	  options.push(['BirchLog','17:2']);
	  options.push(['JungleLog','17:3']);
	  options.push(['OakLeaves','18']);
	  options.push(['SpruceLeaves','18:1']);
	  options.push(['PineLeaves','18:1']);
	  options.push(['BirchLeaves','18:2']);
	  options.push(['JungleLeaves','18:3']);
	  options.push(['Sponge','19']);
	  options.push(['Glass','20']);
	  options.push(['LapisOre','21']);
	  options.push(['LapislazuliOre','21']);
	  options.push(['LapisBlock','22']);
	  options.push(['Dispenser','23']);
	  options.push(['Sandstone','24']);
	  options.push(['SandstoneChiseled','24:1']);
	  options.push(['SandstoneOrnate','24:1']);
	  options.push(['SandstoneSmooth','24:2']);
	  options.push(['SandstoneBlank','24:2']);
	  options.push(['NoteBlock','25']);
	  options.push(['Bed','26']);
	  options.push(['BedBlock','26']);
	  options.push(['PoweredRail','27']);
	  options.push(['DetectorRail','28']);
	  options.push(['StickyPiston','29']);
	  options.push(['Web','30']);
	  options.push(['SpiderWeb','30']);
	  options.push(['Shrub','31']);
	  options.push(['TallGrass','31:1']);
	  options.push(['Fern','31:2']);
	  options.push(['DeadBush','32']);
	  options.push(['Piston','33']);
	  options.push(['PistonHead','34']);
	  options.push(['WhiteWool','35']);
	  options.push(['OrangeWool','35:1']);
	  options.push(['MagentaWool','35:2']);
	  options.push(['LightBlueWool','35:3']);
	  options.push(['YellowWool','35:4']);
	  options.push(['LimeWool','35:5']);
	  options.push(['PinkWool','35:6']);
	  options.push(['GrayWool','35:7']);
	  options.push(['LightGrayWool','35:8']);
	  options.push(['CyanWool','35:9']);
	  options.push(['PurpleWool','35:10']);
	  options.push(['BlueWool','35:11']);
	  options.push(['BrownWool','35:12']);
	  options.push(['GreenWool','35:13']);
	  options.push(['RedWool','35:14']);
	  options.push(['BlackWool','35:15']);
	  options.push(['WoolWhite','35']);
	  options.push(['WoolOrange','35:1']);
	  options.push(['WoolMagenta','35:2']);
	  options.push(['WoolLightBlue','35:3']);
	  options.push(['WoolYellow','35:4']);
	  options.push(['WoolLightGreen','35:5']);
	  options.push(['WoolPink','35:6']);
	  options.push(['WoolGray','35:7']);
	  options.push(['WoolLightGray','35:8']);
	  options.push(['WoolCyan','35:9']);
	  options.push(['WoolPurple','35:10']);
	  options.push(['WoolBlue','35:11']);
	  options.push(['WoolBrown','35:12']);
	  options.push(['WoolDarkGreen','35:13']);
	  options.push(['WoolRed','35:14']);
	  options.push(['WoolBlack','35:15']);
	  options.push(['PistonExtended','36']);
	  options.push(['Dandelion','37']);
	  options.push(['Poppy','38']);
	  options.push(['BlueOrchid','38:1']);
	  options.push(['Allium','38:2']);
	  options.push(['AzureBluet','38:3']);
	  options.push(['RedTulip','38:4']);
	  options.push(['OrangeTulip','38:5']);
	  options.push(['WhiteTulip','38:6']);
	  options.push(['PinkTulip','38:7']);
	  options.push(['OxeyeDaisy','38:8']);
	  options.push(['BrownMushroom','39']);
	  options.push(['RedMushroom','40']);
	  options.push(['GoldBlock','41']);
	  options.push(['IronBlock','42']);
	  options.push(['DoubleStoneSlab','43']);
	  options.push(['DoubleSandStoneSlab','43:1']);
	  options.push(['DoubleWoodSlab','43:2']);
	  options.push(['DoubleCobbleSlab','43:3']);
	  options.push(['DoubleBrickSlab','43:4']);
	  options.push(['DoubleStoneBricksSlab','43:5']);
	  options.push(['DoubleNetherBrickSlab','43:6']);
	  options.push(['DoubleQuartzSlab','43:7']);
	  options.push(['DoubleOrnateStoneSlab','43']);
	  options.push(['DoubleSandStoneTrimSlab','43:1']);
	  options.push(['DoubleBrickBlockSlab','43:4']);
	  options.push(['StoneSlab','44']);
	  options.push(['SandStoneSlab','44:1']);
	  options.push(['WoodSlab','44:2']);
	  options.push(['CobbleSlab','44:3']);
	  options.push(['BrickSlab','44:4']);
	  options.push(['StoneBricksSlab','44:5']);
	  options.push(['NetherBricksSlab','44:6']);
	  options.push(['QuartzSlab','44:7']);
	  options.push(['OrnateStoneSlab','44']);
	  options.push(['SandStoneTrimSlab','44:1']);
	  options.push(['BrickBlockSlab','44:4']);
	  options.push(['Bricks','45']);
	  options.push(['BrickBlock','45']);
	  options.push(['TNT','46']);
	  options.push(['Tnt','46']);
	  options.push(['Bookshelf','47']);
	  options.push(['MossyCobble','48']);
	  options.push(['Obsidian','49']);
	  options.push(['Torch','50']);
	  options.push(['FireBlock','51']);
	  options.push(['MobSpawner','52']);
	  options.push(['OakStairs','53']);
	  options.push(['WoodenStair','53']);
	  options.push(['Chest','54']);
	  options.push(['RedstoneWire','55']);
	  options.push(['DiamondOre','56']);
	  options.push(['DiamondBlock','57']);
	  options.push(['Workbench','58']);
	  options.push(['Wheat','59']);
	  options.push(['Crops','59']);
	  options.push(['Farmland','60']);
	  options.push(['Soil','60']);
	  options.push(['Furnace','61']);
	  options.push(['BurningFurnace','62']);
	  options.push(['StandingSign','63']);
	  options.push(['SignPost','63']);
	  options.push(['WoodenDoor','64']);
	  options.push(['OakDoor','64']);
	  options.push(['Ladder','65']);
	  options.push(['Rail','66']);
	  options.push(['StoneStairs','67']);
	  options.push(['CobbleStair','67']);
	  options.push(['WallSign','68']);
	  options.push(['Lever','69']);
	  options.push(['StonePressurePlate','70']);
	  options.push(['StonePlate','70']);
	  options.push(['IronDoor','71']);
	  options.push(['WoodenPressurePlate','72']);
	  options.push(['WoodPlate','72']);
	  options.push(['RedstoneOre','73']);
	  options.push(['GlowingRedstoneOre','74']);
	  options.push(['RedstoneTorchOff','75']);
	  options.push(['RedstoneTorchOn','76']);
	  options.push(['StoneButton','77']);
	  options.push(['Snow','78']);
	  options.push(['Ice','79']);
	  options.push(['SnowBlock','80']);
	  options.push(['Cactus','81']);
	  options.push(['Clay','82']);
	  options.push(['Reed','83']);
	  options.push(['Jukebox','84']);
	  options.push(['Fence','85']);
	  options.push(['Pumpkin','86']);
	  options.push(['Netherrack','87']);
	  options.push(['SoulSand','88']);
	  options.push(['GlowStone','89']);
	  options.push(['Portal','90']);
	  options.push(['JackOLantern','91']);
	  options.push(['Cake','92']);
	  options.push(['RedstoneRepeaterOff','93']);
	  options.push(['RedstoneRepeaterOn','94']);
	  options.push(['WhiteGlass','95']);
	  options.push(['OrangeGlass','95:1']);
	  options.push(['MagentaGlass','95:2']);
	  options.push(['LightBlueGlass','95:3']);
	  options.push(['YellowGlass','95:4']);
	  options.push(['LimeGlass','95:5']);
	  options.push(['PinkGlass','95:6']);
	  options.push(['GrayGlass','95:7']);
	  options.push(['LightGrayGlass','95:8']);
	  options.push(['CyanGlass','95:9']);
	  options.push(['PurpleGlass','95:10']);
	  options.push(['BlueGlass','95:11']);
	  options.push(['BrownGlass','95:12']);
	  options.push(['GreenGlass','95:13']);
	  options.push(['RedGlass','95:14']);
	  options.push(['BlackGlass','95:15']);
	  options.push(['Trapdoor','96']);
	  options.push(['StoneSilverFishBlock','97']);
	  options.push(['CobbleSilverFishBlock','97:1']);
	  options.push(['StoneBrickSilverFishBlock','97:2']);
	  options.push(['MossyBrickSilverFishBlock','97:3']);
	  options.push(['CrackedSilverFishBlock','97:4']);
	  options.push(['ChiseledSilverFishBlock','97:5']);
	  options.push(['OrnateSilverFishBlock','97:5']);
	  options.push(['StoneBrick','98']);
	  options.push(['MossyStoneBrick','98:1']);
	  options.push(['CrackedStoneBrick','98:2']);
	  options.push(['ChiseledStoneBrick','98:3']);
	  options.push(['OrnateStoneBrick','98:3']);
	  options.push(['HugeBrownMushroom','99']);
	  options.push(['HugeRedMushroom','100']);
	  options.push(['IronBars','101']);
	  options.push(['GlassPane','102']);
	  options.push(['Melon','103']);
	  options.push(['PumpkinStem','104']);
	  options.push(['MelonStem','105']);
	  options.push(['Vines','106']);
	  options.push(['FenceGate','107']);
	  options.push(['BrickStairs','108']);
	  options.push(['BrickStair','108']);
	  options.push(['StoneBrickStairs','109']);
	  options.push(['StoneBrickStair','109']);
	  options.push(['Mycelium','110']);
	  options.push(['Lilypad','111']);
	  options.push(['NetherBrick','112']);
	  options.push(['NetherBrickFence','113']);
	  options.push(['NetherBrickStairs','114']);
	  options.push(['NetherBrickStair','114']);
	  options.push(['NetherWart','115']);
	  options.push(['EnchantmentTable','116']);
	  options.push(['BrewingStand','117']);
	  options.push(['Cauldron','118']);
	  options.push(['EndPortal','119']);
	  options.push(['EndPortalFrame','120']);
	  options.push(['EndStone','121']);
	  options.push(['EnderDragonEgg','122']);
	  options.push(['RedstoneLampOff','123']);
	  options.push(['RedstoneLampOn','124']);
	  options.push(['DoubleOakWoodSlab','125']);
	  options.push(['DoubleSpruceWoodSlab','125:1']);
	  options.push(['DoubleBirchWoodSlab','125:2']);
	  options.push(['DoubleJungleWoodSlab','125:3']);
	  options.push(['DoubleAcaciaWoodSlab','125:4']);
	  options.push(['DoubleDarkOakWoodSlab','125:5']);
	  options.push(['OakWoodSlab','126']);
	  options.push(['SpruceWoodSlab','126:1']);
	  options.push(['BirchWoodSlab','126:2']);
	  options.push(['JungleWoodSlab','126:3']);
	  options.push(['AcaciaWoodSlab','126:4']);
	  options.push(['DarkOakWoodSlab','126:5']);
	  options.push(['CocoaPlant','127']);
	  options.push(['SandstoneStairs','128']);
	  options.push(['SandstoneStair','128']);
	  options.push(['EmeraldOre','129']);
	  options.push(['EnderChest','130']);
	  options.push(['TripwireHook','131']);
	  options.push(['Tripwire','132']);
	  options.push(['EmeraldBlock','133']);
	  options.push(['SpruceStairs','134']);
	  options.push(['BirchStairs','135']);
	  options.push(['JungleStairs','136']);
	  options.push(['PineWoodStair','134']);
	  options.push(['BirchWoodStair','135']);
	  options.push(['JungleWoodStair','136']);
	  options.push(['CommandBlock','137']);
	  options.push(['Beacon','138']);
	  options.push(['CobblestoneWall','139']);
	  options.push(['MossyCobbleWall','139:1']);
	  options.push(['Flowerpot','140']);
	  options.push(['Potatoes','142']);
	  options.push(['WoodenButton','143']);
	  options.push(['Skull','144']);
	  options.push(['SkeletonHead','144']);
	  options.push(['WitherSkeletonHead','144']);
	  options.push(['ZombieHead','144']);
	  options.push(['HumanHead','144']);
	  options.push(['CreeperHead','144']);
	  options.push(['Anvil','145']);
	  options.push(['TrappedChest','146']);
	  options.push(['LightWeightedPressurePlate','147']);
	  options.push(['HeavyWeightedPressurePlate','148']);
	  options.push(['RedstoneComparator','149']);
	  options.push(['RedstoneComparatorPowered','150']);
	  options.push(['DaylightSensor','151']);
	  options.push(['RedstoneBlock','152']);
	  options.push(['QuartzOre','153']);
	  options.push(['NetherQuartzOre','153']);
	  options.push(['Hopper','154']);
	  options.push(['QuartzBlock','155']);
	  options.push(['ChiseledQuartzBlock','155:1']);
	  options.push(['OrnateQuartzBlock','155:1']);
	  options.push(['QuartzPillarVertical','155:2']);
	  options.push(['QuartzPillarHorizontal','155:3']);
	  options.push(['QuartzPillarCap','155:4']);
	  options.push(['QuartzStairs','156']);
	  options.push(['ActivatorRail','157']);
	  options.push(['Dropper','158']);
	  options.push(['WhiteStainedClay','159']);
	  options.push(['OrangeStainedClay','159:1']);
	  options.push(['MagentaStainedClay','159:2']);
	  options.push(['LightBlueStainedClay','159:3"']);
	  options.push(['YellowStainedClay','159:4']);
	  options.push(['LimeStainedClay','159:5']);
	  options.push(['PinkStainedClay','159:6']);
	  options.push(['GrayStainedClay','159:7']);
	  options.push(['LightGrayStainedClay','159:8']);
	  options.push(['CyanStainedClay','159:9']);
	  options.push(['PurpleStainedClay','159:10']);
	  options.push(['BlueStainedClay','159:11']);
	  options.push(['BrownStainedClay','159:12']);
	  options.push(['GreenStainedClay','159:13']);
	  options.push(['RedStainedClay','159:14']);
	  options.push(['BlackStainedClay','159:15']);
	  options.push(['WhiteGlassPane','160']);
	  options.push(['OrangeGlassPane','160:1']);
	  options.push(['MagentaGlassPane','160:2']);
	  options.push(['LightBlueGlassPane','160:3']);
	  options.push(['YellowGlassPane','160:4']);
	  options.push(['LimeGlassPane','160:5']);
	  options.push(['PinkGlassPane','160:6']);
	  options.push(['GrayGlassPane','160:7']);
	  options.push(['LightGrayGlassPane','160:8']);
	  options.push(['CyanGlassPane','160:9']);
	  options.push(['PurpleGlassPane','160:10']);
	  options.push(['BlueGlassPane','160:11']);
	  options.push(['BrownGlassPane','160:12']);
	  options.push(['GreenGlassPane','160:13']);
	  options.push(['RedGlassPane','160:14']);
	  options.push(['BlackGlassPane','160:15']);
	  options.push(['AcaciaLeaves','161']);
	  options.push(['DarkOakLeaves','161:1']);
	  options.push(['AcaciaLog','162']);
	  options.push(['DarkOakLog','162:1']);
	  options.push(['AcaciaStairs','163']);
	  options.push(['DarkOakStairs','164']);
	  options.push(['SlimeBlock','165']);
	  options.push(['Barrier','166']);
	  options.push(['IronTrapDoor','167']);
	  options.push(['Prismarine','168']);
	  options.push(['PrismarineBricks','168:1']);
	  options.push(['DarkPrismarine','168:2']);
	  options.push(['SeaLantern','169']);
	  options.push(['HayBale','170']);
	  options.push(['WhiteCarpet','171']);
	  options.push(['OrangeCarpet','171:1']);
	  options.push(['MagentaCarpet','171:2']);
	  options.push(['LightBlueCarpet','171:3']);
	  options.push(['YellowCarpet','171:4']);
	  options.push(['LimeCarpet','171:5']);
	  options.push(['PinkCarpet','171:6']);
	  options.push(['GrayCarpet','171:7']);
	  options.push(['LightGrayCarpet','171:8']);
	  options.push(['CyanCarpet','171:9']);
	  options.push(['PurpleCarpet','171:10']);
	  options.push(['BlueCarpet','171:11']);
	  options.push(['BrownCarpet','171:12']);
	  options.push(['GreenCarpet','171:13']);
	  options.push(['RedCarpet','171:14']);
	  options.push(['BlackCarpet','171:15']);
	  options.push(['HardenedClay','172']);
	  options.push(['CoalBlock','173']);
	  options.push(['PackedIce','174']);
	  options.push(['Sunflower','175']);
	  options.push(['Lilac','175:1']);
	  options.push(['DoubleGrass','175:2']);
	  options.push(['LargeFern','175:3']);
	  options.push(['RoseBush','175:4']);
	  options.push(['Peony','175:5']);
	  options.push(['StandingBanner','176']);
	  options.push(['WallBanner','177']);
	  options.push(['DaylightSensorInverted','178']);
	  options.push(['RedSandstone','179']);
	  options.push(['RedSandstoneChiseled','179:1']);
	  options.push(['RedSandstoneSmooth','179:2']);
	  options.push(['RedSandstoneOrnate','179:1']);
	  options.push(['RedSandstoneBlank','179:2']);
	  options.push(['RedSandstoneStairs','180']);
	  options.push(['DoubleRedSandstoneSlab','181']);
	  options.push(['RedSandstoneSlab','182']);
	  options.push(['SpruceFenceGate','183']);
	  options.push(['BirchFenceGate','184']);
	  options.push(['JungleFenceGate','185']);
	  options.push(['DarkOakFenceGate','186']);
	  options.push(['AcaciaFenceGate','187']);
	  options.push(['SpruceFence','188']);
	  options.push(['BirchFence','189']);
	  options.push(['JungleFence','190']);
	  options.push(['DarkOakFence','191']);
	  options.push(['AcaciaFence','192']);
	  options.push(['SpruceDoor','193']);
	  options.push(['BirchDoor','194']);
	  options.push(['JungleDoor','195']);
	  options.push(['AcaciaDoor','196']);
	  options.push(['DarkOakDoor','197']);

	  return options;
}
Blockly.Blocks['turtle_setblocktype2'] = {
  init: function() {
	var dropdown = new Blockly.FieldDropdown(blockOptions);
    this.appendDummyInput()
        .appendField("set block type to")
        .appendField(dropdown, 'type');
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