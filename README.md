# dependency-injection
# Need to achieve following targets to create a dependency injection framework:
1. Locating classes
	- scanning jar and directories
2. Mapping classes and their dependencies
3. Instantiating services
4. Create dependency container



# Design Considerations
There are several components to consider as below:
1. Injector
    - main class
2. Configuration
    - to change configuration during starting Injector
3. ScanningService
    - to scan all annotated classes
4. ServiceInstantiationService
    - to instantiate scanned classes (refer to #3)
5. ObjectInstantiationService
    - to instantiate instance for services (refer to #4)
6. DependencyContainer
    - to manage instances as such creation, retrieve, reload, and destroy



##### DEPLOYMENT #####
# To package into JAR file
mvn clean package

# To deploy into local repository
mvn install:install-file -Dfile=.\target\dependency-injection-1.0-SNAPSHOT.jar -DgroupId=com.truongmg -DartifactId=dependency-injection -Dversion=1.0-SNAPSHOT -Dpackaging=jar

# How to use
1. Add library "dependency-injection-1.0-SNAPSHOT.jar" directly or import into project
2. Annotate class with provided annotations in library
3. Run Injector as below:
    MyInject.run(<Main class>)



##### Testing #####
Refer to "test\java\com\truongmg\di\MyInjectorTest.java"


##### Limitation #####
1. If more than one bean of the same type is available in the container, the framework is not able to identify ambiguous class and throw expection
    - to eliminate the issue of which bean needs to be injected, need to support Qualifier annotation

2. Not able to Autowired fields, only constructor is supported.

3. Scope Annotation is not supported yet.
