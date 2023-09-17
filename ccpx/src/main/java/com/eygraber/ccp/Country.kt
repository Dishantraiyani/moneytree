package com.eygraber.ccp

data class Country(
  val countryCode: String,
  val callingCode: String,
  val name: Int,
  val flag: String,
  internal val priority: Boolean = false
) {
  internal companion object {
    val countries: Map<String, Country> by lazy {
      mapOf(
        "af" to Country("af", "93", R.string.ccp_country_afghanistan, "\uD83C\uDDE6\uD83C\uDDEB"),
        "ax" to Country("ax", "358", R.string.ccp_country_aland_islands, "\uD83C\uDDE6\uD83C\uDDFD"),
        "al" to Country("al", "355", R.string.ccp_country_albania, "\uD83C\uDDE6\uD83C\uDDF1"),
        "dz" to Country("dz", "213", R.string.ccp_country_algeria, "\uD83C\uDDE9\uD83C\uDDFF"),
        "as" to Country("as", "1", R.string.ccp_country_american_samoa, "\uD83C\uDDE6\uD83C\uDDF8"),
        "ad" to Country("ad", "376", R.string.ccp_country_andorra, "\uD83C\uDDE6\uD83C\uDDE9"),
        "ao" to Country("ao", "244", R.string.ccp_country_angola, "\uD83C\uDDE6\uD83C\uDDF4"),
        "ai" to Country("ai", "1", R.string.ccp_country_anguilla, "\uD83C\uDDE6\uD83C\uDDEE"),
        "aq" to Country("aq", "672", R.string.ccp_country_antarctica, "\uD83C\uDDE6\uD83C\uDDF6"),
        "ag" to Country("ag", "1", R.string.ccp_country_antigua_and_barbuda, "\uD83C\uDDE6\uD83C\uDDEC"),
        "ar" to Country("ar", "54", R.string.ccp_country_argentina, "\uD83C\uDDE6\uD83C\uDDF7"),
        "am" to Country("am", "374", R.string.ccp_country_armenia, "\uD83C\uDDE6\uD83C\uDDF2"),
        "aw" to Country("aw", "297", R.string.ccp_country_aruba, "\uD83C\uDDE6\uD83C\uDDFC"),
        "au" to Country("au", "61", R.string.ccp_country_australia, "\uD83C\uDDE6\uD83C\uDDFA"),
        "at" to Country("at", "43", R.string.ccp_country_austria, "\uD83C\uDDE6\uD83C\uDDF9"),
        "az" to Country("az", "994", R.string.ccp_country_azerbaijan, "\uD83C\uDDE6\uD83C\uDDFF"),
        "bs" to Country("bs", "1", R.string.ccp_country_bahamas, "\uD83C\uDDE7\uD83C\uDDF8"),
        "bh" to Country("bh", "973", R.string.ccp_country_bahrain, "\uD83C\uDDE7\uD83C\uDDED"),
        "bd" to Country("bd", "880", R.string.ccp_country_bangladesh, "\uD83C\uDDE7\uD83C\uDDE9"),
        "bb" to Country("bb", "1", R.string.ccp_country_barbados, "\uD83C\uDDE7\uD83C\uDDE7"),
        "by" to Country("by", "375", R.string.ccp_country_belarus, "\uD83C\uDDE7\uD83C\uDDFE"),
        "be" to Country("be", "32", R.string.ccp_country_belgium, "\uD83C\uDDE7\uD83C\uDDEA"),
        "bz" to Country("bz", "501", R.string.ccp_country_belize, "\uD83C\uDDE7\uD83C\uDDFF"),
        "bj" to Country("bj", "229", R.string.ccp_country_benin, "\uD83C\uDDE7\uD83C\uDDEF"),
        "bm" to Country("bm", "1", R.string.ccp_country_bermuda, "\uD83C\uDDE7\uD83C\uDDF2"),
        "bt" to Country("bt", "975", R.string.ccp_country_bhutan, "\uD83C\uDDE7\uD83C\uDDF9"),
        "bo" to Country("bo", "591", R.string.ccp_country_bolivia, "\uD83C\uDDE7\uD83C\uDDF4"),
        "ba" to Country("ba", "387", R.string.ccp_country_bosnia_and_herzegovina, "\uD83C\uDDE7\uD83C\uDDE6"),
        "bw" to Country("bw", "267", R.string.ccp_country_botswana, "\uD83C\uDDE7\uD83C\uDDFC"),
        "br" to Country("br", "55", R.string.ccp_country_brazil, "\uD83C\uDDE7\uD83C\uDDF7"),
        "io" to Country("io", "246", R.string.ccp_country_british_indian_ocean_territory, "\uD83C\uDDEE\uD83C\uDDF4"),
        "vg" to Country("vg", "1", R.string.ccp_country_british_virgin_islands, "\uD83C\uDDFB\uD83C\uDDEC"),
        "bn" to Country("bn", "673", R.string.ccp_country_brunei_darussalam, "\uD83C\uDDE7\uD83C\uDDF3"),
        "bg" to Country("bg", "359", R.string.ccp_country_bulgaria, "\uD83C\uDDE7\uD83C\uDDEC"),
        "bf" to Country("bf", "226", R.string.ccp_country_burkina_faso, "\uD83C\uDDE7\uD83C\uDDEB"),
        "bi" to Country("bi", "257", R.string.ccp_country_burundi, "\uD83C\uDDE7\uD83C\uDDEE"),
        "kh" to Country("kh", "855", R.string.ccp_country_cambodia, "\uD83C\uDDF0\uD83C\uDDED"),
        "cm" to Country("cm", "237", R.string.ccp_country_cameroon, "\uD83C\uDDE8\uD83C\uDDF2"),
        "ca" to Country("ca", "1", R.string.ccp_country_canada, "\uD83C\uDDE8\uD83C\uDDE6"),
        "cv" to Country("cv", "238", R.string.ccp_country_cape_verde, "\uD83C\uDDE8\uD83C\uDDFB"),
        "ky" to Country("ky", "1", R.string.ccp_country_cayman_islands, "\uD83C\uDDF0\uD83C\uDDFE"),
        "cf" to Country("cf", "236", R.string.ccp_country_central_african_republic, "\uD83C\uDDE8\uD83C\uDDEB"),
        "td" to Country("td", "235", R.string.ccp_country_chad, "\uD83C\uDDF9\uD83C\uDDE9"),
        "cl" to Country("cl", "56", R.string.ccp_country_chile, "\uD83C\uDDE8\uD83C\uDDF1"),
        "cn" to Country("cn", "86", R.string.ccp_country_china, "\uD83C\uDDE8\uD83C\uDDF3"),
        "cx" to Country("cx", "61", R.string.ccp_country_christmas_island, "\uD83C\uDDE8\uD83C\uDDFD"),
        "cc" to Country("cc", "61", R.string.ccp_country_cocos, "\uD83C\uDDE8\uD83C\uDDE8"),
        "co" to Country("co", "57", R.string.ccp_country_colombia, "\uD83C\uDDE8\uD83C\uDDF4"),
        "km" to Country("km", "269", R.string.ccp_country_comoros, "\uD83C\uDDF0\uD83C\uDDF2"),
        "cg" to Country("cg", "242", R.string.ccp_country_congo, "\uD83C\uDDE8\uD83C\uDDEC"),
        "cd" to Country("cd", "243", R.string.ccp_country_congo_democratic_republic, "\uD83C\uDDE8\uD83C\uDDE9"),
        "ck" to Country("ck", "682", R.string.ccp_country_cook_islands, "\uD83C\uDDE8\uD83C\uDDF0"),
        "cr" to Country("cr", "506", R.string.ccp_country_costa_rica, "\uD83C\uDDE8\uD83C\uDDF7"),
        "ci" to Country("ci", "225", R.string.ccp_country_cote_divoire, "\uD83C\uDDE8\uD83C\uDDEE"),
        "hr" to Country("hr", "385", R.string.ccp_country_croatia, "\uD83C\uDDED\uD83C\uDDF7"),
        "cu" to Country("cu", "53", R.string.ccp_country_cuba, "\uD83C\uDDE8\uD83C\uDDFA"),
        "cw" to Country("cw", "599", R.string.ccp_country_curaçao, "\uD83C\uDDE8\uD83C\uDDFC"),
        "cy" to Country("cy", "357", R.string.ccp_country_cyprus, "\uD83C\uDDE8\uD83C\uDDFE"),
        "cz" to Country("cz", "420", R.string.ccp_country_czech_republic, "\uD83C\uDDE8\uD83C\uDDFF"),
        "dk" to Country("dk", "45", R.string.ccp_country_denmark, "\uD83C\uDDE9\uD83C\uDDF0"),
        "dj" to Country("dj", "253", R.string.ccp_country_djibouti, "\uD83C\uDDE9\uD83C\uDDEF"),
        "dm" to Country("dm", "1", R.string.ccp_country_dominica, "\uD83C\uDDE9\uD83C\uDDF2"),
        "do" to Country("do", "1", R.string.ccp_country_dominican_republic, "\uD83C\uDDE9\uD83C\uDDF4"),
        "ec" to Country("ec", "593", R.string.ccp_country_ecuador, "\uD83C\uDDEA\uD83C\uDDE8"),
        "eg" to Country("eg", "20", R.string.ccp_country_egypt, "\uD83C\uDDEA\uD83C\uDDEC"),
        "sv" to Country("sv", "503", R.string.ccp_country_el_salvador, "\uD83C\uDDF8\uD83C\uDDFB"),
        "gq" to Country("gq", "240", R.string.ccp_country_equatorial_guinea, "\uD83C\uDDEC\uD83C\uDDF6"),
        "er" to Country("er", "291", R.string.ccp_country_eritrea, "\uD83C\uDDEA\uD83C\uDDF7"),
        "ee" to Country("ee", "372", R.string.ccp_country_estonia, "\uD83C\uDDEA\uD83C\uDDEA"),
        "et" to Country("et", "251", R.string.ccp_country_ethiopia, "\uD83C\uDDEA\uD83C\uDDF9"),
        "fk" to Country("fk", "500", R.string.ccp_country_falkland_islands, "\uD83C\uDDEB\uD83C\uDDF0"),
        "fo" to Country("fo", "298", R.string.ccp_country_faroe_islands, "\uD83C\uDDEB\uD83C\uDDF4"),
        "fj" to Country("fj", "679", R.string.ccp_country_fiji, "\uD83C\uDDEB\uD83C\uDDEF"),
        "fi" to Country("fi", "358", R.string.ccp_country_finland, "\uD83C\uDDEB\uD83C\uDDEE"),
        "fr" to Country("fr", "33", R.string.ccp_country_france, "\uD83C\uDDEB\uD83C\uDDF7"),
        "gf" to Country("gf", "594", R.string.ccp_country_french_guyana, "\uD83C\uDDEC\uD83C\uDDEB"),
        "pf" to Country("pf", "689", R.string.ccp_country_french_polynesia, "\uD83C\uDDF5\uD83C\uDDEB"),
        "ga" to Country("ga", "241", R.string.ccp_country_gabon, "\uD83C\uDDEC\uD83C\uDDE6"),
        "gm" to Country("gm", "220", R.string.ccp_country_gambia, "\uD83C\uDDEC\uD83C\uDDF2"),
        "ge" to Country("ge", "995", R.string.ccp_country_georgia, "\uD83C\uDDEC\uD83C\uDDEA"),
        "de" to Country("de", "49", R.string.ccp_country_germany, "\uD83C\uDDE9\uD83C\uDDEA"),
        "gh" to Country("gh", "233", R.string.ccp_country_ghana, "\uD83C\uDDEC\uD83C\uDDED"),
        "gi" to Country("gi", "350", R.string.ccp_country_gibraltar, "\uD83C\uDDEC\uD83C\uDDEE"),
        "gr" to Country("gr", "30", R.string.ccp_country_greece, "\uD83C\uDDEC\uD83C\uDDF7"),
        "gl" to Country("gl", "299", R.string.ccp_country_greenland, "\uD83C\uDDEC\uD83C\uDDF1"),
        "gd" to Country("gd", "1", R.string.ccp_country_grenada, "\uD83C\uDDEC\uD83C\uDDE9"),
        "gp" to Country("gp", "450", R.string.ccp_country_guadeloupe, "\uD83C\uDDEC\uD83C\uDDF5"),
        "gu" to Country("gu", "1", R.string.ccp_country_guam, "\uD83C\uDDEC\uD83C\uDDFA"),
        "gt" to Country("gt", "502", R.string.ccp_country_guatemala, "\uD83C\uDDEC\uD83C\uDDF9"),
        "gn" to Country("gn", "224", R.string.ccp_country_guinea, "\uD83C\uDDEC\uD83C\uDDF3"),
        "gw" to Country("gw", "245", R.string.ccp_country_guinea_bissau, "\uD83C\uDDEC\uD83C\uDDFC"),
        "gy" to Country("gy", "592", R.string.ccp_country_guyana, "\uD83C\uDDEC\uD83C\uDDFE"),
        "ht" to Country("ht", "509", R.string.ccp_country_haiti, "\uD83C\uDDED\uD83C\uDDF9"),
        "hn" to Country("hn", "504", R.string.ccp_country_honduras, "\uD83C\uDDED\uD83C\uDDF3"),
        "hk" to Country("hk", "852", R.string.ccp_country_hong_kong, "\uD83C\uDDED\uD83C\uDDF0"),
        "hu" to Country("hu", "36", R.string.ccp_country_hungary, "\uD83C\uDDED\uD83C\uDDFA"),
        "is" to Country("is", "354", R.string.ccp_country_iceland, "\uD83C\uDDEE\uD83C\uDDF8"),
        "in" to Country("in", "91", R.string.ccp_country_india, "\uD83C\uDDEE\uD83C\uDDF3"),
        "id" to Country("id", "62", R.string.ccp_country_indonesia, "\uD83C\uDDEE\uD83C\uDDE9"),
        "ir" to Country("ir", "98", R.string.ccp_country_iran, "\uD83C\uDDEE\uD83C\uDDF7"),
        "iq" to Country("iq", "964", R.string.ccp_country_iraq, "\uD83C\uDDEE\uD83C\uDDF6"),
        "ie" to Country("ie", "353", R.string.ccp_country_ireland, "\uD83C\uDDEE\uD83C\uDDEA"),
        "im" to Country("im", "44", R.string.ccp_country_isle_of_man, "\uD83C\uDDEE\uD83C\uDDF2"),
        "il" to Country("il", "972", R.string.ccp_country_israel, "\uD83C\uDDEE\uD83C\uDDF1"),
        "it" to Country("it", "39", R.string.ccp_country_italy, "\uD83C\uDDEE\uD83C\uDDF9"),
        "jm" to Country("jm", "1", R.string.ccp_country_jamaica, "\uD83C\uDDEF\uD83C\uDDF2"),
        "jp" to Country("jp", "81", R.string.ccp_country_japan, "\uD83C\uDDEF\uD83C\uDDF5"),
        "je" to Country("je", "44", R.string.ccp_country_jersey_, "\uD83C\uDDEF\uD83C\uDDEA"),
        "jo" to Country("jo", "962", R.string.ccp_country_jordan, "\uD83C\uDDEF\uD83C\uDDF4"),
        "kz" to Country("kz", "7", R.string.ccp_country_kazakhstan, "\uD83C\uDDF0\uD83C\uDDFF"),
        "ke" to Country("ke", "254", R.string.ccp_country_kenya, "\uD83C\uDDF0\uD83C\uDDEA"),
        "ki" to Country("ki", "686", R.string.ccp_country_kiribati, "\uD83C\uDDF0\uD83C\uDDEE"),
        "xk" to Country("xk", "383", R.string.ccp_country_kosovo, "\uD83C\uDDFD\uD83C\uDDF0"),
        "kw" to Country("kw", "965", R.string.ccp_country_kuwait, "\uD83C\uDDF0\uD83C\uDDFC"),
        "kg" to Country("kg", "996", R.string.ccp_country_kyrgyzstan, "\uD83C\uDDF0\uD83C\uDDEC"),
        "la" to Country("la", "856", R.string.ccp_country_lao, "\uD83C\uDDF1\uD83C\uDDE6"),
        "lv" to Country("lv", "371", R.string.ccp_country_latvia, "\uD83C\uDDF1\uD83C\uDDFB"),
        "lb" to Country("lb", "961", R.string.ccp_country_lebanon, "\uD83C\uDDF1\uD83C\uDDE7"),
        "ls" to Country("ls", "266", R.string.ccp_country_lesotho, "\uD83C\uDDF1\uD83C\uDDF8"),
        "lr" to Country("lr", "231", R.string.ccp_country_liberia, "\uD83C\uDDF1\uD83C\uDDF7"),
        "ly" to Country("ly", "218", R.string.ccp_country_libya, "\uD83C\uDDF1\uD83C\uDDFE"),
        "li" to Country("li", "423", R.string.ccp_country_liechtenstein, "\uD83C\uDDF1\uD83C\uDDEE"),
        "lt" to Country("lt", "370", R.string.ccp_country_lithuania, "\uD83C\uDDF1\uD83C\uDDF9"),
        "lu" to Country("lu", "352", R.string.ccp_country_luxembourg, "\uD83C\uDDF1\uD83C\uDDFA"),
        "mo" to Country("mo", "853", R.string.ccp_country_macau, "\uD83C\uDDF2\uD83C\uDDF4"),
        "mk" to Country("mk", "389", R.string.ccp_country_macedonia, "\uD83C\uDDF2\uD83C\uDDF0"),
        "mg" to Country("mg", "261", R.string.ccp_country_madagascar, "\uD83C\uDDF2\uD83C\uDDEC"),
        "mw" to Country("mw", "265", R.string.ccp_country_malawi, "\uD83C\uDDF2\uD83C\uDDFC"),
        "my" to Country("my", "60", R.string.ccp_country_malaysia, "\uD83C\uDDF2\uD83C\uDDFE"),
        "mv" to Country("mv", "960", R.string.ccp_country_maldives, "\uD83C\uDDF2\uD83C\uDDFB"),
        "ml" to Country("ml", "223", R.string.ccp_country_mali, "\uD83C\uDDF2\uD83C\uDDF1"),
        "mt" to Country("mt", "356", R.string.ccp_country_malta, "\uD83C\uDDF2\uD83C\uDDF9"),
        "mh" to Country("mh", "692", R.string.ccp_country_marshall_islands, "\uD83C\uDDF2\uD83C\uDDED"),
        "mq" to Country("mq", "596", R.string.ccp_country_martinique, "\uD83C\uDDF2\uD83C\uDDF6"),
        "mr" to Country("mr", "222", R.string.ccp_country_mauritania, "\uD83C\uDDF2\uD83C\uDDF7"),
        "mu" to Country("mu", "230", R.string.ccp_country_mauritius, "\uD83C\uDDF2\uD83C\uDDFA"),
        "yt" to Country("yt", "262", R.string.ccp_country_mayotte, "\uD83C\uDDFE\uD83C\uDDF9"),
        "mx" to Country("mx", "52", R.string.ccp_country_mexico, "\uD83C\uDDF2\uD83C\uDDFD"),
        "fm" to Country("fm", "691", R.string.ccp_country_micronesia, "\uD83C\uDDEB\uD83C\uDDF2"),
        "md" to Country("md", "373", R.string.ccp_country_moldova, "\uD83C\uDDF2\uD83C\uDDE9"),
        "mc" to Country("mc", "377", R.string.ccp_country_monaco, "\uD83C\uDDF2\uD83C\uDDE8"),
        "mn" to Country("mn", "976", R.string.ccp_country_mongolia, "\uD83C\uDDF2\uD83C\uDDF3"),
        "me" to Country("me", "382", R.string.ccp_country_montenegro, "\uD83C\uDDF2\uD83C\uDDEA"),
        "ms" to Country("ms", "1", R.string.ccp_country_montserrat, "\uD83C\uDDF2\uD83C\uDDF8"),
        "ma" to Country("ma", "212", R.string.ccp_country_morocco, "\uD83C\uDDF2\uD83C\uDDE6"),
        "mz" to Country("mz", "258", R.string.ccp_country_mozambique, "\uD83C\uDDF2\uD83C\uDDFF"),
        "mm" to Country("mm", "95", R.string.ccp_country_myanmar, "\uD83C\uDDF2\uD83C\uDDF2"),
        "na" to Country("na", "264", R.string.ccp_country_namibia, "\uD83C\uDDF3\uD83C\uDDE6"),
        "nr" to Country("nr", "674", R.string.ccp_country_nauru, "\uD83C\uDDF3\uD83C\uDDF7"),
        "np" to Country("np", "977", R.string.ccp_country_nepal, "\uD83C\uDDF3\uD83C\uDDF5"),
        "nl" to Country("nl", "31", R.string.ccp_country_netherlands, "\uD83C\uDDF3\uD83C\uDDF1"),
        "nc" to Country("nc", "687", R.string.ccp_country_new_caledonia, "\uD83C\uDDF3\uD83C\uDDE8"),
        "nz" to Country("nz", "64", R.string.ccp_country_new_zealand, "\uD83C\uDDF3\uD83C\uDDFF"),
        "ni" to Country("ni", "505", R.string.ccp_country_nicaragua, "\uD83C\uDDF3\uD83C\uDDEE"),
        "ne" to Country("ne", "227", R.string.ccp_country_niger, "\uD83C\uDDF3\uD83C\uDDEA"),
        "ng" to Country("ng", "234", R.string.ccp_country_nigeria, "\uD83C\uDDF3\uD83C\uDDEC"),
        "nu" to Country("nu", "683", R.string.ccp_country_niue, "\uD83C\uDDF3\uD83C\uDDFA"),
        "nf" to Country("nf", "672", R.string.ccp_country_norfolk_islands, "\uD83C\uDDF3\uD83C\uDDEB"),
        "kp" to Country("kp", "850", R.string.ccp_country_north_korea, "\uD83C\uDDF0\uD83C\uDDF5"),
        "mp" to Country("mp", "1", R.string.ccp_country_northern_mariana_islands, "\uD83C\uDDF2\uD83C\uDDF5"),
        "no" to Country("no", "47", R.string.ccp_country_norway, "\uD83C\uDDF3\uD83C\uDDF4"),
        "om" to Country("om", "968", R.string.ccp_country_oman, "\uD83C\uDDF4\uD83C\uDDF2"),
        "pk" to Country("pk", "92", R.string.ccp_country_pakistan, "\uD83C\uDDF5\uD83C\uDDF0"),
        "pw" to Country("pw", "680", R.string.ccp_country_palau, "\uD83C\uDDF5\uD83C\uDDFC"),
        "ps" to Country("ps", "970", R.string.ccp_country_palestine, "\uD83C\uDDF5\uD83C\uDDF8"),
        "pa" to Country("pa", "507", R.string.ccp_country_panama, "\uD83C\uDDF5\uD83C\uDDE6"),
        "pg" to Country("pg", "675", R.string.ccp_country_papua_new_guinea, "\uD83C\uDDF5\uD83C\uDDEC"),
        "py" to Country("py", "595", R.string.ccp_country_paraguay, "\uD83C\uDDF5\uD83C\uDDFE"),
        "pe" to Country("pe", "51", R.string.ccp_country_peru, "\uD83C\uDDF5\uD83C\uDDEA"),
        "ph" to Country("ph", "63", R.string.ccp_country_philippines, "\uD83C\uDDF5\uD83C\uDDED"),
        "pn" to Country("pn", "870", R.string.ccp_country_pitcairn_islands, "\uD83C\uDDF5\uD83C\uDDF3"),
        "pl" to Country("pl", "48", R.string.ccp_country_poland, "\uD83C\uDDF5\uD83C\uDDF1"),
        "pt" to Country("pt", "351", R.string.ccp_country_portugal, "\uD83C\uDDF5\uD83C\uDDF9"),
        "pr" to Country("pr", "1", R.string.ccp_country_puerto_rico, "\uD83C\uDDF5\uD83C\uDDF7"),
        "qa" to Country("qa", "974", R.string.ccp_country_qatar, "\uD83C\uDDF6\uD83C\uDDE6"),
        "re" to Country("re", "262", R.string.ccp_country_reunion, "\uD83C\uDDF7\uD83C\uDDEA"),
        "ro" to Country("ro", "40", R.string.ccp_country_romania, "\uD83C\uDDF7\uD83C\uDDF4"),
        "ru" to Country("ru", "7", R.string.ccp_country_russian_federation, "\uD83C\uDDF7\uD83C\uDDFA"),
        "rw" to Country("rw", "250", R.string.ccp_country_rwanda, "\uD83C\uDDF7\uD83C\uDDFC"),
        "bl" to Country("bl", "590", R.string.ccp_country_saint_barthelemy, "\uD83C\uDDE7\uD83C\uDDF1"),
        "sh" to Country("sh", "290", R.string.ccp_country_saint_helena, "\uD83C\uDDF8\uD83C\uDDED"),
        "kn" to Country("kn", "1", R.string.ccp_country_saint_kitts_and_nevis, "\uD83C\uDDF0\uD83C\uDDF3"),
        "lc" to Country("lc", "1", R.string.ccp_country_saint_lucia, "\uD83C\uDDF1\uD83C\uDDE8"),
        "mf" to Country("mf", "590", R.string.ccp_country_saint_martin, "\uD83C\uDDF2\uD83C\uDDEB"),
        "pm" to Country("pm", "508", R.string.ccp_country_saint_pierre_and_miquelon, "\uD83C\uDDF5\uD83C\uDDF2"),
        "vc" to Country("vc", "1", R.string.ccp_country_saint_vincent, "\uD83C\uDDFB\uD83C\uDDE8"),
        "ws" to Country("ws", "685", R.string.ccp_country_samoa, "\uD83C\uDDFC\uD83C\uDDF8"),
        "sm" to Country("sm", "378", R.string.ccp_country_san_marino, "\uD83C\uDDF8\uD83C\uDDF2"),
        "st" to Country("st", "239", R.string.ccp_country_sao_tome_and_principe, "\uD83C\uDDF8\uD83C\uDDF9"),
        "sa" to Country("sa", "966", R.string.ccp_country_saudi_arabia, "\uD83C\uDDF8\uD83C\uDDE6"),
        "sn" to Country("sn", "221", R.string.ccp_country_senegal, "\uD83C\uDDF8\uD83C\uDDF3"),
        "rs" to Country("rs", "381", R.string.ccp_country_serbia, "\uD83C\uDDF7\uD83C\uDDF8"),
        "sc" to Country("sc", "248", R.string.ccp_country_seychelles, "\uD83C\uDDF8\uD83C\uDDE8"),
        "sl" to Country("sl", "232", R.string.ccp_country_sierra_leone, "\uD83C\uDDF8\uD83C\uDDF1"),
        "sg" to Country("sg", "65", R.string.ccp_country_singapore, "\uD83C\uDDF8\uD83C\uDDEC"),
        "sx" to Country("sx", "1", R.string.ccp_country_sint_maarten, "\uD83C\uDDF8\uD83C\uDDFD"),
        "sk" to Country("sk", "421", R.string.ccp_country_slovakia, "\uD83C\uDDF8\uD83C\uDDF0"),
        "si" to Country("si", "386", R.string.ccp_country_slovenia, "\uD83C\uDDF8\uD83C\uDDEE"),
        "sb" to Country("sb", "677", R.string.ccp_country_solomon_islands, "\uD83C\uDDF8\uD83C\uDDE7"),
        "so" to Country("so", "252", R.string.ccp_country_somalia, "\uD83C\uDDF8\uD83C\uDDF4"),
        "za" to Country("za", "27", R.string.ccp_country_south_africa, "\uD83C\uDDFF\uD83C\uDDE6"),
        "kr" to Country("kr", "82", R.string.ccp_country_south_korea, "\uD83C\uDDF0\uD83C\uDDF7"),
        "ss" to Country("ss", "211", R.string.ccp_country_south_sudan, "\uD83C\uDDF8\uD83C\uDDF8"),
        "es" to Country("es", "34", R.string.ccp_country_spain, "\uD83C\uDDEA\uD83C\uDDF8"),
        "lk" to Country("lk", "94", R.string.ccp_country_sri_lanka, "\uD83C\uDDF1\uD83C\uDDF0"),
        "sd" to Country("sd", "249", R.string.ccp_country_sudan, "\uD83C\uDDF8\uD83C\uDDE9"),
        "sr" to Country("sr", "597", R.string.ccp_country_suriname, "\uD83C\uDDF8\uD83C\uDDF7"),
        "sz" to Country("sz", "268", R.string.ccp_country_swaziland, "\uD83C\uDDF8\uD83C\uDDFF"),
        "se" to Country("se", "46", R.string.ccp_country_sweden, "\uD83C\uDDF8\uD83C\uDDEA"),
        "ch" to Country("ch", "41", R.string.ccp_country_switzerland, "\uD83C\uDDE8\uD83C\uDDED"),
        "sy" to Country("sy", "963", R.string.ccp_country_syrian_arab_republic, "\uD83C\uDDF8\uD83C\uDDFE"),
        "tw" to Country("tw", "886", R.string.ccp_country_taiwan, "\uD83C\uDDF9\uD83C\uDDFC"),
        "tj" to Country("tj", "992", R.string.ccp_country_tajikistan, "\uD83C\uDDF9\uD83C\uDDEF"),
        "tz" to Country("tz", "255", R.string.ccp_country_tanzania, "\uD83C\uDDF9\uD83C\uDDFF"),
        "th" to Country("th", "66", R.string.ccp_country_thailand, "\uD83C\uDDF9\uD83C\uDDED"),
        "tl" to Country("tl", "670", R.string.ccp_country_timor, "\uD83C\uDDF9\uD83C\uDDF1"),
        "tg" to Country("tg", "228", R.string.ccp_country_togo, "\uD83C\uDDF9\uD83C\uDDEC"),
        "tk" to Country("tk", "690", R.string.ccp_country_tokelau, "\uD83C\uDDF9\uD83C\uDDF0"),
        "to" to Country("to", "676", R.string.ccp_country_tonga, "\uD83C\uDDF9\uD83C\uDDF4"),
        "tt" to Country("tt", "1", R.string.ccp_country_trinidad_tobago, "\uD83C\uDDF9\uD83C\uDDF9"),
        "tn" to Country("tn", "216", R.string.ccp_country_tunisia, "\uD83C\uDDF9\uD83C\uDDF3"),
        "tr" to Country("tr", "90", R.string.ccp_country_turkey, "\uD83C\uDDF9\uD83C\uDDF7"),
        "tm" to Country("tm", "993", R.string.ccp_country_turkmenistan, "\uD83C\uDDF9\uD83C\uDDF2"),
        "tc" to Country("tc", "1", R.string.ccp_country_turks_and_caicos_islands, "\uD83C\uDDF9\uD83C\uDDE8"),
        "tv" to Country("tv", "688", R.string.ccp_country_tuvalu, "\uD83C\uDDF9\uD83C\uDDFB"),
        "vi" to Country("vi", "1", R.string.ccp_country_us_virgin_islands, "\uD83C\uDDFB\uD83C\uDDEE"),
        "ug" to Country("ug", "256", R.string.ccp_country_uganda, "\uD83C\uDDFA\uD83C\uDDEC"),
        "ua" to Country("ua", "380", R.string.ccp_country_ukraine, "\uD83C\uDDFA\uD83C\uDDE6"),
        "ae" to Country("ae", "971", R.string.ccp_country_united_arab_emirates, "\uD83C\uDDE6\uD83C\uDDEA"),
        "gb" to Country("gb", "44", R.string.ccp_country_united_kingdom, "\uD83C\uDDEC\uD83C\uDDE7"),
        "us" to Country("us", "1", R.string.ccp_country_united_states, "\uD83C\uDDFA\uD83C\uDDF8"),
        "uy" to Country("uy", "598", R.string.ccp_country_uruguay, "\uD83C\uDDFA\uD83C\uDDFE"),
        "uz" to Country("uz", "998", R.string.ccp_country_uzbekistan, "\uD83C\uDDFA\uD83C\uDDFF"),
        "vu" to Country("vu", "678", R.string.ccp_country_vanuatu, "\uD83C\uDDFB\uD83C\uDDFA"),
        "va" to Country("va", "379", R.string.ccp_country_vatican, "\uD83C\uDDFB\uD83C\uDDE6"),
        "ve" to Country("ve", "58", R.string.ccp_country_venezuela, "\uD83C\uDDFB\uD83C\uDDEA"),
        "vn" to Country("vn", "84", R.string.ccp_country_vietnam, "\uD83C\uDDFB\uD83C\uDDF3"),
        "wf" to Country("wf", "681", R.string.ccp_country_wallis_and_futuna, "\uD83C\uDDFC\uD83C\uDDEB"),
        "eh" to Country("eh", "212", R.string.ccp_country_western_sahara, "\uD83C\uDDEA\uD83C\uDDED"),
        "ye" to Country("ye", "967", R.string.ccp_country_yemen, "\uD83C\uDDFE\uD83C\uDDEA"),
        "zm" to Country("zm", "260", R.string.ccp_country_zambia, "\uD83C\uDDFF\uD83C\uDDF2"),
        "zw" to Country("zw", "263", R.string.ccp_country_zimbabwe, "\uD83C\uDDFF\uD83C\uDDFC")
      )
    }


  }

  private fun getEmojiByUnicode(unicode: Int): String? {
    return String(Character.toChars(unicode))
  }

  private val A = getEmojiByUnicode(0x1F1E6)
  private val B = getEmojiByUnicode(0x1F1E7)
  private val C = getEmojiByUnicode(0x1F1E8)
  private val D = getEmojiByUnicode(0x1F1E9)
  private val E = getEmojiByUnicode(0x1F1EA)
  private val F = getEmojiByUnicode(0x1F1EB)
  private val G = getEmojiByUnicode(0x1F1EC)
  private val H = getEmojiByUnicode(0x1F1ED)
  private val I = getEmojiByUnicode(0x1F1EE)
  private val J = getEmojiByUnicode(0x1F1EF)
  private val K = getEmojiByUnicode(0x1F1F0)
  private val L = getEmojiByUnicode(0x1F1F1)
  private val M = getEmojiByUnicode(0x1F1F2)
  private val N = getEmojiByUnicode(0x1F1F3)
  private val O = getEmojiByUnicode(0x1F1F4)
  private val P = getEmojiByUnicode(0x1F1F5)
  private val Q = getEmojiByUnicode(0x1F1F6)
  private val R = getEmojiByUnicode(0x1F1F7)
  private val S = getEmojiByUnicode(0x1F1F8)
  private val T = getEmojiByUnicode(0x1F1F9)
  private val U = getEmojiByUnicode(0x1F1FA)
  private val V = getEmojiByUnicode(0x1F1FB)
  private val W = getEmojiByUnicode(0x1F1FC)
  private val X = getEmojiByUnicode(0x1F1FD)
  private val Y = getEmojiByUnicode(0x1F1FE)
  private val Z = getEmojiByUnicode(0x1F1FF)

  private fun getCodeByCharacter(character: Char): String? {
    val code: String
    code = when (Character.toUpperCase(character)) {
      'A' -> A!!
      'B' -> B!!
      'C' -> C!!
      'D' -> D!!
      'E' -> E!!
      'F' -> F!!
      'G' -> G!!
      'H' -> H!!
      'I' -> I!!
      'J' -> J!!
      'K' -> K!!
      'L' -> L!!
      'M' -> M!!
      'N' -> N!!
      'O' -> O!!
      'P' -> P!!
      'Q' -> Q!!
      'R' -> R!!
      'S' -> S!!
      'T' -> T!!
      'U' -> U!!
      'V' -> V!!
      'W' -> W!!
      'X' -> X!!
      'Y' -> Y!!
      'Z' -> Z!!
      else -> ""
    }
    return code
  }

  fun getCountryFlagByCountryCode(countryCode: String): String? {
    val flag: String
    flag = if (countryCode.length == 2) {
      getCodeByCharacter(countryCode[0]) + getCodeByCharacter(countryCode[1])
    } else {
      ""
    }
    return flag
  }
}