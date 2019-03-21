package com.young40.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@interface JSEvent {
    String event();
}

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick");
                MainActivity.this.jsevent("js1", "arg strs js1");
                MainActivity.this.jsevent("js2", "arg strs js2");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void jsevent(String event, String msg) {
        Log.d(TAG, "jsevent");

        List<Method> methods = getMethodAnnotatedJsEvent(MainActivity.class);

        Log.d(TAG, "got count: " + methods.size() + "");

        Method method = getTargetMethod(methods, event);

        if (method != null) {
            Log.d(TAG, "got methed by: " + event);

            try {
                method.invoke(this, msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "method not found by: " + event);
        }

    }

    @JSEvent(event = "js1")
    private void onJSMethod1(String msg) {
        Log.d(TAG, "onJSMethod1: " + msg);
    }

    @JSEvent(event = "js2")
    private void onJSMethod2(String msg) {
        Log.d(TAG, "onJSMethod2: " + msg);
    }

    private static List<Method> getMethodAnnotatedJsEvent(final Class<?> type) {
        final List<Method> methods = new ArrayList<>();

        final List<Method> allMethods = new ArrayList<>(Arrays.asList(type.getDeclaredMethods()));

        for (final Method method: allMethods) {
            if (method.isAnnotationPresent(JSEvent.class)) {
                methods.add(method);
            }
        }

        return methods;
    }

    private static Method getTargetMethod(List<Method> list, String event) {
        for (Method m: list) {
            JSEvent jsEvent = m.getAnnotation(JSEvent.class);

            if (jsEvent.event().equals(event)) {
                return m;
            }
        }

        return null;
    }
}
