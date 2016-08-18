#!/bin/bash
# TODO: use gradle to build knoxcraft.github.io
browserify gameScript.js -o bundle.js
pushd .
cd ../blockly && ./build.sh
popd
cp ../blockly/blockly-bundle.js blockly
