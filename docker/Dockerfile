FROM jenkins:2.73.1
COPY plugins.txt /usr/share/jenkins/plugins.txt
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false -Dhudson.model.DirectoryBrowserSupport.CSP=\"\""
RUN /usr/local/bin/plugins.sh /usr/share/jenkins/plugins.txt