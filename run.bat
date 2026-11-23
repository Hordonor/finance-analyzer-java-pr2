@echo off
setlocal
chcp 65001 >nul
set MAVEN_OPTS=-Dfile.encoding=UTF-8
call mvnw.cmd clean test
if errorlevel 1 exit /b %errorlevel%
call mvnw.cmd exec:java
