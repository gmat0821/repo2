<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
xmlns:app="http://schemas.android.com/apk/res-auto"
android:layout_width="match_parent"
android:layout_height="match_parent"
android:background="#0F172A"
android:gravity="center"
android:orientation="vertical"
android:padding="24dp">

    <TextView
android:layout_width="wrap_content"
android:layout_height="wrap_content"
android:layout_marginBottom="32dp"
android:letterSpacing="0.05"
android:text="PODÓMETRO"
android:textColor="#F8FAFC"
android:textSize="26sp"
android:textStyle="bold" />

    <androidx.cardview.widget.CardView
android:layout_width="260dp"
android:layout_height="260dp"
app:cardBackgroundColor="#1E293B"
app:cardCornerRadius="130dp"
app:cardElevation="12dp">

        <LinearLayout
android:layout_width="match_parent"
android:layout_height="match_parent"
android:gravity="center"
android:orientation="vertical">

            <TextView
android:id="@+id/txt_pasos"
android:layout_width="wrap_content"
android:layout_height="wrap_content"
android:text="0"
android:textColor="#00F2FE"
android:textSize="54sp"
android:textStyle="bold" />

            <TextView
android:layout_width="wrap_content"
android:layout_height="wrap_content"
android:text="PASOS DADOS"
android:textColor="#64748B"
android:textSize="12sp"
android:textStyle="bold" />
        </LinearLayout>
    </androidx.cardview.widget.CardView>

    <TextView
android:layout_width="wrap_content"
android:layout_height="wrap_content"
android:layout_marginTop="36dp"
android:text="Camina para comenzar a contar"
android:textColor="#94A3B8"
android:textSize="15sp" />

</LinearLayout>