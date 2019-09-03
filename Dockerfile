#
# Dockerfile for test automation
# Based on Ubuntu
#

# Pull base image
FROM ubuntu:18.04

# File Author / Maintainer
MAINTAINER Oscar Calderin (ocalderin14@gmail.com)

# Update and upgrade repository sources list
RUN apt-get update && \
    apt-get install -y default-jdk maven

# Set environment variables.
ENV HOME /root
ENV HUB_TCP_ADDR http://selenium-hub
ENV HUB_TCP_PORT 4444

# Define working directory.
WORKDIR /root/testAutomation

# Adding sources
ADD src /root/testAutomation/src
ADD execute_test.sh /root/testAutomation

# Prepare by downloading dependencies
ADD pom.xml /root/testAutomation/pom.xml
RUN ["mvn", "dependency:resolve"]

# Creating folder for reports
RUN mkdir -p /root/testAutomation/reports && \
    chmod +x /root/testAutomation/execute_test.sh

# Adding volume for reports
VOLUME ["/root/testAutomation/reports"]

# Execute tests
ENTRYPOINT ["/root/testAutomation/execute_test.sh"]