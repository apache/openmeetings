<!--
Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0
-->

# Customize OpenMeetings logo

If you want to set up your logo in Openmeetings header you can do the following:

1. Create your logo image as PNG 40px height
2. Replace `/opt/om/webapps/openmeetings/css/images/logo.png` with your logo file

# Customize OpenMeetings mobile icons

_This is available in OpenMeetings 6.2.0 and later_

**First: Create the mobile icons assets. This can be done via a script**

See also: [Github: pwa-asset-generator](https://github.com/onderceylan/pwa-asset-generator)

1. Install the generation script: `npm install --global pwa-asset-generator`
2. Locate your logo.svg (svg as input recommended) and place into an empty folder
3. Generate images and HTML using: `pwa-asset-generator logo.svg`

**Second: Copy icons to relevant dir**

The command above will generate all assets, place (and replace all) those into `$OM_HOME/webapps/openmeetings/images/icons`

You may need to clear your mobile device browser cache to see icons refresh.