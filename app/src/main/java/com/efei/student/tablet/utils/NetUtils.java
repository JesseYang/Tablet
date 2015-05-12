package com.efei.student.tablet.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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

    // public static String BASE_URL = "http://www.efei.org/";
    public static String BASE_URL = "http://192.168.0.104:3000/";
    // public static String BASE_URL = "http://192.168.2.101:3000/";

    private NetUtils()
    {
    }

    public static String post(String urlApi, JSONObject params) {
        try {
            StringBuilder response = new StringBuilder();

            HttpURLConnection urlConnection;
            Uri builtUri = Uri.parse(BASE_URL + urlApi).buildUpon().build();
            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
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

    public static String put(String urlApi, JSONObject params) {
        try {
            StringBuilder response = new StringBuilder();

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

    public static String get(String urlApi, String params) {
        try {
            StringBuilder response = new StringBuilder();

            HttpURLConnection urlConnection;
            Uri builtUri = Uri.parse(BASE_URL + urlApi + "?" + params).buildUpon().build();
            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
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
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String download_resource(String urlRes, String filename, String type) {
        try {

            HttpURLConnection urlConnection;
            URL url = new URL(BASE_URL + urlRes);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            // urlConnection.setDoOutput(true);
            urlConnection.connect();

            FileUtils.ensure_folder();
            File storageRoot = Environment.getExternalStorageDirectory();
            String path;
            switch (type) {
                case "avatar":
                    path = FileUtils.AVATAR_FOLDER + filename;
                    break;
                case "video":
                    path = FileUtils.VIDEO_FOLDER + filename;
                    break;
                case "textbook":
                    path = FileUtils.TEXTBOOK_FOLDER + filename;
                    break;
                default:
                    return null;
            }
            File file = new File(storageRoot, path);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            FileOutputStream fileOutputStream = new FileOutputStream(file);
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
            return file.getPath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
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
