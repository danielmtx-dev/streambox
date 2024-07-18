package nemosofts.streambox.asyncTask;

import android.os.AsyncTask;

import org.json.JSONObject;

import nemosofts.streambox.interfaces.LoginListener;
import nemosofts.streambox.Util.ApplicationUtil;
import okhttp3.RequestBody;

public class LoadLogin extends AsyncTask<String, String, String> {

    private final RequestBody requestBody;
    private final LoginListener listener;
    // user_info
    int auth = 0;
    String username="", password="", message="", status="", exp_date="0", is_trial="", active_cons="", created_at="", max_connections="";
    // server_info
    boolean xui = false;
    int revision = 0, timestamp_now = 0;
    String version = "1.0.0", url = "", port = "", https_port = "",server_protocol = "", rtmp_port = "", time_now = "", timezone = "";
    private String api_url = "";

    public LoadLogin(LoginListener listener, String api_url , RequestBody requestBody) {
        this.listener = listener;
        this.api_url = api_url;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        listener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = ApplicationUtil.responsePost(api_url+"/player_api.php", requestBody);
            JSONObject mainJson = new JSONObject(json);

            JSONObject user_info = mainJson.getJSONObject("user_info");
            username = user_info.getString("username");
            password = user_info.getString("password");
            message = user_info.getString("message");
            auth = user_info.getInt("auth");
            status = user_info.getString("status");
            if (user_info.getString("exp_date").isEmpty()) {
                exp_date = user_info.getString("exp_date");
            }
            is_trial = user_info.getString("is_trial");
            active_cons = user_info.getString("active_cons");
            created_at = user_info.getString("created_at");
            max_connections = user_info.getString("max_connections");

            JSONObject server_info = mainJson.getJSONObject("server_info");
            if (server_info.has("xui")){
                xui = server_info.getBoolean("xui");
            }
            if (server_info.has("version")){
                version = server_info.getString("version");
            }
            if (server_info.has("revision")){
                revision = server_info.getInt("revision");
            }
            url = server_info.getString("url");
            port = server_info.getString("port");
            https_port = server_info.getString("https_port");
            server_protocol = server_info.getString("server_protocol");
            rtmp_port = server_info.getString("rtmp_port");
            timestamp_now = server_info.getInt("timestamp_now");
            time_now = server_info.getString("time_now");
            timezone = server_info.getString("timezone");

            return "1";
        } catch (Exception ee) {
            ee.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        listener.onEnd(s,
            username,password,message,auth,status,exp_date,is_trial,active_cons,created_at,max_connections,
            xui,version,revision,url,port,https_port,server_protocol,rtmp_port,timestamp_now,time_now,timezone
        );
        super.onPostExecute(s);
    }

}