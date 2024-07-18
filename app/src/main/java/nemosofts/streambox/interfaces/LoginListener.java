package nemosofts.streambox.interfaces;

public interface LoginListener {
    void onStart();
    void onEnd(String success,
            // user_info
            String username, String password, String message, int auth,
            String status, String exp_date, String is_trial, String active_cons, String created_at, String max_connections,
            // server_info
            boolean xui, String version, int revision, String url, String port, String https_port,
            String server_protocol, String rtmp_port, int timestamp_now, String time_now, String timezone
    );
}