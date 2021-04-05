package com.castis.sampleproject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.castis.castisplayer.CastisException;
import com.castis.castisplayer.CastisExoPlayerView;
import com.castis.castisplayer.CastisPlayer;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.pallycon.widevinelibrary.DatabaseDecryptException;
import com.pallycon.widevinelibrary.DetectedDeviceTimeModifiedException;
import com.pallycon.widevinelibrary.NetworkConnectedException;
import com.pallycon.widevinelibrary.PallyconDrmException;
import com.pallycon.widevinelibrary.PallyconServerResponseException;
import com.pallycon.widevinelibrary.PallyconWVMSDK;
import com.pallycon.widevinelibrary.PallyconWVMSDKFactory;
import com.pallycon.widevinelibrary.UnAuthorizedDeviceException;

import org.json.JSONException;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by kskim.Castis
 */

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "CASTIS_playerSDK_Player";
    public static final String AD_TAG_URL = "google_ad_tag_url";
    private LinearLayout debugRootView;
    private CastisExoPlayerView castisExoPlayerView;
    private CastisPlayer player;
    private Uri videoUri;
    private PallyconWVMSDK wvmAgent;
    private DrmSessionManager<FrameworkMediaCrypto> drmSessionManager;
    final Handler timeCheckHandler = new Handler();
    private String adTagUrl;

    //timeline
    private TextView textView_current;
    private TextView textView_buffer;
    private TextView textView_duration;
    private TextView textView_current_ms;
    private TextView textView_buffer_ms;
    private TextView textView_duration_ms;

    //sample buttons
    private Button playButton;
    private Button pauseButton;
    private Button forwardButton;
    private Button backwardButton;
    private Button button_sec;
    private Button button_33per;
    private Button button_66per;
    private Button button_hide;

    // refer to the ExoPlayer guide
    private Player.EventListener playerEventListener = new Player.DefaultEventListener() {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            Log.d(TAG, "onTimelineChanged()!!");
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            Log.d(TAG, "onTracksChanged()!!");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            Log.d(TAG, "onLoadingChanged(" + isLoading + ")");
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case ExoPlayer.STATE_IDLE:
                    Log.i(TAG, "ExoPlayer.STATE_IDLE(" + playbackState + ")");
                    break;
                case ExoPlayer.STATE_BUFFERING:
                    Log.i(TAG, "ExoPlayer.STATE_BUFFERING(" + playbackState + ")");
                    break;
                case ExoPlayer.STATE_READY:
                    if (playWhenReady) {
                        Log.i(TAG, "ExoPlayer.STATE_READY(" + playbackState + ")");
                    } else {
                        Log.i(TAG, "ExoPlayer.STATE_PAUSED(" + playbackState + ")");
                        pauseButton.requestFocus();
                    }
                    break;
                case ExoPlayer.STATE_ENDED:
                    Log.i(TAG, "ExoPlayer.STATE_ENDED(" + playbackState + ")");
                    player.seekToSec(0);
                    break;
                default:
                    Log.i(TAG, "ExoPlayer.default(" + playbackState + ")");
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException e) {
            String errorString;
            if (e.type == ExoPlaybackException.TYPE_RENDERER) {
                Exception cause = e.getRendererException();
                errorString = cause.toString();

            } else if (e.type == ExoPlaybackException.TYPE_SOURCE) {
                Exception cause = e.getSourceException();
                errorString = cause.toString();

            } else if (e.type == ExoPlaybackException.TYPE_UNEXPECTED) {
                Exception cause = e.getUnexpectedException();
                errorString = cause.toString();
            } else {
                errorString = e.toString();
            }
            Log.e(TAG, "onPlayerError:" + errorString);
        }

        @Override
        public void onSeekProcessed() {
            Log.i(TAG, "ExoPlayer onSeekProcessed()!!");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Log.d(TAG, "onCreate");

        castisExoPlayerView = (CastisExoPlayerView) findViewById(R.id.player_view);
        castisExoPlayerView.setFocusable(false);
        Intent intent = getIntent();
        videoUri = intent.getData();
        adTagUrl = intent.getStringExtra(AD_TAG_URL);
        player = new CastisPlayer();
        player.setEventListener(playerEventListener);

        try {
            wvmAgent = PallyconWVMSDKFactory.getInstance(this);
            wvmAgent.setPallyconEventListener(pallyconEventListener);
            wvmAgent.init(this, new Handler(), player.getSiteId(), player.getSiteKey());
            String encToken = player.getEncryptionToken(videoUri.toString());
            drmSessionManager = wvmAgent.createDrmSessionManagerByToken(C.WIDEVINE_UUID, player.getDrmLicenseUrl(), videoUri, encToken);
            castisExoPlayerView.setFocusable(false);
            player.initPlayer(this, castisExoPlayerView, drmSessionManager);
        } catch (PallyconDrmException e) {
            e.printStackTrace();
        } catch (UnAuthorizedDeviceException e) {
            e.printStackTrace();
        } catch (CastisException e) {
            e.printStackTrace();
        }

        // sample buttons
        playButton = (Button) findViewById(R.id.button_play);
        pauseButton = (Button) findViewById(R.id.button_pause);
        forwardButton = (Button) findViewById(R.id.button_forward);
        backwardButton = (Button) findViewById(R.id.button_back);
        button_sec = (Button) findViewById(R.id.button_sec);
        button_33per = (Button) findViewById(R.id.button_33per);
        button_66per = (Button) findViewById(R.id.button_66per);
        button_hide = (Button) findViewById(R.id.button_hide);

        textView_current = (TextView) findViewById(R.id.textView_current);
        textView_buffer = (TextView) findViewById(R.id.textView_buffer);
        textView_duration = (TextView) findViewById(R.id.textView_duration);
        textView_current_ms = (TextView) findViewById(R.id.textView_currentms);
        textView_buffer_ms = (TextView) findViewById(R.id.textView_buffer_ms);
        textView_duration_ms = (TextView) findViewById(R.id.textView_durationms);


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.resume();
            }
        });
        playButton.setFocusableInTouchMode(true);

        Log.d("TEST","현재 포커스=>"+getCurrentFocus());

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.pause();
            }
        });

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.forwardSec(10);
            }
        });

        backwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.backwardSec(10);
            }
        });

        button_sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.seekToSec(5);
            }
        });

        button_33per.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.seekToPercent(33);
            }
        });
        button_66per.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.seekToPercent(66);
            }
        });

        // use below source code when want remove controller in player.
        button_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.hideController();
            }
        });

        // update timeline per sec
        timeCheckHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (player != null) {
                    textView_duration.setText(String.valueOf(player.getDurationSec()));
                    textView_buffer.setText(String.valueOf(player.getBufferedPositionSec()));
                    textView_current.setText(String.valueOf(player.getCurrentPositionSec()));
                    textView_current_ms.setText(String.valueOf(player.getCurrentPosition()));
                    textView_buffer_ms.setText(String.valueOf(player.getBufferedPosition()));
                    textView_duration_ms.setText(String.valueOf(player.getDuration()));
                    timeCheckHandler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (Build.VERSION.SDK_INT <= 23 || player == null) {
                player = new CastisPlayer();
                player.setEventListener(playerEventListener);
                player.initPlayer(this, castisExoPlayerView, drmSessionManager);
            }
            if ( adTagUrl == null || adTagUrl.length() == 0 ) {
                player.play(videoUri.toString());
            } else {
                player.play(videoUri.toString(), adTagUrl);
            }
            playButton.requestFocus();
        } catch (CastisException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT <= 23) {
            try {
                releasePlayer();
            } catch (CastisException e) {
                e.printStackTrace();
            }
        } else {
            player.pause();
        }
    }

    @Override
    protected void onStop() {
        if (Build.VERSION.SDK_INT > 23) {
            try {
                releasePlayer();
            } catch (CastisException e) {
                e.printStackTrace();
            }
        }
        super.onStop();
    }

    private com.pallycon.widevinelibrary.PallyconEventListener pallyconEventListener = new com.pallycon.widevinelibrary.PallyconEventListener() {
        @Override
        public void onDrmKeysLoaded(Map<String, String> licenseInfo) {
            // loaded license information.
            StringBuilder stringBuilder = new StringBuilder();
            Iterator<String> keys = licenseInfo.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = licenseInfo.get(key);
                try {
                    if (Long.parseLong(value) == 0x7fffffffffffffffL) {
                        value = "Unlimited";
                    }
                } catch (Exception e) {
                    // e.printStackTrace();
                }
                stringBuilder.append(key).append(" : ").append(value);
                if (keys.hasNext()) {
                    stringBuilder.append("\n");
                }
            }
            //print License Info in log
            Log.i(TAG, "DRM License Info:" + stringBuilder.toString());
        }

        @Override
        public void onDrmSessionManagerError(Exception e) {
            if (e instanceof NetworkConnectedException) {
                Log.e(TAG, "DRM NetworkConnectedException:" + e.getMessage());
            } else if (e instanceof PallyconServerResponseException) {
                Log.e(TAG, "DRM PallyconServerResponseException:" + e.getMessage());
            } else if (e instanceof DatabaseDecryptException) {
                Log.e(TAG, "DRM DatabaseDecryptException:" + e.getMessage());
            } else if (e instanceof DetectedDeviceTimeModifiedException) {
                Log.e(TAG, "DRM DetectedDeviceTimeModifiedException:" + e.getMessage());
            } else {
                Log.e(TAG, "DRM Exception:" + e.getMessage());
            }
        }

        @Override
        public void onDrmKeysRestored() {
            Log.i(TAG, "DRM key Restored !!!!!");
        }

        @Override
        public void onDrmKeysRemoved() {
            Log.i(TAG, "DRM key Removed !!!!!");
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // do nothing
        } else {
            Toast.makeText(getApplicationContext(), R.string.storage_permission_denied, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void releasePlayer() throws CastisException {
        if (player != null) {
            player.stop();
            player = null;
        }
    }

    @Override
    public void onClick(View view) {
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
