# Troubleshooting Guide for Java Chat Application

## ✅ Quick Fix Steps

### 1. **Compilation Issues**
```bash
# Clean and recompile everything
cd "d:\College Folder\GH Raisoni\SEMESTER V\DBMS\Java Mysql project"
rmdir /s build
.\compile.bat
```

### 2. **Database Connection Issues**
```bash
# Test database setup
.\run_chat.bat
# or manually:
java -cp "Backend\lib\mysql-connector-j-9.4.0.jar;build" SimpleDBSetup
```

### 3. **Application Won't Start**
```bash
# Use the robust run script
.\run_chat.bat
```

## 🔧 Common Problems & Solutions

### **Problem: "MySQL JDBC Driver not found"**
**Solution:**
- Check that `Backend\lib\mysql-connector-j-9.4.0.jar` exists
- Make sure you're running from the project root directory

### **Problem: "Database connection failed"**
**Solution:**
1. **Check MySQL Service:**
   ```bash
   # Windows: Check if MySQL is running
   services.msc
   # Look for MySQL service and start it if stopped
   ```

2. **Verify Credentials:**
   - Open `Backend\src\backend\database\DatabaseManager.java`
   - Check if username/password match your MySQL setup:
   ```java
   private static final String DB_USER = "root";
   private static final String DB_PASSWORD = "VT#Root2026"; // Your password here
   ```

3. **Test Connection:**
   ```bash
   mysql -u root -p
   # Enter your MySQL password when prompted
   ```

### **Problem: "Unknown database 'chat_app'"**
**Solution:**
- The application should auto-create the database, but you can manually create it:
```sql
CREATE DATABASE chat_app;
```

### **Problem: "Exit Code 1" in VS Code**
**Solution:**
- Use PowerShell terminal instead of VS Code integrated terminal
- Or use the batch files: `.\run_chat.bat`

### **Problem: Login fails with correct credentials**
**Solution:**
- Run the database setup to ensure sample users exist:
```bash
java -cp "Backend\lib\mysql-connector-j-9.4.0.jar;build" SimpleDBSetup
```

## 📋 **Default Login Credentials**
| Username | Password | Role |
|----------|----------|------|
| admin | password123 | Administrator |
| john_doe | password123 | User |
| jane_smith | password123 | User |

## 🚀 **Running the Application**

### **Method 1: Use Batch Script (Recommended)**
```bash
.\run_chat.bat
```

### **Method 2: Manual Command**
```bash
java -cp "Backend\lib\mysql-connector-j-9.4.0.jar;build" SimpleChatApp
```

### **Method 3: Database Setup Only**
```bash
java -cp "Backend\lib\mysql-connector-j-9.4.0.jar;build" SimpleDBSetup
```

## 🔍 **Verification Steps**

1. **Check Compilation:**
   ```bash
   dir build
   # Should show: SimpleChatApp.class, backend folder, etc.
   ```

2. **Test Database:**
   ```bash
   # Should show "Database connection successful!"
   java -cp "Backend\lib\mysql-connector-j-9.4.0.jar;build" SimpleDBSetup
   ```

3. **Verify MySQL:**
   ```sql
   USE chat_app;
   SELECT * FROM users;
   SELECT * FROM chat_rooms;
   ```

## 📞 **Still Having Issues?**

1. **Check File Structure:**
   ```
   Java Mysql project/
   ├── Backend/lib/mysql-connector-j-9.4.0.jar ✓
   ├── build/SimpleChatApp.class ✓
   ├── run_chat.bat ✓
   └── compile.bat ✓
   ```

2. **Environment Check:**
   - Java 8+ installed ✓
   - MySQL Server running ✓
   - Correct working directory ✓

3. **Reset Everything:**
   ```bash
   # Delete database and start fresh
   mysql -u root -p -e "DROP DATABASE IF EXISTS chat_app;"
   
   # Clean and rebuild
   rmdir /s build
   .\compile.bat
   .\run_chat.bat
   ```

## ✨ **Success Indicators**

When everything works correctly, you should see:
- ✅ "Connected to MySQL database successfully!"
- ✅ Login window appears
- ✅ Can login with test credentials
- ✅ Can send and receive messages
- ✅ Messages persist in database