package com.castis.sampleproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
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

import com.pallycon.widevinelibrary.PallyconDrmException;
import com.pallycon.widevinelibrary.PallyconWVMSDK;
import com.pallycon.widevinelibrary.PallyconWVMSDKFactory;
import com.pallycon.widevinelibrary.UnAuthorizedDeviceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

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
    private PallyconWVMSDK wvmAgent;
    private Handler eventHandler = new Handler();
    private ArrayList<String> adtagList = new ArrayList<String>();

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
        //entertainment
        adtagList.add("https://pubads.g.doubleclick.net/gampad/ads?iu=/21806146298/dliveplus_entertainment1&description_url=http%3A%2F%2Fwww.dlive.kr%2Ffront%2Fdliveplus%2FFreeAction.do%3Fmethod%3Dlist%26sFirstClCode%3D01&env=vp&impl=s&correlator=&tfcd=0&npa=0&gdfp_req=1&output=vast&sz=640x480&unviewed_position_start=1&devt=stb");
        //kids ani
        adtagList.add("https://pubads.g.doubleclick.net/gampad/ads?iu=/21806146298/dliveplus_kidsani1&description_url=http%3A%2F%2Fwww.dlive.kr%2Ffront%2Fdliveplus%2FFreeAction.do%3Fmethod%3Dlist%26sFirstClCode%3D03&env=vp&impl=s&correlator=&tfcd=0&npa=0&gdfp_req=1&output=vast&sz=640x480&unviewed_position_start=1&devt=stb");
        //life style
        adtagList.add("https://pubads.g.doubleclick.net/gampad/ads?iu=/21806146298/dliveplus_lifestyle1&description_url=http%3A%2F%2Fwww.dlive.kr%2Ffront%2Fdliveplus%2FFreeAction.do%3Fmethod%3Dlist%26sFirstClCode%3D06&env=vp&impl=s&correlator=&tfcd=0&npa=0&gdfp_req=1&output=vast&sz=640x480&unviewed_position_start=1&devt=stb");
        //edu
        adtagList.add("https://pubads.g.doubleclick.net/gampad/ads?iu=/21806146298/dliveplus_edu1&description_url=http%3A%2F%2Fwww.dlive.kr%2Ffront%2Fdliveplus%2FFreeAction.do%3Fmethod%3Dlist%26sFirstClCode%3D04&env=vp&impl=s&correlator=&tfcd=0&npa=0&gdfp_req=1&output=vast&sz=640x480&unviewed_position_start=1&devt=stb");
        // variaty
        adtagList.add("https://pubads.g.doubleclick.net/gampad/ads?iu=/21806146298/dliveplus_variaty1&description_url=http%3A%2F%2Fwww.dlive.kr%2Ffront%2Fdliveplus%2FFreeAction.do%3Fmethod%3Dlist%26sFirstClCode%3D02&env=vp&impl=s&correlator=&tfcd=0&npa=0&gdfp_req=1&output=vast&sz=640x480&unviewed_position_start=1&devt=stb");
        // sport
        adtagList.add("https://pubads.g.doubleclick.net/gampad/ads?iu=/21806146298/dliveplus_sports1&description_url=http%3A%2F%2Fwww.dlive.kr%2Ffront%2Fdliveplus%2FFreeAction.do%3Fmethod%3Dlist%26sFirstClCode%3D05&env=vp&impl=s&correlator=&tfcd=0&npa=0&gdfp_req=1&output=vast&sz=640x480&unviewed_position_start=1&devt=stb");

        try {
            wvmAgent = PallyconWVMSDKFactory.getInstance(this);
            wvmAgent.init(this, eventHandler, "LHV2", "UhKOG96Gl0BTxm7ocmZH0oyAhk2mA5n1");
        } catch (PallyconDrmException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        } catch (UnAuthorizedDeviceException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
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
                // for google ad (0~5)
                Random rand = new Random();
                int randAdTagNumer = rand.nextInt(6);
                String currentADTag = adtagList.get(randAdTagNumer);
                String errorADtag="https://pubads.g.doubleclick.net/gampad/ads?iu=/21806146298/dliveplus_entertainment1&description_url=http://www.dlive.kr/front/dliveplus/FreeAction.do?method=list&sFirstClCode=01&env=vp&impl=s&correlator=&tfcd=0&npa=0&gdfp_req=1&output=vast&sz=640x480&unviewed_position_start=1&devt=stb";
                intent.putExtra(PlayerActivity.AD_TAG_URL, errorADtag);
                Log.i(TAG, "currentADTag:" + currentADTag);
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
                serverAddressEditText.setText("str-ott.dlive.kr");
                serverPortEditText.setText("443");
                playListEditText.setText("video/10000/10000.mpd");
            }
        });
        drmContentHttpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_radio_http.setChecked(true);
                serverAddressEditText.setText("210.109.108.111");
                serverPortEditText.setText("18080");
                playListEditText.setText("DLIVE/10358/10358.mpd");
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