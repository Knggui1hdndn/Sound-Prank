package com.pranksound.fartsound.trollandjoke.funnyapp

import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage

object Constraints {
    const val SOUND_CHILD_CLICK = "SOUND_CHILD_CLICK"
    const val PARENT_SOUND = "PARENT_SOUND"
    const val SIZE_ONL = "SIZE_ONL"
    const val POSITION_FAVORITE_UNCHECKED = "POSITION_FAVORITE_UNCHECKED"
    const val CONNECTION_NETWORK = "CONNECTION_NETWORK"
    const val DISCONNECT_NETWORK = "DISCONNECT_NETWORK"
    var BASE_URL = "http://167.172.146.247:6789/api"
    var BASE_URL1 = "http://167.172.146.247:6789/api"
    const val AUTHORITY = "com.pranksound.fartsound.trollandjoke.funnyapp"
    const val CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE"
    const val FAVORITE_Off = "FAVORITE_Off"
    const val FAVORITE_ONL = "FAVORITE_ONL"
    const val ACTIVITY_LAUNCH = "ACTIVITY_LAUNCH"
    const val TYPE_HOT = 0
    const val TYPE_MEME =1
    val LIST_MEME = listOf(
        DataImage(
            "636b8e5415831ec42fc7d805",
            "Meme sound 1",
            "https://prank-sound-abi.pranksound.com/icons/icon-categories/Meme sound 1.png"
        ),
        DataImage(
            "636b8e5415831ec42fc7d806",
            "Meme sound 2",
            "https://prank-sound-abi.pranksound.com/icons/icon-categories/Meme sound 2.png"
        ),
        DataImage(
            "636b8e5415831ec42fc7d807",
            "Meme sound 3",
            "https://prank-sound-abi.pranksound.com/icons/icon-categories/Meme sound 3.png"
        ),
        DataImage(
            "636b8e5415831ec42fc7d808",
            "Meme sound 4",
            "https://prank-sound-abi.pranksound.com/icons/icon-categories/Meme sound 4.png"
        ),
        DataImage(
            "636b8e5415831ec42fc7d809",
            "Meme sound 5",
            "https://prank-sound-abi.pranksound.com/icons/icon-categories/Meme sound 5.png"
        ),
        DataImage(
            "636dc88cf512f266a311768d",
            "Meme sound 6",
            "https://prank-sound-abi.pranksound.com/icons/icon-categories/Meme sound 6.png"
        ),
        DataImage(
            "636dc40571a30811c821fd47",
            "Anime meme",
            "https://prank-sound-abi.pranksound.com/icons/icon-categories/Anime Meme.png"
        ),
        DataImage(
            "636dc40571a30811c821fd48",
            "Baby sound meme",
            "https://prank-sound-abi.pranksound.com/icons/icon-categories/Baby sound meme.png"
        ),
        DataImage(
            "636dc40571a30811c821fd49",
            "Laughing meme",
            "https://prank-sound-abi.pranksound.com/icons/icon-categories/Laughing meme.png"
        )
    )
}