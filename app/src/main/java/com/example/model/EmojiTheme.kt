package com.example.model

enum class MascotPlacement {
    BESIDE_LEFT,
    BESIDE_RIGHT,
    PEEKING_TOP,
    ON_TOP
}

data class EmojiTheme(
    val id: String,
    val title: String,
    val category: String,
    val mascotEmoji: String,
    val secondaryEmoji: String = "",
    val fillColor: Long = 0xFFFF4B6E, // Dynamic battery liquid fill
    val signalColor: Long = 0xFFFF4B6E, // Accent signal color
    val placement: MascotPlacement = MascotPlacement.BESIDE_LEFT,
    val isVip: Boolean = false,
    val hasAdBadge: Boolean = false,
    val defaultClockEmojis: List<String> = listOf("🔇", "😎", "🐱")
)

object EmojiThemeCatalog {
    val CATEGORIES = listOf(
        "Football",
        "Cute Animals",
        "Valentine",
        "New Year",
        "Christmas",
        "Characters",
        "Anime",
        "Hearts"
    )

    val ALL_THEMES = listOf(
        // Football Category (matches Screenshot 1!)
        EmojiTheme(
            id = "football_tiger",
            title = "Tiger Striker",
            category = "Football",
            mascotEmoji = "🐯⚽",
            secondaryEmoji = "🥅",
            fillColor = 0xFFEF4444,
            signalColor = 0xFFEF4444,
            isVip = true
        ),
        EmojiTheme(
            id = "football_golden_boot",
            title = "Golden Boot",
            category = "Football",
            mascotEmoji = "👟",
            secondaryEmoji = "⚽",
            fillColor = 0xFFF59E0B,
            signalColor = 0xFFF59E0B,
            isVip = false
        ),
        EmojiTheme(
            id = "football_trophy",
            title = "World Champion",
            category = "Football",
            mascotEmoji = "🏆",
            secondaryEmoji = "⭐",
            fillColor = 0xFF10B981,
            signalColor = 0xFF10B981,
            isVip = true
        ),
        EmojiTheme(
            id = "football_moose",
            title = "Moose Footballer",
            category = "Football",
            mascotEmoji = "🦌⚽",
            fillColor = 0xFF10B981,
            signalColor = 0xFF10B981,
            isVip = true
        ),
        EmojiTheme(
            id = "football_eagle",
            title = "Eagle Ace",
            category = "Football",
            mascotEmoji = "🦅⚽",
            fillColor = 0xFF3B82F6,
            signalColor = 0xFF3B82F6,
            hasAdBadge = true
        ),
        EmojiTheme(
            id = "football_leopard",
            title = "Speed Cheetah",
            category = "Football",
            mascotEmoji = "🐆⚽",
            fillColor = 0xFFF97316,
            signalColor = 0xFFF97316,
            hasAdBadge = true
        ),

        // Cute Animals Category (matches Screenshot 1 hero banner & Screenshot 2 status bar!)
        EmojiTheme(
            id = "cute_bunny_love",
            title = "Bunny Love",
            category = "Cute Animals",
            mascotEmoji = "🐰",
            secondaryEmoji = "🥕",
            fillColor = 0xFFFF6584,
            signalColor = 0xFFFF6584,
            isVip = false,
            defaultClockEmojis = listOf("🔇", "😎", "🐱")
        ),
        EmojiTheme(
            id = "cute_hamster",
            title = "Sweet Hamster",
            category = "Cute Animals",
            mascotEmoji = "🐹",
            secondaryEmoji = "🌻",
            fillColor = 0xFFFF8A3D,
            signalColor = 0xFFFF4B6E,
            isVip = true,
            defaultClockEmojis = listOf("🔇", "😎", "🐱")
        ),
        EmojiTheme(
            id = "cute_kitty_cat",
            title = "Peeking Kitty",
            category = "Cute Animals",
            mascotEmoji = "🐱",
            secondaryEmoji = "🐾",
            fillColor = 0xFFA855F7,
            signalColor = 0xFFA855F7,
            isVip = false,
            defaultClockEmojis = listOf("🔇", "😎", "🐱")
        ),
        EmojiTheme(
            id = "cute_shiba_puppy",
            title = "Shiba Pup",
            category = "Cute Animals",
            mascotEmoji = "🐶",
            secondaryEmoji = "🦴",
            fillColor = 0xFFF59E0B,
            signalColor = 0xFFF59E0B,
            isVip = true
        ),
        EmojiTheme(
            id = "cute_panda",
            title = "Boba Panda",
            category = "Cute Animals",
            mascotEmoji = "🐼",
            secondaryEmoji = "🎋",
            fillColor = 0xFF14B8A6,
            signalColor = 0xFF14B8A6,
            hasAdBadge = true
        ),
        EmojiTheme(
            id = "cute_teddy_bear",
            title = "Honey Bear",
            category = "Cute Animals",
            mascotEmoji = "🐻",
            secondaryEmoji = "🍯",
            fillColor = 0xFFD97706,
            signalColor = 0xFFD97706,
            hasAdBadge = true
        ),

        // Valentine Category
        EmojiTheme(
            id = "valentine_cupid_heart",
            title = "Cupid Heart",
            category = "Valentine",
            mascotEmoji = "💖",
            secondaryEmoji = "🏹",
            fillColor = 0xFFFF2D55,
            signalColor = 0xFFFF2D55,
            isVip = true,
            defaultClockEmojis = listOf("💌", "💖", "✨")
        ),
        EmojiTheme(
            id = "valentine_kiss_bear",
            title = "Teddy Love",
            category = "Valentine",
            mascotEmoji = "🧸💕",
            fillColor = 0xFFEC4899,
            signalColor = 0xFFEC4899,
            isVip = false
        ),
        EmojiTheme(
            id = "valentine_sparkle_rose",
            title = "Red Rose",
            category = "Valentine",
            mascotEmoji = "🌹",
            secondaryEmoji = "✨",
            fillColor = 0xFFBE123C,
            signalColor = 0xFFBE123C,
            hasAdBadge = true
        ),

        // New Year Category
        EmojiTheme(
            id = "newyear_fireworks",
            title = "Midnight Sparks",
            category = "New Year",
            mascotEmoji = "🎆",
            secondaryEmoji = "✨",
            fillColor = 0xFF8B5CF6,
            signalColor = 0xFF8B5CF6,
            isVip = true
        ),
        EmojiTheme(
            id = "newyear_champagne",
            title = "Party Popper",
            category = "New Year",
            mascotEmoji = "🎉",
            secondaryEmoji = "🥂",
            fillColor = 0xFFF59E0B,
            signalColor = 0xFFF59E0B,
            isVip = false
        ),
        EmojiTheme(
            id = "newyear_golden_star",
            title = "2026 Star",
            category = "New Year",
            mascotEmoji = "🌟",
            fillColor = 0xFFEAB308,
            signalColor = 0xFFEAB308,
            hasAdBadge = true
        ),

        // Christmas Category
        EmojiTheme(
            id = "christmas_santa",
            title = "Jolly Santa",
            category = "Christmas",
            mascotEmoji = "🎅",
            secondaryEmoji = "🎁",
            fillColor = 0xFFDC2626,
            signalColor = 0xFFDC2626,
            isVip = true
        ),
        EmojiTheme(
            id = "christmas_snowman",
            title = "Frosty Snowman",
            category = "Christmas",
            mascotEmoji = "☃️",
            secondaryEmoji = "❄️",
            fillColor = 0xFF38BDF8,
            signalColor = 0xFF38BDF8,
            isVip = false
        ),
        EmojiTheme(
            id = "christmas_tree",
            title = "Magic Tree",
            category = "Christmas",
            mascotEmoji = "🎄",
            secondaryEmoji = "⭐",
            fillColor = 0xFF16A34A,
            signalColor = 0xFF16A34A,
            hasAdBadge = true
        ),

        // Characters Category
        EmojiTheme(
            id = "char_cool_shades",
            title = "Cool Sunglasses",
            category = "Characters",
            mascotEmoji = "😎",
            secondaryEmoji = "🔥",
            fillColor = 0xFF06B6D4,
            signalColor = 0xFF06B6D4,
            isVip = false,
            defaultClockEmojis = listOf("🔇", "😎", "🐱")
        ),
        EmojiTheme(
            id = "char_gaming",
            title = "Cyber Gamer",
            category = "Characters",
            mascotEmoji = "🎮",
            secondaryEmoji = "👾",
            fillColor = 0xFF8B5CF6,
            signalColor = 0xFF8B5CF6,
            isVip = true
        ),
        EmojiTheme(
            id = "char_ninja",
            title = "Shadow Ninja",
            category = "Characters",
            mascotEmoji = "🥷",
            secondaryEmoji = "🗡️",
            fillColor = 0xFF475569,
            signalColor = 0xFF475569,
            hasAdBadge = true
        ),
        EmojiTheme(
            id = "char_rocket",
            title = "Cosmic Rocket",
            category = "Characters",
            mascotEmoji = "🚀",
            secondaryEmoji = "🪐",
            fillColor = 0xFFF97316,
            signalColor = 0xFFF97316,
            isVip = true
        ),

        // Anime Category
        EmojiTheme(
            id = "anime_fox_spirit",
            title = "Kitsune Fox",
            category = "Anime",
            mascotEmoji = "🦊",
            secondaryEmoji = "🌸",
            fillColor = 0xFFF97316,
            signalColor = 0xFFF97316,
            isVip = true
        ),
        EmojiTheme(
            id = "anime_sailor_moon",
            title = "Crescent Moon",
            category = "Anime",
            mascotEmoji = "🌙",
            secondaryEmoji = "⭐",
            fillColor = 0xFFFACC15,
            signalColor = 0xFFFACC15,
            isVip = false
        ),
        EmojiTheme(
            id = "anime_sparkle_cat",
            title = "Neko Magic",
            category = "Anime",
            mascotEmoji = "🐾✨",
            fillColor = 0xFFEC4899,
            signalColor = 0xFFEC4899,
            hasAdBadge = true
        ),

        // Hearts Category
        EmojiTheme(
            id = "hearts_neon_glow",
            title = "Neon Pulsing",
            category = "Hearts",
            mascotEmoji = "💓",
            fillColor = 0xFFFF2D55,
            signalColor = 0xFFFF2D55,
            isVip = true
        ),
        EmojiTheme(
            id = "hearts_fire_flame",
            title = "Fire Heart",
            category = "Hearts",
            mascotEmoji = "❤️‍🔥",
            fillColor = 0xFFEA580C,
            signalColor = 0xFFEA580C,
            isVip = false
        ),
        EmojiTheme(
            id = "hearts_sparkling",
            title = "Crystal Spark",
            category = "Hearts",
            mascotEmoji = "💖",
            secondaryEmoji = "💎",
            fillColor = 0xFFEC4899,
            signalColor = 0xFFEC4899,
            hasAdBadge = true
        )
    )

    fun getThemeById(id: String): EmojiTheme {
        return ALL_THEMES.find { it.id == id } ?: ALL_THEMES[0]
    }
}
