package com.example.parchisgame;

import java.util.List;
import java.util.Random;

/** Inteligencia de los bots. diff: 0 = fácil, 1 = normal, 2 = difícil. */
final class Bot {
    private Bot() {
    }

    static Move choose(Engine e, int diff, Random rnd) {
        List<Move> l = e.legal;
        if (l.size() == 1 || diff == 0) return l.get(rnd.nextInt(l.size()));
        Move best = null;
        double bestScore = -1e9;
        for (Move m : l) {
            double sc = score(e, m, diff, rnd);
            if (sc > bestScore) {
                bestScore = sc;
                best = m;
            }
        }
        return best;
    }

    private static double score(Engine e, Move m, int diff, Random rnd) {
        boolean hard = diff >= 2;
        int s = m.seat;
        int from = m.from;
        int to = m.to();
        double sc = 0;

        if (m.exit) sc += 45;

        if (to <= 63) {
            int a = Engine.abs(s, to);
            boolean opp = e.oppAt(s, a) > 0;
            if (opp && (!Engine.SAFE[a] || m.exit)) sc += hard ? 125 : 100; // comer
            if (Engine.SAFE[a]) sc += 20;
            if (e.countAt(a, s) == 1) sc += hard ? 20 : 12; // barrera propia
            sc -= e.threats(s, a) * (hard ? 38 : 18);
            if (hard && e.oppAt(s, a) == 0 && !m.exit) {
                // los bots difíciles valoran ponerse justo delante de un rival para estorbarle
                for (int t = 0; t < 4; t++) {
                    if (t == s || !e.active[t]) continue;
                    for (int j = 0; j < 4; j++) {
                        int p = e.prog[t][j];
                        if (p < 0 || p > 63) continue;
                        int d = (a - Engine.abs(t, p) + 68) % 68;
                        if (d >= 1 && d <= 12 && e.countAt(a, s) == 1) sc += 3;
                    }
                }
            }
        } else if (to < Engine.GOAL) {
            sc += 35; // pasillo final: a salvo
        } else {
            sc += 90; // meta
        }

        if (!m.exit && from >= 0 && from <= 63) {
            int a0 = Engine.abs(s, from);
            sc += e.threats(s, a0) * (hard ? 30 : 10); // huir del peligro
            if (e.countAt(a0, s) >= 2) sc -= hard ? 12 : 4; // romper barrera
        }

        sc += m.steps * 1.2;
        if (hard && from > 0) sc += from * 0.05;
        sc += rnd.nextDouble() * (hard ? 3 : 14);
        return sc;
    }
}
