# Digital Diary Management System

A Java 17 Swing and JDBC course project for writing personal diary entries, searching them, viewing mood statistics, exporting entries to text files, and backing up diary data.

The project uses plain `javac` and `java`.

## Features

- User registration and login
- Add, edit and delete diary entries
- Personal, Work and Travel entry types
- Search entries by keyword or exact date
- Mood statistics using `HashMap`
- Entries grouped by date using a `TreeMap`
- Sorting with a separate `Comparator` class
- Export one entry to a text file
- Import an entry from a text file
- Backup all entries to a binary file and restore them
- Live date and time label using `ClockThread`
- Draft auto-save every 30 seconds using `AutoSaveThread`
- Ctrl+S saves the entry dialog
- Double-clicking a table row opens that entry

## Project Structure

```text
JAVA_CEP/
├── Main.java
├── schema.sql
├── README.md
├── lib/
├── model/
│   ├── Entry.java
│   ├── PersonalEntry.java
│   ├── WorkEntry.java
│   ├── TravelEntry.java
│   └── User.java
├── dao/
│   ├── DiaryOperations.java
│   ├── EntryDAO.java
│   └── UserDAO.java
├── exception/
│   ├── DuplicateUserException.java
│   ├── EmptyEntryException.java
│   └── InvalidLoginException.java
├── thread/
│   ├── AutoSaveThread.java
│   └── ClockThread.java
├── ui/
│   ├── DashboardPanel.java
│   ├── EntryDialog.java
│   ├── LoginPanel.java
│   ├── MainFrame.java
│   └── RegisterPanel.java
└── util/
    ├── CollectionUtil.java
    ├── DBConnection.java
    ├── EntryDateComparator.java
    └── FileUtil.java
```

The `drafts/` folder is created automatically when auto-save runs for the first time.

## 1. Create the Database

Install MySQL 8 and start the MySQL server.

Open a terminal in the project folder and run:

```powershell
mysql -u root -p < schema.sql
```

You can also open `schema.sql` in MySQL Workbench and execute the whole file.

This creates:

- Database: `digital_diary`
- Table: `users`
- Table: `entries`
- Foreign key from `entries.user_id` to `users.user_id`

### Set the Database Password

Open `util/DBConnection.java` and change this line to your own MySQL password:

```java
private static final String PASSWORD = "your_password";
```

The default username is `root`. Change `USERNAME` in the same file if your MySQL user is different.

## 2. Add the MySQL JDBC Driver

Download the MySQL Connector/J 8.x JAR from the official MySQL website.

Create the `lib` folder if it is missing, then place the JAR inside it. Example:

```text
lib/mysql-connector-j-8.0.33.jar
```

The exact version in your filename may be different. Use your actual filename in the commands below.

## 3. Compile the Project

Run these commands from the project root in Windows PowerShell:

```powershell
Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force out | Out-Null

$files = Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac --release 17 -cp "lib\mysql-connector-j-8.0.33.jar" -d out $files
```

If there are no error messages, compilation succeeded.

## 4. Run the Project

```powershell
java -cp "out;lib\mysql-connector-j-8.0.33.jar" Main
```

For Linux or macOS, replace the semicolon in the classpath with a colon:

```bash
javac --release 17 -cp "lib/mysql-connector-j-8.0.33.jar" -d out $(find . -name "*.java")
java -cp "out:lib/mysql-connector-j-8.0.33.jar" Main
```

## How to Use

1. Start the program.
2. Select **Create Account** and register a username and password.
3. Log in with the same details.
4. Use the **My Entries** tab to add, edit, delete, export, import, back up or restore entries.
5. Use the **Search** tab to search by keyword or by a date in `yyyy-MM-dd` format.
6. Use the **Mood Statistics** tab to see mood counts and entries grouped by date.
7. Double-click an entry row to open it.
8. Press **Ctrl+S** inside the entry form to save.

## Data Files

- Exported entry: a `.txt` file chosen in the file chooser.
- Backup: a `.dat` file chosen in the file chooser.
- Auto-saved draft: `drafts/draft_user_<userId>.txt`.

The backup uses Java object serialization, so the model classes implement `Serializable`.

## Syllabus Coverage

- **Unit I:** classes, objects, constructors, `this`, inheritance, abstract classes, method overriding, interfaces, packages and access modifiers.
- **Unit II:** custom exceptions, `try/catch/finally`, `throws`, multithreading with `Thread` and `Runnable`, synchronization, `String` and `StringBuffer`.
- **Unit III:** `ArrayList`, `HashMap`, `TreeMap`, `Collections.sort`, `Comparator`, `FileReader`, `FileWriter` and serialization.
- **Unit IV:** Swing frames, panels, labels, text fields, text areas, scroll panes, combo boxes, tables, tabbed panes, buttons, events and the four required layouts.
- **Unit V:** JDBC `DriverManager`, `Connection`, `PreparedStatement`, `ResultSet` and CRUD operations.

## Notes

- All SQL statements that use user input use `PreparedStatement`.
- Database resources are closed in `finally` blocks.
- Passwords are stored as plain text because this is an educational project. Real applications should hash passwords.
- The application expects the MySQL server to be running on `localhost:3306`.
