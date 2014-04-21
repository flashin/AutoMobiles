/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.automobiles.modules;

import android.content.Context;
import android.database.Cursor;
import ge.drivers.automobiles.db.SQLiteDBManager;

/**
 *
 * @author alexx
 */
public class AboutInfo {

    private int totalMakes = 0;
    private int totalModels = 0;
    private int totalCars = 0;

    public AboutInfo(Context context) {

        SQLiteDBManager dbManager = new SQLiteDBManager(context);
        dbManager.openDatabase();

        String query = "select count(1) as cnt from Makes t";
        Cursor cursor = dbManager.qin(query);
        if (cursor.moveToNext()){
            totalMakes = cursor.getInt(0);
        }
        
        query = "select count(1) as cnt from Models t";
        cursor = dbManager.qin(query);
        if (cursor.moveToNext()){
            totalModels = cursor.getInt(0);
        }
        
        query = "select count(1) as cnt from Cars t";
        cursor = dbManager.qin(query);
        if (cursor.moveToNext()){
            totalCars = cursor.getInt(0);
        }
    }
    
    public int getTotalMakes(){
    
        return totalMakes;
    }
    
    public int getTotalModels(){
    
        return totalModels;
    }
    
    public int getTotalCars(){
    
        return totalCars;
    }
}
