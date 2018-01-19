# Spring Boot Artemis Examples

This is a project hosting Artemis examples using Spring tooling. These are very simple examples loosely based on the provided examples in the Artemis project, in the [examples folder](https://github.com/apache/activemq-artemis/tree/master/examples).

Instead of using the manual (e.g. plumbing) setup in the examples, the goal of this project is to provided simple and ([Spring](https://projects.spring.io/spring-boot/)) autoconfigured solutions to these examples using Spring.

## Setup

You first first need to clone this project onto their machine using `git clone https://github.com/dshunfen/spring-boot-artemis-examples.git`.

Each example can be run using the `mvn verify` command.

This can be done on the command line itself, with STS, or via another IDE:

### STS
One can import this project into their local Eclipse/STS IDE workspace. That is, ideally in Spring Tool Suite (STS).

This can be done by [downloading and installing STS](https://spring.io/tools/sts/all):

1. Click `File > Import`
2. Type Maven in the search box under `Select an import source`:
3. Select `Existing Maven Projects`
4. Click `Next`
5. Click `Browse`
6. Navigate to the folder that is the root of the Maven project containing the `pom.xml` file
7. Click `Next`
8. Click `Finish`