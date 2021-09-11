# Run NPM install on each of the packages (only required once unless you change dependency):
# mvn frontend:npm@main-install frontend:npm@chat-install frontend:npm@settings-install frontend:npm@room-install frontend:npm@wb-install
# Run NPM 
mvn frontend:npm@main frontend:npm@chat frontend:npm@settings frontend:npm@room frontend:npm@wb
# Minify CSS
mvn minify:minify@theme-minify minify:minify@nettest-js
# Copy to destination, in this case running OpenMeetings using the embedded Jetty, see above
export CURRENT_DIR=$(pwd)
rsync -a $CURRENT_DIR/target/generated-sources/js/ $CURRENT_DIR/target/openmeetings-web-7.0.0-SNAPSHOT/WEB-INF/classes/
