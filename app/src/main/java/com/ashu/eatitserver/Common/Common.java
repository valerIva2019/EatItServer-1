package com.ashu.eatitserver.Common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.ashu.eatitserver.Model.CategoryModel;
import com.ashu.eatitserver.Model.FoodModel;
import com.ashu.eatitserver.Model.OrderModel;
import com.ashu.eatitserver.Model.ServerUserModel;
import com.ashu.eatitserver.Model.TokenModel;
import com.ashu.eatitserver.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Common {
    public static final String SERVER_REF = "Server";
    public static final String CATEGORY_REF = "Category";
    public static final String ORDER_REF = "Order";
    public static final String SHIPPER_REF = "Shippers";
    public static final String SHIPPING_ORDER_REF = "ShippingOrder";
    public static final String IS_OPEN_ACTIVITY_NEW_ORDER = "IsOpenActivityNewOrder";
    public static final String BEST_DEALS = "BestDeals";
    public static CategoryModel categorySelected;
    public static final int DEFAULT_COLUMN_COUNT = 0;
    public static final int FULL_WIDTH_COLUMN = 1;
    public static final String NOT1_TITLE = "title";
    public static final String NOT1_CONTENT = "content";
    public static final String TOKEN_REF = "Tokens";

    public static ServerUserModel currentServerUser;
    public static FoodModel selectedFood;
    public static OrderModel currentOrderSelected;


    public static void setSpanString(String welcome, String name, TextView textView) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(welcome);
        SpannableString spannableString = new SpannableString(name);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldSpan, 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(spannableString);
        textView.setText(builder, TextView.BufferType.SPANNABLE);
    }

    public static void setSpanStringColor(String welcome, String name, TextView textView, int color) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(welcome);
        SpannableString spannableString = new SpannableString(name);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldSpan, 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(color), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(spannableString);
        textView.setText(builder, TextView.BufferType.SPANNABLE);
    }

    public static String convertStatusToString(int orderStatus) {
        switch (orderStatus) {
            case 0:
                return "Placed";
            case 1:
                return "Shipping";
            case 2:
                return "Shipped";
            case -1:
                return "Cancelled";
            default:
                return "Unk";
        }
    }
    public static void showNotification(Context context, int id, String title, String content, Intent intent) {
        PendingIntent pendingIntent = null;
        if (intent != null)
            pendingIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String NOTIFICATION_CHANNEL_ID = "ashu_eat_it";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Eat It", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Eat It");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);

        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_baseline_restaurant_menu_24));

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }
        Notification notification = builder.build();
        notificationManager.notify(id, notification);

    }
    public static void updateToken(Context context, String newToken, boolean isServer, boolean isShipper) {
     if (Common.currentServerUser != null) {
         FirebaseDatabase.getInstance().
                 getReference(Common.TOKEN_REF)
                 .child(Common.currentServerUser.getUid())
                 .setValue(new TokenModel(Common.currentServerUser.getPhone(), newToken, isServer, isShipper))
                 .addOnFailureListener(e -> Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
     }
    }
    public static String createTopicOrder() {
        return "/topics/new_order";
    }

    public static List<LatLng> decodePoly (String encoded) {
        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat =0, lng = 0;
        while (index < len) {
            int b, shift =0, result =0;
            do {
                b = encoded.charAt(index++)-63;
                result |= (b & 0xff) << shift;
                shift+=5;
            } while (b >= 0x20);

            int dLat = ((result & 1) != 0 ? ~ (result >> 1):(result >> 1));
            lat+=dLat;

            shift =0;
            result = 0;
            do {
                b = encoded.charAt(index++)-63;
                result |= (b & 0xff) << shift;
                shift+=5;
            } while (b >= 0x20);


            int dLng = ((result & 1) != 0 ? ~ (result >> 1):(result >> 1));
            lng+=dLng;

            LatLng p = new LatLng((((double) lat/1E5)), (((double)lng/1E5)));
            poly.add(p);
        }
        return poly;
    }

    public static float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude-end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) Math.toDegrees(Math.atan(lng / lat));
        else {
            double v = 90 - Math.toDegrees(Math.atan(lng / lat));
            if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
                return (float) (v + 90);
            else  if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
                return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
            else  if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
                return (float) (v + 270);
        }
        return -1;
    }

}
