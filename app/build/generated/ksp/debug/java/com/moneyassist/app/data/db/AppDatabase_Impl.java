package com.moneyassist.app.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.moneyassist.app.data.dao.BillDao;
import com.moneyassist.app.data.dao.BillDao_Impl;
import com.moneyassist.app.data.dao.BudgetCategoryDao;
import com.moneyassist.app.data.dao.BudgetCategoryDao_Impl;
import com.moneyassist.app.data.dao.CategoryDao;
import com.moneyassist.app.data.dao.CategoryDao_Impl;
import com.moneyassist.app.data.dao.ExpenseEntryDao;
import com.moneyassist.app.data.dao.ExpenseEntryDao_Impl;
import com.moneyassist.app.data.dao.MissionDao;
import com.moneyassist.app.data.dao.MissionDao_Impl;
import com.moneyassist.app.data.dao.TransactionDao;
import com.moneyassist.app.data.dao.TransactionDao_Impl;
import com.moneyassist.app.data.dao.UserDao;
import com.moneyassist.app.data.dao.UserDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile UserDao _userDao;

  private volatile CategoryDao _categoryDao;

  private volatile ExpenseEntryDao _expenseEntryDao;

  private volatile TransactionDao _transactionDao;

  private volatile BillDao _billDao;

  private volatile MissionDao _missionDao;

  private volatile BudgetCategoryDao _budgetCategoryDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `users` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `username` TEXT NOT NULL, `password` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `categories` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `minimumGoal` REAL NOT NULL, `maximumGoal` REAL NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `expense_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `startTime` TEXT NOT NULL, `endTime` TEXT NOT NULL, `description` TEXT NOT NULL, `amount` REAL NOT NULL, `categoryId` INTEGER NOT NULL, `photoPath` TEXT, FOREIGN KEY(`categoryId`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_expense_entries_categoryId` ON `expense_entries` (`categoryId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `transactions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `date` TEXT NOT NULL, `amount` REAL NOT NULL, `type` TEXT NOT NULL, `category` TEXT NOT NULL, `icon` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `bills` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `dueDate` TEXT NOT NULL, `amount` REAL NOT NULL, `recurring` TEXT NOT NULL, `isPaid` INTEGER NOT NULL, `paidOn` TEXT, `icon` TEXT NOT NULL, `isUrgent` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `missions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `target` REAL NOT NULL, `current` REAL NOT NULL, `deadline` TEXT NOT NULL, `type` TEXT NOT NULL, `icon` TEXT NOT NULL, `monthlyContrib` REAL NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `budget_categories` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `limitAmount` REAL NOT NULL, `spent` REAL NOT NULL, `icon` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '9b07516234309dee14bd0d603dbc78de')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `users`");
        db.execSQL("DROP TABLE IF EXISTS `categories`");
        db.execSQL("DROP TABLE IF EXISTS `expense_entries`");
        db.execSQL("DROP TABLE IF EXISTS `transactions`");
        db.execSQL("DROP TABLE IF EXISTS `bills`");
        db.execSQL("DROP TABLE IF EXISTS `missions`");
        db.execSQL("DROP TABLE IF EXISTS `budget_categories`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsUsers = new HashMap<String, TableInfo.Column>(3);
        _columnsUsers.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("username", new TableInfo.Column("username", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("password", new TableInfo.Column("password", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUsers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUsers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUsers = new TableInfo("users", _columnsUsers, _foreignKeysUsers, _indicesUsers);
        final TableInfo _existingUsers = TableInfo.read(db, "users");
        if (!_infoUsers.equals(_existingUsers)) {
          return new RoomOpenHelper.ValidationResult(false, "users(com.moneyassist.app.data.entity.User).\n"
                  + " Expected:\n" + _infoUsers + "\n"
                  + " Found:\n" + _existingUsers);
        }
        final HashMap<String, TableInfo.Column> _columnsCategories = new HashMap<String, TableInfo.Column>(4);
        _columnsCategories.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("minimumGoal", new TableInfo.Column("minimumGoal", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("maximumGoal", new TableInfo.Column("maximumGoal", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCategories = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCategories = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCategories = new TableInfo("categories", _columnsCategories, _foreignKeysCategories, _indicesCategories);
        final TableInfo _existingCategories = TableInfo.read(db, "categories");
        if (!_infoCategories.equals(_existingCategories)) {
          return new RoomOpenHelper.ValidationResult(false, "categories(com.moneyassist.app.data.entity.Category).\n"
                  + " Expected:\n" + _infoCategories + "\n"
                  + " Found:\n" + _existingCategories);
        }
        final HashMap<String, TableInfo.Column> _columnsExpenseEntries = new HashMap<String, TableInfo.Column>(8);
        _columnsExpenseEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenseEntries.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenseEntries.put("startTime", new TableInfo.Column("startTime", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenseEntries.put("endTime", new TableInfo.Column("endTime", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenseEntries.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenseEntries.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenseEntries.put("categoryId", new TableInfo.Column("categoryId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenseEntries.put("photoPath", new TableInfo.Column("photoPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExpenseEntries = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysExpenseEntries.add(new TableInfo.ForeignKey("categories", "CASCADE", "NO ACTION", Arrays.asList("categoryId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesExpenseEntries = new HashSet<TableInfo.Index>(1);
        _indicesExpenseEntries.add(new TableInfo.Index("index_expense_entries_categoryId", false, Arrays.asList("categoryId"), Arrays.asList("ASC")));
        final TableInfo _infoExpenseEntries = new TableInfo("expense_entries", _columnsExpenseEntries, _foreignKeysExpenseEntries, _indicesExpenseEntries);
        final TableInfo _existingExpenseEntries = TableInfo.read(db, "expense_entries");
        if (!_infoExpenseEntries.equals(_existingExpenseEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "expense_entries(com.moneyassist.app.data.entity.ExpenseEntry).\n"
                  + " Expected:\n" + _infoExpenseEntries + "\n"
                  + " Found:\n" + _existingExpenseEntries);
        }
        final HashMap<String, TableInfo.Column> _columnsTransactions = new HashMap<String, TableInfo.Column>(7);
        _columnsTransactions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("icon", new TableInfo.Column("icon", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTransactions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTransactions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTransactions = new TableInfo("transactions", _columnsTransactions, _foreignKeysTransactions, _indicesTransactions);
        final TableInfo _existingTransactions = TableInfo.read(db, "transactions");
        if (!_infoTransactions.equals(_existingTransactions)) {
          return new RoomOpenHelper.ValidationResult(false, "transactions(com.moneyassist.app.data.entity.Transaction).\n"
                  + " Expected:\n" + _infoTransactions + "\n"
                  + " Found:\n" + _existingTransactions);
        }
        final HashMap<String, TableInfo.Column> _columnsBills = new HashMap<String, TableInfo.Column>(9);
        _columnsBills.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBills.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBills.put("dueDate", new TableInfo.Column("dueDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBills.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBills.put("recurring", new TableInfo.Column("recurring", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBills.put("isPaid", new TableInfo.Column("isPaid", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBills.put("paidOn", new TableInfo.Column("paidOn", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBills.put("icon", new TableInfo.Column("icon", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBills.put("isUrgent", new TableInfo.Column("isUrgent", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBills = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBills = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBills = new TableInfo("bills", _columnsBills, _foreignKeysBills, _indicesBills);
        final TableInfo _existingBills = TableInfo.read(db, "bills");
        if (!_infoBills.equals(_existingBills)) {
          return new RoomOpenHelper.ValidationResult(false, "bills(com.moneyassist.app.data.entity.Bill).\n"
                  + " Expected:\n" + _infoBills + "\n"
                  + " Found:\n" + _existingBills);
        }
        final HashMap<String, TableInfo.Column> _columnsMissions = new HashMap<String, TableInfo.Column>(8);
        _columnsMissions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMissions.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMissions.put("target", new TableInfo.Column("target", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMissions.put("current", new TableInfo.Column("current", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMissions.put("deadline", new TableInfo.Column("deadline", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMissions.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMissions.put("icon", new TableInfo.Column("icon", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMissions.put("monthlyContrib", new TableInfo.Column("monthlyContrib", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMissions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMissions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMissions = new TableInfo("missions", _columnsMissions, _foreignKeysMissions, _indicesMissions);
        final TableInfo _existingMissions = TableInfo.read(db, "missions");
        if (!_infoMissions.equals(_existingMissions)) {
          return new RoomOpenHelper.ValidationResult(false, "missions(com.moneyassist.app.data.entity.Mission).\n"
                  + " Expected:\n" + _infoMissions + "\n"
                  + " Found:\n" + _existingMissions);
        }
        final HashMap<String, TableInfo.Column> _columnsBudgetCategories = new HashMap<String, TableInfo.Column>(5);
        _columnsBudgetCategories.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgetCategories.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgetCategories.put("limitAmount", new TableInfo.Column("limitAmount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgetCategories.put("spent", new TableInfo.Column("spent", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgetCategories.put("icon", new TableInfo.Column("icon", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBudgetCategories = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBudgetCategories = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBudgetCategories = new TableInfo("budget_categories", _columnsBudgetCategories, _foreignKeysBudgetCategories, _indicesBudgetCategories);
        final TableInfo _existingBudgetCategories = TableInfo.read(db, "budget_categories");
        if (!_infoBudgetCategories.equals(_existingBudgetCategories)) {
          return new RoomOpenHelper.ValidationResult(false, "budget_categories(com.moneyassist.app.data.entity.BudgetCategory).\n"
                  + " Expected:\n" + _infoBudgetCategories + "\n"
                  + " Found:\n" + _existingBudgetCategories);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "9b07516234309dee14bd0d603dbc78de", "023720851592195360eee32881bf14d1");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "users","categories","expense_entries","transactions","bills","missions","budget_categories");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `users`");
      _db.execSQL("DELETE FROM `categories`");
      _db.execSQL("DELETE FROM `expense_entries`");
      _db.execSQL("DELETE FROM `transactions`");
      _db.execSQL("DELETE FROM `bills`");
      _db.execSQL("DELETE FROM `missions`");
      _db.execSQL("DELETE FROM `budget_categories`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(UserDao.class, UserDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CategoryDao.class, CategoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ExpenseEntryDao.class, ExpenseEntryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TransactionDao.class, TransactionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BillDao.class, BillDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MissionDao.class, MissionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BudgetCategoryDao.class, BudgetCategoryDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public UserDao userDao() {
    if (_userDao != null) {
      return _userDao;
    } else {
      synchronized(this) {
        if(_userDao == null) {
          _userDao = new UserDao_Impl(this);
        }
        return _userDao;
      }
    }
  }

  @Override
  public CategoryDao categoryDao() {
    if (_categoryDao != null) {
      return _categoryDao;
    } else {
      synchronized(this) {
        if(_categoryDao == null) {
          _categoryDao = new CategoryDao_Impl(this);
        }
        return _categoryDao;
      }
    }
  }

  @Override
  public ExpenseEntryDao expenseEntryDao() {
    if (_expenseEntryDao != null) {
      return _expenseEntryDao;
    } else {
      synchronized(this) {
        if(_expenseEntryDao == null) {
          _expenseEntryDao = new ExpenseEntryDao_Impl(this);
        }
        return _expenseEntryDao;
      }
    }
  }

  @Override
  public TransactionDao transactionDao() {
    if (_transactionDao != null) {
      return _transactionDao;
    } else {
      synchronized(this) {
        if(_transactionDao == null) {
          _transactionDao = new TransactionDao_Impl(this);
        }
        return _transactionDao;
      }
    }
  }

  @Override
  public BillDao billDao() {
    if (_billDao != null) {
      return _billDao;
    } else {
      synchronized(this) {
        if(_billDao == null) {
          _billDao = new BillDao_Impl(this);
        }
        return _billDao;
      }
    }
  }

  @Override
  public MissionDao missionDao() {
    if (_missionDao != null) {
      return _missionDao;
    } else {
      synchronized(this) {
        if(_missionDao == null) {
          _missionDao = new MissionDao_Impl(this);
        }
        return _missionDao;
      }
    }
  }

  @Override
  public BudgetCategoryDao budgetCategoryDao() {
    if (_budgetCategoryDao != null) {
      return _budgetCategoryDao;
    } else {
      synchronized(this) {
        if(_budgetCategoryDao == null) {
          _budgetCategoryDao = new BudgetCategoryDao_Impl(this);
        }
        return _budgetCategoryDao;
      }
    }
  }
}
