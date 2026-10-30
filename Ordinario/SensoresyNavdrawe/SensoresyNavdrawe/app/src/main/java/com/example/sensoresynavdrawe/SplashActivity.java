<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
android:layout_width="match_parent"
android:layout_height="match_parent"
android:background="#0F172A">

    <LinearLayout
android:layout_width="wrap_content"
android:layout_height="wrap_content"
android:layout_centerInParent="true"
android:gravity="center"
android:orientation="vertical">

        <ImageView
android:id="@+id/logo_splash"
android:layout_width="160dp"
android:layout_height="160dp"
android:background="@android:color/transparent"
android:padding="16dp"
android:src="@mipmap/ic_launcher" />

        <TextView
android:layout_width="wrap_content"
android:layout_height="wrap_content"
android:layout_marginTop="24dp"
android:letterSpacing="0.15"
android:text="SENSOR HUB"
android:textColor="#00F2FE"
android:textSize="28sp"
android:textStyle="bold" />

        <TextView
android:layout_width="wrap_content"
android:layout_height="wrap_content"
android:layout_marginTop="8dp"
android:text="Cargando entorno de sensores..."
android:textColor="#94A3B8"
android:textSize="14sp" />
    </LinearLayout>

</RelativeLayout>