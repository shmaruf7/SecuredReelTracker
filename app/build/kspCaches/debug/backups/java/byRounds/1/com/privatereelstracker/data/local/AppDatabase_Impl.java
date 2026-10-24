package com.privatereelstracker.data.local;

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
import com.privatereelstracker.data.local.dao.DiagnosticDao;
import com.privatereelstracker.data.local.dao.DiagnosticDao_Impl;
import com.privatereelstracker.data.local.dao.ReelDao;
import com.privatereelstracker.data.local.dao.ReelDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile ReelDao _reelDao;

  private volatile DiagnosticDao _diagnosticDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `daily_reel_counts` (`dateString` TEXT NOT NULL, `reelCount` INTEGER NOT NULL, `instagramScreenTimeMs` INTEGER NOT NULL, `estimatedReelsTimeMs` INTEGER NOT NULL, `dailyGoal` INTEGER NOT NULL, `lastUpdatedTimestamp` INTEGER NOT NULL, PRIMARY KEY(`dateString`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `diagnostic_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` INTEGER NOT NULL, `eventType` TEXT NOT NULL, `details` TEXT NOT NULL, `wasIncremented` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'a429ace0456de98ab300b2676c08c6e0')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `daily_reel_counts`");
        db.execSQL("DROP TABLE IF EXISTS `diagnostic_logs`");
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
        final HashMap<String, TableInfo.Column> _columnsDailyReelCounts = new HashMap<String, TableInfo.Column>(6);
        _columnsDailyReelCounts.put("dateString", new TableInfo.Column("dateString", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyReelCounts.put("reelCount", new TableInfo.Column("reelCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyReelCounts.put("instagramScreenTimeMs", new TableInfo.Column("instagramScreenTimeMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyReelCounts.put("estimatedReelsTimeMs", new TableInfo.Column("estimatedReelsTimeMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyReelCounts.put("dailyGoal", new TableInfo.Column("dailyGoal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyReelCounts.put("lastUpdatedTimestamp", new TableInfo.Column("lastUpdatedTimestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDailyReelCounts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDailyReelCounts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDailyReelCounts = new TableInfo("daily_reel_counts", _columnsDailyReelCounts, _foreignKeysDailyReelCounts, _indicesDailyReelCounts);
        final TableInfo _existingDailyReelCounts = TableInfo.read(db, "daily_reel_counts");
        if (!_infoDailyReelCounts.equals(_existingDailyReelCounts)) {
          return new RoomOpenHelper.ValidationResult(false, "daily_reel_counts(com.privatereelstracker.data.local.entity.DailyReelCount).\n"
                  + " Expected:\n" + _infoDailyReelCounts + "\n"
                  + " Found:\n" + _existingDailyReelCounts);
        }
        final HashMap<String, TableInfo.Column> _columnsDiagnosticLogs = new HashMap<String, TableInfo.Column>(5);
        _columnsDiagnosticLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiagnosticLogs.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiagnosticLogs.put("eventType", new TableInfo.Column("eventType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiagnosticLogs.put("details", new TableInfo.Column("details", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiagnosticLogs.put("wasIncremented", new TableInfo.Column("wasIncremented", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDiagnosticLogs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDiagnosticLogs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDiagnosticLogs = new TableInfo("diagnostic_logs", _columnsDiagnosticLogs, _foreignKeysDiagnosticLogs, _indicesDiagnosticLogs);
        final TableInfo _existingDiagnosticLogs = TableInfo.read(db, "diagnostic_logs");
        if (!_infoDiagnosticLogs.equals(_existingDiagnosticLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "diagnostic_logs(com.privatereelstracker.data.local.entity.DiagnosticLog).\n"
                  + " Expected:\n" + _infoDiagnosticLogs + "\n"
                  + " Found:\n" + _existingDiagnosticLogs);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "a429ace0456de98ab300b2676c08c6e0", "2ed6c49289711080affbc03bdb8a35d0");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "daily_reel_counts","diagnostic_logs");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `daily_reel_counts`");
      _db.execSQL("DELETE FROM `diagnostic_logs`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
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
    _typeConvertersMap.put(ReelDao.class, ReelDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DiagnosticDao.class, DiagnosticDao_Impl.getRequiredConverters());
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
  public ReelDao reelDao() {
    if (_reelDao != null) {
      return _reelDao;
    } else {
      synchronized(this) {
        if(_reelDao == null) {
          _reelDao = new ReelDao_Impl(this);
        }
        return _reelDao;
      }
    }
  }

  @Override
  public DiagnosticDao diagnosticDao() {
    if (_diagnosticDao != null) {
      return _diagnosticDao;
    } else {
      synchronized(this) {
        if(_diagnosticDao == null) {
          _diagnosticDao = new DiagnosticDao_Impl(this);
        }
        return _diagnosticDao;
      }
    }
  }
}
