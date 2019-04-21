package com.example.licl.myapplication.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class MyIntentService extends IntentService {

    private static final String ACTION_INIT="initApplication";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MyIntentService(String name) {
        super(name);
    }

    public static void start(Context context){
        Intent intent=new Intent(context,MyIntentService.class);
        intent.setAction(ACTION_INIT);
        context.startService(intent);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent!=null){
            final String action=intent.getAction();
            if(ACTION_INIT.equals(action)){
                initApplication();
            }
        }
    }

    private void initApplication(){

    }
}
