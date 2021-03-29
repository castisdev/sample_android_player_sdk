package com.castis.sampleproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.NetworkOnMainThreadException;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "CASTIS_playerSDK_Main";
    private EditText serverAddressEditText;
    private EditText serverPortEditText;
    private EditText playListEditText;
    private RadioButton btn_radio_http;
    private RadioButton btn_radio_https;
    RadioGroup radioGroup;
    private Button playButton;
    private Button cleanContent1Button;
    private Button cleanContent2Button;
    private Button drmContentHttpButton;
    private Button drmWithAdButton;
    private Button clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverAddressEditText = (EditText) findViewById(R.id.serverIpEditText);
        serverPortEditText = (EditText) findViewById(R.id.serverPortEditText);
        playListEditText = (EditText) findViewById(R.id.playListEditText);
        playButton = (Button) findViewById(R.id.button_play);
        btn_radio_http = (RadioButton) findViewById(R.id.btn_radio_http);
        btn_radio_https = (RadioButton) findViewById(R.id.btn_radio_https);
        btn_radio_https.setChecked(true);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

            }
        });
        cleanContent1Button = (Button) findViewById(R.id.button_clean1);
        cleanContent2Button = (Button) findViewById(R.id.button_clean2);
        drmContentHttpButton = (Button) findViewById(R.id.button_drmtest);
        drmWithAdButton = (Button) findViewById(R.id.drmWithAdButton);
        clearButton = (Button) findViewById(R.id.button_clear);

        String savedAddress = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("beforeServerAddress", "");
        String savedPort = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("beforeServerPort", "");
        String savedFileName = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("beforeFileName", "");
        String savedContentId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("beforeContentId", "");
        if (savedAddress.isEmpty() == false) {
            serverAddressEditText.setText(savedAddress);
        }
        if (savedPort.isEmpty() == false) {
            serverPortEditText.setText(savedPort);
        }
        if (savedFileName.isEmpty() == false) {
            playListEditText.setText(savedFileName);
        }
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);

                String contentIdStr = "";

                String playListFileNameStr = playListEditText.getText().toString();
                int dotIndex = playListFileNameStr.lastIndexOf(".");
                if (playListFileNameStr.lastIndexOf("/") > 0) {
                    contentIdStr = playListFileNameStr.substring(playListFileNameStr.lastIndexOf("/") + 1, dotIndex);
                } else {
                    contentIdStr = playListFileNameStr.substring(0, dotIndex);
                }
                Log.i(TAG, "contentIdStr:" + contentIdStr);

                String serverAddressStr = serverAddressEditText.getText().toString();
                String serverPortStr = serverPortEditText.getText().toString();

                if (serverAddressStr.length() == 0 || playListFileNameStr.length() == 0) {
                    Toast.makeText(MainActivity.this, "input server IP and FileName", Toast.LENGTH_LONG).show();
                    return;
                }
                String playUriStr = "";
                String httpString = "";
                if (btn_radio_http.isChecked() == true) {
                    httpString = "http://";
                } else {
                    httpString = "https://";
                }
                if (serverPortStr.length() == 0) {
                    playUriStr = httpString + serverAddressStr + "/" + playListFileNameStr;
                } else {
                    playUriStr = httpString + serverAddressStr + ":" + serverPortStr + "/" + playListFileNameStr;
                }
                Uri playUri = Uri.parse(playUriStr);
                intent.setData(playUri);
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("beforeServerAddress", serverAddressStr).commit();
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("beforeServerPort", serverPortStr).commit();
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("beforeFileName", playListFileNameStr).commit();
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("beforeContentId", contentIdStr).commit();
                startActivity(intent);
            }
        });

        drmWithAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);

                String contentIdStr = "";

                String playListFileNameStr = playListEditText.getText().toString();
                int dotIndex = playListFileNameStr.lastIndexOf(".");
                if (playListFileNameStr.lastIndexOf("/") > 0) {
                    contentIdStr = playListFileNameStr.substring(playListFileNameStr.lastIndexOf("/") + 1, dotIndex);
                } else {
                    contentIdStr = playListFileNameStr.substring(0, dotIndex);
                }
                Log.i(TAG, "contentIdStr:" + contentIdStr);

                String serverAddressStr = serverAddressEditText.getText().toString();
                String serverPortStr = serverPortEditText.getText().toString();

                if (serverAddressStr.length() == 0 || playListFileNameStr.length() == 0) {
                    Toast.makeText(MainActivity.this, "input server IP and FileName", Toast.LENGTH_LONG).show();
                    return;
                }
                String playUriStr = "";
                String httpString = "";
                if (btn_radio_http.isChecked() == true) {
                    httpString = "http://";
                } else {
                    httpString = "https://";
                }
                if (serverPortStr.length() == 0) {
                    playUriStr = httpString + serverAddressStr + "/" + playListFileNameStr;
                } else {
                    playUriStr = httpString + serverAddressStr + ":" + serverPortStr + "/" + playListFileNameStr;
                }
                Uri playUri = Uri.parse(playUriStr);
                intent.setData(playUri);
                // for google ad
                String example_google_ad_tag_lifestyle = "https://pubads.g.doubleclick.net/gampad/ads?iu=/21806146298/dliveplus_lifestyle1&description_url=http%3A%2F%2Fwww.dlive.kr%2Ffront%2Fdliveplus%2FFreeAction.do%3Fmethod%3Dlist%26sFirstClCode%3D06&env=vp&impl=s&correlator=&tfcd=0&npa=0&gdfp_req=1&output=vast&sz=640x480&unviewed_position_start=1&devt=stb";
                intent.putExtra(PlayerActivity.AD_TAG_URL, example_google_ad_tag_lifestyle);
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("beforeServerAddress", serverAddressStr).commit();
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("beforeServerPort", serverPortStr).commit();
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("beforeFileName", playListFileNameStr).commit();
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("beforeContentId", contentIdStr).commit();
                startActivity(intent);
            }
        });

        cleanContent1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_radio_https.setChecked(true);
                serverAddressEditText.setText("//wowzaec2demo.streamlock.net");
                serverPortEditText.setText("443");
                playListEditText.setText("vod/_definst_/ElephantsDream/smil:ElephantsDream.smil/manifest_mvlist.mpd");
            }
        });

        cleanContent2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_radio_https.setChecked(true);
                serverAddressEditText.setText("dash.akamaized.net");
                serverPortEditText.setText("443");
                playListEditText.setText("akamai/bbb_30fps/bbb_30fps.mpd");
            }
        });
        drmContentHttpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_radio_http.setChecked(true);
                serverAddressEditText.setText("210.109.108.111");
                serverPortEditText.setText("18080");
                playListEditText.setText("dash/1234567890ABCDEF.mpd");
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverAddressEditText.setText("");
                serverPortEditText.setText("");
                playListEditText.setText("");
            }
        });
    }

    private void showSimpleDialog(String title, String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showSimpleDialog2(title, message);
            }
        });
    }

    private void showSimpleDialog2(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        Dialog dialog = builder.create();
        dialog.show();
    }
}