<?xml version="1.0" encoding="utf-8"?>
<androidx.drawerlayout.widget.DrawerLayout xmlns:android="http://schemas.android.com/apk/res/android"
xmlns:app="http://schemas.android.com/apk/res-auto"
android:id="@+id/drawer_layout"
android:layout_width="match_parent"
android:layout_height="match_parent"
android:background="#0F172A"
android:fitsSystemWindows="true">

    <LinearLayout
android:layout_width="match_parent"
android:layout_height="match_parent"
android:orientation="vertical">

        <androidx.appcompat.widget.Toolbar
android:id="@+id/toolbar"
android:layout_width="match_parent"
android:layout_height="?attr/actionBarSize"
android:background="#1E293B"
android:elevation="8dp"
app:titleTextColor="#00F2FE" />

        <androidx.cardview.widget.CardView
android:layout_width="match_parent"
android:layout_height="wrap_content"
android:layout_margin="24dp"
app:cardBackgroundColor="#1E293B"
app:cardCornerRadius="20dp"
app:cardElevation="10dp">

            <LinearLayout
android:layout_width="match_parent"
android:layout_height="wrap_content"
android:gravity="center"
android:orientation="vertical"
android:padding="32dp">

                <TextView
android:layout_width="wrap_content"
android:layout_height="wrap_content"
android:text="Bienvenido"
android:textColor="#F8FAFC"
android:textSize="26sp"
android:textStyle="bold" />

                <TextView
android:layout_width="wrap_content"
android:layout_height="wrap_content"
android:layout_marginTop="12dp"
android:gravity="center"
android:text="Desliza o usa el menú lateral para seleccionar un sensor en tiempo real."
android:textColor="#94A3B8"
android:textSize="15sp" />
            </LinearLayout>
        </androidx.cardview.widget.CardView>
    </LinearLayout>

    <com.google.android.material.navigation.NavigationView
android:id="@+id/nav_view"
android:layout_width="wrap_content"
android:layout_height="match_parent"
android:layout_gravity="start"
android:background="#1E293B"
app:itemIconTint="#00F2FE"
app:itemTextColor="#F8FAFC"
app:menu="@menu/nav_menu" />

</androidx.drawerlayout.widget.DrawerLayout>