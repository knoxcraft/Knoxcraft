#!/bin/bash

browserify gameScript.js --stand-alone knoxeljs > bundle.js
mkdir -p pykc
# TODO: use ditt/cp -a
cp ../pykc/pykcbase.py pykc
# TODO: only replace when there is a newer version on the server
wget http://www.skulpt.org/static/skulpt.min.js -O pykc/skulpt.min.js
wget http://www.skulpt.org/static/skulpt-stdlib.js -O pykc/skulpt-stdlib.js
pushd .
cd ../blockly && ./build.sh
popd
cp ../blockly/blockly-bundle.js .
