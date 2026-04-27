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
import com.moneyassist.app.data.entity.Bill;
import java.lang.Class;
import java.lang.Double;
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
public final class BillDao_Impl implements BillDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Bill> __insertionAdapterOfBill;

  private final EntityDeletionOrUpdateAdapter<Bill> __deletionAdapterOfBill;

  private final EntityDeletionOrUpdateAdapter<Bill> __updateAdapterOfBill;

  public BillDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBill = new EntityInsertionAdapter<Bill>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `bills` (`id`,`name`,`dueDate`,`amount`,`recurring`,`isPaid`,`paidOn`,`icon`,`isUrgent`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Bill entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getDueDate());
        statement.bindDouble(4, entity.getAmount());
        statement.bindString(5, entity.getRecurring());
        final int _tmp = entity.isPaid() ? 1 : 0;
        statement.bindLong(6, _tmp);
        if (entity.getPaidOn() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getPaidOn());
        }
        statement.bindString(8, entity.getIcon());
        final int _tmp_1 = entity.isUrgent() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
      }
    };
    this.__deletionAdapterOfBill = new EntityDeletionOrUpdateAdapter<Bill>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `bills` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Bill entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfBill = new EntityDeletionOrUpdateAdapter<Bill>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `bills` SET `id` = ?,`name` = ?,`dueDate` = ?,`amount` = ?,`recurring` = ?,`isPaid` = ?,`paidOn` = ?,`icon` = ?,`isUrgent` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Bill entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getDueDate());
        statement.bindDouble(4, entity.getAmount());
        statement.bindString(5, entity.getRecurring());
        final int _tmp = entity.isPaid() ? 1 : 0;
        statement.bindLong(6, _tmp);
        if (entity.getPaidOn() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getPaidOn());
        }
        statement.bindString(8, entity.getIcon());
        final int _tmp_1 = entity.isUrgent() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        statement.bindLong(10, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final Bill bill, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfBill.insertAndReturnId(bill);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final Bill bill, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfBill.handle(bill);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Bill bill, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfBill.handle(bill);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public LiveData<List<Bill>> getUpcoming() {
    final String _sql = "SELECT * FROM bills WHERE isPaid = 0 ORDER BY dueDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"bills"}, false, new Callable<List<Bill>>() {
      @Override
      @Nullable
      public List<Bill> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfRecurring = CursorUtil.getColumnIndexOrThrow(_cursor, "recurring");
          final int _cursorIndexOfIsPaid = CursorUtil.getColumnIndexOrThrow(_cursor, "isPaid");
          final int _cursorIndexOfPaidOn = CursorUtil.getColumnIndexOrThrow(_cursor, "paidOn");
          final int _cursorIndexOfIcon = CursorUtil.getColumnIndexOrThrow(_cursor, "icon");
          final int _cursorIndexOfIsUrgent = CursorUtil.getColumnIndexOrThrow(_cursor, "isUrgent");
          final List<Bill> _result = new ArrayList<Bill>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Bill _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDueDate;
            _tmpDueDate = _cursor.getString(_cursorIndexOfDueDate);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpRecurring;
            _tmpRecurring = _cursor.getString(_cursorIndexOfRecurring);
            final boolean _tmpIsPaid;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPaid);
            _tmpIsPaid = _tmp != 0;
            final String _tmpPaidOn;
            if (_cursor.isNull(_cursorIndexOfPaidOn)) {
              _tmpPaidOn = null;
            } else {
              _tmpPaidOn = _cursor.getString(_cursorIndexOfPaidOn);
            }
            final String _tmpIcon;
            _tmpIcon = _cursor.getString(_cursorIndexOfIcon);
            final boolean _tmpIsUrgent;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsUrgent);
            _tmpIsUrgent = _tmp_1 != 0;
            _item = new Bill(_tmpId,_tmpName,_tmpDueDate,_tmpAmount,_tmpRecurring,_tmpIsPaid,_tmpPaidOn,_tmpIcon,_tmpIsUrgent);
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
  public LiveData<List<Bill>> getPaid() {
    final String _sql = "SELECT * FROM bills WHERE isPaid = 1 ORDER BY paidOn DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"bills"}, false, new Callable<List<Bill>>() {
      @Override
      @Nullable
      public List<Bill> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfRecurring = CursorUtil.getColumnIndexOrThrow(_cursor, "recurring");
          final int _cursorIndexOfIsPaid = CursorUtil.getColumnIndexOrThrow(_cursor, "isPaid");
          final int _cursorIndexOfPaidOn = CursorUtil.getColumnIndexOrThrow(_cursor, "paidOn");
          final int _cursorIndexOfIcon = CursorUtil.getColumnIndexOrThrow(_cursor, "icon");
          final int _cursorIndexOfIsUrgent = CursorUtil.getColumnIndexOrThrow(_cursor, "isUrgent");
          final List<Bill> _result = new ArrayList<Bill>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Bill _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDueDate;
            _tmpDueDate = _cursor.getString(_cursorIndexOfDueDate);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpRecurring;
            _tmpRecurring = _cursor.getString(_cursorIndexOfRecurring);
            final boolean _tmpIsPaid;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPaid);
            _tmpIsPaid = _tmp != 0;
            final String _tmpPaidOn;
            if (_cursor.isNull(_cursorIndexOfPaidOn)) {
              _tmpPaidOn = null;
            } else {
              _tmpPaidOn = _cursor.getString(_cursorIndexOfPaidOn);
            }
            final String _tmpIcon;
            _tmpIcon = _cursor.getString(_cursorIndexOfIcon);
            final boolean _tmpIsUrgent;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsUrgent);
            _tmpIsUrgent = _tmp_1 != 0;
            _item = new Bill(_tmpId,_tmpName,_tmpDueDate,_tmpAmount,_tmpRecurring,_tmpIsPaid,_tmpPaidOn,_tmpIcon,_tmpIsUrgent);
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
  public LiveData<List<Bill>> getAll() {
    final String _sql = "SELECT * FROM bills ORDER BY dueDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"bills"}, false, new Callable<List<Bill>>() {
      @Override
      @Nullable
      public List<Bill> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfRecurring = CursorUtil.getColumnIndexOrThrow(_cursor, "recurring");
          final int _cursorIndexOfIsPaid = CursorUtil.getColumnIndexOrThrow(_cursor, "isPaid");
          final int _cursorIndexOfPaidOn = CursorUtil.getColumnIndexOrThrow(_cursor, "paidOn");
          final int _cursorIndexOfIcon = CursorUtil.getColumnIndexOrThrow(_cursor, "icon");
          final int _cursorIndexOfIsUrgent = CursorUtil.getColumnIndexOrThrow(_cursor, "isUrgent");
          final List<Bill> _result = new ArrayList<Bill>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Bill _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDueDate;
            _tmpDueDate = _cursor.getString(_cursorIndexOfDueDate);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpRecurring;
            _tmpRecurring = _cursor.getString(_cursorIndexOfRecurring);
            final boolean _tmpIsPaid;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPaid);
            _tmpIsPaid = _tmp != 0;
            final String _tmpPaidOn;
            if (_cursor.isNull(_cursorIndexOfPaidOn)) {
              _tmpPaidOn = null;
            } else {
              _tmpPaidOn = _cursor.getString(_cursorIndexOfPaidOn);
            }
            final String _tmpIcon;
            _tmpIcon = _cursor.getString(_cursorIndexOfIcon);
            final boolean _tmpIsUrgent;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsUrgent);
            _tmpIsUrgent = _tmp_1 != 0;
            _item = new Bill(_tmpId,_tmpName,_tmpDueDate,_tmpAmount,_tmpRecurring,_tmpIsPaid,_tmpPaidOn,_tmpIcon,_tmpIsUrgent);
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
  public LiveData<Double> getTotalUpcoming() {
    final String _sql = "SELECT SUM(amount) FROM bills WHERE isPaid = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"bills"}, false, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
