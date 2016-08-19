#!/bin/bash

LOCATION=../../knoxcraft.github.io
OS=`uname`

datestr=`date +"%Y-%m-%d-%H-%M-%S"`
echo $datestr

# on Linux, use cp -a
CMD="cp -a"
if [ "$OS" = "Darwin" ]; then
    # on OSX, use ditto
    CMD=ditto
fi

echo "browserify gameScript.js -o bundle.js"
browserify gameScript.js -o bundle.js

for f in textures images index.html ace-builds bundle.js blockly-bundle.js java ; do
    echo "updating $LOCATION/$f, if necessary"
    $CMD $f $LOCATION/$f
done


pushd .
cd $LOCATION
git add .
git commit -m "posting new version at $datestr"
git push origin master
popd

