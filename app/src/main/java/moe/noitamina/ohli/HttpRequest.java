package moe.noitamina.ohli;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ghsong on 2018-07-31.
 * 개발 중
 */

public class HttpRequest {
    public static final long DEVIDE = 1024 * 1024 * 1; // 파일 분할 기준

    public static class FileField {
        File file;
        long seek;
        long size;
        public FileField(File file) {
            this(file, 0, file.length());
        }
        public FileField(File file, long seek, long size) {
            this.file = file;
            this.seek = seek;
            this.size = size;
        }
    }

    private static final String LINE_FEED = "\r\n";
    private String charset = "UTF-8";
    private String url;
    private String boundary;
    private Map<String, String> headerField = new HashMap<>();
    private Map<String, String> formField = new HashMap<>();
    private Map<String, FileField> fileField = new HashMap<>();

    public HttpRequest(String url) {
        this(url, null);
    }
    public HttpRequest(String url, Map<String, Object> param) {
        this.url = url;
        boundary = "===" + System.currentTimeMillis() + "===";
        if (param != null) {
            for (String key : param.keySet()) {
                formField.put(key, param.get(key).toString());
            }
        }
    }
    public void addHeaderField(String name, String value) {
        headerField.put(name, value);
    }
    public void addFormField(String name, String value) {
        formField.put(name, value);
    }
    public void addFileField(String name, File file) {
        fileField.put(name, new FileField(file));
    }
    public void addFileField(String name, FileField value) {
        fileField.put(name, value);
    }

    public Map<String, Object> requestGetJsonToMap() {
        return new Gson().fromJson(this.requestGet(), HashMap.class);
    }
    public Map<String, Object> requestPostJsonToMap() {
        return new Gson().fromJson(this.requestPost(), HashMap.class);
    }
    public Map<String, Object> requestPostBodyJsonToMap() {
        return new Gson().fromJson(this.requestPostBody(), HashMap.class);
    }
    public Map<String, Object> requestPostJsonToMap(String body) {
        return new Gson().fromJson(this.requestPost(body), HashMap.class);
    }
    public Map<String, Object> requestPostJsonToMap(Map<String, Object> data) {
        return new Gson().fromJson(this.requestPost(data), HashMap.class);
    }
    public Map<String, Object> requestMultipartFileJsonToMap() {
        return new Gson().fromJson(this.requestMultipartFile(), HashMap.class);
    }

    public String requestGet() {
        try {
            URL url = new URL(this.url + "?" + paramString(this));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");
            for (String name : headerField.keySet()) {
                conn.setRequestProperty(name, headerField.get(name));
            }
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String response = inputStreamToString(new BufferedInputStream(conn.getInputStream()));
                conn.disconnect();
                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String requestPost() {
        return requestPost(paramString(this));
    }
    public String requestPostBody() {
        return requestPost(new Gson().toJson(formField));
    }
    public String requestPost(Map<String, Object> data) {
        return requestPost(new Gson().toJson(data));
    }
    public String requestPost(String body) {
        headerField.put("Content-Type", "application/json;");
        try {
            URL url = new URL(this.url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            for (String name : headerField.keySet()) {
                conn.setRequestProperty(name, headerField.get(name));
            }
            conn.connect();

            if (body != null) {
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(body);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String response = inputStreamToString(new BufferedInputStream(conn.getInputStream()));
                conn.disconnect();
                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String paramString(HttpRequest request) {
        StringBuilder paramString = null;
        for (String key : request.formField.keySet()) {
            if (paramString == null) {
                paramString = new StringBuilder();
            } else {
                paramString.append('&');
            }
            String value = request.formField.get(key).toString();
            try {
                value = URLEncoder.encode(value, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            paramString.append(key).append('=').append(/* html escape 넣어야 함*/(value));
        }
        return (paramString == null) ? null : paramString.toString();
    }

    public String requestMultipartFile() {
        try {
            // creates a unique boundary based on time stamp
            URL url = new URL(this.url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true); // indicates POST method
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            OutputStream os = conn.getOutputStream();
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, charset), true);

            for (String name : headerField.keySet()) {
                pw.append(name + ": " + headerField.get(name)).append(LINE_FEED);
                pw.flush();
            }
            for (String name : formField.keySet()) {
                pw.append("--" + boundary).append(LINE_FEED);
                pw.append("Content-Disposition: form-data; name=\"" + name + "\"") .append(LINE_FEED);
                pw.append("Content-Type: text/plain; charset=" + charset).append(LINE_FEED);
                pw.append(LINE_FEED);
                pw.append(formField.get(name)).append(LINE_FEED);
                pw.flush();
            }
            for (String name : fileField.keySet()) {
                FileField field = fileField.get(name);
                String fileName = field.file.getName();

                pw.append("--" + boundary).append(LINE_FEED);
                pw.append("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
                pw.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
                pw.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
                pw.append(LINE_FEED);
                pw.flush();

                FileInputStream fs = new FileInputStream(field.file);
                fs.skip(field.seek);
                int bytesRead = 4096;
                byte[] buffer = new byte[bytesRead];
                long written = 0;
                while ((bytesRead = fs.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                    written += bytesRead;
                    if (written >= field.size) break;
                }
                os.flush();
                fs.close();
                pw.flush();
            }

            pw.append(LINE_FEED).flush();
            pw.append("--" + boundary + "--").append(LINE_FEED);
            pw.close();

            // checks server's status code first
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String response = inputStreamToString(new BufferedInputStream(conn.getInputStream()));
                conn.disconnect();
                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String inputStreamToString(InputStream in) {
        String result = "";
        if (in == null) {
            return result;
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            result = out.toString();
            reader.close();

            return result;
        } catch (Exception e) {
            Log.e("InputStream", "Error : " + e.toString());
            return result;
        }
    }
}
