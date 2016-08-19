/////////////////////////LOAD MATERIALS///////////////////////

var allMaterials = [];
var materialNames = {};
materialNames['AIR'] = 0;

// Registers a material
function addMat(name, filename) {
  // Attaches the new filename to the end of the allMaterials array

  allMaterials.push(filename);
  /* Note that the first index in the materials array is actually
   * item '1' (air is 0).
   */
  materialNames[name] = allMaterials.length;
}

addMat("STONE", "stone");
addMat("GRANITE", "stone_granite");
addMat("POLISHED_GRANITE", "stone_granite_smooth");
addMat("DIORITE", "stone_diorite");
addMat("POLISHED_DIORITE", "stone_diorite_smooth");
addMat("ANDESITE", "stone_andesite");
addMat("POLISHED_ANDESITE", "stone_andesite_smooth");
addMat("GRASS", ["grass_side", "grass_side", "grass_top", "dirt", "grass_side", "grass_side"]);
addMat("DIRT", "dirt");
addMat("COARSE_DIRT", "coarse_dirt");
addMat("PODZOL", ["dirt_podzol_top", "dirt_podzol_side"]);
addMat("COBBLESTONE", "cobblestone");
addMat("OAK_WOOD_PLANK", "planks_oak");
addMat("SPRUCE_WOOD_PLANK", "planks_spruce");
addMat("BIRCH_WOOD_PLANK", "planks_birch");
addMat("JUNGLE_WOOD_PLANK", "planks_jungle");
addMat("ACACIA_WOOD_PLANK", "planks_acacia");
addMat("BIRCH_WOOD_PLANK", "planks_birch");
addMat("DARK_OAK_WOOD_PLANK", "planks_big_oak");
// TODO Add the following:
/*
 * OAK_SAPLING
 * SPRUCE_SAPLING
 * BIRCH_SAPLING
 * JUNGLE_SAPLING
 * ACACIA_SAPLING
 * DARK_OAK_SAPLING
 * FLOWING_WATER
 * STILL_WATER
 * FLOWING_LAVA
 * STILL_LAVA
 */
addMat("BEDROCK", "bedrock");
addMat("SAND", "sand");
addMat("RED_SAND", "red_sand");
addMat("GRAVEL", "gravel");
addMat("GOLD_ORE", "gold_ore");
addMat("IRON_ORE", "iron_ore");
addMat("COAL_ORE", "coal_ore");
addMat("OAK_WOOD", ["log_oak", "log_oak", "log_oak_top", "log_oak", "log_oak", "log_oak_top"]);
addMat("SPRUCE_WOOD", ["log_spruce", "log_spruce", "log_spruce_top", "log_spruce", "log_spruce", "log_spruce_top"]);
addMat("BIRCH_WOOD", ["log_birch", "log_birch", "log_birch_top", "log_birch", "log_birch", "log_birch_top"]);
addMat("JUNGLE_WOOD", ["log_jungle", "log_jungle", "log_jungle_top", "log_jungle", "log_jungle", "log_jungle_top"]);
// TODO: Add leaves
addMat("SPONGE", "sponge");
addMat("WET_SPONGE", "sponge_wet");
// TODO: Add GLASS
addMat("LAPIS_LAZULI_ORE", "lapis_ore");
addMat("LAPIS_LAZULI_BLOCK", "lapis_block");
// TODO Add dispenser
addMat("SANDSTONE", ["sandstone_normal", "sandstone_normal", "sandstone_top", "sandstone_bottom", "sandstone_normal", "sandstone_normal"]);
addMat("CHISELED_SANDSTONE", ["sandstone_carved", "sandstone_carved", "sandstone_top", "sandstone_bottom", "sandstone_carved", "sandstone_carved"]);
addMat("SMOOTH_SANDSTONE", ["sandstone_smooth", "sandstone_smooth", "sandstone_top", "sandstone_bottom", "sandstone_smooth", "sandstone_smooth"]);
// TODO add noteblock
addMat("WHITE_WOOL", "wool_colored_white");
addMat("ORANGE_WOOL", "wool_colored_orange");
addMat("MAGENTA_WOOL", "wool_colored_magenta");
addMat("LIGHT_BLUE_WOOL", "wool_colored_light_blue");
addMat("YELLOW_WOOL", "wool_colored_yellow");
addMat("LIME_WOOL", "wool_colored_lime");
addMat("PINK_WOOL", "wool_colored_pink");
addMat("GRAY_WOOL", "wool_colored_gray");
addMat("LIGHT_GRAY_WOOL", "wool_colored_silver");
addMat("CYAN_WOOL", "wool_colored_cyan");
addMat("PURPLE_WOOL", "wool_colored_purple");
addMat("BLUE_WOOL", "wool_colored_blue");
addMat("BROWN_WOOL", "wool_colored_brown");
addMat("GREEN_WOOL", "wool_colored_green");
addMat("RED_WOOL", "wool_colored_red");
addMat("BLACK_WOOL", "wool_colored_black");
addMat("GOLD_BLOCK", "gold_block");
addMat("IRON_BLOCK", "iron_block");
addMat("BRICKS", "brick");
addMat("TNT", ["tnt_side", "tnt_side", "tnt_top", "tnt_bottom", "tnt_side", "tnt_side"]);
addMat("BOOKSHELF", "bookshelf");
addMat("MOSS_STONE", "cobblestone_mossy");
addMat("OBSIDIAN", "obsidian");
addMat("MONSTER_SPAWNER", "mob_spawner");
addMat("DIAMOND_ORE", "diamond_ore");
addMat("DIAMOND_BLOCK", "diamond_block");
addMat("CRAFTING_TABLE", ["crafting_table_side", "crafting_table_side", "crafting_table_top", "crafting_table_top", "crafting_table_side", "crafting_table_side"]);
addMat("FARMLAND", "farmland_dry");
// TODO Add Furnace
addMat("SNOW", ["grass_side_snowed", "grass_side_snowed", "snow", "dirt", "grass_side_snowed", "grass_side_snowed"]);
addMat("ICE", "ice_packed");
addMat("SNOW_BLOCK", "snow");
addMat("CACTUS", ["cactus_side", "cactus_side", "cactus_top", "cactus_top", "cactus_side", "cactus_side"]);
addMat("CLAY", "clay");



/////////////////////////Initialize world/////////////////////
var createGame = require('voxel-engine');
var game = createGame({
  generate: function(x, y, z) {
    return y === 0 ? materialNames['GRASS'] : 0;
  },
  chunkDistance: 2,
  materials: allMaterials,
  materialFlatColor: false,
  texturePath: '/textures/'
});
// TODO: should we attach this to a div?
game.appendTo(document.getElementById("Game"));

// Set origin to RED_WOOL
game.setBlock(new Array(0, 0, 0), materialNames['RED_WOOL']);

// Now we have a world, but no player. The following code fixes that
var createPlayer = require('voxel-player')(game);
var substack = createPlayer('substack.png');
substack.possess();
substack.position.set(0,5,0);

// I believe I can fly!
var fly = require('voxel-fly');
var makeFly = fly(game);
makeFly(game.controls.target());

// highlight blocks when you look at them
var highlight = require('voxel-highlight')
var highlightPos
var hl = game.highlighter = highlight(game, { color: 0x00ff00 })
hl.on('highlight', function (voxelPos) { highlightPos = voxelPos })
hl.on('remove', function (voxelPos) { highlightPos = null })

//
// Create a div containing all of the textures
//
function td(val){
  return '<td>'+val+'</td>';
}
function tr(val){
  return '<tr>'+val+'</tr>';
}
function th(val){
  return '<th>'+val+'</th>';
}
function makeTextureTable() {
  var table="<table border=1>" +tr(th('Name of Block')+th('Texture'));
  for (var key in materialNames) {
    if (materialNames.hasOwnProperty(key)){
      if (allMaterials[materialNames[key]-1] instanceof Array) {
        var filename=allMaterials[materialNames[key]-1][0]+'.png';
      } else {
        var filename=allMaterials[materialNames[key]-1]+'.png';
      }
      if (key != 'AIR') {
        // table += tr(td(key)+td('<img src="https://knoxcraft.github.io/textures/' +filename+ '" width="64" height="64"/>'));
        table += tr(td(key)+td('<img src="/textures/' +filename+ '" width="64" height="64"/>'));
      } else {
        table += tr(td(key)+td('<img src="/textures/air.png" width="64" height="64"/>'));
      }
    }
  }
  table += "</table>";
  return table;
}
var blocktypes=document.getElementById("blocktypes").innerHTML = makeTextureTable();


/////////////////////////////////////Begin Turtle related code/////////////////

// Turtle variables
// For some ungodly reason, the creators of voxeljs decided to use Arrays to represent positions instead of vectors
var position = new Array(0,1,0)
var turnAngle = 0;
var blockPlace = true;
var defaultBlockName = "STONE";
var scriptLaunchLocation = new Array(0,1,0);
var blockType = materialNames[defaultBlockName];

// The current commands that will be run (extracted directly from the JSON)
var curScript = null;
var curACEErrorMarker = null;

// The name of the turtle class
var className = 'HelloWorld';

// Undo functionality
var curUndo = [];
var allUndos = [];

/* This is a lame javascript way of creating hashmaps. The things in brackets
 * are keys, and their assigned objects are values. Note that these "integers"
 * are actually keys, not array indexes. Really, they're parameters
 */
var angleMappings = {}
angleMappings[0] = new Array(1,0,0);
angleMappings[45] = new Array(1,0,1);
angleMappings[90] = new Array(0,0,1);
angleMappings[135] = new Array(-1,0,1);
angleMappings[180] = new Array(-1,0,0);
angleMappings[225] = new Array(-1,0,-1);
angleMappings[270] = new Array(0,0,-1);
angleMappings[315] = new Array(1,0,-1);

//
// Helper functions for updating the DOM
//
function setStatus(message) {
  document.getElementById("scriptstatus").innerHTML = message;
}
function setMessage(outcome) {
  // TODO: improve how we post and communicate errors?
  document.getElementById("message").innerHTML = outcome;
}
function running() {
  setMessage('Java code compiling and running!<br><img src="/images/loading.gif"/><br>');
}


// Updates the lookLocation indicator when the mouse is clicked ("fired")
game.on('fire', function (target, state) {
  // Purely for debugging purposes
  document.getElementById("looklocation").innerHTML = "(" + highlightPos + ")";
  scriptLaunchLocation = highlightPos;
});

function uploadCode(evt) {
  setStatus("Loading script...");
  readFile(evt.target.files[0], updateCode);
}

function updateCode(e) {
  try {
    var result = e.target.result;
    editor.setValue(result);
  }
  catch(err) {
    setStatus("ERROR READING FILE");
  }
}


// Called when user presses JSONUploadButton- begins
// reading the uploaded file
function parseJSON(evt) {
  setStatus("Loading script...");
  //document.getElementById("scriptstatus").innerHTML = "Loading script...";
  // registers updateJSON to run after the file has been read
  // (since we're not uploading multiple files at once, we can
  // just get the first element in the button's files list)
  readFile(evt.target.files[0], updateJSON);
}

// Called after the uploaded file's text has been processed
function updateJSON(e) {
  try {
    // Gets the text from the file reader (which triggered event e)
    var result = e.target.result;
    // Converts the text to a JSON file
    extractCommandsFromJSON(result);
  }
  catch(err) {
    setStatus("ERROR READING FILE");
  }
}

// Called once the JSON has been loaded
window.extractCommandsFromJSON = function(jsontext) {
  var json = JSON.parse(jsontext);
  // Sets the current script to be the JSON's list of commands
  curScript = json.commands;
  if (curScript != null) {
    goToTab("gameTab");
    setStatus(json.scriptname + " has been loaded successfully!");
    if (document.getElementById("autoRunScript").checked) {
      runScript();
    }
  } else {
    setStatus("LOADED JSON BUT COULD NOT FIND COMMANDS! (Did you upload the right JSON file?)");
  }
}
// HACK exporting this one function from browserify so that Blockly can call it
var exports = module.exports = {};
exports.extractCommandsFromJSON=extractCommandsFromJSON;
// Registers the function onLoadCallBack to run after file has been loaded
function readFile(file, onLoadCallback){
    var reader = new FileReader();
    reader.onload = onLoadCallback;
    reader.readAsText(file);
}

// Executes the commands stored in curScript
var runScript = function() {
  if (curScript === null) {
    window.alert("There is no loaded script!");
    return;
  }
  if (!(allUndos === null) && (allUndos.length > 0) && document.getElementById("clearMapBeforeScript").checked) {
    while(allUndos.length > 0) {
      undo();
    }
  }
  // Set turtle's position to the player's current location
  // and its rotation to 0
  position[0] = scriptLaunchLocation[0];
  position[1] = scriptLaunchLocation[1];
  position[2] = scriptLaunchLocation[2];
  turnAngle = 0;
  for (i = 0; i < curScript.length; i++) {
    //window.alert("Executing command: " + curScript[i].cmd);
    var cmd = curScript[i]
    switch(cmd.cmd) {
      case "forward":
        forward(cmd.args);
        break;
      case "backward":
        backward(cmd.args);
        break;
      case "right":
        right(cmd.args);
        break;
      case "left":
        left(cmd.args);
        break;
      case "up":
        up(cmd.args);
        break;
      case "down":
        down(cmd.args);
        break;
      case "turn":
        turnCommand(cmd.args);
        break;
      case "placeBlocks":
        setBlockPlace(cmd.args);
        break;
      case "setBlock":
        setBlock(cmd.args);
        break;
      case "setPosition":
        setPosition(cmd.args);
        break;
      case "setDirection":
        setDirection(cmd.args);
        break;
      default:
        window.alert("Did not recognize command " + cmd.cmd);
    }
  }
  if (curUndo.length > 0) {
    allUndos.push(curUndo);
    curUndo = [];
  }
}

// Runs when the user presses the undo button
function undo() {
  if (!(allUndos === null) && allUndos.length > 0) {
    var undo = allUndos[allUndos.length - 1];
    // Removes the last element from the array (the second parameter is the number of elements
    // to remove- in this case 1
    allUndos.splice(allUndos.length - 1, 1);
    for (var i = 0; i < undo.length; i++) {
      game.setBlock(undo[i].pos, undo[i].type);
    }
  } else {
    window.alert("There are no actions to undo!");
  }
}

// Turns the turtle deg degrees and bounds the result between 0 and 360
function turn(deg) {
  turnAngle = (turnAngle + (deg % 360) + 360) % 360 ;
}

// These functions let the turtle respond to commands
function forward(args) {
  var direction = angleMappings[turnAngle];
  var dist = args.dist;
  if (blockPlace === true) {
    while (dist > 0) {
      position[0] += direction[0];
      position[2] += direction[2];
      var copyPosition = new Array(position[0], position[1], position[2]);
      curUndo.push({pos: copyPosition, type: game.getBlock(copyPosition)});
      game.setBlock(position, blockType);
      dist--;
    }
  } else {
    position[0] += direction[0] * dist;
    position[2] += direction[2] * dist;
  }
}

function backward(args) {
  turn(180);
  forward(args);
  turn(-180);
}

// I'm confused by these degree turns, but they seem to work
function right(args) {
  turn(90);
  forward(args);
  turn(-90);
}

function left(args) {
  turn(-90);
  forward(args);
  turn(90);
}

function up(args) {
  var dist = args.dist;
  if (blockPlace === true) {
    while (dist > 0) {
      position[1] += 1;
      game.setBlock(position, blockType);
      dist--;
    }
  } else {
    position[1] += dist;
  }
}

function down(args) {
  var dist = args.dist;
  if (blockPlace === true) {
    while (dist > 0) {
      position[1] -= 1;
      game.setBlock(position, blockType);
      dist--;
    }
  } else {
    position[1] -= dist;
  }
}

function turnCommand(args) {
  if (args.dir === "right") {
    turn(-1 * args.degrees);
  }

  if (args.dir === "left") {
    turn(args.degrees);
  }

}

function setBlockPlace(args) {
  blockPlace = args.blockPlaceMode;
}

function setBlock(args) {
  if (args.blockType in materialNames) {
    blockType = materialNames[args.blockType];
  } else {
    window.alert("Warning! The BlockType " + args.blockType + " is not supported in KnoxelJS. These blocks have been replaced with " + defaultBlockName);
    blockType = materialNames[defaultBlockName];
  }
}

function setPosition(args) {
  position[0] = args.x;
  position[1] = args.y;
  position[2] = args.z;
}


function saveToFile(filename, data) {
    var blob = new Blob([data], {type: 'text/csv'});
    if(window.navigator.msSaveOrOpenBlob) {
        window.navigator.msSaveBlob(blob, filename);
    }
    else{
        var elem = window.document.createElement('a');
        elem.href = window.URL.createObjectURL(blob);
        elem.download = filename;
        document.body.appendChild(elem);
        elem.click();
        document.body.removeChild(elem);
        window.URL.revokeObjectURL(blob);
    }
}

function downloadEditorCode() {
  var code = editor.getValue();
  saveToFile(getClassName(code) + ' ' + js_yyyy_mm_dd_hh_mm_ss(), code);
}

function js_yyyy_mm_dd_hh_mm_ss () {
  now = new Date();
  year = "" + now.getFullYear();
  month = "" + (now.getMonth() + 1); if (month.length == 1) { month = "0" + month; }
  day = "" + now.getDate(); if (day.length == 1) { day = "0" + day; }
  hour = "" + now.getHours(); if (hour.length == 1) { hour = "0" + hour; }
  minute = "" + now.getMinutes(); if (minute.length == 1) { minute = "0" + minute; }
  second = "" + now.getSeconds(); if (second.length == 1) { second = "0" + second; }
  return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
}

function getClassName(code) {
  var rx = /class ([^\s]*)[\s]*?[\n]*?{/;
  return rx.exec(code)[1];
}

function goToTab(tab) {
  document.getElementById(tab).click();
}

function highlightErrors(compilerError) {
  window.alert(compilerError);

  var regLineStart = /startLine: ([0-9]*)/;
  var regLineEnd = /endLine: ([0-9]*)/;
  var regColStart = /startColumn: ([0-9]*)/;
  var regColEnd = /endColumn: ([0-9]*)/;

  var lineStart = Number(regLineStart.exec(compilerError)[1]);
  var lineEnd = Number(regLineEnd.exec(compilerError)[1]);
  var colStart = Number(regColStart.exec(compilerError)[1]);
  var colEnd = Number(regColEnd.exec(compilerError)[1]);

  colStart = colStart - 1;

  if (colStart >= colEnd) {
    colEnd = colStart + 1;
  }

  var Range = ace.require('ace/range').Range;
  var range = new Range(lineStart, colStart, lineEnd, colEnd);
  //var range = new Range(1,1,1,1);
  window.alert(lineStart + " " + colStart + " " + lineEnd + " " + colEnd);
  curACEErrorMarker = editor.getSession().addMarker(range,"aceErrorMarker", "text");
}

//////////////////////////////////// Setup HTML Page /////////////////////////////////

// Register the HTML buttons to run the relevant scripts
document.addEventListener("DOMContentLoaded", function(event) {
  // TODO: test DOMContentLoaded with IE8 (does anyone still use IE8?)
  // DOMContentLoaded may not be supported by IE8
  document.getElementById("runscript").addEventListener("click", runScript);
  document.getElementById("codeUploadButton").addEventListener('change', uploadCode, false);
  document.getElementById("JSONUploadButton").addEventListener('change', parseJSON, false);
  document.getElementById("downloadButton").addEventListener('click', downloadEditorCode);
  document.getElementById("undo").addEventListener("click", undo);
  document.getElementById("compileandrun").addEventListener("click", function() {
    running();
    downloadEditorCode();
    editor.getSession().removeMarker(curACEErrorMarker);
    JavaPoly.type('org.knoxcraft.javapoly.JavaPolyCompiler').then(function(JavaPolyCompiler){
      // constants that tell us where things are in the array returned
      // from the JavaPolyCompiler
      var TOTAL_SUCCESS=0;
      var JSON_RESULT=1;
      var RUNTIME_SUCCESS=2;
      var RUNTIME_MESSAGE=3;
      var COMPILE_SUCCESS=4;
      var COMPILE_MESSAGE=5;
      var code = editor.getValue();
      var className;
      var userClassName = document.getElementById("classname").value;
      if (userClassName != null && userClassName != "") {
        className = userClassName;
      } else {
        className = getClassName(code);
      }
      // TODO: someday timeout if this call takes too long
      JavaPolyCompiler.compileAndRun(code, className).then(function(result){
        console.log("result is "+result);
        if (result[TOTAL_SUCCESS]==='true'){
          // success
          // alert("success");
	  // TODO: I hope this is the correct place to send the JSON commands
	  extractCommandsFromJSON(result[JSON_RESULT]);
          setMessage("successfully compiled and loaded code!");
        } else if (result[COMPILE_SUCCESS]==='true' && result[RUNTIME_SUCCESS]==="false"){
	  // runtime error
          console.log(result[RUNTIME_MESSAGE]);
          setMessage("runtime error:\n"+result[RUNTIME_MESSAGE]);
          highlightErrors(result[COMPILE_MESSAGE]);
        } else {
          // compiler error
          console.log(result[RUNTIME_MESSAGE]);
          setMessage("compile error: \n"+result[COMPILE_MESSAGE]);
          highlightErrors(result[COMPILE_MESSAGE]);
        }
      }, function(error) {
        console.log(error);
        window.alert("Unexpected error! "+error);
        setMessage("Unexpected internal error! In all likelyhood, we could not determine your class name correctly, or you don't have a main method. Try overriding the class name in Options.");
      });
    });
  }, function(error) {
       setMessage("A very bad error occurred (JavaPoly isn't working or we can't find an essential dependency- probably JavaPolyCompiler). There is nothing that you can do. I'm sorry.")
});
});

// No tabs are open by default, which is sort of strange. This opens the coding tab.
goToTab("javaTab");
