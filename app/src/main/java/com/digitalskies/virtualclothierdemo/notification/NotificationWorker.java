package com.digitalskies.virtualclothierdemo.notification;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationWorker extends Worker {

    Context context;
    WorkerParameters parameters;
    public static final String USERNAME="username";
    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context=context;
        parameters=workerParams;
    }

    @NonNull
    @Override
    public Result doWork() {
        String userName=getInputData().getString(USERNAME);
        NotificationCreator notificationCreator=new NotificationCreator();
        notificationCreator.buildNotification(userName,context);
        return Result.success();
    }
}
