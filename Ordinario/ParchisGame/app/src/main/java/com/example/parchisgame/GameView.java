package com.example.parchisgame;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/** Única vista de la app: lleva la pantalla actual, los botones y el escalado. */
public class GameView extends View {
    static final class Btn {
        String key;
        float x, y, w, h;
        boolean enabled;
        Runnable act;
    }

    Screen cur;
    float scale = 1, ox, oy;
    private List<Btn> btns = new ArrayList<>();
    private List<Btn> next = new ArrayList<>();
    private String downKey;
    private final long t0 = System.nanoTime();
    private long last = System.nanoTime();
    private String toastText;
    private long toastUntil;

    public GameView(Context ctx) {
        super(ctx);
        setFocusable(true);
        cur = new TitleScreen(this);
    }

    void go(Screen s) {
        cur = s;
        downKey = null;
        invalidate();
    }

    void toast(String s) {
        toastText = s;
        toastUntil = System.currentTimeMillis() + 1800;
    }

    boolean back() {
        return cur.back();
    }

    float time() {
        return (System.nanoTime() - t0) / 1e9f;
    }

    // ---------- botones ----------
    Btn reg(String key, float x, float y, float w, float h, boolean enabled, Runnable act) {
        Btn b = new Btn();
        b.key = key;
        b.x = x;
        b.y = y;
        b.w = w;
        b.h = h;
        b.enabled = enabled;
        b.act = act;
        next.add(b);
        return b;
    }

    boolean pressed(String key) {
        return key.equals(downKey);
    }

    /** Dibuja y registra un botón redondeado. */
    void button(Canvas c, String key, float x, float y, float w, float h, String label,
                int fill, int txt, boolean selected, Runnable act) {
        reg(key, x, y, w, h, true, act);
        float off = pressed(key) ? 4 : 0;
        float rad = Math.min(h * 0.42f, 40);
        Art.rrect(c, x, y + 6 - off, w, h, rad, 0x30000000, 0, 0);
        Art.rrect(c, x, y + off, w, h, rad, fill, selected ? Art.INK : Art.WHITE, selected ? 5 : 4);
        Art.text(c, label, x + w / 2, y + off + h / 2, h * 0.4f, true, txt, w - 24);
    }

    private Btn hit(float x, float y) {
        for (int i = btns.size() - 1; i >= 0; i--) {
            Btn b = btns.get(i);
            if (x >= b.x && x <= b.x + b.w && y >= b.y && y <= b.y + b.h) return b;
        }
        return null;
    }

    // ---------- ciclo ----------
    @Override
    protected void onDraw(Canvas c) {
        int w = getWidth(), h = getHeight();
        long now = System.nanoTime();
        float dt = Math.min(3f, (now - last) / 16_666_667f);
        last = now;
        float t = time();

        scale = Math.min(w / (float) Art.W, h / (float) Art.H);
        ox = (w - Art.W * scale) / 2f;
        oy = (h - Art.H * scale) / 2f;

        Art.background(c, w, h, t);
        cur.tick(dt);
        next = new ArrayList<>();
        c.save();
        c.translate(ox, oy);
        c.scale(scale, scale);
        cur.draw(c, t);
        drawToast(c);
        c.restore();
        btns = next;
        postInvalidateOnAnimation();
    }

    private void drawToast(Canvas c) {
        if (toastText == null) return;
        long left = toastUntil - System.currentTimeMillis();
        if (left <= 0) {
            toastText = null;
            return;
        }
        int a = (int) (255 * Math.min(1f, left / 400f));
        Art.rrect(c, 50, 860, 440, 64, 32, Art.alpha(Art.INK, a), 0, 0);
        Art.text(c, toastText, 270, 892, 24, true, Art.alpha(Art.WHITE, a), 410);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = (e.getX() - ox) / scale;
        float y = (e.getY() - oy) / scale;
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                Btn b = hit(x, y);
                downKey = b == null ? null : b.key;
                return true;
            }
            case MotionEvent.ACTION_UP: {
                Btn b = hit(x, y);
                if (b != null) {
                    if (b.key.equals(downKey) && b.enabled && b.act != null) b.act.run();
                } else if (downKey == null) {
                    cur.click(x, y);
                }
                downKey = null;
                return true;
            }
            case MotionEvent.ACTION_CANCEL:
                downKey = null;
                return true;
            default:
                return true;
        }
    }
}
