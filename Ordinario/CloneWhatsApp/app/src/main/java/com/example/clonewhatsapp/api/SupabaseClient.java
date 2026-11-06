package com.example.clonewhatsapp.api;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.json.JSONArray;
import org.json.JSONObject;

public class SupabaseClient {
    public static final String SUPABASE_URL = "https://oeolmcbcrrlxvtrjslaf.supabase.co";
    public static final String SUPABASE_KEY = "sb_publishable_v4-xxudTAZQlR75C8Y-Ehw_HEQgk-uq";

    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static void signUp(String email, String password, Callback callback) {
        String url = SUPABASE_URL + "/auth/v1/signup";
        try {
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void signIn(String email, String password, Callback callback) {
        String url = SUPABASE_URL + "/auth/v1/token?grant_type=password";
        try {
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void resetPassword(String email, Callback callback) {
        String url = SUPABASE_URL + "/auth/v1/recover";
        try {
            JSONObject json = new JSONObject();
            json.put("email", email);

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void signOut(String accessToken, Callback callback) {
        String url = SUPABASE_URL + "/auth/v1/logout";
        RequestBody body = RequestBody.create("", JSON);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void getProfile(String accessToken, String userId, Callback callback) {
        String url = SUPABASE_URL + "/rest/v1/profiles?id=eq." + userId + "&select=*";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void updateProfile(String accessToken, String userId, String fullName, String avatarUrl, Callback callback) {
        String url = SUPABASE_URL + "/rest/v1/profiles?id=eq." + userId;
        try {
            JSONObject json = new JSONObject();
            json.put("full_name", fullName);
            json.put("avatar_url", avatarUrl);

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=representation")
                    .patch(body)
                    .build();

            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getUsers(String accessToken, String currentUserId, Callback callback) {
        String url = SUPABASE_URL + "/rest/v1/profiles?id=neq." + currentUserId + "&select=*";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void searchUsers(String accessToken, String currentUserId, String query, Callback callback) {
        String url = SUPABASE_URL + "/rest/v1/profiles?id=neq." + currentUserId +
                "&or=(username.ilike.*" + query + "*,full_name.ilike.*" + query + "*)" + "&select=*";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    // --- FASES 5, 6, 7 Y 8: CONVERSACIONES, MENSAJES, REALTIME Y ESTADOS ---

    public static void getOrCreateConversation(String accessToken, String currentUserId, String otherUserId, Callback callback) {
        String url = SUPABASE_URL + "/rest/v1/conversations_members?select=conversation_id&user_id=eq." + currentUserId;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void createConversation(String accessToken, String currentUserId, String otherUserId, Callback callback) {
        String url = SUPABASE_URL + "/rest/v1/conversaciones";
        try {
            JSONObject json = new JSONObject();
            json.put("is_group", false);

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=representation")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addConversationMember(String accessToken, String conversationId, String userId, Callback callback) {
        String url = SUPABASE_URL + "/rest/v1/conversations_members";
        try {
            JSONObject json = new JSONObject();
            json.put("conversation_id", conversationId);
            json.put("user_id", userId);

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getUserConversations(String accessToken, String currentUserId, Callback callback) {
        String url = SUPABASE_URL + "/rest/v1/conversations_members?user_id=eq." + currentUserId + "&select=conversation_id,conversaciones(*)";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void getMessages(String accessToken, String conversationId, Callback callback) {
        String url = SUPABASE_URL + "/rest/v1/messages?conversation_id=eq." + conversationId + "&order=created_at.asc&select=*";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void sendMessage(String accessToken, String conversationId, String senderId, String content, Callback callback) {
        String url = SUPABASE_URL + "/rest/v1/messages";
        try {
            JSONObject json = new JSONObject();
            json.put("conversation_id", conversationId);
            json.put("sender_id", senderId);
            json.put("content", content);
            json.put("status", "sent");

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=representation")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void markMessagesAsRead(String accessToken, String conversationId, String currentUserId, Callback callback) {
        String url = SUPABASE_URL + "/rest/v1/messages?conversation_id=eq." + conversationId + "&sender_id=neq." + currentUserId + "&status=neq.read";
        try {
            JSONObject json = new JSONObject();
            json.put("status", "read");

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Content-Type", "application/json")
                    .patch(body)
                    .build();

            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTypingStatus(String accessToken, String userId, String conversationId, Callback callback) {
        String url = SUPABASE_URL + "/rest/v1/profiles?id=eq." + userId;
        try {
            JSONObject json = new JSONObject();
            json.put("is_typing_in", conversationId != null ? conversationId : JSONObject.NULL);
            json.put("last_seen", "now()");

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Content-Type", "application/json")
                    .patch(body)
                    .build();

            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getOtherUserStatus(String accessToken, String otherUserId, Callback callback) {
        String url = SUPABASE_URL + "/rest/v1/profiles?id=eq." + otherUserId + "&select=last_seen,is_typing_in";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static WebSocket connectRealtime(WebSocketListener listener) {
        String wsUrl = SUPABASE_URL.replace("https://", "wss://") + "/realtime/v1/websocket?apikey=" + SUPABASE_KEY + "&vsn=1.0.0";
        Request request = new Request.Builder().url(wsUrl).build();
        return client.newWebSocket(request, listener);
    }
}