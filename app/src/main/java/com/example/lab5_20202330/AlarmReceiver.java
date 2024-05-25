package com.example.lab5_20202330;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import java.util.Date;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {

    private final String CHANNEL_ID = "mirecordatorioapp_channel";
    List<Task> tasks = TaskStorage.loadTasks(String.valueOf(20202332));
    @Override
    public void onReceive(Context context, Intent intent) {
         // Obtener lista de tareas
        for (Task task : tasks) {
            long dueTime = task.getDueDate();
            long currentTime = System.currentTimeMillis();

            long diffInMs = dueTime - currentTime;

            if (diffInMs <= 0) {
                // Si la tarea está programada para una hora pasada, ignórala
                continue;
            }

            if (diffInMs <= 60 * 60 * 1000) { // Si la diferencia es de una hora o menos
                showNotification(context, task);
            }
        }
    }



    private void showNotification(Context context, Task task) {
        // Crear una notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.time2)
                .setContentTitle("Recordatorio")
                .setContentText("¡Hola! La tarea '" + task.getTitle() + "' está programada para ahora.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Verificar si el dispositivo tiene Android Oreo (API 26) o superior, y si es así, crear un canal de notificación
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "Channel for my app";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        // Mostrar la notificación
        notificationManager.notify((int) task.getDueDate(), builder.build());
    }

}

