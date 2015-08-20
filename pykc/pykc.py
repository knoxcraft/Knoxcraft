import http.client
import urllib
import time
import inspect
import os


VERSION='0.1'

'''
{"scriptname" : "phillipe", 
	"commands" : [
		{"cmd" : "forward", "args" : {"dist" : 10}}, 
		{"cmd" : "turnRight", "args" : {"degrees" : 90}}, 
		{"cmd" : "forward", "args" : {"dist" : 20}}
	]
}
'''

class Command:
	def __init__(self, cmd, args):
		self.command=cmd
		self.args=args
	def __str__(self):
		argList=[]
		for k,v in self.args.items():
			if type(v) == str:
				v='"%s"' % v
			elif type(v) == int or type(v) == long:
				v='%d' % v
			argList.append('"%s" : %s' % (k, v))
		argStr=', '.join(argList)
		return '{"cmd" : "%s", "args" : {%s}}' % (self.command, argStr)

class Turtle:
	#TODO testing
	#TODO Add value support? (verify it atleast)
	def __init__(self, name):
		self.name=name
		self.commands=[]
	def forward(self, numBlocks):
		self.commands.append(Command('forward', {'dist' : numBlocks}))
	def backward(self, numBlocks):
		self.commands.append(Command('backward', {'dist' : numBlocks}))
	def right(self, numBlocks):
		self.commands.append(Command('right', {'dist' : numBlocks}))
	def left(self, numBlocks):
		self.commands.append(Command('left', {'dist' : numBlocks}))
	def up(self, numBlocks):
		self.commands.append(Command('up', {'dist' : numBlocks}))
	def down(self, numBlocks):
		self.commands.append(Command('down', {'dist' : numBlocks}))		
	def turnRight(self, numDegrees):
		self.commands.append(Command('turnRight', {'degrees' : numDegrees}))
	def turnLeft(self, numDegrees):
		self.commands.append(Command('turnLeft', {'degrees' : numDegrees}))
	def setPosition(self, x, y, z):
		self.commands.append(Command('setPosition', {'x' : x, 'y' : y, 'z' : z}))
	def setDirection(self, direction):
		self.commands.append(Command('setDirection', {'direction' : direction}))
	def blockPlace(self, place):
		self.commands.append(Command('blockPlace', {'dist' : place}))
	def setBlock(self, blockType):
		'''
		# this is a really hacky way to handle blocks with both id and data
		# such as RedWool (id 35, data 14)
		# But it was very quick to implement
		if '-' in blockType:
			(id, data) = blockType.split('-')
			self.commands.append(Command('setBlock', {'blockType' : blockType}))
		else:
			self.commands.append(Command('setBlock', {'blockType' : int(blockType)}))
		'''
		self.commands.append(Command('setBlock', {'blockType' : blockType}))

	def toJson(self):
		# future work: use the actual Python json library instead of rewriting it...
		cmdList=[]
		for cmd in self.commands:
			cmdList.append(str(cmd))
		cmdStr=', '.join(cmdList) + "\n"
		return \
'''{"scriptname" : "%s",
	"commands" : [
	%s]
}''' % (self.name, cmdStr)

	

	def upload(self, url, minecraftName):
		conn = http.client.HTTPConnection(url)
		json = self.toJson()
		source = sourceCodeOfCaller()
		params = {'jsontext': json, 
			'sourcetext': source, 
			'language': 'python', 
			'playerName' : minecraftName, 
			'client' : 'pykc-%s' % VERSION}
		
		boundary='P8qZ0SA4n1v9T'+str(round(time.time() * 1000))
		# holy crap, building a multipart upload by hand is a huge pain in the arse
		# I could have saved a whole day if I had read the documentation more closely... Ugh.
		dataList=[]
		dataList.append('--'+boundary)
		for key, val in params.items():
			# Add boundary and header
			dataList.append('Content-Disposition: form-data; name="{0}"'.format(key))
			dataList.append('')
			dataList.append(val)
			dataList.append('--'+boundary)
		dataList[-1]+='--'
		dataList.append('')
		body = '\r\n'.join(dataList)
		#print(body)
		contentType = 'multipart/form-data; boundary={}'.format(boundary)
		headers = {"Content-Type" : contentType, "Accept" : "text/plain"}
		conn.request("POST", "/kctupload", body, headers)
		response = conn.getresponse()

		if response.status==200:
			print('Success!')
			print(response.status, response.reason)	
			print(response.read().decode("utf-8"))
		else:
			print('Failed to upload...')
			print(response.status, response.reason)	
			print(response.read().decode("utf-8"))

def sourceCodeOfCaller():
	firstframe=inspect.getouterframes(inspect.currentframe())[-1][1]
	dir=os.path.dirname(os.path.abspath(firstframe))
	try:
		with open('%s/%s' % (dir, firstframe), 'r') as content_file:
			return content_file.read()
	except:
		return 'Cannot find source file'

class BlockType:
	'''Sort of like an enum, but we literally want the int values and we don't
need any of the features we get from extending a Python Enum.
	'''
	Air = "0"
	Stone = "1"
	Granite = "1:1"
	PolishedGranite = "1:2"
	Diorite = "1:3"
	PolishedDiorite = "1:4"
	Andesite = "1:5"
	PolishedAndesite = "1:6"
	Grass = "2"
	Dirt = "3"
	CoarseDirt = "3:1"
	Podzol = "3:2"
	Cobble = "4"
	OakPlanks = "5"
	SprucePlanks = "5:1"
	BirchPlanks = "5:2"
	JunglePlanks = "5:3"
	AcaciaPlanks = "5:4"
	DarkOakPlanks = "5:5"
	OakWood = "5"
	SpruceWood = "5:1"
	BirchWood = "5:2"
	JungleWood = "5:3"
	AcaciaWood = "5:4"
	DarkOakWood = "5:5"
	OakSapling = "6"
	SpruceSapling = "6:1"
	BirchSapling = "6:2"
	JungleSapling = "6:3"
	AcaciaSapling = "6:4"
	DarkOakSapling = "6:5"
	Bedrock = "7"
	WaterFlowing = "8"
	Water = "9"
	LavaFlowing = "10"
	Lava = "11"
	Sand = "12"
	RedSand = "12:1"
	Gravel = "13"
	GoldOre = "14"
	IronOre = "15"
	CoalOre = "16"
	OakLog = "17"
	SpruceLog = "17:1"
	PineLog = "17:1"
	BirchLog = "17:2"
	JungleLog = "17:3"
	OakLeaves = "18"
	SpruceLeaves = "18:1"
	PineLeaves = "18:1"
	BirchLeaves = "18:2"
	JungleLeaves = "18:3"
	Sponge = "19"
	Glass = "20"
	LapisOre = "21"
	LapislazuliOre = "21"
	LapisBlock = "22"
	Dispenser = "23"
	Sandstone = "24"
	SandstoneChiseled = "24:1"
	SandstoneOrnate = "24:1"
	SandstoneSmooth = "24:2"
	SandstoneBlank = "24:2"
	NoteBlock = "25"
	Bed = "26"
	BedBlock = "26"
	PoweredRail = "27"
	DetectorRail = "28"
	StickyPiston = "29"
	Web = "30"
	SpiderWeb = "30"
	Shrub = "31"
	TallGrass = "31:1"
	Fern = "31:2"
	DeadBush = "32"
	Piston = "33"
	PistonHead = "34"
	WhiteWool = "35"
	OrangeWool = "35:1"
	MagentaWool = "35:2"
	LightBlueWool = "35:3"
	YellowWool = "35:4"
	LimeWool = "35:5"
	PinkWool = "35:6"
	GrayWool = "35:7"
	LightGrayWool = "35:8"
	CyanWool = "35:9"
	PurpleWool = "35:10"
	BlueWool = "35:11"
	BrownWool = "35:12"
	GreenWool = "35:13"
	RedWool = "35:14"
	BlackWool = "35:15"
	WoolWhite = "35"
	WoolOrange = "35:1"
	WoolMagenta = "35:2"
	WoolLightBlue = "35:3"
	WoolYellow = "35:4"
	WoolLightGreen = "35:5"
	WoolPink = "35:6"
	WoolGray = "35:7"
	WoolLightGray = "35:8"
	WoolCyan = "35:9"
	WoolPurple = "35:10"
	WoolBlue = "35:11"
	WoolBrown = "35:12"
	WoolDarkGreen = "35:13"
	WoolRed = "35:14"
	WoolBlack = "35:15"
	PistonExtended = "36"
	Dandelion = "37"
	Poppy = "38"
	BlueOrchid = "38:1"
	Allium = "38:2"
	AzureBluet = "38:3"
	RedTulip = "38:4"
	OrangeTulip = "38:5"
	WhiteTulip = "38:6"
	PinkTulip = "38:7"
	OxeyeDaisy = "38:8"
	BrownMushroom = "39"
	RedMushroom = "40"
	GoldBlock = "41"
	IronBlock = "42"
	DoubleStoneSlab = "43"
	DoubleSandStoneSlab = "43:1"
	DoubleWoodSlab = "43:2"
	DoubleCobbleSlab = "43:3"
	DoubleBrickSlab = "43:4"
	DoubleStoneBricksSlab = "43:5"
	DoubleNetherBrickSlab = "43:6"
	DoubleQuartzSlab = "43:7"
	DoubleOrnateStoneSlab = "43"
	DoubleSandStoneTrimSlab = "43:1"
	DoubleBrickBlockSlab = "43:4"
	StoneSlab = "44"
	SandStoneSlab = "44:1"
	WoodSlab = "44:2"
	CobbleSlab = "44:3"
	BrickSlab = "44:4"
	StoneBricksSlab = "44:5"
	NetherBricksSlab = "44:6"
	QuartzSlab = "44:7"
	OrnateStoneSlab = "44"
	SandStoneTrimSlab = "44:1"
	BrickBlockSlab = "44:4"
	Bricks = "45"
	BrickBlock = "45"
	TNT = "46"
	Tnt = "46"
	Bookshelf = "47"
	MossyCobble = "48"
	Obsidian = "49"
	Torch = "50"
	FireBlock = "51"
	MobSpawner = "52"
	OakStairs = "53"
	WoodenStair = "53"
	Chest = "54"
	RedstoneWire = "55"
	DiamondOre = "56"
	DiamondBlock = "57"
	Workbench = "58"
	Wheat = "59"
	Crops = "59"
	Farmland = "60"
	Soil = "60"
	Furnace = "61"
	BurningFurnace = "62"
	StandingSign = "63"
	SignPost = "63"
	WoodenDoor = "64"
	OakDoor = "64"
	Ladder = "65"
	Rail = "66"
	StoneStairs = "67"
	CobbleStair = "67"
	WallSign = "68"
	Lever = "69"
	StonePressurePlate = "70"
	StonePlate = "70"
	IronDoor = "71"
	WoodenPressurePlate = "72"
	WoodPlate = "72"
	RedstoneOre = "73"
	GlowingRedstoneOre = "74"
	RedstoneTorchOff = "75"
	RedstoneTorchOn = "76"
	StoneButton = "77"
	Snow = "78"
	Ice = "79"
	SnowBlock = "80"
	Cactus = "81"
	Clay = "82"
	Reed = "83"
	Jukebox = "84"
	Fence = "85"
	Pumpkin = "86"
	Netherrack = "87"
	SoulSand = "88"
	GlowStone = "89"
	Portal = "90"
	JackOLantern = "91"
	Cake = "92"
	RedstoneRepeaterOff = "93"
	RedstoneRepeaterOn = "94"
	WhiteGlass = "95"
	OrangeGlass = "95:1"
	MagentaGlass = "95:2"
	LightBlueGlass = "95:3"
	YellowGlass = "95:4"
	LimeGlass = "95:5"
	PinkGlass = "95:6"
	GrayGlass = "95:7"
	LightGrayGlass = "95:8"
	CyanGlass = "95:9"
	PurpleGlass = "95:10"
	BlueGlass = "95:11"
	BrownGlass = "95:12"
	GreenGlass = "95:13"
	RedGlass = "95:14"
	BlackGlass = "95:15"
	Trapdoor = "96"
	StoneSilverFishBlock = "97"
	CobbleSilverFishBlock = "97:1"
	StoneBrickSilverFishBlock = "97:2"
	MossyBrickSilverFishBlock = "97:3"
	CrackedSilverFishBlock = "97:4"
	ChiseledSilverFishBlock = "97:5"
	OrnateSilverFishBlock = "97:5"
	StoneBrick = "98"
	MossyStoneBrick = "98:1"
	CrackedStoneBrick = "98:2"
	ChiseledStoneBrick = "98:3"
	OrnateStoneBrick = "98:3"
	HugeBrownMushroom = "99"
	HugeRedMushroom = "100"
	IronBars = "101"
	GlassPane = "102"
	Melon = "103"
	PumpkinStem = "104"
	MelonStem = "105"
	Vines = "106"
	FenceGate = "107"
	BrickStairs = "108"
	BrickStair = "108"
	StoneBrickStairs = "109"
	StoneBrickStair = "109"
	Mycelium = "110"
	Lilypad = "111"
	NetherBrick = "112"
	NetherBrickFence = "113"
	NetherBrickStairs = "114"
	NetherBrickStair = "114"
	NetherWart = "115"
	EnchantmentTable = "116"
	BrewingStand = "117"
	Cauldron = "118"
	EndPortal = "119"
	EndPortalFrame = "120"
	EndStone = "121"
	EnderDragonEgg = "122"
	RedstoneLampOff = "123"
	RedstoneLampOn = "124"
	DoubleOakWoodSlab = "125"
	DoubleSpruceWoodSlab = "125:1"
	DoubleBirchWoodSlab = "125:2"
	DoubleJungleWoodSlab = "125:3"
	DoubleAcaciaWoodSlab = "125:4"
	DoubleDarkOakWoodSlab = "125:5"
	OakWoodSlab = "126"
	SpruceWoodSlab = "126:1"
	BirchWoodSlab = "126:2"
	JungleWoodSlab = "126:3"
	AcaciaWoodSlab = "126:4"
	DarkOakWoodSlab = "126:5"
	CocoaPlant = "127"
	SandstoneStairs = "128"
	SandstoneStair = "128"
	EmeraldOre = "129"
	EnderChest = "130"
	TripwireHook = "131"
	Tripwire = "132"
	EmeraldBlock = "133"
	SpruceStairs = "134"
	BirchStairs = "135"
	JungleStairs = "136"
	PineWoodStair = "134"
	BirchWoodStair = "135"
	JungleWoodStair = "136"
	CommandBlock = "137"
	Beacon = "138"
	CobblestoneWall = "139"
	MossyCobbleWall = "139:1"
	Flowerpot = "140"
	Carrots = "141"
	Potatoes = "142"
	WoodenButton = "143"
	Skull = "144"
	SkeletonHead = "144"
	WitherSkeletonHead = "144"
	ZombieHead = "144"
	HumanHead = "144"
	CreeperHead = "144"
	Anvil = "145"
	TrappedChest = "146"
	LightWeightedPressurePlate = "147"
	HeavyWeightedPressurePlate = "148"
	RedstoneComparator = "149"
	RedstoneComparatorPowered = "150"
	DaylightSensor = "151"
	RedstoneBlock = "152"
	QuartzOre = "153"
	NetherQuartzOre = "153"
	Hopper = "154"
	QuartzBlock = "155"
	ChiseledQuartzBlock = "155:1"
	OrnateQuartzBlock = "155:1"
	QuartzPillarVertical = "155:2"
	QuartzPillarHorizontal = "155:3"
	QuartzPillarCap = "155:4"
	QuartzStairs = "156"
	ActivatorRail = "157"
	Dropper = "158"
	WhiteStainedClay = "159"
	OrangeStainedClay = "159:1"
	MagentaStainedClay = "159:2"
	LightBlueStainedClay = "159:3"
	YellowStainedClay = "159:4"
	LimeStainedClay = "159:5"
	PinkStainedClay = "159:6"
	GrayStainedClay = "159:7"
	LightGrayStainedClay = "159:8"
	CyanStainedClay = "159:9"
	PurpleStainedClay = "159:10"
	BlueStainedClay = "159:11"
	BrownStainedClay = "159:12"
	GreenStainedClay = "159:13"
	RedStainedClay = "159:14"
	BlackStainedClay = "159:15"
	WhiteGlassPane = "160"
	OrangeGlassPane = "160:1"
	MagentaGlassPane = "160:2"
	LightBlueGlassPane = "160:3"
	YellowGlassPane = "160:4"
	LimeGlassPane = "160:5"
	PinkGlassPane = "160:6"
	GrayGlassPane = "160:7"
	LightGrayGlassPane = "160:8"
	CyanGlassPane = "160:9"
	PurpleGlassPane = "160:10"
	BlueGlassPane = "160:11"
	BrownGlassPane = "160:12"
	GreenGlassPane = "160:13"
	RedGlassPane = "160:14"
	BlackGlassPane = "160:15"
	AcaciaLeaves = "161"
	DarkOakLeaves = "161:1"
	AcaciaLog = "162"
	DarkOakLog = "162:1"
	AcaciaStairs = "163"
	DarkOakStairs = "164"
	SlimeBlock = "165"
	Barrier = "166"
	IronTrapDoor = "167"
	Prismarine = "168"
	PrismarineBricks = "168:1"
	DarkPrismarine = "168:2"
	SeaLantern = "169"
	HayBale = "170"
	WhiteCarpet = "171"
	OrangeCarpet = "171:1"
	MagentaCarpet = "171:2"
	LightBlueCarpet = "171:3"
	YellowCarpet = "171:4"
	LimeCarpet = "171:5"
	PinkCarpet = "171:6"
	GrayCarpet = "171:7"
	LightGrayCarpet = "171:8"
	CyanCarpet = "171:9"
	PurpleCarpet = "171:10"
	BlueCarpet = "171:11"
	BrownCarpet = "171:12"
	GreenCarpet = "171:13"
	RedCarpet = "171:14"
	BlackCarpet = "171:15"
	HardenedClay = "172"
	CoalBlock = "173"
	PackedIce = "174"
	Sunflower = "175"
	Lilac = "175:1"
	DoubleGrass = "175:2"
	LargeFern = "175:3"
	RoseBush = "175:4"
	Peony = "175:5"
	StandingBanner = "176"
	WallBanner = "177"
	DaylightSensorInverted = "178"
	RedSandstone = "179"
	RedSandstoneChiseled = "179:1"
	RedSandstoneSmooth = "179:2"
	RedSandstoneOrnate = "179:1"
	RedSandstoneBlank = "179:2"
	RedSandstoneStairs = "180"
	DoubleRedSandstoneSlab = "181"
	RedSandstoneSlab = "182"
	SpruceFenceGate = "183"
	BirchFenceGate = "184"
	JungleFenceGate = "185"
	DarkOakFenceGate = "186"
	AcaciaFenceGate = "187"
	SpruceFence = "188"
	BirchFence = "189"
	JungleFence = "190"
	DarkOakFence = "191"
	AcaciaFence = "192"
	SpruceDoor = "193"
	BirchDoor = "194"
	JungleDoor = "195"
	AcaciaDoor = "196"
	DarkOakDoor = "197"

