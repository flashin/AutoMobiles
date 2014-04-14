/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.automobiles.db;

import android.database.Cursor;

/**
 *
 * @author alexx
 * Database interface
 */
public interface DatabaseManager {
    //Opens database
    void openDatabase();
    
    //Close database connection
    void closeDatabase();
    
    //get cursor of the query
    Cursor qin(String query);
    
    //execute query
    void qout(String query);
}
