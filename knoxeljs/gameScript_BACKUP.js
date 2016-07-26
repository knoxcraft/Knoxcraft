/////////////////////////Initialize world/////////////////////
var createGame = require('voxel-engine');
var game = createGame({
  generate: function(x, y, z) {
    if (y === 0) {
      return (x + z) % 2 === 0 ? 1 : 1
    } return 0;
  },
  chunkDistance: 2,
  materials: ["#fff", "#000", "#ff0000", "#00ff00", "#0000ff", "#ffff00", "#00ffff", "#ff00ff"],
  materialFlatColor: true
});
game.appendTo(document.body);

// Set origin to RED
game.setBlock(new Array(0, 0, 0), 3);

// Now we have a world, but no player. The following code fixes that
var createPlayer = require('voxel-player')(game);
var substack = createPlayer('substack.png');
substack.possess();
substack.position.set(0,5,0);

// highlight blocks when you look at them
var highlight = require('voxel-highlight')
var highlightPos
var hl = game.highlighter = highlight(game, { color: 0x00ff00 })
hl.on('highlight', function (voxelPos) { highlightPos = voxelPos })
hl.on('remove', function (voxelPos) { highlightPos = null })

game.on('fire', function (target, state) {
  // Purely for debugging purposes
  document.getElementById("looklocation").innerHTML = highlightPos + " (type: " + game.getBlock(highlightPos) + ")";
})

/////////////////////////////////////Begin Turtle related code/////////////////

// Register the HTML buttons to run the relevant scripts
document.getElementById("runscript").addEventListener("click", runScript);
document.getElementById("JSONUploadButton").addEventListener('change', parseJSON, false);

// Turtle variables
// For some ungodly reason, the creators of voxeljs decided to use Arrays to represent positions instead of vectors
var position = new Array(0,1,0)
var turnAngle = 0;
var blockPlace = true;
var blockType = 1;

// This is a lame javascript way of creating hashmaps. The things in brackets are keys, and their assigned objects are values. Note that these "integers" are actually keys, not array indexes. Really, they're parameters
var angleMappings = {}
angleMappings[0] = new Array(1,0,0);
angleMappings[45] = new Array(1,0,1);
angleMappings[90] = new Array(0,0,1);
angleMappings[135] = new Array(-1,0,1);
angleMappings[180] = new Array(-1,0,0);
angleMappings[225] = new Array(-1,0,-1);
angleMappings[270] = new Array(0,0,-1);
angleMappings[315] = new Array(1,0,-1);

// The current commands that will be run (extracted directly from the JSON)
var curScript = null;

// Called when user presses JSONUploadButton- begins
// reading the uploaded file
function parseJSON(evt) {
  document.getElementById("scriptstatus").innerHTML = "Loading script...";
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
    var json = JSON.parse(result);
    // Sets the current script to be the JSON's list of commands
    curScript = json.commands;
    document.getElementById("scriptstatus").innerHTML = json.scriptname + " has been loaded successfully!";
  }
  catch(err) {
    document.getElementById("scriptstatus").innerHTML = "ERROR READING FILE";
  }
}

// Registers the function onLoadCallBack to run after the file has been loaded
function readFile(file, onLoadCallback){
    var reader = new FileReader();
    reader.onload = onLoadCallback;
    reader.readAsText(file);
}

// Executes the commands stored in curScript
function runScript() {
  if (curScript === null) {
    window.alert("There is no loaded script!");
  }
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
      case "blockPlaceMode":
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
  blockPlace = args.place;
}

function setBlock(args) {
  blockType = args.blockType;
}

function setPosition(args) {
  position[0] = args.x;
  position[1] = args.y;
  position[2] = args.z;
}

// Direction? Is it just a degree?
function setDirection(args) {

}

