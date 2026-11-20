package com.example.parchisgame;

import android.graphics.Canvas;

/** Menú principal: título, iniciar sesión (aún no sirve) y entrar como invitado. */
final class TitleScreen extends Screen {
    TitleScreen(GameView v) {
        super(v);
    }

    @Override
    void draw(Canvas c, float t) {
        Art.outlined(c, "Chiikawa", 270, 175, 92, 0xFFFF7FA8, Art.WHITE, 14);
        Art.outlined(c, "Parchís", 270, 275, 70, 0xFF4FA8E0, Art.WHITE, 12);

        Chara[] all = Chara.values();
        for (int i = 0; i < 4; i++) {
            float bob = (float) Math.sin(t * 3 + i * 1.3) * 12;
            Art.token(c, all[i], 90 + 120 * i, 430 + bob, 52);
        }
        Art.text(c, "¡Juega con tus amigos de Chiikawa!", 270, 530, 23, true, Art.INK_SOFT, 480);

        v.button(c, "login", 70, 600, 400, 92, "Iniciar sesión", 0xFFFFFFFF, Art.INK, false,
                () -> v.toast("Iniciar sesión: ¡próximamente!"));
        v.button(c, "guest", 70, 720, 400, 92, "Entrar como invitado", Art.ACCENT, Art.WHITE, true,
                () -> v.go(new LobbyScreen(v)));

        float pulse = 1f + 0.15f * (float) Math.sin(t * 3);
        Art.heart(c, 270, 890, 14 * pulse, 0xFFFF9DB5);
    }

    @Override
    boolean back() {
        return false;
    }
}

/** Menú de invitado: contra jugadores (próximamente) o contra bots. */
final class LobbyScreen extends Screen {
    LobbyScreen(GameView v) {
        super(v);
    }

    @Override
    void draw(Canvas c, float t) {
        Art.token(c, Chara.CHIIKAWA, 270, 160 + (float) Math.sin(t * 3) * 8, 62);
        Art.outlined(c, "¡Hola, invitado!", 270, 270, 52, 0xFFFF7FA8, Art.WHITE, 11);
        Art.text(c, "¿Cómo quieres jugar?", 270, 335, 26, true, Art.INK_SOFT);

        v.button(c, "pvp", 50, 410, 440, 110, "Jugar contra jugadores", 0xFFE4DDD8, Art.INK_SOFT, false,
                () -> v.toast("Jugar contra jugadores: ¡próximamente!"));
        // etiqueta "Próximamente"
        Art.rrect(c, 330, 394, 150, 34, 17, 0xFFFFD166, Art.WHITE, 3);
        Art.text(c, "Próximamente", 405, 411, 18, true, Art.INK, 136);

        v.button(c, "bots", 50, 560, 440, 110, "Jugar contra bots", Art.ACCENT, Art.WHITE, true,
                () -> v.go(new SetupScreen(v)));

        v.button(c, "back", 150, 740, 240, 74, "Volver", 0xFFFFFFFF, Art.INK, false,
                () -> v.go(new TitleScreen(v)));
    }

    @Override
    boolean back() {
        v.go(new TitleScreen(v));
        return true;
    }
}

/** Ajustes de la partida contra bots: dificultad, personaje y cantidad de bots. */
final class SetupScreen extends Screen {
    private int diff = 1, chara = 0, bots = 3;

    private static final String[] DIFF_NAMES = {"Fácil", "Normal", "Difícil"};
    private static final int[] DIFF_COLORS = {0xFF8ED8A0, 0xFFFFD166, 0xFFFF8A8A};
    private static final String[] DIFF_INFO = {
            "Los bots mueven las fichas al azar.",
            "Los bots juegan con algo de estrategia.",
            "Los bots comen, huyen y te estorban."};

    SetupScreen(GameView v) {
        super(v);
    }

    @Override
    void draw(Canvas c, float t) {
        v.button(c, "back", 16, 16, 120, 50, "‹ Volver", 0xFFFFFFFF, Art.INK, false,
                () -> v.go(new LobbyScreen(v)));
        Art.outlined(c, "Nueva partida", 270, 112, 52, 0xFFFF7FA8, Art.WHITE, 11);

        // dificultad
        Art.textLeft(c, "Dificultad", 30, 178, 26, true, Art.INK);
        for (int i = 0; i < 3; i++) {
            final int k = i;
            v.button(c, "diff" + i, 30 + i * 164, 200, 152, 62, DIFF_NAMES[i],
                    diff == i ? DIFF_COLORS[i] : Art.mix(DIFF_COLORS[i], Art.WHITE, 0.6f),
                    Art.INK, diff == i, () -> diff = k);
        }
        Art.text(c, DIFF_INFO[diff], 270, 292, 20, false, Art.INK_SOFT, 480);

        // personaje
        Art.textLeft(c, "Tu personaje", 30, 350, 26, true, Art.INK);
        Chara[] all = Chara.values();
        for (int i = 0; i < 4; i++) {
            final int k = i;
            float x = 24 + i * 123;
            boolean sel = chara == i;
            float lift = sel ? -6 : 0;
            v.button(c, "ch" + i, x, 372 + lift, 114, 170, "", sel ? all[i].light : Art.mix(all[i].light, Art.WHITE, 0.55f),
                    Art.INK, sel, () -> chara = k);
            float bob = sel ? (float) Math.sin(t * 5) * 4 : 0;
            Art.token(c, all[i], x + 57, 438 + lift + bob, 42);
            Art.text(c, all[i].label, x + 57, 512 + lift, 18, true, Art.INK, 104);
        }

        // bots
        Art.textLeft(c, "Cantidad de bots", 30, 598, 26, true, Art.INK);
        for (int i = 1; i <= 3; i++) {
            final int k = i;
            v.button(c, "bots" + i, 30 + (i - 1) * 164, 620, 152, 62, i + (i == 1 ? " bot" : " bots"),
                    bots == i ? 0xFF9ED8F5 : Art.mix(0xFF9ED8F5, Art.WHITE, 0.6f),
                    Art.INK, bots == i, () -> bots = k);
        }

        // rivales
        Art.text(c, "Tus rivales", 270, 722, 22, true, Art.INK_SOFT);
        int n = 0;
        float startX = 270 - (bots - 1) * 70f;
        for (int i = 0; i < 4 && n < bots; i++) {
            if (i == chara) continue;
            float bob = (float) Math.sin(t * 3 + n) * 4;
            Art.token(c, all[i], startX + n * 140, 782 + bob, 36);
            Art.text(c, all[i].label, startX + n * 140, 835, 16, true, Art.INK, 120);
            n++;
        }

        v.button(c, "start", 70, 860, 400, 84, "¡Empezar partida!", Art.ACCENT, Art.WHITE, true,
                () -> v.go(new GameScreen(v, diff, all[chara], bots)));
    }

    @Override
    boolean back() {
        v.go(new LobbyScreen(v));
        return true;
    }
}
