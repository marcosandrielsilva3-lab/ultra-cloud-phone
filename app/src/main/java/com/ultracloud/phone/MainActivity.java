package com.ultracloud.phone;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.net.http.SslError;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String PREFS = "ultra_cloud_settings";
    private static final String PREF_URL = "last_server_url";
    private static final int BG = Color.rgb(10, 16, 23);
    private static final int CARD = Color.rgb(20, 29, 39);
    private static final int GREEN = Color.rgb(115, 230, 160);
    private static final int WHITE = Color.rgb(240, 246, 250);
    private static final int MUTED = Color.rgb(160, 174, 188);

    private FrameLayout root;
    private LinearLayout homeScreen;
    private LinearLayout viewerScreen;
    private EditText addressInput;
    private TextView statusText;
    private TextView viewerStatus;
    private ProgressBar progress;
    private WebView webView;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        getWindow().getDecorView().setSystemUiVisibility(0);
        buildInterface();
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable rounded(int color, int radiusDp, int strokeColor) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radiusDp));
        if (strokeColor != 0) d.setStroke(dp(1), strokeColor);
        return d;
    }

    private TextView text(String value, float size, int color, boolean bold) {
        TextView v = new TextView(this);
        v.setText(value);
        v.setTextSize(size);
        v.setTextColor(color);
        if (bold) v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return v;
    }

    private LinearLayout column() {
        LinearLayout v = new LinearLayout(this);
        v.setOrientation(LinearLayout.VERTICAL);
        return v;
    }

    private void buildInterface() {
        root = new FrameLayout(this);
        root.setBackgroundColor(BG);
        setContentView(root);

        homeScreen = column();
        homeScreen.setPadding(dp(22), dp(30), dp(22), dp(24));
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.addView(homeScreen);
        root.addView(scroll, new FrameLayout.LayoutParams(-1, -1));

        TextView brand = text("ULTRA CLOUD", 13, GREEN, true);
        brand.setLetterSpacing(0.16f);
        homeScreen.addView(brand, margin(-1, -2, 0, 18));

        TextView title = text("Seu Android na nuvem", 27, WHITE, true);
        homeScreen.addView(title, margin(-1, -2, 0, 8));

        TextView subtitle = text("Conecte ao servidor privado e controle a tela virtual sem processar o jogo no seu telefone.", 14, MUTED, false);
        subtitle.setLineSpacing(dp(3), 1f);
        homeScreen.addView(subtitle, margin(-1, -2, 0, 24));

        LinearLayout card = column();
        card.setPadding(dp(16), dp(18), dp(16), dp(16));
        card.setBackground(rounded(CARD, 18, Color.rgb(39, 54, 68)));
        homeScreen.addView(card, margin(-1, -2, 0, 18));

        TextView label = text("ENDEREÇO DO SERVIDOR", 11, GREEN, true);
        label.setLetterSpacing(0.08f);
        card.addView(label, margin(-1, -2, 0, 10));

        addressInput = new EditText(this);
        addressInput.setSingleLine(true);
        addressInput.setTextSize(14);
        addressInput.setTextColor(WHITE);
        addressInput.setHintTextColor(MUTED);
        addressInput.setHint("http://100.x.x.x:5800/vnc.html");
        addressInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_URI);
        addressInput.setImeOptions(EditorInfo.IME_ACTION_GO);
        addressInput.setPadding(dp(12), dp(13), dp(12), dp(13));
        addressInput.setBackground(rounded(Color.rgb(10, 16, 23), 10, Color.rgb(54, 72, 89)));
        String saved = getPreferences(MODE_PRIVATE).getString(PREF_URL, "");
        addressInput.setText(saved);
        card.addView(addressInput, margin(-1, -2, 0, 12));

        Button connect = button("CONECTAR À NUVEM", true);
        card.addView(connect, margin(-1, dp(50), 0, 0));
        connect.setOnClickListener(v -> connectToServer());
        addressInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                connectToServer();
                return true;
            }
            return false;
        });

        statusText = text("●  Desconectado", 13, MUTED, false);
        homeScreen.addView(statusText, margin(2, -2, 0, 20));

        TextView noteTitle = text("COMO USAR", 12, WHITE, true);
        homeScreen.addView(noteTitle, margin(0, -2, 0, 8));
        TextView note = text("1. Ligue o servidor Android virtual.\n2. Conecte o celular à mesma rede privada Tailscale.\n3. Cole o endereço noVNC mostrado pelo instalador.\n4. Toque em Conectar e informe a senha VNC.", 13, MUTED, false);
        note.setLineSpacing(dp(5), 1f);
        homeScreen.addView(note, margin(0, -2, 0, 18));

        TextView warning = text("Este aplicativo é o controle remoto. Ele precisa de um servidor Android na nuvem já instalado e ligado. O APK sozinho não fornece jogos ou processamento remoto.", 12, Color.rgb(238, 190, 112), false);
        warning.setPadding(dp(12), dp(12), dp(12), dp(12));
        warning.setBackground(rounded(Color.rgb(43, 35, 24), 12, Color.rgb(86, 65, 38)));
        homeScreen.addView(warning, margin(0, -2, 0, 0));

        buildViewerScreen();
    }

    private void buildViewerScreen() {
        viewerScreen = column();
        viewerScreen.setVisibility(View.GONE);

        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setGravity(Gravity.CENTER_VERTICAL);
        toolbar.setPadding(dp(12), dp(8), dp(12), dp(8));
        toolbar.setBackgroundColor(CARD);

        Button back = smallButton("VOLTAR");
        toolbar.addView(back, new LinearLayout.LayoutParams(dp(86), dp(42)));
        back.setOnClickListener(v -> showHome("Sessão fechada no aplicativo."));

        viewerStatus = text("Conectando...", 13, WHITE, true);
        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(0, -2, 1f);
        statusParams.leftMargin = dp(12);
        toolbar.addView(viewerStatus, statusParams);
        viewerScreen.addView(toolbar, new LinearLayout.LayoutParams(-1, dp(58)));

        progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progress.setMax(100);
        progress.setProgressTintList(android.content.res.ColorStateList.valueOf(GREEN));
        viewerScreen.addView(progress, new LinearLayout.LayoutParams(-1, dp(3)));

        webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        webView.setBackgroundColor(Color.BLACK);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public void onProgressChanged(WebView view, int newProgress) {
                progress.setProgress(newProgress);
                progress.setVisibility(newProgress >= 100 ? View.GONE : View.VISIBLE);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                viewerStatus.setText("Página aberta — conecte ao VNC");
            }
            @Override public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame()) viewerStatus.setText("Falha ao abrir. Confira servidor e Tailscale.");
            }
            @Override public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.cancel();
                Toast.makeText(MainActivity.this, "Conexão HTTPS inválida. Certificado recusado.", Toast.LENGTH_LONG).show();
            }
        });
        viewerScreen.addView(webView, new LinearLayout.LayoutParams(-1, 0, 1f));
        root.addView(viewerScreen, new FrameLayout.LayoutParams(-1, -1));
    }

    private LinearLayout.LayoutParams margin(int width, int height, int bottom, int ignored) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(width, height);
        p.bottomMargin = dp(bottom);
        return p;
    }

    private Button button(String label, boolean primary) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(13);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setAllCaps(false);
        b.setTextColor(primary ? BG : WHITE);
        b.setBackground(rounded(primary ? GREEN : CARD, 12, 0));
        return b;
    }

    private Button smallButton(String label) {
        Button b = button(label, false);
        b.setTextSize(11);
        b.setPadding(dp(4), 0, dp(4), 0);
        return b;
    }

    private void connectToServer() {
        String raw = addressInput.getText().toString().trim();
        if (raw.isEmpty()) {
            addressInput.setError("Cole o endereço do servidor.");
            return;
        }
        String url = normaliseUrl(raw);
        if (url == null) {
            addressInput.setError("Use um endereço HTTP ou HTTPS válido.");
            return;
        }
        getPreferences(MODE_PRIVATE).edit().putString(PREF_URL, raw).apply();
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(addressInput.getWindowToken(), 0);
        } catch (Exception ignored) { }
        statusText.setText("●  Conectando...");
        viewerStatus.setText("Abrindo servidor...");
        homeScreen.setVisibility(View.GONE);
        viewerScreen.setVisibility(View.VISIBLE);
        webView.loadUrl(url);
    }

    private String normaliseUrl(String raw) {
        try {
            String value = raw.contains("://") ? raw : "http://" + raw;
            Uri uri = Uri.parse(value);
            String scheme = uri.getScheme();
            if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) return null;
            if (uri.getHost() == null || uri.getUserInfo() != null) return null;

            Uri.Builder b = uri.buildUpon();
            if (uri.getPort() == -1) b.port(5800);
            String path = uri.getPath();
            if (path == null || path.isEmpty() || path.equals("/")) b.path("/vnc.html");
            if (uri.getQueryParameter("autoconnect") == null) b.appendQueryParameter("autoconnect", "true");
            if (uri.getQueryParameter("host") == null) b.appendQueryParameter("host", uri.getHost());
            if (uri.getQueryParameter("port") == null) b.appendQueryParameter("port", "5900");
            return b.build().toString();
        } catch (Exception e) {
            return null;
        }
    }

    private void showHome(String message) {
        if (webView != null) {
            webView.stopLoading();
            webView.loadUrl("about:blank");
        }
        viewerScreen.setVisibility(View.GONE);
        homeScreen.setVisibility(View.VISIBLE);
        statusText.setText("●  " + message);
    }

    @Override
    public void onBackPressed() {
        if (viewerScreen != null && viewerScreen.getVisibility() == View.VISIBLE) {
            if (webView != null && webView.canGoBack()) webView.goBack();
            else showHome("Desconectado");
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
