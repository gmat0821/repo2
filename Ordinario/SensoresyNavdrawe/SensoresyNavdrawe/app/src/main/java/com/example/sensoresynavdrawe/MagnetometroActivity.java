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
android:text="MAGNETÓMETRO"
android:textColor="#F8FAFC"
android:textSize="26sp"
android:textStyle="bold" />

    <androidx.cardview.widget.CardView
android:layout_width="match_parent"
android:layout_height="wrap_content"
app:cardBackgroundColor="#1E293B"
app:cardCornerRadius="16dp"
app:cardElevation="8dp">

        <TextView
android:id="@+id/txt_valores_magnetometro"
android:layout_width="match_parent"
android:layout_height="wrap_content"
android:lineSpacingExtra="12dp"
android:padding="28dp"
android:text="X: 0.0\nY: 0.0\nZ: 0.0"
android:textColor="#F43F5E"
android:textSize="20sp"
android:textStyle="bold" />
    </androidx.cardview.widget.CardView>

</LinearLayout>