package com.ultracloud.phone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    private LinearLayout.LayoutParams spaced(int width, int height, int bottomDp) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(width, height);
        p.bottomMargin = dp(bottomDp);
        return p;
    }

    private void buildInterface() {
        root = new FrameLayout(this);
        root.setBackgroundColor(BG);
        setContentView(root);

        homeScreen = column();
        homeScreen.setPadding(dp(22), dp(30), dp(22), dp(24));
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.addView(homeScreen);
        root.addView(scroll, new FrameLayout.LayoutParams(-1, -1));

        TextView brand = text("ULTRA CLOUD PHONE", 13, GREEN, true);
        brand.setLetterSpacing(0.12f);
        homeScreen.addView(brand, spaced(-1, -2, 18));

        TextView title = text("Seu Android na nuvem", 27, WHITE, true);
        homeScreen.addView(title, spaced(-1, -2, 8));

        TextView subtitle = text(
            "Acesse um Android remoto pela internet e controle a tela pelo seu celular.",
            14, MUTED, false
        );
        subtitle.setLineSpacing(dp(3), 1f);
        homeScreen.addView(subtitle, spaced(-1, -2, 24));

        LinearLayout card = column();
        card.setPadding(dp(16), dp(18), dp(16), dp(16));
        card.setBackground(rounded(CARD, 18, Color.rgb(39, 54, 68)));
        homeScreen.addView(card, spaced(-1, -2, 14));

        TextView label = text("ENDEREÇO DO SERVIDOR", 11, GREEN, true);
        label.setLetterSpacing(0.08f);
        card.addView(label, spaced(-1, -2, 10));

        addressInput = new EditText(this);
        addressInput.setSingleLine(true);
        addressInput.setTextSize(14);
        addressInput.setTextColor(WHITE);
        addressInput.setHintTextColor(MUTED);
        addressInput.setHint("http://100.x.x.x:5800/vnc.html");
        addressInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
            android.text.InputType.TYPE_TEXT_VARIATION_URI);
        addressInput.setImeOptions(EditorInfo.IME_ACTION_GO);
        addressInput.setPadding(dp(12), dp(13), dp(12), dp(13));
        addressInput.setBackground(rounded(Color.rgb(10, 16, 23), 10, Color.rgb(54, 72, 89)));
        String saved = getPreferences(MODE_PRIVATE).getString(PREF_URL, "");
        addressInput.setText(saved);
        card.addView(addressInput, spaced(-1, -2, 12));

        Button connect = button("CONECTAR À NUVEM", true);
        card.addView(connect, spaced(-1, dp(50), 0));
        connect.setOnClickListener(v -> connectToServer());
        addressInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                connectToServer();
                return true;
            }
            return false;
        });

        Button help = button("AINDA NÃO TENHO UM SERVIDOR", false);
        homeScreen.addView(help, spaced(-1, dp(48), 16));
        help.setOnClickListener(v -> showServerHelp());

        statusText = text("●  Servidor não conectado", 13, MUTED, false);
        homeScreen.addView(statusText, spaced(-1, -2, 20));

        TextView noteTitle = text("COMO FUNCIONA", 12, WHITE, true);
        homeScreen.addView(noteTitle, spaced(-1, -2, 8));

        TextView note = text(
            "1. Primeiro, uma máquina na nuvem precisa executar o Android virtual.\n" +
            "2. O servidor mostra um endereço noVNC no final da instalação.\n" +
            "3. O celular e o servidor precisam estar na mesma rede privada Tailscale.\n" +
            "4. Cole aqui o endereço noVNC e toque em Conectar.",
            13, MUTED, false
        );
        note.setLineSpacing(dp(5), 1f);
        homeScreen.addView(note, spaced(-1, -2, 18));

        TextView warning = text(
            "IMPORTANTE: este APK é o controle remoto. Ele não cria sozinho a máquina na nuvem e não executa jogos sem um servidor Android ligado.",
            12, Color.rgb(238, 190, 112), false
        );
        warning.setPadding(dp(12), dp(12), dp(12), dp(12));
        warning.setLineSpacing(dp(3), 1f);
        warning.setBackground(rounded(Color.rgb(43, 35, 24), 12, Color.rgb(86, 65, 38)));
        homeScreen.addView(warning, spaced(-1, -2, 0));

        buildViewerScreen();
    }

    private void showServerHelp() {
        String message =
            "Esse campo NÃO recebe link de vídeo, link do GitHub ou endereço de um site comum.\n\n" +
            "Ele recebe o endereço do painel noVNC, que só existe depois que o servidor Android é instalado e ligado.\n\n" +
            "O processo é: criar uma máquina Ubuntu ARM64 compatível na nuvem, instalar o servidor Ultra Cloud Phone nela, entrar na mesma rede privada Tailscale pelo celular e copiar o endereço que o instalador mostrar no final.\n\n" +
            "Exemplo de formato: http://100.x.x.x:5800/vnc.html\n\n" +
            "Sem essa máquina pronta, deixe o campo vazio. O APK sozinho não fornece um Android remoto.";
        new AlertDialog.Builder(this)
            .setTitle("Como conseguir o endereço")
            .setMessage(message)
            .setPositiveButton("ABRIR GUIA COMPLETO", (dialog, which) -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                        "https://github.com/marcosandrielsilva3-lab/ultra-cloud-phone#preparar-o-servidor-android-virtual"
                    ));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Não foi possível abrir o guia.", Toast.LENGTH_LONG).show();
                }
            })
            .setNegativeButton("FECHAR", null)
            .show();
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
        back.setOnClickListener(v -> showHome("Desconectado"));

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
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        webView.setBackgroundColor(Color.BLACK);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public void onProgressChanged(WebView view, int newProgress) {
                progress.setProgress(newProgress);
                progress.setVisibility(newProgress >= 100 ? View.GONE : View.VISIBLE);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                viewerStatus.setText("Carregando painel...");
                progress.setVisibility(View.VISIBLE);
            }

            @Override public void onPageFinished(WebView view, String url) {
                viewerStatus.setText("Painel aberto — informe a senha VNC se pedir");
            }

            @Override public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame()) {
                    viewerStatus.setText("Falha ao abrir. Confira servidor e Tailscale.");
                }
            }

            @Override public void onReceivedHttpError(WebView view, WebResourceRequest request,
                                                       android.webkit.WebResourceResponse response) {
                if (request.isForMainFrame()) {
                    viewerStatus.setText("Servidor respondeu com erro HTTP " + response.getStatusCode());
                }
            }

            @Override public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.cancel();
                Toast.makeText(MainActivity.this, "Certificado HTTPS inválido. Conexão recusada.", Toast.LENGTH_LONG).show();
            }
        });
        viewerScreen.addView(webView, new LinearLayout.LayoutParams(-1, 0, 1f));
        root.addView(viewerScreen, new FrameLayout.LayoutParams(-1, -1));
    }

    private Button button(String label, boolean primary) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(13);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setAllCaps(false);
        b.setTextColor(primary ? BG : WHITE);
        b.setBackground(rounded(primary ? GREEN : CARD, 12, primary ? 0 : Color.rgb(54, 72, 89)));
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
            addressInput.setError("Este campo precisa do endereço noVNC do servidor, não de um vídeo.");
            return;
        }
        String url = normaliseUrl(raw);
        if (url == null) {
            addressInput.setError("Use um IP ou endereço HTTP/HTTPS válido do painel noVNC.");
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
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
            if (uri.getPort() == -1) {
                String host = uri.getHost();
                String safeHost = host.contains(":") ? "[" + host + "]" : host;
                b.encodedAuthority(safeHost + ":5800");
            }
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
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
