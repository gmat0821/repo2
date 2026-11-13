package com.example.clonewhatsapp.api;

import android.net.Uri;
import org.json.JSONObject;
import okhttp3.*;

public class SupabaseClient {
    // Reemplaza estos valores con los de tu proyecto en Supabase (Settings -> API)
    private static final String SUPABASE_URL = "https://oeolmcbcrrlxvtrjslaf.supabase.co";
    private static final String SUPABASE_KEY = "sb_publishable_v4-xxudTAZQlR75C8Y-Ehw_HEQgk-uq";

    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    // --- MÉTODO BASE PARA PETICIONES HTTP ---
    private static void req(String method, String path, String token, String body, Callback cb) {
        Request.Builder b = new Request.Builder()
                .url(SUPABASE_URL + path)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + token);

        if ("GET".equals(method)) {
            b.get();
        } else if ("DELETE".equals(method)) {
            b.delete();
        } else {
            RequestBody rb = RequestBody.create(body != null ? body : "{}", JSON);
            b.addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=representation");

            if ("POST".equals(method)) {
                b.post(rb);
            } else if ("PATCH".equals(method)) {
                b.patch(rb);
            }
        }
        client.newCall(b.build()).enqueue(cb);
    }

    // --- HELPER PARA CONSTRUIR JSON ---
    private static String j(Object... kv) {
        JSONObject o = new JSONObject();
        try {
            for (int i = 0; i < kv.length; i += 2) {
                o.put(String.valueOf(kv[i]), kv[i + 1]);
            }
        } catch (Exception ignored) {}
        return o.toString();
    }

    // =========================================================================
    // 1. AUTENTICACIÓN (AUTH)
    // =========================================================================
    public static void signIn(String email, String password, Callback cb) {
        req("POST", "/auth/v1/token?grant_type=password", SUPABASE_KEY, j("email", email, "password", password), cb);
    }

    public static void signUp(String email, String password, Callback cb) {
        req("POST", "/auth/v1/signup", SUPABASE_KEY, j("email", email, "password", password), cb);
    }

    public static void resetPassword(String email, Callback cb) {
        req("POST", "/auth/v1/recover", SUPABASE_KEY, j("email", email), cb);
    }

    // =========================================================================
    // 2. PERFILES DE USUARIO Y PRESENCIA
    // =========================================================================
    public static void getProfile(String token, String userId, Callback cb) {
        req("GET", "/rest/v1/profiles?id=eq." + userId + "&select=*", token, null, cb);
    }

    public static void updateProfile(String token, String userId, String name, String avatarUrl, Callback cb) {
        String body = avatarUrl != null
                ? j("full_name", name, "avatar_url", avatarUrl)
                : j("full_name", name);
        req("PATCH", "/rest/v1/profiles?id=eq." + userId, token, body, cb);
    }

    public static void getUsers(String token, String myId, Callback cb) {
        req("GET", "/rest/v1/profiles?id=neq." + myId + "&select=*", token, null, cb);
    }

    public static void searchUsers(String token, String myId, String query, Callback cb) {
        String eq = Uri.encode(query);
        req("GET", "/rest/v1/profiles?id=neq." + myId + "&or=(full_name.ilike.*" + eq + "*,email.ilike.*" + eq + "*)&select=*", token, null, cb);
    }

    public static void getOtherUserStatus(String token, String otherUserId, Callback cb) {
        req("GET", "/rest/v1/profiles?id=eq." + otherUserId + "&select=*", token, null, cb);
    }

    public static void updateTypingStatus(String token, String myId, String