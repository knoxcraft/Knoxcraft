/*
This is what we would require if Blockly used module.exports
Instead we are going to just concatenate all of files together,
and then minify them.

require('./js/blockly_compressed.js');
require('./js/blocks_compressed.js');
require('./js/javascript_compressed.js');
require('./msg/js/en.js');
require('./blocks/turtle.js');
require('./generators/javascript/turtle.js');
*/

// HACK global variables
var onresize=function(){}
var workspace=null;

//blocklySetup : function(blocklyAreaName, blocklyDivName, toolboxName) {
var blocklySetup = function(blocklyAreaName, blocklyDivName, toolboxName) {
  var blocklyArea = document.getElementById(blocklyAreaName);
  var blocklyDiv = document.getElementById(blocklyDivName);
  workspace = Blockly.inject(blocklyDiv,
    {toolbox: document.getElementById(toolboxName)}
  );
  onresize = function(e) {
    //console.log('resizing blockly');
    // Compute the absolute coordinates and dimensions of blocklyArea.
    var element = blocklyArea;
    var x = 0;
    var y = 0;
    do {
      x += element.offsetLeft;
      y += element.offsetTop;
      element = element.offsetParent;
    } while (element);
    // Position blocklyDiv over blocklyArea.
    blocklyDiv.style.left = x + 'px';
    blocklyDiv.style.top = y + 'px';
    blocklyDiv.style.width = blocklyArea.offsetWidth + 'px';
    blocklyDiv.style.height = blocklyArea.offsetHeight + 'px';
  };
  window.addEventListener('resize', onresize, false);
  onresize();
}

//showCode : function() {
var showBlocklyCode = function() {
  // Generate JavaScript code and display it.
  Blockly.JavaScript.INFINITE_LOOP_TRAP = null;
  var code = Blockly.JavaScript.workspaceToCode(workspace);
  alert(code);
}

//displayCode : function() {
var displayBlocklyCode = function() {
  var code=runBlocklyCode();
  alert(code);
}

//runCode : function() {
var runBlocklyCode = function() {
  // Generate JavaScript code and run it.
  window.LoopTrap = 10000;	//TODO:  Will this cause problems for us?  Can increase if necessary.
  Blockly.JavaScript.INFINITE_LOOP_TRAP =
    'if (--window.LoopTrap == 0) throw "Infinite loop.";\n';
  Blockly.JavaScript.addReservedWords('code');
  Blockly.JavaScript.addReservedWords('json');
  var code = Blockly.JavaScript.workspaceToCode(workspace);
  Blockly.JavaScript.INFINITE_LOOP_TRAP = null;
  try {
    return eval(code);
  } catch (e) {
    alert(e);
  }
}

var uploadBlockly = function(url) {
  //console.log("trying to upload");
  var formData = {};
  //TODO: get player name out of page
  var playerName="spacdog";
  formData["playerName"]=playerName;

  var jsontext=runBlocklyCode();
  formData["jsontext"]=jsontext;
  //TODO: figure out how to get a representation of the blocks
  // that we can use to regenerate our blocks
  formData["sourcetext"]=jsontext;
  formData["language"]="blockly";
  formData["client"]="web";

  var xhttp=new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (xhttp.readyState == XMLHttpRequest.DONE) {
      alert(xhttp.responseText);
    }
  }
  // TODO: fix this URL
  // typical url is: http://localhost:8888/kctupload
  xhttp.open("POST", url, true);
  // pick a boundary character
  var boundary='Pqrfh'+Math.random().toString().substr(5);
  xhttp.setRequestHeader("content-type",
              'multipart/form-data; charset=utf-8; boundary=' + boundary);
  // create the multipart form element
  // I don't know how to make JS do this automatically without also using
  // JQuery and I want to test this without one more dependence
  var multipart="";
  for(var key in formData){
    multipart += "--" + boundary
             + '\r\nContent-Disposition: form-data; name="' + key +'"'
             //+ "\r\nContent-type: application/octet-stream"
             + "\r\n\r\n" + formData[key] + "\r\n";
  }
  multipart += "--"+boundary+"--\r\n\r\n";
  //console.log(multipart);
  //alert(multipart);
  xhttp.send(multipart);
}
