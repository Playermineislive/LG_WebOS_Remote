package com.tv.lg_webos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static LGTV m_tv;
    private static boolean m_firstLaunch = true;
    public static boolean m_debugMode;

    public enum KEY_INDEX {
        ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, HEIGHT, NINE,
        TV, YOUTUBE, NETFLIX, AMAZON, HDMI1, HDMI2, HDMI3,
        COMPONENT, AV1, GUIDE, SMART_SHARE, ON, OFF, MUTE,
        VOLUME_INCREASE, VOLUME_DECREASE, CHANNEL_INCREASE,
        CHANNEL_DECREASE, PLAY, PAUSE, STOP, REWIND, FORWARD,
        NEXT, PREVIOUS, PROGRAM, SOURCE, INTERNET, BACK, UP,
        DOWN, LEFT, RIGHT, ENTER, EXIT, DASH, HOME, RED, GREEN,
        YELLOW, BLUE, THREE_D;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (BuildConfig.DEBUG)
            m_debugMode = true;

        m_tv = new LGTV(this);
        m_tv.loadMainPreferences();

        EditText ip_view = findViewById(R.id.editText);
        ip_view.setText(m_tv.getMyIP());
        ip_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                m_tv.setMyIP(s.toString());
                m_tv.saveIPPreference();
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        // BUTTON MAPPING (SYNCED WITH XML)
        findViewById(R.id.btnUp).setOnClickListener(v -> sendKey(KEY_INDEX.UP));
        findViewById(R.id.btnDown).setOnClickListener(v -> sendKey(KEY_INDEX.DOWN));
        findViewById(R.id.btnLeft).setOnClickListener(v -> sendKey(KEY_INDEX.LEFT));
        findViewById(R.id.btnRight).setOnClickListener(v -> sendKey(KEY_INDEX.RIGHT));
        findViewById(R.id.btnOk).setOnClickListener(v -> sendKey(KEY_INDEX.ENTER));
        findViewById(R.id.btnBack).setOnClickListener(v -> sendKey(KEY_INDEX.BACK));
        findViewById(R.id.btnHome).setOnClickListener(v -> sendKey(KEY_INDEX.HOME));
    }

    private void sendKey(KEY_INDEX key) {
        if (m_firstLaunch) {
            Toast.makeText(this, "Press DETECT first", Toast.LENGTH_SHORT).show();
            return;
        }
        m_tv.send_key(key.name(), key);
    }

    public void onDetectButtonClick(View view) {
        m_tv.TV_Pairing();
        m_firstLaunch = false;
    }

    public static boolean isWifiAvailable(Context context) {
        if (BuildConfig.DEBUG)
            return true;

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

        if (cm != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Network network = cm.getActiveNetwork();
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
                return capabilities != null &&
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
            } else {
                NetworkInfo ni = cm.getActiveNetworkInfo();
                return ni != null && ni.isConnected() &&
                        ni.getType() == ConnectivityManager.TYPE_WIFI;
            }
        }
        return false;
    }
    }
