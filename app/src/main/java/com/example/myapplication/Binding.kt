package com.example.myapplication

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.androidnetworking.widget.ANImageView

object Binding {

    @BindingAdapter("app:setImage")
    @JvmStatic
    fun setImage(imageView: ANImageView, imageUrl: String) {
        imageView.apply {
            setDefaultImageResId(android.R.drawable.ic_menu_camera)
            setErrorImageResId(android.R.drawable.stat_notify_error)
            setImageUrl(imageUrl)
        }
    }
}