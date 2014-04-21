/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.automobiles.lib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author alexx
 */
public class ServerConn {

    public static String url = "https://www.drivers.ge/catalog";

    // Makes HttpURLConnection and returns InputStream
    public static InputStream getHttpConnection(String urlString)
            throws IOException {
        InputStream stream = null;
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = httpConnection.getInputStream();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream;
    }

    // Creates Bitmap from InputStream and returns it
    public static Bitmap downloadImage(String url) {
        Bitmap bitmap = null;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;

        try {
            InputStream stream = getHttpConnection(url);
            bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
            stream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
            return null;
        }
        return bitmap;
    }

    // Load url in image view
    public static void loadUrlInImageView(final ImageView im, String url) {

        String img_url = ServerConn.url + url;
        AsyncTask loader = new AsyncTask<String, Void, Bitmap>() {

            private String error = null;

            @Override
            protected Bitmap doInBackground(String... args) {

                try {
                    Bitmap bm = BitmapCache.getInstance().getBitmapFromDiskCache(args[0]);
                    if (bm == null) {
                        bm = downloadImage(args[0]);
                        BitmapCache.getInstance().addBitmapToCache(args[0], bm);
                    }
                    return bm;
                } catch (Exception e) {
                    error = e.toString();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap result) {

                if (result != null && result.getByteCount() > 0) {
                    im.setImageBitmap(result);
                } else {
                    im.setImageResource(MyResource.getDrawable(im.getContext(), "noimage"));
                }
            }
        };
        loader.execute(new String[]{img_url});
    }

    // Load url in image view
    public static void loadUrlInImageView(final ImageView im, String url, final int height) {

        String img_url = ServerConn.url + url;
        AsyncTask loader = new AsyncTask<String, Void, Bitmap>() {

            private String error = null;

            @Override
            protected Bitmap doInBackground(String... args) {

                try {
                    Bitmap bm = BitmapCache.getInstance().getBitmapFromDiskCache(args[0]);
                    if (bm == null) {
                        bm = downloadImage(args[0]);
                        BitmapCache.getInstance().addBitmapToCache(args[0], bm);
                    }
                    return bm;
                } catch (Exception e) {
                    error = e.toString();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap result) {

                try {
                    if (result != null && result.getByteCount() > 0 && result.getHeight() > 0) {
                        int reswidth = result.getWidth();
                        int resheight = result.getHeight();
                        float width = (float) reswidth * ((float) height / (float) resheight);

                        im.setImageBitmap(Bitmap.createScaledBitmap(result, (int) width, height, false));
                    } else {
                        im.setImageResource(MyResource.getDrawable(im.getContext(), "noimage"));
                    }
                } catch (Exception e) {
                    MyAlert.alertWin(im.getContext(), e.toString());
                }
            }
        };
        loader.execute(new String[]{img_url});
    }
}
