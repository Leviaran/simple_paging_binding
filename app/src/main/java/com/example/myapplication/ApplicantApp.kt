package com.example.myapplication

import android.app.Application
import com.androidnetworking.AndroidNetworking

class ApplicantApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidNetworking.initialize(this);
    }
}