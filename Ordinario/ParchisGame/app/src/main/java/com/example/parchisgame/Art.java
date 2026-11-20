package com.example.parchisgame;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;

/** Utilidades de dibujo: formas, texto y las caras de Chiikawa y compañía. */
final class Art {
    private Art() {
    }

    static final int W = 540, H = 960;
    static final int CREAM = 0xFFFFF6EA, PINK_BG = 0xFFFFE4EE;
    static final int INK = 0xFF6B4A3A, INK_SOFT = 0xFF9A7B6B;
    static final int ACCENT = 0xFFFF7FA8, WHITE = 0xFFFFFFFF;
    static final int GRAY = 0xFFE6E0DC;

    static final Paint P = new Paint(Paint.ANTI_ALIAS_FLAG);
    static final RectF RF = new RectF();
    static final Path PATH = new Path();
    static final Typeface BOLD = Typeface.create("casual", Typeface.BOLD);
    static final Typeface REG = Typeface.create("casual", Typeface.NORMAL);

    private static Shader bgShader;
    private static int bgH;

    // ---------- color ----------
    static int alpha(int c, int a) {
        return (c & 0x00FFFFFF) | (a << 24);
    }

    static int mix(int a, int b, float t) {
        int r = (int) (((a >> 16) & 255) * (1 - t) + ((b >> 16) & 255) * t);
        int g = (int) (((a >> 8) & 255) * (1 - t) + ((b >> 8) & 255) * t);
        int bl = (int) ((a & 255) * (1 - t) + (b & 255) * t);
        return 0xFF000000 | (r << 16) | (g << 8) | bl;
    }

    // ---------- formas ----------
    static void fillStyle(int color) {
        P.setShader(null);
        P.setStyle(Paint.Style.FILL);
        P.setColor(color);
    }

    static void strokeStyle(int color, float w) {
        P.setShader(null);
        P.setStyle(Paint.Style.STROKE);
        P.setStrokeWidth(w);
        P.setStrokeCap(Paint.Cap.ROUND);
        P.setStrokeJoin(Paint.Join.ROUND);
        P.setColor(color);
    }

    static void oval(Canvas c, float cx, float cy, float rx, float ry, int fill, int line, float sw) {
        RF.set(cx - rx, cy - ry, cx + rx, cy + ry);
        if (fill != 0) {
            fillStyle(fill);
            c.drawOval(RF, P);
        }
        if (line != 0) {
            strokeStyle(line, sw);
            c.drawOval(RF, P);
        }
    }

    static void circle(Canvas c, float cx, float cy, float r, int fill, int line, float sw) {
        oval(c, cx, cy, r, r, fill, line, sw);
    }

    static void rrect(Canvas c, float x, float y, float w, float h, float rad, int fill, int line, float sw) {
        RF.set(x, y, x + w, y + h);
        if (fill != 0) {
            fillStyle(fill);
            c.drawRoundRect(RF, rad, rad, P);
        }
        if (line != 0) {
            strokeStyle(line, sw);
            c.drawRoundRect(RF, rad, rad, P);
        }
    }

    /** Arco abierto (ángulos Android: 0 = derecha, positivo = sentido horario). */
    static void arc(Canvas c, float cx, float cy, float rx, float ry, float start, float sweep, int line, float sw) {
        RF.set(cx - rx, cy - ry, cx + rx, cy + ry);
        strokeStyle(line, sw);
        c.drawArc(RF, start, sweep, false, P);
    }

    static void halfDisc(Canvas c, float cx, float cy, float rx, float ry, int fill, int line, float sw) {
        RF.set(cx - rx, cy - ry, cx + rx, cy + ry);
        fillStyle(fill);
        c.drawArc(RF, 0, 180, true, P);
        strokeStyle(line, sw);
        c.drawArc(RF, 0, 180, true, P);
    }

    static void heart(Canvas c, float cx, float cy, float s, int color) {
        Path p = PATH;
        p.reset();
        p.moveTo(cx, cy + s * 0.9f);
        p.cubicTo(cx - s * 1.6f, cy - s * 0.1f, cx - s * 0.7f, cy - s * 1.1f, cx, cy - s * 0.35f);
        p.cubicTo(cx + s * 0.7f, cy - s * 1.1f, cx + s * 1.6f, cy - s * 0.1f, cx, cy + s * 0.9f);
        p.close();
        fillStyle(color);
        c.drawPath(p, P);
    }

    static void star(Canvas c, float cx, float cy, float R, float r, int color) {
        Path p = PATH;
        p.reset();
        for (int i = 0; i < 10; i++) {
            double ang = -Math.PI / 2 + i * Math.PI / 5;
            float rad = (i % 2 == 0) ? R : r;
            float x = cx + (float) Math.cos(ang) * rad;
            float y = cy + (float) Math.sin(ang) * rad;
            if (i == 0) p.moveTo(x, y);
            else p.lineTo(x, y);
        }
        p.close();
        fillStyle(color);
        c.drawPath(p, P);
    }

    // ---------- texto ----------
    static void textStyle(float size, boolean bold, int color, Paint.Align align) {
        P.setShader(null);
        P.setStyle(Paint.Style.FILL);
        P.setTypeface(bold ? BOLD : REG);
        P.setTextSize(size);
        P.setColor(color);
        P.setTextAlign(align);
    }

    private static float baseline(float cy) {
        Paint.FontMetrics fm = P.getFontMetrics();
        return cy - (fm.ascent + fm.descent) / 2f;
    }

    /** Texto centrado; si maxW > 0 encoge la letra hasta que quepa. */
    static void text(Canvas c, String s, float cx, float cy, float size, boolean bold, int color, float maxW) {
        textStyle(size, bold, color, Paint.Align.CENTER);
        if (maxW > 0) {
            float w = P.measureText(s);
            if (w > maxW) P.setTextSize(size * maxW / w);
        }
        c.drawText(s, cx, baseline(cy), P);
    }

    static void text(Canvas c, String s, float cx, float cy, float size, boolean bold, int color) {
        text(c, s, cx, cy, size, bold, color, 0);
    }

    static void textLeft(Canvas c, String s, float x, float cy, float size, boolean bold, int color) {
        textStyle(size, bold, color, Paint.Align.LEFT);
        c.drawText(s, x, baseline(cy), P);
    }

    static void outlined(Canvas c, String s, float cx, float cy, float size, int fill, int outline, float ow) {
        textStyle(size, true, outline, Paint.Align.CENTER);
        float by = baseline(cy);
        P.setStyle(Paint.Style.STROKE);
        P.setStrokeWidth(ow);
        P.setStrokeJoin(Paint.Join.ROUND);
        c.drawText(s, cx, by, P);
        P.setStyle(Paint.Style.FILL);
        P.setColor(fill);
        c.drawText(s, cx, by, P);
    }

    // ---------- fondo ----------
    static void background(Canvas c, int w, int h, float t) {
        if (bgShader == null || bgH != h) {
            bgShader = new LinearGradient(0, 0, 0, h, CREAM, PINK_BG, Shader.TileMode.CLAMP);
            bgH = h;
        }
        P.setStyle(Paint.Style.FILL);
        P.setShader(bgShader);
        c.drawRect(0, 0, w, h, P);
        P.setShader(null);
        float s = w / 540f;
        float sp = 70 * s;
        float off = (t * 8 * s) % sp;
        fillStyle(0x8CFFFFFF);
        for (int y = -1; y < h / sp + 2; y++) {
            for (int x = -1; x < w / sp + 2; x++) {
                float cx = x * sp + ((y & 1) == 0 ? 0 : sp / 2) + off;
                float cy = y * sp + off;
                c.drawCircle(cx, cy, 9 * s, P);
            }
        }
        int[] cols = {0x66FF8FB1, 0x667CC6F2, 0x66F6C531, 0x66A98BDB};
        for (int i = 0; i < 10; i++) {
            float span = h / s + 120;
            float y = h - ((t * 22 + i * 140) % span) * s + 40 * s;
            float x = ((i * 173) % 540) * s + (float) Math.sin(t * 0.8 + i) * 18 * s;
            float sz = (10 + (i % 3) * 3) * s;
            if (i % 2 == 0) heart(c, x, y, sz, cols[i % 4]);
            else star(c, x, y, sz * 1.2f, sz * 0.5f, cols[i % 4]);
        }
    }

    // ---------- personajes ----------
    private static void dot(Canvas c, float x, float y, float rx, float ry, int col) {
        oval(c, x, y, rx, ry, col, 0, 0);
    }

    private static void eyes(Canvas c, float cx, float cy, float r, float spread, float dy, float rx, float ry, boolean shine) {
        for (int s = -1; s <= 1; s += 2) {
            float ex = cx + s * spread * r;
            float ey = cy + dy * r;
            dot(c, ex, ey, rx * r, ry * r, INK);
            if (shine) dot(c, ex - rx * r * 0.3f, ey - ry * r * 0.35f, rx * r * 0.38f, rx * r * 0.38f, WHITE);
        }
    }

    private static void cheeks(Canvas c, float cx, float cy, float r, float spread, float dy) {
        for (int s = -1; s <= 1; s += 2) {
            dot(c, cx + s * spread * r, cy + dy * r, 0.2f * r, 0.12f * r, 0x99FF8FA8);
        }
    }

    /** Cara de un personaje centrada en (cx, cy); r es el radio de la cabeza. */
    static void face(Canvas c, Chara ch, float cx, float cy, float r) {
        float sw = Math.max(1f, r * 0.075f);
        int line = INK;
        switch (ch) {
            case CHIIKAWA: {
                int skin = 0xFFFFFCF8;
                for (int s = -1; s <= 1; s += 2) {
                    oval(c, cx + s * 0.62f * r, cy - 0.8f * r, 0.34f * r, 0.34f * r, skin, line, sw);
                    dot(c, cx + s * 0.62f * r, cy - 0.78f * r, 0.16f * r, 0.16f * r, 0xFFFFD3DE);
                }
                oval(c, cx, cy, 1.08f * r, 0.92f * r, skin, line, sw);
                eyes(c, cx, cy, r, 0.4f, -0.02f, 0.085f, 0.1f, false);
                cheeks(c, cx, cy, r, 0.66f, 0.25f);
                arc(c, cx, cy + 0.13f * r, 0.13f * r, 0.1f * r, 20, 140, line, sw);
                break;
            }
            case HACHIWARE: {
                int blue = 0xFF69B4EA;
                for (int s = -1; s <= 1; s += 2) {
                    Path p = new Path();
                    p.moveTo(cx + s * 0.3f * r, cy - 0.75f * r);
                    p.lineTo(cx + s * 0.95f * r, cy - 0.35f * r);
                    p.lineTo(cx + s * 0.9f * r, cy - 1.2f * r);
                    p.close();
                    fillStyle(blue);
                    c.drawPath(p, P);
                    strokeStyle(line, sw);
                    c.drawPath(p, P);
                }
                oval(c, cx, cy, 1.08f * r, 0.92f * r, WHITE, 0, 0);
                RF.set(cx - 1.08f * r, cy - 0.92f * r, cx + 1.08f * r, cy + 0.92f * r);
                Path head = new Path();
                head.addOval(RF, Path.Direction.CW);
                c.save();
                c.clipPath(head);
                oval(c, cx, cy - 1.15f * r, 1.35f * r, 0.85f * r, blue, 0, 0);
                c.restore();
                oval(c, cx, cy, 1.08f * r, 0.92f * r, 0, line, sw);
                eyes(c, cx, cy, r, 0.42f, 0.08f, 0.085f, 0.1f, false);
                cheeks(c, cx, cy, r, 0.68f, 0.34f);
                halfDisc(c, cx, cy + 0.22f * r, 0.27f * r, 0.26f * r, 0xFFE8627A, line, sw * 0.8f);
                break;
            }
            case USAGI: {
                int skin = 0xFFFFF0A0;
                for (int s = -1; s <= 1; s += 2) {
                    c.save();
                    c.rotate(s * 10, cx + s * 0.38f * r, cy - 0.8f * r);
                    oval(c, cx + s * 0.38f * r, cy - 1.05f * r, 0.22f * r, 0.58f * r, skin, line, sw);
                    dot(c, cx + s * 0.38f * r, cy - 1.0f * r, 0.1f * r, 0.36f * r, 0xFFFFC2D0);
                    c.restore();
                }
                oval(c, cx, cy, 1.08f * r, 0.92f * r, skin, line, sw);
                eyes(c, cx, cy, r, 0.4f, -0.02f, 0.14f, 0.17f, true);
                cheeks(c, cx, cy, r, 0.68f, 0.3f);
                halfDisc(c, cx, cy + 0.2f * r, 0.3f * r, 0.27f * r, 0xFFE8627A, line, sw * 0.8f);
                break;
            }
            case MOMONGA: {
                int fur = 0xFFD9B98F;
                for (int s = -1; s <= 1; s += 2) {
                    oval(c, cx + s * 0.74f * r, cy - 0.68f * r, 0.28f * r, 0.28f * r, fur, line, sw);
                    dot(c, cx + s * 0.74f * r, cy - 0.66f * r, 0.13f * r, 0.13f * r, 0xFFFFC2D0);
                }
                oval(c, cx, cy, 1.08f * r, 0.92f * r, fur, line, sw);
                dot(c, cx, cy + 0.14f * r, 0.8f * r, 0.62f * r, 0xFFFFF1DE);
                eyes(c, cx, cy, r, 0.38f, 0.04f, 0.12f, 0.16f, true);
                cheeks(c, cx, cy, r, 0.62f, 0.32f);
                dot(c, cx, cy + 0.2f * r, 0.06f * r, 0.045f * r, 0xFFE07090);
                arc(c, cx - 0.09f * r, cy + 0.26f * r, 0.09f * r, 0.07f * r, 10, 150, line, sw * 0.8f);
                arc(c, cx + 0.09f * r, cy + 0.26f * r, 0.09f * r, 0.07f * r, 20, 150, line, sw * 0.8f);
                break;
            }
        }
    }

    /** Ficha redonda: aro del color del equipo con la cara dentro. */
    static void token(Canvas c, Chara ch, float cx, float cy, float R) {
        oval(c, cx, cy + R * 0.82f, R * 0.8f, R * 0.2f, 0x33000000, 0, 0);
        circle(c, cx, cy, R, WHITE, 0, 0);
        circle(c, cx, cy, R * 0.9f, ch.main, 0, 0);
        circle(c, cx, cy, R * 0.76f, ch.light, 0, 0);
        face(c, ch, cx, cy + 0.14f * R, 0.6f * R);
    }
}
