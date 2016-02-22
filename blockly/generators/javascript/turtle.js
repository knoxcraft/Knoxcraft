/**
*  Code generators for Knoxcraft Turtles
*/

//TODO:  Do we want these to just produce json, or to be print statments and then run the code?  (for loops, ifs, etc?)
//TODO:  Update:  yes.  We need to print and run the code, because otherwise the loops mess up the json.

Blockly.JavaScript['turtle_init'] = function(block) {
  var text_name = block.getFieldValue('name');
  var statements_script = Blockly.JavaScript.statementToCode(block, 'script');
  
  //remove trailing comma
  if (statements_script.endsWith(',\n'))  {
	  statements_script = statements_script.substring(0, statements_script.length-2) + '\n';
  }
  
  var code = '{ \n' + Blockly.JavaScript.INDENT + '"scriptname" : "' + text_name + 
			'",\n' + Blockly.JavaScript.INDENT + '"commands" : [\n' + statements_script + Blockly.JavaScript.INDENT + ']\n}';
  
  return code;
};

Blockly.JavaScript['turtle_move'] = function(block) {
  var text_dist = block.getFieldValue('dist');
  var dropdown_dir = block.getFieldValue('dir');
  var code = Blockly.JavaScript.INDENT + '{"cmd" : "' + dropdown_dir.toLowerCase() + '", \n' + Blockly.JavaScript.INDENT +  Blockly.JavaScript.INDENT + '"args" : {"dist" : ' + text_dist + '}},\n';
  return code;
};

Blockly.JavaScript['turtle_turn'] = function(block) {
  var text_deg = block.getFieldValue('deg');
  var dropdown_turndir = block.getFieldValue('turnDir');
  var code = '';
  if (dropdown_turndir == 'LEFT')  {
	code = Blockly.JavaScript.INDENT + '{"cmd" : "turnLeft", \n' + Blockly.JavaScript.INDENT + Blockly.JavaScript.INDENT + '"args" : {"degrees" : ' + text_deg + '}},\n';
  }  else {
	code = Blockly.JavaScript.INDENT + '{"cmd" : "turnRight", \n' + Blockly.JavaScript.INDENT + Blockly.JavaScript.INDENT + '"args" : {"degrees" : ' + text_deg + '}},\n';
  }
  return code;
};

Blockly.JavaScript['turtle_setblockplace'] = function(block) {
  var dropdown_mode = block.getFieldValue('mode');
  var code = Blockly.JavaScript.INDENT + '{"cmd" : "setBlockPlace", \n' + Blockly.JavaScript.INDENT + Blockly.JavaScript.INDENT + '"args" : {"mode" : "' + dropdown_mode.toLowerCase() + '"}},\n';
  return code;
};

Blockly.JavaScript['turtle_setblocktype'] = function(block) {
  var text_type = block.getFieldValue('type');
  var code = Blockly.JavaScript.INDENT + '{"cmd" : "setBlock", \n' + Blockly.JavaScript.INDENT + Blockly.JavaScript.INDENT + '"args" : {"type" : ' + text_type + '}},\n';
  return code;
};

Blockly.JavaScript['turtle_setblocktype2'] = function(block) {
  var dropdown_type = block.getFieldValue('type');
  var code = Blockly.JavaScript.INDENT + '{"cmd" : "setBlock", \n' + Blockly.JavaScript.INDENT + Blockly.JavaScript.INDENT + '"args" : {"type" : ' + dropdown_type + '}},\n';
  return code;
};

Blockly.JavaScript['turtle_setpos'] = function(block) {
  var text_x = block.getFieldValue('X');
  var text_y = block.getFieldValue('Y');
  var text_z = block.getFieldValue('Z');
  var code = Blockly.JavaScript.INDENT + '{"cmd" : "setPosition", \n' + Blockly.JavaScript.INDENT + Blockly.JavaScript.INDENT + '"args" : {"x" : ' + text_x + ', "y" : ' + text_y + ', "z": ' + text_z + '}},\n';
  return code;
};

Blockly.JavaScript['turtle_setdir'] = function(block) {
  var dropdown_dir = block.getFieldValue('dir');
  // TODO: Is this the right json for directions?
  var code = Blockly.JavaScript.INDENT + '{"cmd" : "setDirection", \n' + Blockly.JavaScript.INDENT + Blockly.JavaScript.INDENT + '"args" : {"dir" : "' + dropdown_dir + '"}},\n';
  return code;
};