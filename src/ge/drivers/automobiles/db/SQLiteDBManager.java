/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.automobiles.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import ge.drivers.automobiles.lib.MyAlert;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author alexx
 * Implements database manager interface for SQLite database
 */
public class SQLiteDBManager extends SQLiteOpenHelper implements DatabaseManager {    
    public static final String DATABASE_PATH = "/data/data/ge.drivers.automobiles/databases/";
    public static final String DATABASE_NAME = "auto_catalog.sqlite";
    public static final int DATABASE_VERSION = 1;
    private SQLiteDatabase database;
    private Context myContext;
    
    public SQLiteDBManager(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            myContext = context;
        }
    
    /**
     * Open DataBase connection
     */
    public void openDatabase(){
            
            if (!checkDatabase()){
                try {
                    //If database does not exist, copy it from assets folder
                    copyDatabase();
                }
                catch (IOException e){
                    //Alert database copy error
                    MyAlert.alertWin(myContext, "COPY DB Error: " + e);
                }
                
                String db_path = myContext.getFilesDir() + DATABASE_NAME;
                try {
                    //open database connection
                    database = SQLiteDatabase.openDatabase(db_path, null, SQLiteDatabase.OPEN_READWRITE);
                }
                catch (SQLiteException e){
                    //Alert open database error
                    MyAlert.alertWin(myContext, "Open DB Error: " + e);
                }
            }
        }
    
    /**
     * Close database connection
     */
    public void closeDatabase(){
            database.close();
        }
    
    /**
     * executes database query and returns cursor of the result
     */
    public Cursor qin(String query){
            Cursor cursor = database.rawQuery(query, null);

            return cursor;
        }
    
    /**
     * executes database query
     */
    public void qout(String query){
            database.execSQL(query);
        }
    
    /**
     * Checks database exists or not
     */
    private boolean checkDatabase(){
        
            String db_path = myContext.getFilesDir() + DATABASE_NAME;

            try {
                database = SQLiteDatabase.openDatabase(db_path, null, SQLiteDatabase.OPEN_READWRITE);
            }
            catch (SQLiteException e){
                return false;
            }

            return true;
        }
    
    /**
     * Copies database from assets folder
     */
    private void copyDatabase() throws IOException {

            //Open your local db as the input stream
            InputStream myInput = myContext.getAssets().open(DATABASE_NAME);

            //Path to the just created empty db
            String outFileName = myContext.getFilesDir() + DATABASE_NAME;

            //Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }
    
    /**
     * Override onCreate method
     */
    public void onCreate(SQLiteDatabase db) {

        }
    
    /**
     * Override onUpgrade method
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onCreate(db);
        }
}
