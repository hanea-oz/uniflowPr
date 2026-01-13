@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%

echo Starting University Management System...
echo.
echo Once started, open your browser to: http://localhost:8080
echo.
echo Demo accounts:
echo   Admin: admin@uniflow.com / admin123
echo   Teacher: teacher@uniflow.com / teacher123
echo   Student: student@uniflow.com / student123
echo.
echo Press Ctrl+C to stop the application
echo.

mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
