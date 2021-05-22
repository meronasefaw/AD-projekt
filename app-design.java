<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/activity_main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:paddingBottom="16dp"
    android:paddingLeft="16dp"
    android:paddingRight="16dp"
    android:paddingTop="16dp"
    tools:context="com.myapp.bluetooth.MainActivity">

    <Button
        android:text="På/Av"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentTop="true"
        android:layout_alignParentRight="true"
        android:id="@+id/btnONOFF"/>

    <Button
        android:id="@+id/btnDiscoverable_on_off"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentTop="true"
        android:layout_marginEnd="0dp"
        android:layout_marginRight="38dp"
        android:layout_toStartOf="@+id/btnONOFF"
        android:layout_toLeftOf="@+id/btnONOFF"
        android:onClick="btnEnableDisable_Discoverable"
        android:text="upptäckbar" />

    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:id="@+id/btnFindUnpairedDevices"
        android:text="Hitta enheter"
        android:onClick="btnDiscover"/>

    <ListView
        android:id="@+id/lvNewDevices"
        android:layout_width="match_parent"
        android:layout_height="150dp"
        android:layout_below="@+id/btnStartConnection"
        android:layout_marginTop="15dp" />

    <Button
        android:layout_margin="10dp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_below="@+id/btnFindUnpairedDevices"
        android:id="@+id/btnStartConnection"
        android:text="Starta Avslutning"/>

    <EditText
        android:layout_below="@+id/lvNewDevices"
        android:layout_width="250dp"
        android:layout_height="wrap_content"
        android:hint="skriv text här"
        android:layout_alignParentStart="true"
        android:id="@+id/editText"
        android:layout_alignParentLeft="true" />

    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Skicka"
        android:layout_below="@+id/lvNewDevices"
        android:id="@+id/btnSend"
        android:layout_toRightOf="@id/editText"/>

</RelativeLayout>
