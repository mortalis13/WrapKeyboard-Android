@echo off
rem Generate Eclipse classpath based on Gradle dependencies

call gradle androidEclipse cleanEclipseClasspath eclipse
pause
