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
android:text="UBICACIÓN GPS"
android:textColor="#F8FAFC"
android:textSize="26sp"
android:textStyle="bold" />

    <androidx.cardview.widget.CardView
android:layout_width="match_parent"
android:layout_height="wrap_content"
android:layout_marginBottom="28dp"
app:cardBackgroundColor="#1E293B"
app:cardCornerRadius="16dp"
app:cardElevation="8dp">

        <TextView
android:id="@+id/txt_ubicacion"
android:layout_width="match_parent"
android:layout_height="wrap_content"
android:lineSpacingExtra="12dp"
android:padding="28dp"
android:text="Latitud: ---\nLongitud: ---"
android:textColor="#38BDF8"
android:textSize="20sp"
android:textStyle="bold" />
    </androidx.cardview.widget.CardView>

    <Button
android:id="@+id/btn_actualizar_gps"
android:layout_width="match_parent"
android:layout_height="60dp"
android:backgroundTint="#38BDF8"
android:elevation="6dp"
android:text="Obtener Ubicación"
android:textColor="#0F172A"
android:textSize="16sp"
android:textStyle="bold"
app:cornerRadius="16dp" />

</LinearLayout>