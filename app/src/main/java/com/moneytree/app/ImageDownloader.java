package com.moneytree.app;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class ImageDownloader extends AsyncTask<String,Void, Bitmap> {
    OutputStream output;

    @Override
    protected Bitmap doInBackground(String... strings) {
		int count;
		Long tsLong = System.currentTimeMillis() / 1000;
		String ts = tsLong.toString();
		try {
			URL url = new URL(strings[0]);
			URLConnection conection = url.openConnection();
			conection.connect();


			int lenghtOfFile = conection.getContentLength();

			// download the file
			InputStream input = new BufferedInputStream(url.openStream(),
					8192);

			// Output stream
			output = new FileOutputStream(Environment
					.getExternalStorageDirectory().toString()
					+ "DownloadedFile" + ts + ".jpg");


			byte data[] = new byte[1024];

			long total = 0;

			while ((count = input.read(data)) != -1) {
				total += count;

				int cur = (int) ((total * 100) / lenghtOfFile);

				if (Math.min(cur, 100) > 98) {
					try {
						// Sleep for 5 seconds
						Thread.sleep(500);
					} catch (InterruptedException e) {
						Log.d("Failure", "sleeping failure");
					}
				}

				Log.i("currentProgress", "currentProgress: " + Math.min(cur, 100) + "\n " + cur);

				output.write(data, 0, count);
			}

			// flushing output
			output.flush();

			// closing streams
			output.close();
			input.close();

		} catch (Exception e) {
			Log.e("Error: ", e.getMessage());
		}

		return null;
	}
}
