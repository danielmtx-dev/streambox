package nemosofts.streambox.interfaces;

public interface LiveListener {
    void onStart();
    void onEnd(String success);
    void onCancel(String message);
}