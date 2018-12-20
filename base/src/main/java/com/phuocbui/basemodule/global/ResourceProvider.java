package com.phuocbui.basemodule.global;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import javax.inject.Inject;

import androidx.annotation.RawRes;
import androidx.annotation.StringRes;

public class ResourceProvider {

    private Context context;

    @Inject
    public ResourceProvider(Context context) {
        this.context = context.getApplicationContext();
    }

    public String getString(@StringRes int resId) {
        return context.getString(resId);
    }

    public String getString(@StringRes int formatId, Object... formatArgs) {
        return context.getString(formatId, formatArgs);
    }

    public String readJsonFile(@RawRes int rawId) {
        InputStream is = context.getResources().openRawResource(rawId);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return writer.toString();
    }
}
