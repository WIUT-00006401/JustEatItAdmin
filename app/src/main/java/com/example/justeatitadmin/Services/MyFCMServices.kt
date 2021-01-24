package com.example.justeatitadmin.Services

import android.content.Intent
import com.example.justeatitadmin.Common.Common
import com.example.justeatitadmin.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class MyFCMServices: FirebaseMessagingService() {
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Common.updateToken(this,p0,true,false)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val dataRecv = remoteMessage.data
        if (dataRecv != null)
        {
            if (dataRecv[Common.NOTI_TITLE]!!.equals("New Order"))
            {
                val intent = Intent(this,MainActivity::class.java)
                intent.putExtra(Common.IS_OPEN_ACTIVITY_NEW_ORDER,true)
                Common.showNotification(this,Random().nextInt(),
                    dataRecv[Common.NOTI_TITLE],
                    dataRecv[Common.NOTI_CONTENT],
                    intent)
            }
            else
                Common.showNotification(this, Random().nextInt(),
                    dataRecv[Common.NOTI_TITLE],
                    dataRecv[Common.NOTI_CONTENT],
                    null)
        }
    }
}