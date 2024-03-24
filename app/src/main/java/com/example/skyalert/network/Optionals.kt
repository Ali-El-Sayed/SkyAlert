package com.example.skyalert.network

/**
 *  Enum class for the units of the weather
 *  @param value the value of the unit
 * */
enum class UNITS(val value: String) {
    METRIC("metric"),
    IMPERIAL("imperial"),
    STANDARD("standard")
}

/**
 *  Enum class for the response mode of the weather
 *  @param value the value of the mode
 * */
enum class MODE(val value: String) {
    JSON("json"),
    XML("xml"),
    HTML("html")
}

/**
 * Enum class for the language of the weather
 * @param value the value of the language
 * */
enum class LANG(val value: String) {
    AFRIKAANS("af"),
    ALBANIAN("al"),
    ARABIC("ar"),
    AZERBAIJANI("az"),
    BULGARIAN("bg"),
    CATALAN("ca"),
    CZECH("cz"),
    DANISH("da"),
    GERMAN("de"),
    GREEK("el"),
    ENGLISH("en"),
    BASQUE("eu"),
    PERSIAN("fa"),
    FINNISH("fi"),
    FRENCH("fr"),
    GALICIAN("gl"),
    HEBREW("he"),
    HINDI("hi"),
    CROATIAN("hr"),
    HUNGARIAN("hu"),
    INDONESIAN("id"),
    ITALIAN("it"),
    JAPANESE("ja"),
    KOREAN("kr"),
    LATVIAN("la"),
    LITHUANIAN("lt"),
    MACEDONIAN("mk"),
    NORWEGIAN("no"),
    DUTCH("nl"),
    POLISH("pl"),
    PORTUGUESE("pt"),
    PORTUGUESE_BR("pt_br"),
    ROMANIAN("ro"),
    RUSSIAN("ru"),
    SWEDISH("sv"),
    SLOVAK("sk"),
    SLOVENIAN("sl"),
    SPANISH("es"),
    SERBIAN("sr"),
    THAI("th"),
    TURKISH("tr"),
    UKRAINIAN("ua"),
    VIETNAMESE("vi"),
    CHINESE_SIMPLIFIED("zh_cn"),
    CHINESE_TRADITIONAL("zh_tw"),
    ZULU("zu");
}
