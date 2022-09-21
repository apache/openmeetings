<!--
Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0
-->
# Issues with mail sending

#### Q: Email messages not getting out of the openmeetings server
A: If you change `mail.smtp.port` from the default (25) you may have to add a permission for your port to the catalina securty policy e.g.

```
Add the following section to ${OM_HOME}/conf/catalina.policy.

// Permissions for jakarta mail
grant {
    permission java.net.SocketPermission
                "SMTPHOST:SMTPPORT", "connect,resolve";
};
```

