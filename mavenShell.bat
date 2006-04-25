@echo "Starting maven"
@echo "%PROJECT_HOME%\ejb3unit"
@echo off
@REM Set this variable to your project root

cd "%PROJECT_HOME%\ejb3unit"

@REM Reaching here means variables are defined and arguments have been captured
:endInit
SET MAVEN_OPTS="-Xmx256m"
SET MAVEN_JAVA_EXE="%JAVA_HOME%\bin\java.exe"
SET MAVEN_CLASSPATH="%MAVEN_HOME%\lib\forehead-1.0-beta-5.jar"
SET MAVEN_MAIN_CLASS="com.werken.forehead.Forehead"
SET MAVEN_CMD_LINE_ARGS="console"

@REM Start MAVEN without MAVEN_HOME_LOCAL override
%MAVEN_JAVA_EXE% "-Dmaven.home=%MAVEN_HOME%" "-Dtools.jar=%JAVA_HOME%\lib\tools.jar" "-Dforehead.conf.file=%MAVEN_HOME%\bin\forehead.conf" %MAVEN_OPTS% -classpath %MAVEN_CLASSPATH% %MAVEN_MAIN_CLASS% %MAVEN_CMD_LINE_ARGS%
pause