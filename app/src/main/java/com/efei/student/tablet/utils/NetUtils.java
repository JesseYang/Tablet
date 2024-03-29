package com.efei.student.tablet.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.efei.student.tablet.views.SettingView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jesse on 15-5-3.
 */
public final class NetUtils {

    public static String BASE_URL = "http://www.efei.org/";
    // public static String BASE_URL = "http://192.168.0.102:3000/";

    private NetUtils()
    {
    }

    public static String post(Context context, String urlApi, JSONObject params) {

        try {
            StringBuilder response = new StringBuilder();

            String auth_key = getAuthKey(context);

            try {
                params.put("auth_key", auth_key);
            } catch (Exception e) {
                e.printStackTrace();
            }

            HttpURLConnection urlConnection;
            Uri builtUri = Uri.parse(BASE_URL + urlApi).buildUpon().build();
            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(3000);
            urlConnection.setReadTimeout(3000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept","application/json");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            // set params
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8")
            );
            writer.write(params.toString());
            writer.flush();
            writer.close();

            urlConnection.connect();
            int res_code = urlConnection.getResponseCode();
            if (res_code == HttpURLConnection.HTTP_OK) {

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));

                String strLine = null;
                while ((strLine = input.readLine()) != null)
                {
                    response.append(strLine);
                }
                input.close();
                return response.toString();
            }
            return "";
        } catch (java.net.SocketTimeoutException e) {
            e.printStackTrace();
            return "timeout";
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return "timeout";
        }
    }

    public static String put(Context context, String urlApi, JSONObject params) {
        try {
            StringBuilder response = new StringBuilder();

            String auth_key = getAuthKey(context);

            try {
                params.put("auth_key", auth_key);
            } catch (Exception e) {
                e.printStackTrace();
            }

            HttpURLConnection urlConnection;
            Uri builtUri = Uri.parse(BASE_URL + urlApi).buildUpon().build();
            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("PUT");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept","application/json");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            // set params
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8")
            );
            writer.write(params.toString());
            writer.flush();
            writer.close();

            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));

                String strLine = null;
                while ((strLine = input.readLine()) != null)
                {
                    response.append(strLine);
                }
                input.close();
                return response.toString();
            }
            return "";
        } catch (IOException e) {
            return "";
        } finally {
        }
    }

    public static String getAuthKey(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPref", 0);
        String auth_key = sharedPreferences.getString("auth_key", "");
        return auth_key;
    }

    public static String get(Context context, String urlApi, String params) {
        try {
            StringBuilder response = new StringBuilder();

            String auth_key = getAuthKey(context);

            if (params.equals("")) {
                params = "auth_key=" + auth_key;
            } else {
                params += "&auth_key=" + auth_key;
            }

            HttpURLConnection urlConnection;
            Uri builtUri = Uri.parse(BASE_URL + urlApi + "?" + params).buildUpon().build();
            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(3000);
            urlConnection.setReadTimeout(3000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));

                String strLine = null;
                while ((strLine = input.readLine()) != null)
                {
                    response.append(strLine);
                }
                input.close();
                return response.toString();
            }
            return "";
        } catch (java.net.SocketTimeoutException e) {
            e.printStackTrace();
            return "timeout";
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return "timeout";
        }
    }

    public static boolean download_video(SettingView.DownloadContentTask task, String video_filename, Context context) {
        try {
            HttpURLConnection urlConnection;
            URL url;
            String urlRes = "/videos/" + video_filename;
            url = new URL(BASE_URL + urlRes);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            FileOutputStream fileOutputStream = FileUtils.get_output_stream(video_filename, "video", context);

            InputStream inputStream = urlConnection.getInputStream();
            int totalSize = urlConnection.getContentLength();
            int downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            String totalSizeInMb = String.valueOf(Math.round(totalSize / 1024 / 102.4) / 10.0);
            double prev = 0;
            double cur = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                cur = Math.round(downloadedSize / 1024 / 102.4) / 10.0;
                if (cur != prev) {
                    task.appendProgress("共" + totalSizeInMb + "MB，已下载" + String.valueOf(cur) + "MB。");
                    prev = cur;
                }
            }
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void download_resource(String urlRes, String filename, String type, Context context) {
        try {

            HttpURLConnection urlConnection;
            URL url;
            if (urlRes.startsWith("http")) {
                url = new URL(urlRes);
            } else {
                url = new URL(BASE_URL + urlRes);
            }

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            
            FileOutputStream fileOutputStream = FileUtils.get_output_stream(filename, type, context);

            InputStream inputStream = urlConnection.getInputStream();
            int totalSize = urlConnection.getContentLength();
            int downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
            }
            fileOutputStream.close();
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public static boolean isConnect(Context context)
    {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try
        {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null)
            {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected())
                {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
