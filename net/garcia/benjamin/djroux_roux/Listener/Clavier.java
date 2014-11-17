package net.garcia.benjamin.djroux_roux.Listener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.StringTokenizer;

public final class Clavier {

    private static int instance = 0;
    private static BufferedReader bufIn = null;
    private static StringTokenizer st = null;

    public static void initialise() {
        if (bufIn == null) {
            bufIn = new BufferedReader(new InputStreamReader(System.in));
        }
    }

    public Clavier() {
        if (instance++ == 0) {
            initialise();
        } else {
            System.err.println("Clavier : 1 seule instance au plus !");
            System.exit(1);
        }
    }

    public static void read() {
        if (bufIn == null) {
            initialise();
        }
        try {
            String s = bufIn.readLine();
            st = new StringTokenizer(s);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }

    }

    public static void flush() {
        st = null;
    }

    public static int lireInt() {
        if (st == null) {
            read();
        }
        while (!st.hasMoreTokens()) {
            read();
        }
        String ss = st.nextToken();
        int i = Integer.parseInt(ss);
        return (i);
    }

    public static long lireLong() {
        if (st == null) {
            read();
        }
        while (!st.hasMoreTokens()) {
            read();
        }
        String ss = st.nextToken();
        long i = Long.parseLong(ss);
        return (i);
    }

    public static float lireFloat() {
        if (st == null) {
            read();
        }
        while (!st.hasMoreTokens()) {
            read();
        }
        String ss = st.nextToken();
        float f = Float.parseFloat(ss);
        return (f);
    }

    public static double lireDouble() {
        if (st == null) {
            read();
        }
        while (!st.hasMoreTokens()) {
            read();
        }
        String ss = st.nextToken();
        double f = Double.parseDouble(ss);
        return (f);
    }

    public static String lireString() {
        if (st == null) {
            read();
        }
        while (!st.hasMoreTokens()) {
            read();
        }
        return (st.nextToken());
    }
}
