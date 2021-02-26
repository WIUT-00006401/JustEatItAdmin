package com.example.justeatitadmin.Common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.OpenableColumns
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.example.justeatitadmin.HomeActivity
import com.example.justeatitadmin.Model.*
import com.example.justeatitadmin.R
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import io.reactivex.Observable
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.StringBuilder
import java.util.*

object Common {

    fun updateToken(context: Context, token: String, isServerToken:Boolean, isShipperToken:Boolean) {
        if (Common.currentServerUser != null)
            FirebaseDatabase.getInstance()
                .getReference(TOKEN_REF)
                .child(Common.currentServerUser!!.uid!!)
                .setValue(TokenModel(Common.currentServerUser!!.phone!!,token,isServerToken,isShipperToken))
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
            .setLargeIcon(BitmapFactory.decodeResource(context.resources,R.drawable.ic_restaurant_menu_white_24dp))
        if (pendingIntent!=null)
            builder.setContentIntent(pendingIntent)

        val notification = builder.build()

        notificationManager.notify(id,notification)
    }

    fun getNewOrderTopic(): String {
        return java.lang.StringBuilder("/topics/")
            .append(Common.currentServerUser!!.restaurant)
            .append("_")
            .append("new_order")
            .toString()
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

    fun decodePoly(encoded: String): List<LatLng>
    {
        val poly:MutableList<LatLng> = ArrayList<LatLng>()
        var index = 0
        var len = encoded.length
        var lat = 0
        var lng = 0
        while(index < len)
        {
            var b:Int
            var shift = 0
            var result = 0
            do{
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            }while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do{
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            }while (b>=0x20)
            val dlng = if (result and 1 != 0)(result shr 1).inv() else result shr 1
            lng += dlng
            val p = LatLng(lat.toDouble()/1E5,lng.toDouble()/1E5)
            poly.add(p)
        }
        return poly
    }

    fun getBearing(begin: LatLng, end: LatLng): Float {
        val lat = Math.abs(begin.latitude - end.longitude)
        val lng = Math.abs(begin.longitude - end.longitude)
        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return Math.toDegrees(Math.atan(lng/lat)).toFloat()
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (90-Math.toDegrees(Math.atan(lng/lat))+90).toFloat()
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (Math.toDegrees(Math.atan(lng/lat))+180).toFloat()
        else if (begin.latitude<end.latitude&&begin.longitude >= end.longitude)
            return (90-Math.toDegrees(Math.atan(lng/lat))+270).toFloat()
        return -1.0f
    }

    fun getNewsTopic(): String {
        return StringBuilder("/topics/")
            .append(Common.currentServerUser!!.restaurant!!)
            .append("_")
            .append("news")
            .toString()
    }

    fun getFileName(contentResolver: ContentResolver?, fileUri: Uri): Any {
        var result:String?=null
        if (fileUri.scheme == "content")
        {
            val cursor = contentResolver!!.query(fileUri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst())
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }finally {
                cursor!!.close()
            }
        }
        if (result == null)
        {
            result = fileUri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) result = result.substring(cut+1)
        }
        return result
    }

    fun getAppPath(context: Context): String {
        val dir = File(Environment.getExternalStorageDirectory().toString()
        +File.separator
        +context.resources.getString(R.string.app_name)
        +File.separator)
        if (!dir.exists())
            dir.mkdir()
        return dir.path+File.separator

    }

    fun getBitmapFromUrl(
        context: Context,
        cartItem: CartItem,
        document: Document
    ): Observable<CartItem> {
        return Observable.fromCallable{
            val bitmap = Glide.with(context)
                .asBitmap()
                .load(cartItem.foodImage)
                .submit().get()
            val image = Image.getInstance(bitmapToByteArray(bitmap))
            image.scaleAbsolute(80.0f,80.0f)
            document.add(image)
            cartItem
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap?): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.PNG,100,stream)
        return stream.toByteArray()

    }

    fun formatSizeJsonToString(foodSize: String): String? {
        return if (foodSize.equals("Default")) foodSize else{
            val gson = Gson()
            val sizeModel = gson.fromJson<SizeModel>(foodSize,SizeModel::class.java)
            sizeModel.name
        }
    }

    fun formatAddonJsonToString(foodAddon: String): String? {
        return if (foodAddon.equals("Default")) foodAddon else{
            val stringBuilder = StringBuilder()
            val gson = Gson()
            val addonModels = gson.fromJson<List<AddonModel>>(foodAddon,object :
            TypeToken<List<AddonModel>>(){}.type)
            for (addon in addonModels)
                stringBuilder.append(addon.name).append(",")
            stringBuilder.substring(0,stringBuilder.length-1)
        }
    }

    enum class ACTION{
        CREATE,
        DELETE,
        UPDATE

    }

    val FILE_PRINT: String = "last_order_print"
    val CHAT_DETAIL_REF: String = "ChatDetail"
    val KEY_CHAT_SENDER: String?="CHAT_SENDER"
    val KEY_CHAT_ROOM_ID: String?="CHAT_ROOM_ID"
    val CHAT_REF: String = "Chat"
    val RESTAURANT_REF: String = "Restaurant"
    val SHIPPING_ORDER_REF: String="ShippingOrder"
    val SHIPPER_REF: String = "Shipper"
    val ORDER_REF: String = "Order"
    val SERVER_REF = "Server"
    const val CATEGORY_REF: String="Category"

    val IMAGE_URL: String="IMAGE_URL"
    val IS_SEND_IMAGE: String="IS_SEND_IMAGE"
    var mostPopularSelected: MostPopularModel?=null
    val MOST_POPULAR: String="MostPopular"
    var bestDealsSelected: BestDealsModel?=null
    val BEST_DEALS: String="BestDeals"
    val IS_OPEN_ACTIVITY_NEW_ORDER: String?="IsOpenActivityOrder"
    var currentOrderSelected: OrderModel?=null

    var currentServerUser: ServerUserModel?=null
    var foodSelected: FoodModel?=null
    var categorySelected: CategoryModel?=null
    val DEFAULT_COLUMN_COUNT: Int=0
    val FULL_WIDTH_COLUMN: Int = 1
    const val NOTI_TITLE = "title"
    const val NOTI_CONTENT = "content"
    const val TOKEN_REF = "Tokens"
}