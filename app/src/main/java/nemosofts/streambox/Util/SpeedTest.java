package nemosofts.streambox.Util;


import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class SpeedTest extends AsyncTask<String, String, String> {

    private static long downloadSpeedKbps = 0;
    private final OnSpeedCheckListener listener;

    public SpeedTest(OnSpeedCheckListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        downloadSpeedKbps = 0;
        listener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String downloadUrl = "https://download.nemosofts.com/sample.txt";

            downloadSpeedKbps = 0;
            long startTime = System.currentTimeMillis();
            long downloadedBytes = 0;

            URL url = new URL(downloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                downloadedBytes += bytesRead;
                // You can update a progress bar here if needed
            }
            inputStream.close();
            connection.disconnect();

            long endTime = System.currentTimeMillis();
            long totalTimeInMillis = endTime - startTime;

            // Calculate download speed in Kbps (kilobits per second)
            downloadSpeedKbps = (long) ((downloadedBytes * 8.0) / (totalTimeInMillis / 1000.0));

            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        listener.onEnd(s, "Download Speed: " + ApplicationUtil.readableFileSize(downloadSpeedKbps));
        super.onPostExecute(s);
    }

    public interface OnSpeedCheckListener {
        void onStart();
        void onEnd(String success, String speedMbps);
    }
}