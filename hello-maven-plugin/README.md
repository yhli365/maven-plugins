# hello-maven-plugin
http://maven.apache.org/plugin-developers/index.html
http://maven.apache.org/guides/plugin/guide-java-plugin-development.html
http://maven.apache.org/developers/mojo-api-specification.html

# mvn
mvn eclipse:eclipse -DdownloadSources=true
mvn package

mvn dependency:sources

mvn help:effective-pom
mvn help:effective-settings

mvn help:describe -Dplugin=help
mvn help:describe -Dplugin=help -Ddetail
mvn help:describe -Dplugin=help -Dmojo=describe -Ddetail

# Shortening the Command Line
mvn hello:sayhi
a) vi src\test\resources\hello\pom.xml
b) vi settings.xml
<pluginGroups>
  <pluginGroup>yhli.maven.plugin</pluginGroup>
</pluginGroups>

# deploy
--local repo
mvn install

--nexus repo
<version>1.0-SNAPSHOT</version>
mvn deploy

<version>1.0</version>
mvn deploy

# Mojo archetype [optional]
mvn archetype:generate \
  -DgroupId=sample.plugin \
  -DartifactId=hello-maven-plugin \
  -Dversion=1.0 \
  -DarchetypeGroupId=org.apache.maven.archetypes \
  -DarchetypeArtifactId=maven-archetype-plugin

# Your First Mojo
mvn dependency:purge-local-repository -DmanualInclude="yhli.maven.plugin:hello-maven-plugin"
mvn help:describe -Dplugin=yhli.maven.plugin:hello-maven-plugin -Ddetail
mvn help:describe -Dplugin=hello -Ddetail

mvn yhli.maven.plugin:hello-maven-plugin:sayhi
mvn hello:sayhi

--Attaching the Mojo to the Build Lifecycle
cd src\test\resources\lifecycle
mvn compile

--Parameters
mvn hello:sayhi
mvn hello:echo
mvn hello:sayhi -Dgreeting=baby
mvn hello:sayhi -Dgreeting=error
mvn hello:sayhi -Dgreeting=fail

cd src\test\resources\parameters
mvn hello:sayhi
mvn hello:echo

# maven setting
mvn hello:config


