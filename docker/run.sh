#!/bin/bash
docker run --name jenkins-living-documentation -p 8280:8080 -v /var/jenkins_home jenkins-living-documentation
