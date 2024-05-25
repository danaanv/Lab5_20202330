package com.example.lab5_20202330;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.List;

public class TaskNotificationService extends Service {

    private static final long CHECK_INTERVAL_MS = 1000 * 60 * 60; // Verificar cada hora
    private static final long THREE_HOURS_MS = 1000 * 60 * 60 * 3; // Tres horas en milisegundos

    private TaskStorage taskStorage;
    private String currentPucpCode;

    @Override
    public void onCreate() {
        super.onCreate();
        taskStorage = new TaskStorage(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Verificar las tareas y recordatorios al iniciar el servicio
        checkTasksAndReminders();

        // Programar un proceso periódico para verificar las tareas y recordatorios cada CHECK_INTERVAL_MS
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, TaskNotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, alarmIntent, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + CHECK_INTERVAL_MS, CHECK_INTERVAL_MS, pendingIntent);

        // Devolver START_STICKY para que el servicio se reinicie si se cancela
        return START_STICKY;
    }

    private void checkTasksAndReminders() {
        // Lógica para verificar tareas y recordatorios
        List<Task> tasks = taskStorage.loadTasks(currentPucpCode);

        for (Task task : tasks) {
            Long reminder = task.getDueDate();
            showNotificationForReminder(task, reminder);
        }
    }

    private void showNotificationForReminder(Task task, long reminderTime) {
        // Lógica para mostrar la notificación
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("reminder_channel", "Recordatorios", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "reminder_channel")
                .setSmallIcon(R.drawable.time)
                .setContentTitle("Recordatorio de tarea")
                .setContentText("Recordatorio para la tarea: " + task.getTitle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify((int) reminderTime, builder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
