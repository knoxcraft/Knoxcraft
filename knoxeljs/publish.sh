#!/bin/bash

LOCATION=../../knoxcraft.github.io
OS=`uname`

# on Linux, use cp -a
CMD="cp -a"
if [ "$OS" = "Darwin" ]; then
    # on OSX, use ditto
    CMD=ditto
fi

echo "browserify gameScript.js -o bundle.js"
browserify gameScript.js -o bundle.js

for f in textures css images ace-builds bundle.js blockly-bundle.js java pykc ; do
    echo "updating $LOCATION/$f, if necessary"
    $CMD $f $LOCATION/$f
done

datestr=`date +"%Y-%m-%d-%H-%M-%S"`
echo $datestr

# update the timestamp in index.html
sed "s/<\!-- hhmts start --><\!-- hhmts end -->/<\!-- hhmts start -->Last updated `date` ($datestr)<\!-- hhmts end -->/" index.html > $LOCATION/index.html

pushd .
cd $LOCATION
git add .
git commit -m "posting new version at $datestr"
git push origin master
popd

