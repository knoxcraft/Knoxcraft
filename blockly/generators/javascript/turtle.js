/**
*  Code generators for Knoxcraft Turtles
*/

Blockly.JavaScript['turtle_init'] = function(block) {
  var text_name = block.getFieldValue('name');
  var statements_script = Blockly.JavaScript.statementToCode(block, 'script');
  // TODO: Assemble JavaScript into code variable.
  var code = '...';
  return code;
};

Blockly.JavaScript['turtle_move'] = function(block) {
  var text_dist = block.getFieldValue('dist');
  var dropdown_dir = block.getFieldValue('dir');
  // TODO: Assemble JavaScript into code variable.
  var code = '...';
  return code;
};

Blockly.JavaScript['turtle_turn'] = function(block) {
  var text_deg = block.getFieldValue('deg');
  var dropdown_turndir = block.getFieldValue('turnDir');
  // TODO: Assemble JavaScript into code variable.
  var code = '...';
  return code;
};

Blockly.JavaScript['turtle_setblockplace'] = function(block) {
  var dropdown_mode = block.getFieldValue('mode');
  // TODO: Assemble JavaScript into code variable.
  var code = '...';
  return code;
};

Blockly.JavaScript['turtle_setblocktype'] = function(block) {
  var text_type = block.getFieldValue('type');
  // TODO: Assemble JavaScript into code variable.
  var code = '...';
  return code;
};

Blockly.JavaScript['turtle_setpos'] = function(block) {
  var text_x = block.getFieldValue('X');
  var text_y = block.getFieldValue('Y');
  var text_z = block.getFieldValue('Z');
  // TODO: Assemble JavaScript into code variable.
  var code = '...';
  return code;
};

Blockly.JavaScript['turtle_setdir'] = function(block) {
  var dropdown_dir = block.getFieldValue('dir');
  // TODO: Assemble JavaScript into code variable.
  var code = '...';
  return code;
};