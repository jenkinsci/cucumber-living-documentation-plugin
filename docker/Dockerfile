FROM jenkins/jenkins:2.73.1
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false -Dhudson.model.DirectoryBrowserSupport.CSP=\"\""
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt