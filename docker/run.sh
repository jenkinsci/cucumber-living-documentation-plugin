#!/bin/bash
docker run --name jenkins-living-documentation -p 8080:8080 -v /var/jenkins_home rmpestano/jenkins-living-documentation
