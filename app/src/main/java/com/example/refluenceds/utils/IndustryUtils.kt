package com.example.refluenceds.utils

import androidx.annotation.DrawableRes
import com.example.refluenceds.R

object IndustryUtils {
    @DrawableRes
    fun getIndustryDrawable(name: String?): Int {
        val clean = name?.trim()?.lowercase() ?: return R.drawable.lifestyle
        return when {
            clean.contains("beauty") || clean.contains("beautiful") || clean.contains("cosmetic") -> R.drawable.beauty
            clean.contains("fashion") || clean.contains("cloth") || clean.contains("apparel") -> R.drawable.fashion
            clean.contains("gastro") || clean.contains("culinary") -> R.drawable.gastronomy
            clean.contains("food") || clean.contains("drink") || clean.contains("beverage") -> R.drawable.food_drink
            clean.contains("travel") || clean.contains("tourism") || clean.contains("trip") -> R.drawable.travel
            clean.contains("sport") || clean.contains("fit") || clean.contains("gym") -> R.drawable.sports
            clean.contains("event") || clean.contains("fest") || clean.contains("entertain") -> R.drawable.events
            clean.contains("sustain") || clean.contains("eco") || clean.contains("green") -> R.drawable.sustainability
            clean.contains("home") || clean.contains("living") || clean.contains("interior") -> R.drawable.home
            clean.contains("financ") || clean.contains("money") || clean.contains("bank") -> R.drawable.finances
            clean.contains("car") || clean.contains("auto") || clean.contains("motor") || clean.contains("vehicle") -> R.drawable.cars
            clean.contains("life") -> R.drawable.lifestyle
            else -> R.drawable.lifestyle
        }
    }
}
