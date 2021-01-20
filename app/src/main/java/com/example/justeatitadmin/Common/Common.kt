package com.example.justeatitadmin.Common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.justeatitadmin.Model.CategoryModel
import com.example.justeatitadmin.Model.FoodModel
import com.example.justeatitadmin.Model.ServerUserModel
import com.example.justeatitadmin.Model.TokenModel
import com.example.justeatitadmin.R
import com.google.firebase.database.FirebaseDatabase
import java.lang.StringBuilder

object Common {

    fun updateToken(context: Context, token: String, isServerToken:Boolean, isShipperToken:Boolean) {
        FirebaseDatabase.getInstance()
            .getReference(TOKEN_REF)
            .child(Common.currentServerUser!!.uid!!)
            //.setValue(TokenModel(Common.currentServerUser!!.phone!!,token,isServerToken,isShipperToken))
            .setValue(TokenModel(Common.currentServerUser!!.phone!!,token))
            .addOnFailureListener{e-> Toast.makeText(context, ""+e.message, Toast.LENGTH_SHORT).show()}
    }

    fun showNotification(context: Context, id: Int, title: String?, content: String?, intent: Intent?) {
        var pendingIntent: PendingIntent?=null
        if (intent != null)
            pendingIntent = PendingIntent.getActivity(context,id,intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val NOTIFICATION_CHANNEL_ID = "DJDEV"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
        {
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
                "Just Eat It", NotificationManager.IMPORTANCE_DEFAULT)

            notificationChannel.description = "Just Eat It"
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.lightColor=(Color.RED)
            notificationChannel.vibrationPattern = longArrayOf(0,1000,500,1000)

            notificationManager.createNotificationChannel(notificationChannel)
        }
        val builder  = NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID)

        builder.setContentTitle(title!!).setContentText(content!!).setAutoCancel(true)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources,R.drawable.ic_restaurant_menu_black_24dp))
        if (pendingIntent!=null)
            builder.setContentIntent(pendingIntent)

        val notification = builder.build()

        notificationManager.notify(id,notification)
    }

    fun getNewOrderTopic(): String {
        return StringBuilder("/topics/new_order").toString()
    }

    fun setSpanString(welcome: String, name: String?, txtUser: TextView?) {
        val builder = SpannableStringBuilder()
        builder.append(welcome)
        val txtSpannable = SpannableString(name)
        val boldSpan = StyleSpan(Typeface.BOLD)
        txtSpannable.setSpan(boldSpan, 0, name!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.append(txtSpannable)
        txtUser!!.setText(builder, TextView.BufferType.SPANNABLE)
    }

    fun setSpanStringColor(welcome: String, name: String?, txtUser: TextView?, color: Int) {
        val builder = SpannableStringBuilder()
        builder.append(welcome)
        val txtSpannable = SpannableString(name)
        val boldSpan = StyleSpan(Typeface.BOLD)
        txtSpannable.setSpan(boldSpan, 0, name!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        txtSpannable.setSpan(ForegroundColorSpan(color), 0, name!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.append(txtSpannable)
        txtUser!!.setText(builder, TextView.BufferType.SPANNABLE)
    }

    fun convertStatusToString(orderStatus: Int): String? =
        when(orderStatus)
        {
            0 -> "Placed"
            1 -> "Shipping"
            2 -> "Shipped"
            -1 -> "Cancelled"
            else -> "Unk"
        }

    val SHIPPING_ORDER_REF: String="ShippingOrder"
    val SHIPPER_REF: String = "Shipper"
    val ORDER_REF: String = "Order"
    val SERVER_REF = "Server"
    var currentServerUser: ServerUserModel?=null

    const val CATEGORY_REF: String="Category"
    var foodSelected: FoodModel?=null
    var categorySelected: CategoryModel?=null
    val DEFAULT_COLUMN_COUNT: Int=0
    val FULL_WIDTH_COLUMN: Int = 1
    const val NOTI_TITLE = "title"
    const val NOTI_CONTENT = "content"
    const val TOKEN_REF = "Tokens"
}