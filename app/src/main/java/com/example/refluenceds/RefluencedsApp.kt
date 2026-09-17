package com.example.refluenceds

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RefluencedsApp : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true)
            .placeholder(R.drawable.ic_broken_image)
            .error(R.drawable.ic_broken_image)
            .fallback(R.drawable.ic_broken_image)
            .build()
    }
}
