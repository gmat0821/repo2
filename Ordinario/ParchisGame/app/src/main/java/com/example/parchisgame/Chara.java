package com.example.parchisgame;

/** Los cuatro personajes (cada uno es también un color de equipo). */
enum Chara {
    CHIIKAWA("Chiikawa", 0xFFF28FAD, 0xFFFFDDE8, 0xFFC95C80),
    HACHIWARE("Hachiware", 0xFF5DB7F0, 0xFFD2ECFC, 0xFF2E88C4),
    USAGI("Usagi", 0xFFF6C531, 0xFFFFF1B0, 0xFFC49608),
    MOMONGA("Momonga", 0xFFA98BDB, 0xFFE6DCF8, 0xFF7656B2);

    final String label;
    final int main, light, dark;

    Chara(String label, int main, int light, int dark) {
        this.label = label;
        this.main = main;
        this.light = light;
        this.dark = dark;
    }
}
