package com.example.clonewhatsapp.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.clonewhatsapp.ChatActivity;
import com.example.clonewhatsapp.MainActivity;
import com.example.clonewhatsapp.R;
import com.example.clonewhatsapp.api.SupabaseClient;
import com.example.clonewhatsapp.utils.SessionManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    public static final String CHANNEL_ID = "chat_messages_channel";
    public static final String CHANNEL_NAME = "Mensajes de Chat";
    public static final String CHANNEL_DESC = "Notificaciones de nuevos mensajes en CloneWhatsApp";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = "Nuevo mensaje";
        String body = "Tienes un mensaje nuevo";
        String senderId = null;
        String senderName = null;
        String conversationId = null;

        if (remoteMessage.getNotification() != null) {
            if (remoteMessage.getNotification().getTitle() != null) {
                title = remoteMessage.getNotification().getTitle();
            }
            if (remoteMessage.getNotification().getBody() != null) {
                body = remoteMessage.getNotification().getBody();
            }
        }

        if (!remoteMessage.getData().isEmpty()) {
            if (remoteMessage.getData().containsKey("title")) {
                title = remoteMessage.getData().get("title");
            }
            if (remoteMessage.getData().containsKey("body")) {
                body = remoteMessage.getData().get("body");
            }
            senderId = remoteMessage.getData().get("sender_id");
            senderName = remoteMessage.getData().get("sender_name");
            conversationId = remoteMessage.getData().get("conversation_id");
        }

        showNotification(title, body, conversationId, senderId, senderName);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Nuevo token FCM generado: " + token);

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        String userId = sessionManager.getUserId();
        String authToken = sessionManager.getToken();

        if (userId != null && !userId.isEmpty() && authToken != null && !authToken.isEmpty()) {
            SupabaseClient.updateFcmToken(authToken, userId, token, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG, "Error actualizando token FCM: " + e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Token FCM actualizado en Supabase desde onNewToken");
                    } else {
                        Log.e(TAG, "Error HTTP actualizando token FCM: " + response.code());
                    }
                    response.close();
                }
            });
        }
    }

    private void showNotification(String title, String messageBody, String conversationId, String senderId, String senderName) {
        createNotificationChannel(this);

        Intent intent;
        if (senderId != null && !senderId.isEmpty()) {
            intent = new Intent(this, ChatActivity.class);
            if (conversationId != null) {
                intent.putExtra("conversation_id", conversationId);
            }
            intent.putExtra("other_user_id", senderId);
            intent.putExtra("other_user_name", senderName != null ? senderName : title);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int requestCode = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(requestCode, notificationBuilder.build());
        }
    }

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESC);
            channel.enableVibration(true);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}