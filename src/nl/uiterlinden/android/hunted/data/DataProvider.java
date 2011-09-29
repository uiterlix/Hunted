package nl.uiterlinden.android.hunted.data;

import java.util.ArrayList;
import java.util.List;

import nl.uiterlinden.android.hunted.domain.Clue;
import nl.uiterlinden.android.hunted.domain.LogItem;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class DataProvider {

	private static final String CREATE_POINTS_SQL = "CREATE TABLE IF NOT EXISTS POINTS("
			+ "NAME TEXT PRIMARY KEY,"
			+ "COORDINATE TEXT,"
			+ "WORD TEXT,"
			+ "TIMEFOUND INTEGER" + ")";
	private static final String CREATE_STATUS_SQL = "CREATE TABLE IF NOT EXISTS STATUS(ELEMENT TEXT PRIMARY KEY, VALUE TEXT);";
	private static final String CREATE_LOG_SQL = "CREATE TABLE IF NOT EXISTS LOG(ID INTEGER PRIMARY KEY AUTOINCREMENT, DIRECTION INTEGER, MESSAGE TEXT, MOMENT INTEGER)";
	private static final String CREATE_UNDO_SQL = "CREATE TABLE IF NOT EXISTS UNDO_LOG(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT)";
	private static final String DROP_POINTS_SQL = "DROP TABLE IF EXISTS POINTS;";
	private static final String DROP_STATUS_SQL = "DROP TABLE IF EXISTS STATUS;";
	private static final String DROP_LOG_SQL = "DROP TABLE IF EXISTS LOG;";
	private static final String DROP_UNDO_SQL = "DROP TABLE IF EXISTS UNDO_LOG";
	
	public static final String CURRENT_TARGET = "CURRENT_TARGET";
	public static final String LAST_CATCH_TIME = "LAST_CATCH_TIME";
	public static final String CATCH_COUNT = "CATCH_COUNT";
	
	private Context context;
	private SQLiteDatabase db;

	public DataProvider(Context context) {
		this.context = context;
	    OpenHelper openHelper = new OpenHelper(this.context);
	    this.db = openHelper.getWritableDatabase();
	}
	
	public void updateStatus(String key, String value) {
		// insert or replace into
		SQLiteStatement stmt = db.compileStatement("INSERT OR REPLACE INTO STATUS(ELEMENT, VALUE) VALUES(?,?)");
		stmt.bindString(1, key);
		stmt.bindString(2, value);
		stmt.execute();
		stmt.close();
		
		if (key.equals(CURRENT_TARGET)) {
			SQLiteStatement undoStmt = db.compileStatement("INSERT INTO UNDO_LOG(NAME) VALUES(?)");
			undoStmt.bindString(1, value);
			undoStmt.execute();
			undoStmt.close();
		}
		//getWritableDatabase().close();
//		getWritableDatabase().execSQL("INSERT OR REPLACE INTO STATUS(ELEMENT, VALUE) VALUES('" + key + "','" + value + "')");
	}
	
	public String getStatus(String key) {
		String status = null;
		Cursor cursor = db.query("STATUS", new String[] { "VALUE" }, "ELEMENT='" + key + "'", null, null, null, null);
		if (cursor.moveToFirst()) {
			status = cursor.getString(0);
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		//getWritableDatabase().close();
		return status;
	}
	
	public void reset() {
		deleteAllClues();
		db.execSQL("DELETE FROM LOG;");
		db.execSQL("DELETE FROM UNDO_LOG;");
		//getWritableDatabase().close();
	}

	public void deleteAllClues() {
		db.execSQL("DELETE FROM POINTS;");
		db.execSQL("DELETE FROM STATUS;");
		//getWritableDatabase().close();
	}

	public void insertClues(List<Clue> clues, ProgressMonitor monitor) {
		SQLiteStatement insertStmt = this.db.compileStatement("INSERT INTO POINTS(NAME, COORDINATE) VALUES(?,?)");
		monitor.setMaxProgress(clues.size());
		int progress = 0;
		for (Clue clue : clues) {
			try {
				insertStmt.bindString(1, clue.getName());
				insertStmt.bindString(2, clue.getCoordinate());
				insertStmt.executeInsert();
				progress ++;
			monitor.update(progress);
			} catch (SQLiteConstraintException ce) {
				// ignore possible duplicates
			}
		}
		//getWritableDatabase().close();
	}
	
	public int getClueCount() {
		int count = 0;
		Cursor cursor = db.query("POINTS", new String[] { "NAME" }, null, null, null, null, null);
		count = cursor.getCount();
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}		
		//getWritableDatabase().close();
		return count;
	}
	
	public int getFoundClueCount() {
		int count = 0;
		Cursor cursor = db.query("POINTS", new String[] { "NAME" }, "TIMEFOUND > 0", null, null, null, null);
		count = cursor.getCount();
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}	
		return count;
	}

	public List<Clue> fetchClues() {
		List<Clue> clues = new ArrayList<Clue>();
		Cursor cursor = db.query("POINTS",
				new String[] { "NAME", "COORDINATE", "WORD", "TIMEFOUND" }, null, null, null, null,
				null);
		if (cursor.moveToFirst()) {
			do {
				clues.add(new Clue(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getLong(3)));
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		//getWritableDatabase().close();
		return clues;
	}

	public void clearStatus() {
		db.execSQL("DELETE FROM STATUS");
		//getWritableDatabase().close();
	}

	public Clue getClue(String name) {
		Clue clue = null;
		Cursor cursor = db.query("POINTS",
				new String[] { "NAME", "COORDINATE", "WORD", "TIMEFOUND" }, "NAME='" + name + "'", null, null, null,
				null);
		if (cursor.moveToFirst()) {
			clue = new Clue(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getLong(3));
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		//getWritableDatabase().close();
		return clue;
	}

	public void foundClue(String currentTarget, String word) {
		SQLiteStatement stmt = db.compileStatement("UPDATE POINTS SET TIMEFOUND = ?, WORD = ? WHERE NAME = ?");
		stmt.bindLong(1, System.currentTimeMillis());
		stmt.bindString(2, word);
		stmt.bindString(3, currentTarget);
		stmt.execute();
		stmt.close();
		//getWritableDatabase().close();
//		getWritableDatabase().execSQL("UPDATE POINTS SET TIMEFOUND = " + System.currentTimeMillis() + ", WORD = '" + word + "' WHERE NAME='" + currentTarget + "'");
	}
	
	public void undo() {
		// select max id from undo_log minus one
		System.out.println("UNDO");
		long lastEntryId = -1;
		long previousEntryId = -1;
		String lastEntry = null;
		String previousEntry = null;
		Cursor cursor = db.query("UNDO_LOG",
				new String[] { "ID", "NAME" }, null, null, null, null,
				null);
		if (cursor.moveToLast()) {
			System.out.println("MOVE TO LAST");
			lastEntryId = cursor.getLong(0);
			lastEntry = cursor.getString(1);
			if (cursor.moveToPrevious()) {
				System.out.println("MOVE TO PREVIOUS");
				previousEntryId = cursor.getLong(0);
				previousEntry = cursor.getString(1);
			}
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		if (lastEntry != null && previousEntry != null) {
			System.out.println("PERFORM UNDO, lastEntry: " + lastEntry + ", prev: " + previousEntry);
			// perform actual undo
			// delete last entry from undo_log table
			SQLiteStatement undoLogStatement = db.compileStatement("DELETE FROM UNDO_LOG WHERE ID = ?");
			undoLogStatement.bindLong(1, lastEntryId);
			undoLogStatement.execute();
			undoLogStatement.close();
			
			// update entry in points table
			SQLiteStatement updatePointsStatement = db.compileStatement("UPDATE POINTS SET TIMEFOUND = NULL WHERE NAME = ?");
			updatePointsStatement.bindString(1, previousEntry);
			updatePointsStatement.execute();
			updatePointsStatement.close();
			
			// update current target in status table
			SQLiteStatement updateStatus = db.compileStatement("UPDATE STATUS SET VALUE = ? WHERE ELEMENT = ?");
			updateStatus.bindString(1, previousEntry);
			updateStatus.bindString(2, CURRENT_TARGET);
			updateStatus.execute();
			updateStatus.close();
		} else if (previousEntry == null) {
			// delete vertrek naar
			// update current target in status table
			System.out.println("No previous entry!");
			SQLiteStatement updateStatus = db.compileStatement("UPDATE STATUS SET VALUE = NULL WHERE ELEMENT = ?");
			updateStatus.bindString(1, CURRENT_TARGET);
			updateStatus.execute();
			updateStatus.close();
			
			// delete previous entry from undo_log table
			SQLiteStatement undoLogStatement = db.compileStatement("DELETE FROM UNDO_LOG WHERE ID = ?");
			undoLogStatement.bindLong(1, lastEntryId);
			undoLogStatement.execute();
			undoLogStatement.close();
		}
		//db.close();
		
	}

	public void storeLogItem(LogItem logItem) {
		SQLiteStatement stmt = db.compileStatement("INSERT INTO LOG(DIRECTION, MESSAGE, MOMENT) VALUES(?,?,?)");
		stmt.bindLong(1, logItem.getDirection());
		stmt.bindString(2, logItem.getMessage());
		stmt.bindLong(3, logItem.getMoment());
		stmt.execute();
		stmt.close();
		//db.close();
//		db.execSQL("INSERT INTO LOG(DIRECTION, MESSAGE, MOMENT) VALUES(" + logItem.getDirection() + ",'" + logItem.getMessage() + "', " + logItem.getMoment() + ");");
	}

	public Cursor fetchAllLogItems() {
		return db.query("LOG", new String[] { "ID as _id","DIRECTION","MESSAGE","MOMENT" }, null, null, null, null, "MOMENT DESC");
	}

	public List<Clue> getTrackLog() {
		List<Clue> clues = new ArrayList<Clue>();
		
		Cursor cursor = db.query("POINTS",
				new String[] { "NAME", "COORDINATE", "WORD", "TIMEFOUND" }, "TIMEFOUND  > 0", null, null, null,
				"TIMEFOUND");
		if (cursor.moveToFirst()) {
			do {
				clues.add(new Clue(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getLong(3)));
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		String target = getStatus(CURRENT_TARGET);
		if (target != null) {
			clues.add(getClue(target));
		}
		
		return clues;
	}
	
	private static class OpenHelper extends SQLiteOpenHelper {
		
		private static final String DATABASE_NAME = "hunted";
		private static final int DATABASE_VERSION = 8;
		
		public OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// create table
			db.execSQL(CREATE_POINTS_SQL);
			db.execSQL(CREATE_STATUS_SQL);
			db.execSQL(CREATE_LOG_SQL);
			db.execSQL(CREATE_UNDO_SQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int fromVersion, int toVersion) {
			db.execSQL(DROP_POINTS_SQL);
			db.execSQL(DROP_STATUS_SQL);
			db.execSQL(DROP_LOG_SQL);
			db.execSQL(DROP_UNDO_SQL);
			db.execSQL(CREATE_POINTS_SQL);
			db.execSQL(CREATE_STATUS_SQL);
			db.execSQL(CREATE_LOG_SQL);
			db.execSQL(CREATE_UNDO_SQL);
		}		
	}

	public void close() {
		db.close();
		
	}

}
