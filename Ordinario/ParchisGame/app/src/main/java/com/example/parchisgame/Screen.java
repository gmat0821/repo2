package com.example.parchisgame;

import android.graphics.Canvas;

/** Una pantalla del juego (menú, ajustes, partida). Se dibuja en coordenadas lógicas 540 x 960. */
abstract class Screen {
    final GameView v;

    Screen(GameView v) {
        this.v = v;
    }

    abstract void draw(Canvas c, float t);

    /** dt = fotogramas de 1/60 s transcurridos. */
    void tick(float dt) {
    }

    /** Toque que no cayó en ningún botón. */
    void click(float x, float y) {
    }

    /** Botón atrás del móvil. Devuelve true si la pantalla lo gestionó. */
    boolean back() {
        return false;
    }
}
