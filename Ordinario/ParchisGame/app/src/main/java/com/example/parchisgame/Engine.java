package com.example.parchisgame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Un movimiento posible de una ficha. */
final class Move {
    final int seat, piece, from, steps;
    final boolean exit;

    Move(int seat, int piece, int from, int steps, boolean exit) {
        this.seat = seat;
        this.piece = piece;
        this.from = from;
        this.steps = steps;
        this.exit = exit;
    }

    /** Progreso de destino (0 = casilla de salida). */
    int to() {
        return exit ? 0 : from + steps;
    }
}

/**
 * Reglas del parchís (sin nada de interfaz).
 *
 * Cada ficha tiene un "progreso": -1 = en casa, 0..63 = recorrido por el tablero
 * (0 es la casilla de salida del jugador), 64..70 = pasillo final, 71 = meta.
 * El tablero tiene 68 casillas; cada asiento (0..3) sale de START[asiento].
 */
final class Engine {
    static final int[] START = {4, 21, 38, 55};
    static final boolean[] SAFE = new boolean[68];

    static {
        int[] safe = {4, 11, 16, 21, 28, 33, 38, 45, 50, 55, 62, 67};
        for (int v : safe) SAFE[v] = true;
    }

    static final int ROLL = 0, MOVE = 1, OVER = 2, GOAL = 71;

    static final class Result {
        Move move;
        int capSeat = -1, capPiece = -1;
        boolean goal;
    }

    final int[][] prog = new int[4][4];
    final boolean[] active = new boolean[4];
    int turn, phase = ROLL, die, sixes, winner = -1;
    boolean bonusMode;
    int bonusValue;
    int lastPiece = -1;
    List<Move> legal = new ArrayList<>();
    // Eventos de la última tirada (para que la interfaz los cuente)
    boolean evNoMoves, evPenalty;

    Engine(boolean[] act, int first) {
        for (int s = 0; s < 4; s++) {
            active[s] = act[s];
            for (int i = 0; i < 4; i++) prog[s][i] = -1;
        }
        turn = first;
    }

    static int abs(int seat, int p) {
        return (START[seat] + p) % 68;
    }

    /** Fichas (de cualquier color) en una casilla del recorrido. */
    int countAt(int a) {
        int n = 0;
        for (int s = 0; s < 4; s++) if (active[s]) n += countAt(a, s);
        return n;
    }

    int countAt(int a, int seat) {
        int n = 0;
        for (int i = 0; i < 4; i++) {
            int p = prog[seat][i];
            if (p >= 0 && p <= 63 && abs(seat, p) == a) n++;
        }
        return n;
    }

    /** Fichas rivales de {@code seat} en la casilla. */
    int oppAt(int seat, int a) {
        int n = 0;
        for (int s = 0; s < 4; s++) if (active[s] && s != seat) n += countAt(a, s);
        return n;
    }

    boolean barrierAt(int a) {
        for (int s = 0; s < 4; s++) if (active[s] && countAt(a, s) >= 2) return true;
        return false;
    }

    /** Cuántas fichas rivales podrían comerse una ficha de {@code seat} en la casilla a. */
    int threats(int seat, int a) {
        int c = 0;
        for (int t = 0; t < 4; t++) {
            if (t == seat || !active[t]) continue;
            boolean nest = false;
            for (int j = 0; j < 4; j++) {
                int p = prog[t][j];
                if (p == -1) {
                    nest = true;
                } else if (!SAFE[a] && p <= 63) {
                    int b = abs(t, p);
                    int d = (a - b + 68) % 68;
                    if (d >= 1 && d <= 6 && p + d <= 63) c++;
                }
            }
            if (nest && a == START[t]) c++;
        }
        return c;
    }

    List<Move> legalMoves(int seat, int steps, boolean allowExit) {
        List<Move> res = new ArrayList<>();
        Set<Integer> seen = new HashSet<>();
        for (int i = 0; i < 4; i++) {
            int p = prog[seat][i];
            if (p == GOAL || seen.contains(p)) continue;
            if (p == -1) {
                if (!allowExit || steps != 5) continue;
                int a = START[seat];
                if (barrierAt(a) || countAt(a) >= 2) continue;
                res.add(new Move(seat, i, -1, steps, true));
                seen.add(p);
            } else {
                int np = p + steps;
                if (np > GOAL) continue;
                boolean ok = true;
                for (int q = p + 1; q <= Math.min(np, 63); q++) {
                    if (barrierAt(abs(seat, q))) {
                        ok = false;
                        break;
                    }
                }
                if (!ok) continue;
                if (np <= 63 && countAt(abs(seat, np)) >= 2) continue;
                res.add(new Move(seat, i, p, steps, false));
                seen.add(p);
            }
        }
        return res;
    }

    private void nextTurn() {
        do {
            turn = (turn + 1) % 4;
        } while (!active[turn]);
        sixes = 0;
        lastPiece = -1;
    }

    private void endAction() {
        bonusMode = false;
        legal = new ArrayList<>();
        if (die == 6) {
            phase = ROLL; // el 6 repite turno
        } else {
            nextTurn();
            phase = ROLL;
        }
    }

    /** Aplica una tirada de dado al jugador en turno. */
    void roll(int v) {
        die = v;
        evNoMoves = false;
        evPenalty = false;
        if (v == 6) sixes++;
        if (sixes >= 3) {
            evPenalty = true;
            if (lastPiece >= 0) {
                int p = prog[turn][lastPiece];
                if (p >= 0 && p < GOAL) prog[turn][lastPiece] = -1;
            }
            nextTurn();
            phase = ROLL;
            legal = new ArrayList<>();
            return;
        }
        List<Move> l = legalMoves(turn, v, true);
        if (v == 5) { // con un 5 es obligatorio sacar ficha
            List<Move> exits = new ArrayList<>();
            for (Move m : l) if (m.exit) exits.add(m);
            if (!exits.isEmpty()) l = exits;
        }
        if (l.isEmpty()) {
            evNoMoves = true;
            endAction();
            return;
        }
        legal = l;
        phase = MOVE;
        bonusMode = false;
    }

    /** Ejecuta un movimiento (de dado o de bonus). */
    Result move(Move m) {
        Result r = new Result();
        r.move = m;
        int s = m.seat, i = m.piece;
        int np = m.exit ? 0 : prog[s][i] + m.steps;
        prog[s][i] = np;
        lastPiece = i;
        int bonus = 0;
        if (np >= 0 && np <= 63) {
            int a = abs(s, np);
            boolean canCapture = !SAFE[a] || m.exit;
            if (canCapture) {
                outer:
                for (int t = 0; t < 4; t++) {
                    if (t == s || !active[t]) continue;
                    for (int j = 0; j < 4; j++) {
                        int q = prog[t][j];
                        if (q >= 0 && q <= 63 && abs(t, q) == a) {
                            prog[t][j] = -1;
                            r.capSeat = t;
                            r.capPiece = j;
                            bonus = 20;
                            break outer;
                        }
                    }
                }
            }
        }
        if (np == GOAL) {
            bonus = 10;
            r.goal = true;
            boolean all = true;
            for (int k = 0; k < 4; k++) if (prog[s][k] != GOAL) all = false;
            if (all) {
                winner = s;
                phase = OVER;
                legal = new ArrayList<>();
                return r;
            }
        }
        if (bonus > 0) {
            List<Move> l = legalMoves(s, bonus, false);
            if (!l.isEmpty()) {
                legal = l;
                phase = MOVE;
                bonusMode = true;
                bonusValue = bonus;
                return r;
            }
        }
        endAction();
        return r;
    }
}
