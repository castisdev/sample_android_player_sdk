package com.castis.sampleproject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.castis.castisplayer.CastisException;
import com.castis.castisplayer.CastisExoPlayerView;
import com.castis.castisplayer.CastisPlayer;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
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
    private LinearLayout debugRootView;
    private CastisExoPlayerView castisExoPlayerView;
    private CastisPlayer player;
    private Uri videoUri;
    private PallyconWVMSDK wvmAgent;
    private DrmSessionManager<FrameworkMediaCrypto> drmSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        debugRootView = findViewById(R.id.controls_root);

        Log.d(TAG, "onCreate");

        castisExoPlayerView = (CastisExoPlayerView)findViewById(R.id.player_view);
        castisExoPlayerView.requestFocus();

        Intent intent = getIntent();
        videoUri = intent.getData();
        player = new CastisPlayer();

        try {
            wvmAgent = PallyconWVMSDKFactory.getInstance(this);
            wvmAgent.setPallyconEventListener(pallyconEventListener);

            wvmAgent.init(this, new Handler(), player.getSiteId(), player.getSiteKey());
            String encToken=player.getEncryptionToken(videoUri.toString());
            drmSessionManager = wvmAgent.createDrmSessionManagerByToken(C.WIDEVINE_UUID, player.getDrmLicenseUrl(), videoUri, encToken);
            player.initPlayer(this, castisExoPlayerView, drmSessionManager);
        } catch (PallyconDrmException e) {
            e.printStackTrace();
        } catch (UnAuthorizedDeviceException e) {
            e.printStackTrace();
        } catch (CastisException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT <= 23 || player == null) {
            try {
                player.initPlayer(this, castisExoPlayerView, drmSessionManager);
                player.play(videoUri.toString());
            } catch (CastisException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            castisExoPlayerView.setUseController(true);
            try {
                if (player != null ) {
                    player.play(videoUri.toString());
                }
            } catch (CastisException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        if (Build.VERSION.SDK_INT <= 23) {
            try {
                releasePlayer();
            } catch (CastisException e) {
                e.printStackTrace();
            }
        }

        super.onPause();
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

    // TODO : must implement PallyconEventListener
    private com.pallycon.widevinelibrary.PallyconEventListener pallyconEventListener = new com.pallycon.widevinelibrary.PallyconEventListener() {
        @Override
        public void onDrmKeysLoaded(Map<String, String> licenseInfo) {
            // TODO: Use the loaded license information.
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
        }

        @Override
        public void onDrmSessionManagerError(Exception e) {
            // TODO: Handle exceptions in error situations. Please refer to the API guide document for details of exception.
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
            player.initPlayer(this, castisExoPlayerView, drmSessionManager);
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

    private void updateButtonVisibility() {
    }


    @Override
    public void onClick(View view) {
    }

    private void showToast(int messageId) {
        showToast(getString(messageId));
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
