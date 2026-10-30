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
android:layout_marginBottom="24dp"
android:letterSpacing="0.05"
android:text="CÁMARA"
android:textColor="#F8FAFC"
android:textSize="26sp"
android:textStyle="bold" />

    <androidx.cardview.widget.CardView
android:layout_width="match_parent"
android:layout_height="320dp"
android:layout_marginBottom="28dp"
app:cardBackgroundColor="#1E293B"
app:cardCornerRadius="20dp"
app:cardElevation="8dp">

        <ImageView
android:id="@+id/img_foto"
android:layout_width="match_parent"
android:layout_height="match_parent"
android:scaleType="centerCrop" />
    </androidx.cardview.widget.CardView>

    <Button
android:id="@+id/btn_tomar_foto"
android:layout_width="match_parent"
android:layout_height="60dp"
android:backgroundTint="#00F2FE"
android:elevation="6dp"
android:text="Tomar Fotografía"
android:textColor="#0F172A"
android:textSize="16sp"
android:textStyle="bold"
app:cornerRadius="16dp" />

</LinearLayout>