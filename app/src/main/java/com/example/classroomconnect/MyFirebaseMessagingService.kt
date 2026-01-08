package com.example.classroomconnect
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "From: ${remoteMessage.from}")
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FCM", "Data payload: ${remoteMessage.data}")
        }
        remoteMessage.notification?.let {
            Log.d("FCM", "Notification Body: ${it.body}")
        }
    }
}
