yrepo-maven-plugin

# mvn
mvn clean
mvn eclipse:clean
mvn eclipse:eclipse -DdownloadSources=true
mvn install

# Mojo Test
mvn help:describe -Dplugin=yrepo -Ddetail
mvn yrepo:eclipse

