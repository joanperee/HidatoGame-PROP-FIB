FONTS/src/persistence

Contains the persistence layer source code.

Files:
- DataListManager.java - Interface for managing lists of objects. 

- DataManager.java - Interface for managing the load and storage of data.

- JsonManager.java - Base implementation of the data & list manager for Json type files.

- JsonGameSessionManager.java - Concrete implementation of the JsonManager for GameSession.

- JsonHidatoManager.java - Concrete implementation of the JsonManager for Hidato (had to switch to CSV for subject rules).

- JsonUserManager.java - Concrete implementation of the JsonManager for User.