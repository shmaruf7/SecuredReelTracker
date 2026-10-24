package com.privatereelstracker.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.privatereelstracker.data.local.entity.DailyReelCount;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ReelDao_Impl implements ReelDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DailyReelCount> __insertionAdapterOfDailyReelCount;

  private final SharedSQLiteStatement __preparedStmtOfIncrementReelCount;

  private final SharedSQLiteStatement __preparedStmtOfUpdateScreenTime;

  private final SharedSQLiteStatement __preparedStmtOfUpdateDailyGoal;

  private final SharedSQLiteStatement __preparedStmtOfClearAllData;

  public ReelDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDailyReelCount = new EntityInsertionAdapter<DailyReelCount>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `daily_reel_counts` (`dateString`,`reelCount`,`instagramScreenTimeMs`,`estimatedReelsTimeMs`,`dailyGoal`,`lastUpdatedTimestamp`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyReelCount entity) {
        statement.bindString(1, entity.getDateString());
        statement.bindLong(2, entity.getReelCount());
        statement.bindLong(3, entity.getInstagramScreenTimeMs());
        statement.bindLong(4, entity.getEstimatedReelsTimeMs());
        statement.bindLong(5, entity.getDailyGoal());
        statement.bindLong(6, entity.getLastUpdatedTimestamp());
      }
    };
    this.__preparedStmtOfIncrementReelCount = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE daily_reel_counts SET reelCount = reelCount + 1, lastUpdatedTimestamp = ? WHERE dateString = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateScreenTime = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE daily_reel_counts SET instagramScreenTimeMs = ?, estimatedReelsTimeMs = ? WHERE dateString = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateDailyGoal = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE daily_reel_counts SET dailyGoal = ? WHERE dateString = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAllData = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM daily_reel_counts";
        return _query;
      }
    };
  }

  @Override
  public Object insertOrUpdate(final DailyReelCount dailyReelCount,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDailyReelCount.insert(dailyReelCount);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementReelCount(final String date, final long now,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementReelCount.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, now);
        _argIndex = 2;
        _stmt.bindString(_argIndex, date);
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfIncrementReelCount.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateScreenTime(final String date, final long screenTimeMs,
      final long estimatedReelsMs, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateScreenTime.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, screenTimeMs);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, estimatedReelsMs);
        _argIndex = 3;
        _stmt.bindString(_argIndex, date);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateScreenTime.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDailyGoal(final String date, final int goal,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateDailyGoal.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, goal);
        _argIndex = 2;
        _stmt.bindString(_argIndex, date);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateDailyGoal.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAllData(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAllData.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAllData.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<DailyReelCount> getDailyCountFlow(final String date) {
    final String _sql = "SELECT * FROM daily_reel_counts WHERE dateString = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_reel_counts"}, new Callable<DailyReelCount>() {
      @Override
      @Nullable
      public DailyReelCount call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDateString = CursorUtil.getColumnIndexOrThrow(_cursor, "dateString");
          final int _cursorIndexOfReelCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reelCount");
          final int _cursorIndexOfInstagramScreenTimeMs = CursorUtil.getColumnIndexOrThrow(_cursor, "instagramScreenTimeMs");
          final int _cursorIndexOfEstimatedReelsTimeMs = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedReelsTimeMs");
          final int _cursorIndexOfDailyGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyGoal");
          final int _cursorIndexOfLastUpdatedTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdatedTimestamp");
          final DailyReelCount _result;
          if (_cursor.moveToFirst()) {
            final String _tmpDateString;
            _tmpDateString = _cursor.getString(_cursorIndexOfDateString);
            final int _tmpReelCount;
            _tmpReelCount = _cursor.getInt(_cursorIndexOfReelCount);
            final long _tmpInstagramScreenTimeMs;
            _tmpInstagramScreenTimeMs = _cursor.getLong(_cursorIndexOfInstagramScreenTimeMs);
            final long _tmpEstimatedReelsTimeMs;
            _tmpEstimatedReelsTimeMs = _cursor.getLong(_cursorIndexOfEstimatedReelsTimeMs);
            final int _tmpDailyGoal;
            _tmpDailyGoal = _cursor.getInt(_cursorIndexOfDailyGoal);
            final long _tmpLastUpdatedTimestamp;
            _tmpLastUpdatedTimestamp = _cursor.getLong(_cursorIndexOfLastUpdatedTimestamp);
            _result = new DailyReelCount(_tmpDateString,_tmpReelCount,_tmpInstagramScreenTimeMs,_tmpEstimatedReelsTimeMs,_tmpDailyGoal,_tmpLastUpdatedTimestamp);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getDailyCount(final String date,
      final Continuation<? super DailyReelCount> $completion) {
    final String _sql = "SELECT * FROM daily_reel_counts WHERE dateString = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DailyReelCount>() {
      @Override
      @Nullable
      public DailyReelCount call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDateString = CursorUtil.getColumnIndexOrThrow(_cursor, "dateString");
          final int _cursorIndexOfReelCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reelCount");
          final int _cursorIndexOfInstagramScreenTimeMs = CursorUtil.getColumnIndexOrThrow(_cursor, "instagramScreenTimeMs");
          final int _cursorIndexOfEstimatedReelsTimeMs = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedReelsTimeMs");
          final int _cursorIndexOfDailyGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyGoal");
          final int _cursorIndexOfLastUpdatedTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdatedTimestamp");
          final DailyReelCount _result;
          if (_cursor.moveToFirst()) {
            final String _tmpDateString;
            _tmpDateString = _cursor.getString(_cursorIndexOfDateString);
            final int _tmpReelCount;
            _tmpReelCount = _cursor.getInt(_cursorIndexOfReelCount);
            final long _tmpInstagramScreenTimeMs;
            _tmpInstagramScreenTimeMs = _cursor.getLong(_cursorIndexOfInstagramScreenTimeMs);
            final long _tmpEstimatedReelsTimeMs;
            _tmpEstimatedReelsTimeMs = _cursor.getLong(_cursorIndexOfEstimatedReelsTimeMs);
            final int _tmpDailyGoal;
            _tmpDailyGoal = _cursor.getInt(_cursorIndexOfDailyGoal);
            final long _tmpLastUpdatedTimestamp;
            _tmpLastUpdatedTimestamp = _cursor.getLong(_cursorIndexOfLastUpdatedTimestamp);
            _result = new DailyReelCount(_tmpDateString,_tmpReelCount,_tmpInstagramScreenTimeMs,_tmpEstimatedReelsTimeMs,_tmpDailyGoal,_tmpLastUpdatedTimestamp);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DailyReelCount>> getRecentDaysFlow(final int limit) {
    final String _sql = "SELECT * FROM daily_reel_counts ORDER BY dateString DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_reel_counts"}, new Callable<List<DailyReelCount>>() {
      @Override
      @NonNull
      public List<DailyReelCount> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDateString = CursorUtil.getColumnIndexOrThrow(_cursor, "dateString");
          final int _cursorIndexOfReelCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reelCount");
          final int _cursorIndexOfInstagramScreenTimeMs = CursorUtil.getColumnIndexOrThrow(_cursor, "instagramScreenTimeMs");
          final int _cursorIndexOfEstimatedReelsTimeMs = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedReelsTimeMs");
          final int _cursorIndexOfDailyGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyGoal");
          final int _cursorIndexOfLastUpdatedTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdatedTimestamp");
          final List<DailyReelCount> _result = new ArrayList<DailyReelCount>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyReelCount _item;
            final String _tmpDateString;
            _tmpDateString = _cursor.getString(_cursorIndexOfDateString);
            final int _tmpReelCount;
            _tmpReelCount = _cursor.getInt(_cursorIndexOfReelCount);
            final long _tmpInstagramScreenTimeMs;
            _tmpInstagramScreenTimeMs = _cursor.getLong(_cursorIndexOfInstagramScreenTimeMs);
            final long _tmpEstimatedReelsTimeMs;
            _tmpEstimatedReelsTimeMs = _cursor.getLong(_cursorIndexOfEstimatedReelsTimeMs);
            final int _tmpDailyGoal;
            _tmpDailyGoal = _cursor.getInt(_cursorIndexOfDailyGoal);
            final long _tmpLastUpdatedTimestamp;
            _tmpLastUpdatedTimestamp = _cursor.getLong(_cursorIndexOfLastUpdatedTimestamp);
            _item = new DailyReelCount(_tmpDateString,_tmpReelCount,_tmpInstagramScreenTimeMs,_tmpEstimatedReelsTimeMs,_tmpDailyGoal,_tmpLastUpdatedTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllHistory(final Continuation<? super List<DailyReelCount>> $completion) {
    final String _sql = "SELECT * FROM daily_reel_counts ORDER BY dateString DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DailyReelCount>>() {
      @Override
      @NonNull
      public List<DailyReelCount> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDateString = CursorUtil.getColumnIndexOrThrow(_cursor, "dateString");
          final int _cursorIndexOfReelCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reelCount");
          final int _cursorIndexOfInstagramScreenTimeMs = CursorUtil.getColumnIndexOrThrow(_cursor, "instagramScreenTimeMs");
          final int _cursorIndexOfEstimatedReelsTimeMs = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedReelsTimeMs");
          final int _cursorIndexOfDailyGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyGoal");
          final int _cursorIndexOfLastUpdatedTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdatedTimestamp");
          final List<DailyReelCount> _result = new ArrayList<DailyReelCount>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyReelCount _item;
            final String _tmpDateString;
            _tmpDateString = _cursor.getString(_cursorIndexOfDateString);
            final int _tmpReelCount;
            _tmpReelCount = _cursor.getInt(_cursorIndexOfReelCount);
            final long _tmpInstagramScreenTimeMs;
            _tmpInstagramScreenTimeMs = _cursor.getLong(_cursorIndexOfInstagramScreenTimeMs);
            final long _tmpEstimatedReelsTimeMs;
            _tmpEstimatedReelsTimeMs = _cursor.getLong(_cursorIndexOfEstimatedReelsTimeMs);
            final int _tmpDailyGoal;
            _tmpDailyGoal = _cursor.getInt(_cursorIndexOfDailyGoal);
            final long _tmpLastUpdatedTimestamp;
            _tmpLastUpdatedTimestamp = _cursor.getLong(_cursorIndexOfLastUpdatedTimestamp);
            _item = new DailyReelCount(_tmpDateString,_tmpReelCount,_tmpInstagramScreenTimeMs,_tmpEstimatedReelsTimeMs,_tmpDailyGoal,_tmpLastUpdatedTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
