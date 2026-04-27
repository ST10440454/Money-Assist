package com.moneyassist.app.data.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.moneyassist.app.data.entity.Mission;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MissionDao_Impl implements MissionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Mission> __insertionAdapterOfMission;

  private final EntityDeletionOrUpdateAdapter<Mission> __deletionAdapterOfMission;

  private final EntityDeletionOrUpdateAdapter<Mission> __updateAdapterOfMission;

  public MissionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMission = new EntityInsertionAdapter<Mission>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `missions` (`id`,`name`,`target`,`current`,`deadline`,`type`,`icon`,`monthlyContrib`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Mission entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getTarget());
        statement.bindDouble(4, entity.getCurrent());
        statement.bindString(5, entity.getDeadline());
        statement.bindString(6, entity.getType());
        statement.bindString(7, entity.getIcon());
        statement.bindDouble(8, entity.getMonthlyContrib());
      }
    };
    this.__deletionAdapterOfMission = new EntityDeletionOrUpdateAdapter<Mission>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `missions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Mission entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfMission = new EntityDeletionOrUpdateAdapter<Mission>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `missions` SET `id` = ?,`name` = ?,`target` = ?,`current` = ?,`deadline` = ?,`type` = ?,`icon` = ?,`monthlyContrib` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Mission entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getTarget());
        statement.bindDouble(4, entity.getCurrent());
        statement.bindString(5, entity.getDeadline());
        statement.bindString(6, entity.getType());
        statement.bindString(7, entity.getIcon());
        statement.bindDouble(8, entity.getMonthlyContrib());
        statement.bindLong(9, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final Mission mission, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMission.insertAndReturnId(mission);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final Mission mission, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfMission.handle(mission);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Mission mission, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMission.handle(mission);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public LiveData<List<Mission>> getAll() {
    final String _sql = "SELECT * FROM missions ORDER BY deadline ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"missions"}, false, new Callable<List<Mission>>() {
      @Override
      @Nullable
      public List<Mission> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfTarget = CursorUtil.getColumnIndexOrThrow(_cursor, "target");
          final int _cursorIndexOfCurrent = CursorUtil.getColumnIndexOrThrow(_cursor, "current");
          final int _cursorIndexOfDeadline = CursorUtil.getColumnIndexOrThrow(_cursor, "deadline");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfIcon = CursorUtil.getColumnIndexOrThrow(_cursor, "icon");
          final int _cursorIndexOfMonthlyContrib = CursorUtil.getColumnIndexOrThrow(_cursor, "monthlyContrib");
          final List<Mission> _result = new ArrayList<Mission>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Mission _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final double _tmpTarget;
            _tmpTarget = _cursor.getDouble(_cursorIndexOfTarget);
            final double _tmpCurrent;
            _tmpCurrent = _cursor.getDouble(_cursorIndexOfCurrent);
            final String _tmpDeadline;
            _tmpDeadline = _cursor.getString(_cursorIndexOfDeadline);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpIcon;
            _tmpIcon = _cursor.getString(_cursorIndexOfIcon);
            final double _tmpMonthlyContrib;
            _tmpMonthlyContrib = _cursor.getDouble(_cursorIndexOfMonthlyContrib);
            _item = new Mission(_tmpId,_tmpName,_tmpTarget,_tmpCurrent,_tmpDeadline,_tmpType,_tmpIcon,_tmpMonthlyContrib);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
