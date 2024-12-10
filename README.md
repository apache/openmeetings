## Apache OpenMeetings

![About Openmeetings Logo](/openmeetings-server/src/site/resources/images/logo.png)

[Apache OpenMeetings](https://openmeetings.apache.org) provides:
 - [x] **video conferencing**
 - [x] **instant messaging**
 - [x] **white board**
 - [x] **collaborative document editing**
 - [x] **other groupware tools**

It uses API functions of Media Server for Remoting and Streaming [Kurento](https://www.kurento.org)).

### Getting Started
For the latest information, please visit the project website:
  - [OpenMeetings Website](https://openmeetings.apache.org/)

Documentation for installation and upgrade can be found at:
  - [Installation Guide](https://openmeetings.apache.org/installation.html)
  - [Upgrade Instructions](https://openmeetings.apache.org/Upgrade.html)

All available mailing lists are listed here:
  - [Mailing Lists](https://openmeetings.apache.org/mailing-lists.html)

### Prerequisites
Apache OpenMeetings requires the following software to run:
- [Java SE 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Apache Maven 3.8.7 or later](https://maven.apache.org/)

### System Requirements
You need a platform that supports Java SE 17.

### Installation Steps
To build and run the project from source:
1. Ensure the prerequisites are installed.
2. Build the project using Maven in the root directory:
   ```
   mvn clean install -PallModules
   ```
3. To run OpenMeetings:
   - Navigate to `openmeetings-server/target` directory.
   - Extract `apache-openmeetings-x.x.x.tar.gz` (or `apache-openmeetings-x.x.x.zip` for Windows).
   - Enter the `apache-openmeetings-x.x.x` directory and execute the startup script:
     ```
     ./bin/startup.sh
     ```
     (Use `./bin/startup.bat` for Windows.)
4. Detailed build documentation: [Build Instructions](https://openmeetings.apache.org/BuildInstructions.html)

### Builds and CI
| Description         | Jenkins CI                                                                                                                                                         |
|---------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Master nightly      | [![Build Status](https://ci-builds.apache.org/job/OpenMeetings/job/openmeetings/badge/icon)](https://ci-builds.apache.org/job/OpenMeetings/job/openmeetings/)       |
| Master Pull Request | [![Build Status](https://ci-builds.apache.org/job/OpenMeetings/job/openmeetings-pr-build/badge/icon)](https://ci-builds.apache.org/job/OpenMeetings/job/openmeetings-pr-build/) |

### Release Notes
See the [CHANGELOG.md](/CHANGELOG.md) file for a detailed log.

Recent Releases:
- **7.2.0**: [Release 7.2.0](https://www.apache.org/dyn/closer.lua/openmeetings/7.2.0) - Java 17 and KMS 6.18.0+ required. Includes security, UI, and other improvements.
- **7.1.0**: [Release 7.1.0](https://archive.apache.org/dist/openmeetings/7.1.0) - Various security updates and stability fixes.
- **7.0.0**: [Release 7.0.0](https://archive.apache.org/dist/openmeetings/7.0.0) - Improved UI, 2-factor authentication, and more.

### License
Apache OpenMeetings is licensed under the Apache License 2.0. For more details, see [LICENSE](http://www.apache.org/licenses/LICENSE-2.0).

This product also includes third-party software. See more at [NOTICE.md](/NOTICE.md).
