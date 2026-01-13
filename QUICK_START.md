# Quick Start Guide - Without Maven Installed

Since Maven is not installed on your system, here are **3 ways** to run the application:

---

## ✅ Option 1: Use Maven Wrapper (Recommended)

Maven wrapper is now included in the project. Use `mvnw.cmd` instead of `mvn`:

```powershell
# Run the application
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local

# Or build first, then run
.\mvnw.cmd clean package
java -jar target\university-management-1.0.0.jar --spring.profiles.active=local
```

**First time it runs, it will automatically download Maven** - this is normal and only happens once.

---

## ✅ Option 2: Run from IDE (Easiest)

### Using VS Code:
1. Install "Spring Boot Extension Pack" from Extensions
2. Open the project folder
3. Press `F5` or click "Run" → "Start Debugging"
4. Or right-click `UniversityManagementApplication.java` → "Run Java"

### Using IntelliJ IDEA:
1. Open the project (it will auto-import Maven dependencies)
2. Right-click on `UniversityManagementApplication.java`
3. Select "Run 'UniversityManagementApplication.main()'"
4. Add VM options: `-Dspring.profiles.active=local`

### Using Eclipse:
1. Import project: File → Import → Maven → Existing Maven Projects
2. Right-click on project → Run As → Spring Boot App
3. Edit configuration to add: `-Dspring.profiles.active=local`

---

## ✅ Option 3: Install Maven (One-time setup)

### Using Chocolatey (Recommended for Windows):
```powershell
# Install Chocolatey if not installed (run as Administrator)
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Install Maven
choco install maven

# Verify installation
mvn --version
```

### Manual Installation:
1. Download Maven from: https://maven.apache.org/download.cgi
2. Extract to `C:\Program Files\Apache\maven`
3. Add to PATH:
   - Open "Environment Variables"
   - Add `C:\Program Files\Apache\maven\bin` to System PATH
   - Restart PowerShell
4. Verify: `mvn --version`

---

## 🚀 Quick Start (Maven Wrapper)

```powershell
# Navigate to project directory
cd C:\Users\Admin\Documents\WebProject

# Run application (will auto-download Maven first time)
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local

# Application will start on http://localhost:8080
```

**Demo Accounts:**
- Admin: `admin@uniflow.com` / `admin123`
- Teacher: `teacher@uniflow.com` / `teacher123`
- Student: `student@uniflow.com` / `student123`

---

## ⚠️ Troubleshooting

### Issue: "Cannot find JAVA_HOME"
**Solution:** Install Java 17 or set JAVA_HOME:
```powershell
# Check if Java is installed
java -version

# If not, install with Chocolatey
choco install openjdk17

# Or set JAVA_HOME manually
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
```

### Issue: Maven wrapper fails to download
**Solution:** Check internet connection or download manually:
```powershell
# Create directory
New-Item -ItemType Directory -Force -Path .mvn\wrapper

# Download wrapper jar
Invoke-WebRequest -Uri "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar" -OutFile ".mvn\wrapper\maven-wrapper.jar"
```

### Issue: Port 8080 already in use
**Solution:** Change port in application-local.properties:
```properties
server.port=8081
```

---

## 📝 Next Steps

After starting the application:
1. Open browser: http://localhost:8080
2. Login with demo account
3. Explore the features!

For more details, see the main [README.md](README.md)
