/**
*  Code generators for Knoxcraft Turtles
*/

Blockly.JavaScript['turtle_init'] = function(block) {
  var text_name = block.getFieldValue('name');
  var statements_script = Blockly.JavaScript.statementToCode(block, 'script');
			
  var code = 'var json = \'\';\n';
  code += 'json += \'{\\n     "scriptname" : "' + text_name + '",\\n     "commands" : [\\n\';';
  code += statements_script; 
  
  //remove trailing comma
  code += 'if (json.endsWith(\',\\n\'))  {';
  code += '     json = json.substring(0, json.length-2) + \'\\n\';\n';
  code += '}';
  
  code += 'json += \'     ]\\n}\';\n';
	
  code += 'alert(json);\n'; //display for testing
			
  return code;
};

Blockly.JavaScript['turtle_move'] = function(block) {
  var text_dist = block.getFieldValue('dist');
  var dropdown_dir = block.getFieldValue('dir');
  var code = 'json += \'     {"cmd" : "' + dropdown_dir.toLowerCase() + '", \\n          "args" : {"dist" : ' + text_dist + '}},\\n\';\n';
  
  return code;
};

Blockly.JavaScript['turtle_turn'] = function(block) {
  var text_deg = block.getFieldValue('deg');
  var dropdown_turndir = block.getFieldValue('turnDir');
  var code = '';
  if (dropdown_turndir == 'LEFT')  {
	code = 'json += \'     {"cmd" : "turnLeft", \\n          "args" : {"degrees" : ' + text_deg + '}},\\n\';\n';
  }  else {
	code = 'json += \'     {"cmd" : "turnRight", \\n          "args" : {"degrees" : ' + text_deg + '}},\\n\';\n';
  }
  return code;
};

Blockly.JavaScript['turtle_setblockplace'] = function(block) {
  var dropdown_mode = block.getFieldValue('mode');
  var code = 'json += \'     {"cmd" : "setBlockPlace", \\n          "args" : {"mode" : "' + dropdown_mode.toLowerCase() + '"}},\\n\';\n';
  return code;
};

Blockly.JavaScript['turtle_setblocktype'] = function(block) {
  var text_type = block.getFieldValue('type');
  var code = 'json += \'     {"cmd" : "setBlock", \\n          "args" : {"type" : ' + text_type + '}},\\n\';\n';
  return code;
};

Blockly.JavaScript['turtle_setblocktype2'] = function(block) {
  var dropdown_type = block.getFieldValue('type');
  var code = 'json += \'     {"cmd" : "setBlock", \\n          "args" : {"type" : ' + dropdown_type + '}},\\n\';\n';
  return code;
};

Blockly.JavaScript['turtle_setpos'] = function(block) {
  var text_x = block.getFieldValue('X');
  var text_y = block.getFieldValue('Y');
  var text_z = block.getFieldValue('Z');
  var code = 'json += \'     {"cmd" : "setPosition", \\n          "args" : {"x" : ' + text_x + ', "y" : ' + text_y + ', "z": ' + text_z + '}},\\n\';\n';
  return code;
};

Blockly.JavaScript['turtle_setdir'] = function(block) {
  var dropdown_dir = block.getFieldValue('dir');
  // TODO: Is this the right json for directions?  Or does it need to be converted to an int?  I can't find it in our code anywhere.
  var code = 'json += \'     {"cmd" : "setDirection", \\n          "args" : {"dir" : "' + dropdown_dir + '"}},\\n\';\n';
  return code;
};