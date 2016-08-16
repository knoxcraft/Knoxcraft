#!/bin/bash
# TODO: replace with package.json or gradle
cat ./blockly_compressed.js ./blocks_compressed.js \
  ./javascript_compressed.js ./msg/js/en.js \
  ./blocks/turtle.js ./generators/javascript/turtle.js \
  ./blockly.js > blockly-bundle.js
#minify
