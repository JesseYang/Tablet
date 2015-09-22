package com.efei.student.tablet.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.HashMap;
import java.util.Map;

public class EfeiApplication extends Application
{
    private static EfeiApplication application;

    private Map<String, Object> map = new HashMap<String, Object>();

    @Override
    public void onCreate()
    {
        super.onCreate();
        application = this;
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    public void onTerminate()
    {
        application = null;
        super.onTerminate();
    }

    public static Context getContext()
    {
        return application;
    }

    public static void switchToActivity(Class<? extends Activity> clazz)
    {
        Intent intent = new Intent(EfeiApplication.getContext(), clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        EfeiApplication.getContext().startActivity(intent);
    }

    public void addTemporary(String key , Object objTmp)
    {
        map.put(key, objTmp);
    }

    @SuppressWarnings("unchecked")
    public <T> T removeTemporary(String key)
    {
        return (T) map.get(key);
    }
}
