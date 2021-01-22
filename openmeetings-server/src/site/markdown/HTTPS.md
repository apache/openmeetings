<!--
Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0
-->

# Using OpenMeetings with HTTPS

## Using OpenMeetings with HTTPS

There are 2 ways the client communicates with the server:

1. Pure HTTP: all browser-server communications go through plain, unsecured HTTP

    This option can be used in case you will have frontend proxy to do encryption
2. HTTPS: all browser-server communications will be encrypted

### Self-signed certificate

This option is available out-of-the-box, just follow the URL: [https://localhost:5443/openmeetings](https://localhost:5443/openmeetings)

### 'Real' certificate
#### Prerequisites
1. You need OpenMeetings 5.0.x or later for this
1. Rename the existing keystore file `$OM_HOME/conf/keystore` to `$OM_HOME/conf/keystore.bak`

#### Create Keystore from the scratch
1. Create a new keystore and key, use the same password for both:

```
keytool -keysize 4096 -genkey -alias openmeetings -keyalg RSA -storetype PKCS12 -keystore $OM_HOME/conf/keystore
Enter keystore password:
Re-enter new password:
What is your first and last name?
[Unknown]:  <your hostname, e.g demo.openmeetings.de>
What is the name of your organizational unit?
[Unknown]:  Dev
What is the name of your organization?
[Unknown]:  OpenMeetings
What is the name of your City or Locality?
[Unknown]:  Henderson
What is the name of your State or Province?
[Unknown]:  Nevada
What is the two-letter country code for this unit?
[Unknown]:  US
Is CN=demo.openmeetings.de, OU=Dev, O=OpenMeetings, L=Henderson, ST=Nevada, C=US correct?
[no]:  yes
Enter key password for <openmeetings>
```

2. Generate a CSR: `keytool -certreq -keyalg RSA -alias openmeetings -file openmeetings.csr -keystore $OM_HOME/conf/keystore`
3. Submit CSR to your CA of choice and receive a signed certificate
4. Import your chosen CA's root certificate into the keystore (may need to download it from their site - make sure to get the root CA and not the intermediate one): `keytool -import -alias root -keystore $OM_HOME/conf/keystore -trustcacerts -file root.crt` (NOTE: you may receive a warning that the certificate already exists in the system wide keystore - import anyway)
5. Import the intermediate certificate(s) you normally receive with the certificate: `keytool -import -alias intermed -keystore $OM_HOME/conf/keystore -trustcacerts -file intermediate.crt`
6. Import the certificate you received: `keytool -import -alias openmeetings -keystore $OM_HOME/conf/keystore -trustcacerts -file demo.openmeetings.de.crt`

### Create Keystore using existing key-pair
#### Prerequisites
- Server key: openmeetings.key
- Signed CSR: openmeetings.crt
- CA's root certificate: root.crt
- ** Intermediate certificate(s): intermedXX.crt

#### Steps
1. Export existing keys into PKCS12 format:

```
openssl pkcs12 -export -in openmeetings.crt -inkey openmeetings.key -out openmeetings.p12 -name openmeetings -certfile root.crt -certfile intermedXX.crt`

Enter Export Password: openmeetings
Verifying - Enter Export Password: openmeetings
```
2. Import resulting openmeetings.p12 into keystore: `keytool -importkeystore -srcstorepass openmeetings -srckeystore openmeetings.p12 -srcstoretype PKCS12 -deststorepass password -destkeystore $OM_HOME/conf/keystore -alias openmeetings -deststoretype PKCS12`
3. Import your chosen CA's root certificate into the keystore (may need to download it from their site - make sure to get the root CA and not the intermediate one): `keytool -import -alias root -keystore $OM_HOME/conf/keystore -trustcacerts -file root.crt` (note: you may receive a warning that the certificate already exists in the system wide keystore - import anyway)
4. Import the intermediate certificate(s) you normally receive with the certificate: `keytool -import -alias intermed -keystore $OM_HOME/conf/keystore -trustcacerts -file intermedXX.crt`

### More configuration options

Additional info on HTTPS configuration can be found at [Apache Tomcat site](https://tomcat.apache.org/tomcat-9.0-doc/ssl-howto.html)
