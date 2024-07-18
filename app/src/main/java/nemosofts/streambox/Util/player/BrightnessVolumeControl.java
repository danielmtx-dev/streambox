package nemosofts.streambox.Util.player;

import android.app.Activity;
import android.media.AudioManager;
import android.view.WindowManager;

import androidx.media3.common.util.UnstableApi;

@UnstableApi
public class BrightnessVolumeControl {

    private final Activity activity;
    public int currentBrightnessLevel = -1;
    public int currentVolumeLevel = 1;

    public BrightnessVolumeControl(Activity activity) {
        this.activity = activity;
    }

    public float getScreenBrightness() {
        return activity.getWindow().getAttributes().screenBrightness;
    }

    public void setScreenBrightness(final float brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = brightness;
        activity.getWindow().setAttributes(lp);
    }

    public float levelToBrightness(final float level) {
        final double d = 0.064 + 0.936 / (double) 30 * (double) level;
        return (float) (d * d);
    }

    public void changeBrightness(final CustomPlayerView playerView, final boolean increase, final boolean canSetAuto) {
        int newBrightnessLevel = (increase ? currentBrightnessLevel + 1 : currentBrightnessLevel - 1);

        if (canSetAuto && newBrightnessLevel < 0)
            currentBrightnessLevel = -1;
        else if (newBrightnessLevel >= 0 && newBrightnessLevel <= 30)
            currentBrightnessLevel = newBrightnessLevel;

        if (currentBrightnessLevel == -1 && canSetAuto)
            setScreenBrightness(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE);
        else if (currentBrightnessLevel != -1)
            setScreenBrightness(levelToBrightness(currentBrightnessLevel));

        playerView.setHighlight(false);
        if (currentBrightnessLevel == -1 && canSetAuto) {
            playerView.setIconBrightnessAuto();
            playerView.setCustomErrorMessage("");
        } else {
            playerView.setIconBrightness();
            playerView.setCustomErrorMessage(" " + currentBrightnessLevel);
        }
    }

    public void adjustVolume(AudioManager audioManager, final CustomPlayerView playerView, final boolean increase) {
        if (audioManager != null){
            int volume_max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            currentVolumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

            int newVolumeLevel = (increase ? currentVolumeLevel + 1 : currentVolumeLevel - 1);

            if (newVolumeLevel < 0){
                currentVolumeLevel = -1;
            } else if (newVolumeLevel <= volume_max){
                currentVolumeLevel = newVolumeLevel;
            }

            if (currentVolumeLevel == -1){
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            } else {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolumeLevel, 0);
            }

            playerView.setHighlight(false);
            if (currentVolumeLevel == -1) {
                playerView.setIconVolumeError();
                playerView.setCustomErrorMessage("");
            } else {
                if (currentVolumeLevel < 0){
                    playerView.setIconVolumeError();
                }  else if (currentVolumeLevel < 10){
                    playerView.setIconVolumeDown();
                } else if (newVolumeLevel <= volume_max){
                    playerView.setIconVolumeUp();
                } else {
                    playerView.setIconVolumeUp();
                }
                playerView.setCustomErrorMessage(" " + currentVolumeLevel);
            }
        }
    }
}