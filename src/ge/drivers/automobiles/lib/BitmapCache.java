/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.automobiles.lib;

import ge.drivers.automobiles.bitmapfun.DiskLruCache;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import java.io.File;

/**
 *
 * @author alexx
 */
public class BitmapCache {

    private static BitmapCache instance = null;
    private DiskLruCache mDiskLruCache = null;
    private boolean avail = true;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "auto_images";

    private BitmapCache() {
    }

    public static BitmapCache getInstance() {

        if (instance == null) {
            instance = new BitmapCache();
        }

        return instance;
    }

    public void initCache(Context context) {

        File cacheDir = DiskLruCache.getDiskCacheDir(context, DISK_CACHE_SUBDIR);
        try {
            mDiskLruCache = DiskLruCache.openCache(context, cacheDir, DISK_CACHE_SIZE);
        } catch (Exception e) {
            avail = false;
        }
    }

    public void addBitmapToCache(String key, Bitmap bitmap) {
        try {
            if (mDiskLruCache != null && mDiskLruCache.get(key) == null) {
                mDiskLruCache.put(key, bitmap);
            }
        } catch (Exception e) {
            avail = false;
        }
    }

    public Bitmap getBitmapFromDiskCache(String key) {

        if (mDiskLruCache == null) {
            return null;
        }

        return mDiskLruCache.get(key);
    }
    
    public void clearCache(Context context){
    
        if (mDiskLruCache != null){
            DiskLruCache.clearCache(context, DISK_CACHE_SUBDIR);
        }
    }
}
