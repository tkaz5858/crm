package com.bands.crm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CallReceiver extends BroadcastReceiver {
    private static final String TAG = CallReceiver.class.getSimpleName();
    private MediaRecorder recorder;
    private boolean isRecording = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                // Arama başladı
                startRecording(context);
            } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                // Arama bitti
                stopRecording();
            }
        }
    }

    private void startRecording(Context context) {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(getOutputFile(context));
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setAudioChannels(1); // Mono olarak kaydetmek için
        recorder.setAudioSamplingRate(44100); // Ses örnekleme hızını ayarla
        recorder.setAudioEncodingBitRate(192000); // Bit hızını ayarla
        try {
            recorder.prepare();
            Thread.sleep(2000); // birkaç saniye bekleyin
            recorder.start();
            isRecording = true;
        } catch (IOException | IllegalStateException | InterruptedException e) {
            Log.e(TAG, "startRecording exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (isRecording) {
            recorder.stop();
            recorder.release();
            isRecording = false;
        }
    }

    private String getOutputFile(Context context) {
        File myDir = new File(context.getExternalFilesDir(null) + "/CallRecordings");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        return myDir.getAbsolutePath() + "/" + currentDateAndTime + ".mp3"; // MP3 olarak kaydet
    }
}
