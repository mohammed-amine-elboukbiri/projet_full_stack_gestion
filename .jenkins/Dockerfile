FROM jenkins/jenkins:lts-jdk21

USER root
RUN apt-get update && apt-get install -y curl git && rm -rf /var/lib/apt/lists/*
USER jenkins

COPY .jenkins/plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt

COPY .jenkins/init.groovy.d/ /usr/share/jenkins/ref/init.groovy.d/
