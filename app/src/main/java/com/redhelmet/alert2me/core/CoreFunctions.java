package com.redhelmet.alert2me.core;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.redhelmet.alert2me.interfaces.ServerCallback;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.redhelmet.alert2me.R;

public class CoreFunctions {

    public static Context _context;


    static int BUFFER_SIZE =8192;
    public CoreFunctions(Context context) {
        this._context = context;

    }


    public static String ConfigUrl(){
            String url=_context.getString(R.string.api_url);
        String platform=_context.getString(R.string.platform);
        String appName=_context.getString(R.string.appName);

        return url+"config/ios" ; //+ platform ;//+ "?appName="+appName;


    }
    public static Boolean VerifySuccess(String value){

        if(value=="true"){
            return true;
        }
        return false;
    }



    public void ZipDownload(String url,final ServerCallback callback){

        if(_context!=null) {
            ZipDownloadRequestHandler request = new ZipDownloadRequestHandler(Request.Method.GET, url,
                    new Response.Listener<byte[]>() {
                        @Override
                        public void onResponse(byte[] response) {
                            // TODO handle the response
                            try {
                                if (response != null) {

                                    new WriteUnzip(callback).execute(new ByteArrayInputStream(response) );
                                    ;
                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
                                e.printStackTrace();

                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO handle the error

                        Toast.makeText(_context, _context.getString(R.string.timeOut), Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                    callback.onSuccess(false);

                }
            }, null);
            RequestQueue mRequestQueue = Volley.newRequestQueue(_context, new HurlStack());
            mRequestQueue.add(request);
        }
    }
    private class WriteUnzip extends AsyncTask<InputStream, Void, Void> {
        private ServerCallback listener;

        public WriteUnzip(ServerCallback listener){
            this.listener=listener;
        }

        @Override
        protected Void doInBackground(InputStream... streams) {
            for (InputStream stream : streams) {
                try {
                    writeFile(stream);

                } catch (IOException e) {
                    // ExceptionHandler.saveException(e, null);
                    listener.onSuccess(false);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void value) {
            try {
             if(unzip(new File(_context.getFilesDir() + "/Downloads/")))
            listener.onSuccess(true);

            } catch (IOException e) {
                e.printStackTrace();
                listener.onSuccess(false);
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }


    }

    private void writeFile(InputStream is) throws IOException {

        OutputStream os = null;

        File tmp = File.createTempFile("fileTemp", ".zip", _context.getFilesDir());
        File outputFile = new File(_context.getFilesDir(), "file.zip");
        try {

            os = new BufferedOutputStream(new FileOutputStream(tmp));
            copyStream(is, os);
            tmp.renameTo(outputFile);


        } catch (IOException e) {
            //   ExceptionHandler.saveException(e, null);

        } finally {
            if (tmp != null) {
                try {
                    tmp.delete();
                    tmp = null;
                } catch (Exception ignore) {
                    ;
                }
            }
            if (is != null) {
                try {
                    is.close();
                    is = null;
                } catch (Exception ignore) {
                    ;
                }
            }
            if (os != null) {
                try {
                    os.close();
                    os = null;
                } catch (Exception ignore) {
                    ;
                }
            }
            if (os != null) {
                try {
                    os.close();
                    os = null;
                } catch (Exception ignore) {
                    ;
                }
            }
        }

    }

    private boolean unzip(File targetDirectory) throws IOException {
        FileOutputStream jsonFile = null;
        ZipEntry ze;

        File zipFile = new File(_context.getFilesDir(), "file.zip");
        if (zipFile.exists()) {
            ZipInputStream zis = new ZipInputStream(
                    new BufferedInputStream(new FileInputStream(zipFile)));

            try {
                while ((ze = zis.getNextEntry()) != null) {
                    File file = new File(targetDirectory, ze.getName());
                    File dir = ze.isDirectory() ? file : file.getParentFile();
                    if (!dir.isDirectory() && !dir.mkdirs())
                        throw new FileNotFoundException("Failed to ensure directory: " +
                                dir.getAbsolutePath());
                    if (ze.isDirectory())
                        continue;

                    jsonFile = new FileOutputStream(file);
                    IOUtils.copy(zis, jsonFile);
                    jsonFile.close();
                    zis.closeEntry();
                }
                return true;
            } catch (IOException e) {
                // ExceptionHandler.saveException(e, null);
                return false;
            } finally {
                if (zis != null) {
                    try {
                        zis.close();
                    } catch (IOException ignore) {
                    }
                }
                if (jsonFile != null) {
                    try {
                        jsonFile.close();
                    } catch (IOException e) {
                        //   ExceptionHandler.saveException(e, null);

                    }
                }

            }
        }else{
            return false;
        }
    }


    public static void copyStream(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        copyStream(is, os, buffer, BUFFER_SIZE);
    }


    public static void copyStream(InputStream is, OutputStream os,
                                  byte[] buffer, int bufferSize) throws IOException {
        try {
            for (; ; ) {
                int count = is.read(buffer, 0, bufferSize);
                if (count == -1) {
                    break;
                }
                os.write(buffer, 0, count);
            }
        } catch (IOException e) {
            throw e;
        }
    }

    public static String readJsonFile(File yourFile) {
        BufferedReader streamReader = null;

        if(yourFile.exists()) {
            try {
                streamReader = new BufferedReader(new InputStreamReader(new FileInputStream(yourFile), "UTF-8"));

                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                return responseStrBuilder.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "";
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "";
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }else{
            return "";
        }
    }
}