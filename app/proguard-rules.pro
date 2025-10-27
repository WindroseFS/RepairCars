# Otimizações para dispositivos mais antigos
-keep class androidx.lifecycle.ViewModel
-keep class com.thorapps.repaircars.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Navigation
-keep class androidx.navigation.** { *; }