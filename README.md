Thucydides TeamCity StepListener
========================================

`Thucydides TeamCity StepListener` is extension for integration [Thucydides](http://thucydides.info/) and [TeamCity](http://www.jetbrains.com/teamcity/) continious integration server. Based on Thucydides's StepListener interface implementation and TeamCity's Service Messages providing fast and easy test reporting during build process.

Requirements
------------

* TeamCity 7.0+
* Thucydides

Installation
------------

Simply add a dependency to your project's pom file:

        <dependency>
            <groupId>com.github.crystalservice</groupId>
            <artifactId>thucydides-teamcity-steplistener</artifactId>
            <version>0.3</version>
            <exclusions>
                <exclusion>
                    <groupId>commons-lang</groupId>
                    <artifactId>commons-lang</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        
####JBehave:
        
Add a configuration parameter to your maven-failsafe-plugin:

        <disableXmlReport>true</disableXmlReport>
        
For example like this:

        <plugin>
                <artifactId>maven-failsafe-plugin</artifactId>
                <version>2.11</version>
                <configuration>
                    <disableXmlReport>true</disableXmlReport>
                </configuration>
        </plugin>            

####JUnit:

Add a configuration parameters to your maven-surefire-plugin:

        <disableXmlReport>true</disableXmlReport>
        <skip>false</skip>
        
        
For example like this:

        <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>2.12</version>
                <configuration>
                    <disableXmlReport>true</disableXmlReport>
                    <skip>false</skip>
                </configuration>
        </plugin>           

Documentation
-------------

####JBehave: 
Run with `mvn integration-test` command

####JUnit:
Run with `mvn test` command

####TeamCity:
Press "Run..." button in TeamCity.
Now build will display executed tests in realtime in "Overview" screen
