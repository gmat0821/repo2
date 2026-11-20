package com.example.parchisgame;

import android.graphics.Canvas;
import android.graphics.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Pantalla de la partida: tablero, fichas, dado, turnos de los bots y animaciones. */
final class GameScreen extends Screen {
    static final float BX = 14, BY = 172, CS = (540 - 28) / 19f;
    static final int[][] TRACK = new int[68][];

    static {
        int k = 0;
        for (int r = 18; r >= 11; r--) TRACK[k++] = new int[]{8, r};      // 0..7
        for (int cc = 7; cc >= 0; cc--) TRACK[k++] = new int[]{cc, 10};   // 8..15
        TRACK[k++] = new int[]{0, 9};                                     // 16
        for (int cc = 0; cc <= 7; cc++) TRACK[k++] = new int[]{cc, 8};    // 17..24
        for (int r = 7; r >= 0; r--) TRACK[k++] = new int[]{8, r};        // 25..32
        TRACK[k++] = new int[]{9, 0};                                     // 33
        for (int r = 0; r <= 7; r++) TRACK[k++] = new int[]{10, r};       // 34..41
        for (int cc = 11; cc <= 18; cc++) TRACK[k++] = new int[]{cc, 8};  // 42..49
        TRACK[k++] = new int[]{18, 9};                                    // 50
        for (int cc = 18; cc >= 11; cc--) TRACK[k++] = new int[]{cc, 10}; // 51..58
        for (int r = 11; r <= 18; r++) TRACK[k++] = new int[]{10, r};     // 59..66
        TRACK[k] = new int[]{9, 18};                                      // 67
    }

    private static final class Particle {
        float x, y, vx, vy, life, max, size;
        int kind, color;
    }

    private final int diff, botCount;
    private final Chara player;
    private final Chara[] seatChar = new Chara[4];
    private final boolean[] active = new boolean[4];
    private final Engine eng;
    private final float[][] vis = new float[4][4];
    private final boolean[][] goalFx = new boolean[4][4];
    private final Random rnd = new Random();
    private final List<Particle> parts = new ArrayList<>();

    private boolean rolling, animating, confirmExit;
    private long rollEnd, nextFace, pauseUntil;
    private int pendingDie, shownDie = 1, dieSeat;
    private String msg = "";

    GameScreen(GameView v, int diff, Chara player, int bots) {
        super(v);
        this.diff = diff;
        this.player = player;
        this.botCount = bots;
        List<Chara> others = new ArrayList<>();
        for (Chara ch : Chara.values()) if (ch != player) others.add(ch);
        int[] botSeats = bots == 1 ? new int[]{2} : bots == 2 ? new int[]{1, 3} : new int[]{1, 2, 3};
        active[0] = true;
        seatChar[0] = player;
        for (int i = 0; i < botSeats.length; i++) {
            active[botSeats[i]] = true;
            seatChar[botSeats[i]] = others.get(i);
        }
        eng = new Engine(active, 0);
        for (int s = 0; s < 4; s++) for (int i = 0; i < 4; i++) vis[s][i] = -1;
        pauseUntil = System.currentTimeMillis() + 700;
        msg = "¡Que empiece el juego!";
    }

    // ------------------------------------------------------------------ geometría
    private static float[] cellC(float col, float row) {
        return new float[]{BX + (col + 0.5f) * CS, BY + (row + 0.5f) * CS};
    }

    private static float[] pt(float cx, float cy) {
        return new float[]{BX + cx * CS, BY + cy * CS};
    }

    private static float[] spot(int s, int p, int i) {
        if (p < 0) {
            float[][] nc = {{4, 15}, {4, 4}, {15, 4}, {15, 15}};
            float dx = ((i % 2) * 2 - 1) * 1.4f, dy = ((i / 2) * 2 - 1) * 1.4f;
            return pt(nc[s][0] + dx, nc[s][1] + dy);
        }
        if (p <= 63) {
            int[] t = TRACK[(Engine.START[s] + p) % 68];
            return cellC(t[0], t[1]);
        }
        if (p <= 70) {
            int k = p - 64;
            switch (s) {
                case 0: return cellC(9, 17 - k);
                case 1: return cellC(1 + k, 9);
                case 2: return cellC(9, 1 + k);
                default: return cellC(17 - k, 9);
            }
        }
        float o = (i - 1.5f) * 0.52f;
        switch (s) {
            case 0: return pt(9.5f + o, 10.45f);
            case 1: return pt(8.55f, 9.5f + o);
            case 2: return pt(9.5f + o, 8.55f);
            default: return pt(10.45f, 9.5f + o);
        }
    }

    private static float[] posOf(int s, int i, float v) {
        if (v <= -1) return spot(s, -1, i);
        int b = (int) Math.floor(v);
        float f = v - b;
        float[] a = spot(s, b, i);
        if (f < 0.001f || b >= 71) return a;
        float[] n = spot(s, b + 1, i);
        return new float[]{a[0] + (n[0] - a[0]) * f, a[1] + (n[1] - a[1]) * f};
    }

    private static float radiusAt(float v) {
        if (v < 0) return 0.56f * CS;
        if (v >= 71) return 0.3f * CS;
        return 0.45f * CS;
    }

    private int colorOf(int s) {
        return active[s] ? seatChar[s].main : Art.GRAY;
    }

    private String label(int s) {
        return seatChar[s].label;
    }

    private String nameOf(int s) {
        return s == 0 ? label(s) + " (tú)" : label(s);
    }

    private int goals(int s) {
        int n = 0;
        for (int i = 0; i < 4; i++) if (eng.prog[s][i] == Engine.GOAL) n++;
        return n;
    }

    // ------------------------------------------------------------------ lógica / animación
    @Override
    void tick(float dt) {
        long now = System.currentTimeMillis();
        stepParticles(dt);

        boolean forward = false;
        for (int s = 0; s < 4; s++) {
            for (int i = 0; i < 4; i++) {
                float p = eng.prog[s][i], w = vis[s][i];
                if (w < p) {
                    if (w < 0) {
                        vis[s][i] = p; // salir de casa: salta a la casilla
                    } else {
                        vis[s][i] = Math.min(p, w + (0.2f + 0.02f * (p - w)) * dt);
                        forward = true;
                    }
                }
            }
        }
        if (!forward) {
            for (int s = 0; s < 4; s++) {
                for (int i = 0; i < 4; i++) {
                    float p = eng.prog[s][i];
                    if (vis[s][i] > p) { // ficha comida o castigada: vuelve a casa
                        float[] q = posOf(s, i, vis[s][i]);
                        burst(q[0], q[1], seatChar[s].main, 10);
                        vis[s][i] = p;
                    }
                    if (vis[s][i] >= 71 && !goalFx[s][i]) {
                        goalFx[s][i] = true;
                        float[] q = spot(s, 71, i);
                        burst(q[0], q[1], 0xFFF6C531, 16);
                    }
                }
            }
        }
        animating = forward;

        if (rolling) {
            if (now >= nextFace) {
                shownDie = 1 + rnd.nextInt(6);
                nextFace = now + 70;
            }
            if (now >= rollEnd) {
                rolling = false;
                finishRoll(now);
            }
            return;
        }
        if (animating || now < pauseUntil || confirmExit) return;
        if (eng.phase == Engine.OVER) {
            if (rnd.nextInt(3) == 0) confetti();
            return;
        }
        if (eng.phase == Engine.ROLL) {
            if (eng.turn != 0) startRoll();
        } else {
            List<Move> l = eng.legal;
            if (l.size() == 1) doMove(l.get(0), now);
            else if (eng.turn != 0) doMove(Bot.choose(eng, diff, rnd), now);
        }
    }

    private void startRoll() {
        if (rolling || animating || eng.phase != Engine.ROLL) return;
        long now = System.currentTimeMillis();
        rolling = true;
        rollEnd = now + 700;
        nextFace = now;
        pendingDie = 1 + rnd.nextInt(6);
        dieSeat = eng.turn;
    }

    private void finishRoll(long now) {
        shownDie = pendingDie;
        int who = dieSeat;
        eng.roll(pendingDie);
        String n = nameOf(who);
        if (eng.evPenalty) {
            msg = n + ": ¡tres 6 seguidos! Su última ficha vuelve a casa";
            pauseUntil = now + 1400;
        } else if (eng.evNoMoves) {
            msg = n + ": sacó " + pendingDie + " y no puede mover";
            pauseUntil = now + 1100;
        } else {
            msg = n + ": sacó un " + pendingDie;
            pauseUntil = now + 450;
        }
    }

    private void doMove(Move m, long now) {
        Engine.Result r = eng.move(m);
        StringBuilder sb = new StringBuilder(nameOf(m.seat)).append(": ");
        if (m.exit) sb.append("saca una ficha");
        else if (m.steps > 6) sb.append("usa su bono y avanza ").append(m.steps);
        else sb.append("avanza ").append(m.steps);
        if (r.capSeat >= 0) sb.append(" ¡y se come a ").append(label(r.capSeat)).append("! +20");
        if (r.goal) sb.append(" ¡llega a la meta! +10");
        msg = sb.toString();
        pauseUntil = now + 300;
    }

    private Move pick(float x, float y) {
        for (Move m : eng.legal) {
            float[] a = posOf(m.seat, m.piece, vis[m.seat][m.piece]);
            if (dist(a, x, y) < radiusAt(vis[m.seat][m.piece]) * 1.4f) return m;
        }
        for (Move m : eng.legal) {
            float[] d = spot(m.seat, m.to(), m.piece);
            if (dist(d, x, y) < CS * 0.6f) return m;
        }
        return null;
    }

    private static float dist(float[] a, float x, float y) {
        float dx = a[0] - x, dy = a[1] - y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    void click(float x, float y) {
        long now = System.currentTimeMillis();
        if (confirmExit || rolling || animating || now < pauseUntil) return;
        if (eng.phase == Engine.MOVE && eng.turn == 0) {
            Move m = pick(x, y);
            if (m != null) doMove(m, now);
        }
    }

    @Override
    boolean back() {
        if (eng.phase == Engine.OVER && !animating) {
            v.go(new LobbyScreen(v));
        } else {
            confirmExit = !confirmExit;
        }
        return true;
    }

    // ------------------------------------------------------------------ partículas
    private void burst(float x, float y, int color, int n) {
        for (int i = 0; i < n; i++) {
            Particle p = new Particle();
            double a = rnd.nextDouble() * Math.PI * 2;
            float sp = 1.5f + rnd.nextFloat() * 3f;
            p.x = x;
            p.y = y;
            p.vx = (float) Math.cos(a) * sp;
            p.vy = (float) Math.sin(a) * sp - 1.5f;
            p.max = p.life = 30 + rnd.nextInt(25);
            p.size = 5 + rnd.nextFloat() * 5;
            p.kind = rnd.nextInt(2);
            p.color = color;
            parts.add(p);
        }
    }

    private void confetti() {
        int[] cols = {0xFFFF8FB1, 0xFF7CC6F2, 0xFFF6C531, 0xFFA98BDB, 0xFF8ED8A0};
        Particle p = new Particle();
        p.x = rnd.nextFloat() * 540;
        p.y = -10;
        p.vx = (rnd.nextFloat() - 0.5f) * 2;
        p.vy = 1.5f + rnd.nextFloat() * 2;
        p.max = p.life = 260;
        p.size = 6 + rnd.nextFloat() * 6;
        p.kind = rnd.nextInt(2);
        p.color = cols[rnd.nextInt(cols.length)];
        parts.add(p);
    }

    private void stepParticles(float dt) {
        for (int i = parts.size() - 1; i >= 0; i--) {
            Particle p = parts.get(i);
            p.x += p.vx * dt;
            p.y += p.vy * dt;
            p.vy += 0.06f * dt;
            p.life -= dt;
            if (p.life <= 0) parts.remove(i);
        }
    }

    private void drawParticles(Canvas c) {
        for (Particle p : parts) {
            int col = Art.alpha(p.color, (int) (255 * Math.min(1f, p.life / (p.max * 0.5f))));
            if (p.kind == 0) Art.star(c, p.x, p.y, p.size, p.size * 0.45f, col);
            else Art.heart(c, p.x, p.y, p.size * 0.8f, col);
        }
    }

    // ------------------------------------------------------------------ dibujo
    @Override
    void draw(Canvas c, float t) {
        long now = System.currentTimeMillis();
        boolean over = eng.phase == Engine.OVER && !animating;
        boolean modal = confirmExit || over;
        drawHeader(c, modal);
        drawCards(c, t);
        drawBoard(c);
        boolean humanMove = !modal && !animating && !rolling && eng.phase == Engine.MOVE && eng.turn == 0
                && eng.legal.size() > 1;
        if (humanMove) drawDestHints(c, t);
        drawPieces(c, t, humanMove);
        drawParticles(c);
        drawBottom(c, t, now, modal);
        if (confirmExit) drawConfirm(c);
        else if (over) drawOver(c, t);
    }

    private void drawHeader(Canvas c, boolean modal) {
        if (!modal) {
            v.button(c, "exit", 14, 14, 104, 48, "Salir", Art.WHITE, Art.INK, false, () -> confirmExit = true);
        }
        Art.outlined(c, "Parchís Chiikawa", 345, 34, 30, 0xFFFF7FA8, Art.WHITE, 8);
        String[] dn = {"Fácil", "Normal", "Difícil"};
        Art.text(c, "Dificultad " + dn[diff] + " · " + botCount + (botCount == 1 ? " bot" : " bots"),
                345, 62, 15, true, Art.INK_SOFT, 380);
    }

    private void drawCards(Canvas c, float t) {
        List<Integer> as = new ArrayList<>();
        for (int s = 0; s < 4; s++) if (active[s]) as.add(s);
        int n = as.size();
        float gap = 8, w = (520 - gap * (n - 1)) / n, y = 76, h = 86;
        for (int k = 0; k < n; k++) {
            int s = as.get(k);
            float x = 10 + k * (w + gap);
            boolean turn = eng.turn == s && eng.phase != Engine.OVER;
            Art.rrect(c, x, y + 4, w, h, 22, 0x1F000000, 0, 0);
            Art.rrect(c, x, y, w, h, 22, turn ? seatChar[s].light : 0xCCFFFFFF, turn ? seatChar[s].main : 0xFFE8D8CB, turn ? 5 : 3);
            float bob = turn ? (float) Math.sin(t * 6) * 3 : 0;
            Art.token(c, seatChar[s], x + w / 2, y + 30 + bob, 22);
            Art.text(c, s == 0 ? "Tú" : label(s), x + w / 2, y + 62, 16, true, Art.INK, w - 10);
            Art.text(c, "Meta " + goals(s) + "/4", x + w / 2, y + 78, 13, false, Art.INK_SOFT, w - 10);
        }
    }

    private void drawBoard(Canvas c) {
        float size = 19 * CS;
        Art.rrect(c, BX - 8, BY - 3, size + 16, size + 16, 28, 0x22000000, 0, 0);
        Art.rrect(c, BX - 8, BY - 8, size + 16, size + 16, 28, 0xFFFFFDF8, 0xFFE8D5C4, 4);

        // casas
        int[][] nr = {{0, 11}, {0, 0}, {11, 0}, {11, 11}};
        for (int s = 0; s < 4; s++) {
            float[] p = pt(nr[s][0] + 0.15f, nr[s][1] + 0.15f);
            float w = 7.7f * CS;
            int base = active[s] ? seatChar[s].light : 0xFFF1ECE8;
            int bord = active[s] ? seatChar[s].main : 0xFFDCD3CD;
            Art.rrect(c, p[0], p[1], w, w, 26, base, bord, 4);
            float[] q = pt(nr[s][0] + 1, nr[s][1] + 1);
            Art.rrect(c, q[0], q[1], 6 * CS, 6 * CS, 20, 0xB0FFFFFF, 0, 0);
            for (int i = 0; i < 4; i++) {
                float[] sp = spot(s, -1, i);
                Art.circle(c, sp[0], sp[1], 0.62f * CS, Art.alpha(bord, 70), 0, 0);
            }
        }

        // casillas del recorrido
        for (int k = 0; k < 68; k++) {
            int[] t = TRACK[k];
            float x = BX + t[0] * CS + 1.2f, y = BY + t[1] * CS + 1.2f, w = CS - 2.4f;
            int seat = -1;
            for (int s = 0; s < 4; s++) if (Engine.START[s] == k) seat = s;
            int fill = Art.WHITE;
            if (Engine.SAFE[k]) fill = 0xFFFFF3C8;
            if (seat >= 0) fill = colorOf(seat);
            Art.rrect(c, x, y, w, w, 6, fill, 0xFFE3D3C5, 2);
            if (Engine.SAFE[k]) {
                float R = CS * 0.3f;
                Art.star(c, x + w / 2, y + w / 2, R, R * 0.45f, seat >= 0 ? Art.WHITE : 0xFFF2B93B);
            }
        }

        // pasillos finales
        for (int s = 0; s < 4; s++) {
            for (int k = 0; k < 7; k++) {
                float[] cc = spot(s, 64 + k, 0);
                int fill = active[s] ? Art.mix(seatChar[s].light, seatChar[s].main, 0.15f + 0.1f * k) : 0xFFF1ECE8;
                Art.rrect(c, cc[0] - CS / 2 + 1.2f, cc[1] - CS / 2 + 1.2f, CS - 2.4f, CS - 2.4f, 6, fill, 0xFFE3D3C5, 2);
            }
        }

        // centro
        float[][][] tri = {
                {{8, 11}, {11, 11}}, {{8, 8}, {8, 11}}, {{8, 8}, {11, 8}}, {{11, 8}, {11, 11}}};
        float[] mid = pt(9.5f, 9.5f);
        for (int s = 0; s < 4; s++) {
            float[] a = pt(tri[s][0][0], tri[s][0][1]);
            float[] b = pt(tri[s][1][0], tri[s][1][1]);
            Path p = Art.PATH;
            p.reset();
            p.moveTo(a[0], a[1]);
            p.lineTo(b[0], b[1]);
            p.lineTo(mid[0], mid[1]);
            p.close();
            Art.fillStyle(colorOf(s));
            c.drawPath(p, Art.P);
            Art.strokeStyle(Art.WHITE, 2);
            c.drawPath(p, Art.P);
        }
        Art.circle(c, mid[0], mid[1], CS * 0.42f, Art.WHITE, 0, 0);
        Art.heart(c, mid[0], mid[1] + 1, CS * 0.22f, 0xFFFF9DB5);
    }

    private void drawDestHints(Canvas c, float t) {
        float pulse = 1f + 0.1f * (float) Math.sin(t * 7);
        for (Move m : eng.legal) {
            float[] d = spot(m.seat, m.to(), m.piece);
            Art.circle(c, d[0], d[1], CS * 0.5f * pulse, Art.alpha(seatChar[m.seat].main, 110), Art.WHITE, 3);
        }
    }

    private void drawPieces(Canvas c, float t, boolean showHints) {
        class PP {
            int s, i;
            float x, y, r, v;
            boolean moving, movable;
        }
        List<PP> list = new ArrayList<>();
        for (int s = 0; s < 4; s++) {
            if (!active[s]) continue;
            for (int i = 0; i < 4; i++) {
                PP q = new PP();
                q.s = s;
                q.i = i;
                q.v = vis[s][i];
                float[] p = posOf(s, i, q.v);
                q.x = p[0];
                q.y = p[1];
                q.r = radiusAt(q.v);
                q.moving = q.v != eng.prog[s][i];
                if (showHints) {
                    for (Move m : eng.legal) {
                        if (m.seat == s && (m.exit ? eng.prog[s][i] == -1 : eng.prog[s][i] == m.from)) q.movable = true;
                    }
                }
                list.add(q);
            }
        }
        // dos fichas en la misma casilla: se colocan lado a lado
        for (PP a : list) {
            if (a.v < 0 || a.v >= 71 || a.v != Math.floor(a.v) || a.moving) continue;
            int others = 0, rank = 0;
            for (PP b : list) {
                if (b == a || b.v < 0 || b.v >= 71 || b.v != Math.floor(b.v) || b.moving) continue;
                if (Math.abs(a.x - b.x) < 1 && Math.abs(a.y - b.y) < 1) {
                    others++;
                    if (list.indexOf(b) < list.indexOf(a)) rank++;
                }
            }
            if (others == 1) {
                a.x += (rank == 0 ? -1 : 1) * 0.25f * CS;
                a.r *= 0.82f;
            }
        }
        list.sort((a, b) -> {
            if (a.moving != b.moving) return a.moving ? 1 : -1;
            return Float.compare(a.y, b.y);
        });
        for (PP q : list) {
            if (q.movable) {
                float pulse = 1.2f + 0.1f * (float) Math.sin(t * 7);
                Art.circle(c, q.x, q.y, q.r * pulse, Art.alpha(Art.WHITE, 200), Art.alpha(seatChar[q.s].dark, 255), 3);
            }
            float lift = q.moving ? -3 : 0;
            Art.token(c, seatChar[q.s], q.x, q.y + lift, q.r);
        }
    }

    private void drawDie(Canvas c, float cx, float cy, float size, int value, int rim, float rot, float sc) {
        c.save();
        c.translate(cx, cy);
        c.rotate(rot);
        c.scale(sc, sc);
        Art.rrect(c, -size / 2 + 2, -size / 2 + 8, size, size, size * 0.22f, 0x30000000, 0, 0);
        Art.rrect(c, -size / 2, -size / 2, size, size, size * 0.22f, Art.WHITE, rim, 7);
        float d = size * 0.24f;
        int[][] pips = {
                {}, {0, 0}, {-1, -1, 1, 1}, {-1, -1, 0, 0, 1, 1}, {-1, -1, 1, -1, -1, 1, 1, 1},
                {-1, -1, 1, -1, 0, 0, -1, 1, 1, 1}, {-1, -1, 1, -1, -1, 0, 1, 0, -1, 1, 1, 1}};
        int[] pp = pips[Math.max(1, Math.min(6, value))];
        for (int i = 0; i + 1 < pp.length; i += 2) {
            Art.circle(c, pp[i] * d, pp[i + 1] * d, size * 0.085f, Art.INK, 0, 0);
        }
        c.restore();
    }

    private void drawBottom(Canvas c, float t, long now, boolean modal) {
        String st;
        int turnSeat = eng.turn;
        if (eng.phase == Engine.OVER) st = "¡Fin de la partida!";
        else if (rolling) st = nameOf(dieSeat) + " tira el dado...";
        else if (eng.phase == Engine.ROLL) st = turnSeat == 0 ? "¡Tu turno! Toca el dado" : "Turno de " + label(turnSeat);
        else if (turnSeat == 0) st = eng.bonusMode ? "¡Bono! Avanza " + eng.bonusValue + " con una ficha"
                : "Elige una ficha para avanzar " + eng.die;
        else st = label(turnSeat) + " está pensando...";
        Art.text(c, st, 270, 722, 27, true, seatChar[Math.min(3, turnSeat)] != null ? seatChar[turnSeat].dark : Art.INK, 500);

        boolean canRoll = !modal && !rolling && !animating && eng.phase == Engine.ROLL && eng.turn == 0 && now >= pauseUntil;
        if (!modal) v.reg("dice", 210, 760, 120, 120, canRoll, this::startRoll);

        int rimSeat = (eng.phase == Engine.ROLL && !rolling) ? eng.turn : dieSeat;
        float rot = rolling ? (float) Math.sin(t * 25) * 18 : 0;
        float sc = 1f;
        float cy = 820;
        if (rolling) cy -= Math.abs((float) Math.sin(t * 14)) * 14;
        else if (canRoll) sc = 1f + 0.06f * (float) Math.sin(t * 6);
        if (v.pressed("dice")) sc *= 0.94f;
        drawDie(c, 270, cy, 104, shownDie, seatChar[rimSeat].main, rot, sc);

        if (canRoll) Art.text(c, "¡Toca el dado para tirar!", 270, 897, 19, true, Art.INK_SOFT, 400);
        if (eng.bonusMode && eng.phase == Engine.MOVE) {
            Art.rrect(c, 360, 790, 150, 60, 30, 0xFFFFD166, Art.WHITE, 4);
            Art.text(c, "BONO +" + eng.bonusValue, 435, 820, 24, true, Art.INK, 130);
        }
        if (!msg.isEmpty()) Art.text(c, msg, 270, 928, 17, true, Art.INK, 510);
    }

    private void drawConfirm(Canvas c) {
        Art.rrect(c, -20, -20, 580, 1000, 0, 0x99000000, 0, 0);
        Art.rrect(c, 50, 320, 440, 270, 36, Art.WHITE, Art.ACCENT, 6);
        Art.text(c, "¿Salir de la partida?", 270, 385, 31, true, Art.INK, 400);
        Art.text(c, "Se perderá el avance.", 270, 428, 20, false, Art.INK_SOFT, 400);
        v.button(c, "m_stay", 74, 470, 190, 80, "Seguir", 0xFF8ED8A0, Art.INK, false, () -> confirmExit = false);
        v.button(c, "m_leave", 276, 470, 190, 80, "Salir", 0xFFFF8A8A, Art.INK, false, () -> v.go(new LobbyScreen(v)));
    }

    private void drawOver(Canvas c, float t) {
        int w = eng.winner;
        Art.rrect(c, -20, -20, 580, 1000, 0, 0x88000000, 0, 0);
        Art.rrect(c, 40, 230, 460, 530, 40, Art.WHITE, seatChar[w].main, 8);
        Art.token(c, seatChar[w], 270, 360 + (float) Math.sin(t * 4) * 8, 78);
        String big = w == 0 ? "¡Ganaste!" : label(w) + " ganó";
        Art.outlined(c, big, 270, 495, 50, seatChar[w].main, Art.WHITE, 10);
        Art.text(c, w == 0 ? "¡Felicidades, " + player.label + "!" : "¡Suerte en la próxima!", 270, 550, 24, true, Art.INK_SOFT, 420);
        v.button(c, "m_again", 90, 585, 360, 76, "Jugar otra vez", Art.ACCENT, Art.WHITE, true,
                () -> v.go(new GameScreen(v, diff, player, botCount)));
        v.button(c, "m_menu", 90, 676, 360, 70, "Menú principal", Art.WHITE, Art.INK, false,
                () -> v.go(new LobbyScreen(v)));
    }
}
