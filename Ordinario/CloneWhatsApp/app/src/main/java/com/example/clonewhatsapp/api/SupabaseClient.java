package com.example.clonewhatsapp.api;

import android.net.Uri;
import org.json.JSONObject;
import okhttp3.*;

public class SupabaseClient {
    private static final String SUPABASE_URL = "https://TU_PROYECTO.supabase.co";
    private static final String SUPABASE_KEY = "TU_SUPABASE_ANON_KEY";
    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static void req(String method, String path, String token, String body, Callback cb) {
        Request.Builder b = new Request.Builder()
                .url(SUPABASE_URL + path)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + token);
        if ("GET".equals(method)) b.get();
        else {
            RequestBody rb = RequestBody.create(body, JSON);
            b.addHeader("Content-Type", "application/json").addHeader("Prefer", "return=representation");
            if ("POST".equals(method)) b.post(rb); else b.patch(rb);
        }
        client.newCall(b.build()).enqueue(cb);
    }

    private static String j(Object... kv) {
        JSONObject o = new JSONObject();
        try { for (int i = 0; i < kv.length; i += 2) o.put(String.valueOf(kv[i]), kv[i + 1]); } catch (Exception ignored) {}
        return o.toString();
    }

    public static void signIn(String e, String p, Callback cb) { req("POST", "/auth/v1/token?grant_type=password", SUPABASE_KEY, j("email", e, "password", p), cb); }
    public static void signUp(String e, String p, Callback cb) { req("POST", "/auth/v1/signup", SUPABASE_KEY, j("email", e, "password", p), cb); }
    public static void resetPassword(String e, Callback cb) { req("POST", "/auth/v1/recover", SUPABASE_KEY, j("email", e), cb); }

    public static void getProfile(String t, String id, Callback cb) { req("GET", "/rest/v1/profiles?id=eq." + id + "&select=*", t, null, cb); }
    public static void updateProfile(String t, String id, String name, String av, Callback cb) {
        req("PATCH", "/rest/v1/profiles?id=eq." + id, t, av != null ? j("full_name", name, "avatar_url", av) : j("full_name", name), cb);
    }
    public static void getUsers(String t, String myId, Callback cb) { req("GET", "/rest/v1/profiles?id=neq." + myId + "&select=*", t, null, cb); }
    public static void searchUsers(String t, String myId, String q, Callback cb) {
        String eq = Uri.encode(q);
        req("GET", "/rest/v1/profiles?id=neq." + myId + "&or=(full_name.ilike.*" + eq + "*,email.ilike.*" + eq + "*)&select=*", t, null, cb);
    }
    public static void getOtherUserStatus(String t, String id, Callback cb) { req("GET", "/rest/v1/profiles?id=eq." + id + "&select=*", t, null, cb); }
    public static void updateTypingStatus(String t, String id, String to, Callback cb) {
        req("PATCH", "/rest/v1/profiles?id=eq." + id, t, j("typing_to", to == null ? JSONObject.NULL : to), cb);
    }

    public static void getUserConversationsMembers(String t, String uid, Callback cb) { req("GET", "/rest/v1/conversation_members?user_id=eq." + uid + "&select=*", t, null, cb); }
    public static void getMembersOfConversations(String t, String ids, Callback cb) {
        String filter = ids.startsWith("(") ? "in." + ids : "in.(" + ids + ")";
        req("GET", "/rest/v1/conversation_members?conversation_id=" + filter + "&select=*", t, null, cb);
    }
    public static void createConversation(String t, Callback cb) { req("POST", "/rest/v1/conversations", t, "{}", cb); }
    public static void addConversationMember(String t, String cid, String uid, Callback cb) { req("POST", "/rest/v1/conversation_members", t, j("conversation_id", cid, "user_id", uid), cb); }
    public static void getMessages(String t, String cid, Callback cb) { req("GET", "/rest/v1/messages?conversation_id=eq." + cid + "&order=created_at.asc", t, null, cb); }
    public static void sendMessage(String t, String cid, String sid, String content, String type, Callback cb) {
        req("POST", "/rest/v1/messages", t, j("conversation_id", cid, "sender_id", sid, "content", content, "type", type), cb);
    }

    public static void uploadFile(String bucket, String name, byte[] bytes, String mime, Callback cb) {
        Request r = new Request.Builder().url(SUPABASE_URL + "/storage/v1/object/" + bucket + "/" + name)
                .post(RequestBody.create(bytes, MediaType.parse(mime)))
                .addHeader("apikey", SUPABASE_KEY).addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .addHeader("Content-Type", mime).addHeader("x-upsert", "true").build();
        client.newCall(r).enqueue(cb);
    }
    public static String getPublicUrl(String bucket, String name) { return SUPABASE_URL + "/storage/v1/object/public/" + bucket + "/" + name; }
    public static WebSocket connectRealtime(WebSocketListener l) {
        return client.newWebSocket(new Request.Builder().url(SUPABASE_URL.replace("https://", "wss://") + "/realtime/v1/websocket?apikey=" + SUPABASE_KEY + "&vsn=1.0.0").build(), l);
    }
    public static void updateFcmToken(String t, String uid, String fcm, Callback cb) { req("PATCH", "/rest/v1/profiles?id=eq." + uid, t, j("fcm_token", fcm), cb); }
}