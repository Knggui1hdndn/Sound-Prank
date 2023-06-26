package com.pranksound.fartsound.trollandjoke.funnyapp.model

import java.io.Serializable

class DataSound(var source: String, var isPremium: String, var image: String) : Serializable {

    override fun toString(): String {
        return "DataSound{" +
                "source='" + source + '\'' +
                ", isPremium='" + isPremium + '\'' +
                ", image='" + image + '\'' +
                '}'
    }
}