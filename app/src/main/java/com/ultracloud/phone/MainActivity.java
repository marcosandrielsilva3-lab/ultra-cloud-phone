package com.ultracloud.phone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String ROBLOX_URL = "https://www.easyfun.gg/cloud-games/roblox.html";
    private static final String FAQ_URL = "https://www.easyfun.gg/faq.html";

    private static final int BG = Color.rgb(9, 15, 22);
    private static final int CARD = Color.rgb(19, 29, 40);
    private static final int GREEN = Color.rgb(115, 230, 160);
    private static final int WHITE = Color.rgb(240, 246, 250);
    private static final int MUTED = Color.rgb(164, 179, 194);
    private static final int AMBER = Color.rgb(245, 195, 110);

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

    private Button button(String label, boolean primary) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(13);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setAllCaps(false);
        b.setTextColor(primary ? BG : WHITE);
        b.setMinHeight(dp(50));
        b.setBackground(rounded(primary ? GREEN : CARD, 12,
            primary ? 0 : Color.rgb(54, 72, 89)));
        return b;
    }

    private void buildInterface() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setBackgroundColor(BG);

        LinearLayout page = column();
        page.setPadding(dp(22), dp(30), dp(22), dp(28));
        scroll.addView(page, new ScrollView.LayoutParams(-1, -2));
        setContentView(scroll);

        TextView brand = text("ULTRA CLOUD PLAY  •  TESTE", 12, GREEN, true);
        brand.setLetterSpacing(0.10f);
        page.addView(brand, spaced(-1, -2, 17));

        TextView title = text("Roblox na nuvem", 29, WHITE, true);
        page.addView(title, spaced(-1, -2, 8));

        TextView subtitle = text(
            "Seu celular recebe a imagem e envia os comandos. O Roblox roda nos servidores remotos da EasyFun.",
            15, MUTED, false
        );
        subtitle.setLineSpacing(dp(4), 1f);
        page.addView(subtitle, spaced(-1, -2, 22));

        LinearLayout planCard = column();
        planCard.setPadding(dp(16), dp(17), dp(16), dp(16));
        planCard.setBackground(rounded(CARD, 17, Color.rgb(42, 59, 74)));
        page.addView(planCard, spaced(-1, -2, 14));

        TextView planTitle = text("ACESSO GRATUITO DA EASYFUN", 11, GREEN, true);
        planTitle.setLetterSpacing(0.07f);
        planCard.addView(planTitle, spaced(-1, -2, 9));

        TextView plan = text(
            "• Até 6 horas por dia com conta conectada\n" +
            "• 30 minutos por dia como visitante\n" +
            "• Pode haver fila e o tempo pode mudar conforme os servidores",
            14, WHITE, false
        );
        plan.setLineSpacing(dp(5), 1f);
        planCard.addView(plan, spaced(-1, -2, 0));

        Button play = button("ABRIR ROBLOX NA NUVEM", true);
        page.addView(play, spaced(-1, dp(54), 12));
        play.setOnClickListener(v -> confirmAndOpen());

        Button rules = button("REGRAS E TEMPO GRATUITO", false);
        page.addView(rules, spaced(-1, dp(50), 18));
        rules.setOnClickListener(v -> openUrl(FAQ_URL));

        LinearLayout safetyCard = column();
        safetyCard.setPadding(dp(15), dp(15), dp(15), dp(14));
        safetyCard.setBackground(rounded(Color.rgb(42, 34, 23), 14,
            Color.rgb(93, 69, 37)));
        page.addView(safetyCard, spaced(-1, -2, 13));

        TextView safetyTitle = text("IMPORTANTE: USE A VERSÃO GLOBAL", 12, AMBER, true);
        safetyCard.addView(safetyTitle, spaced(-1, -2, 7));

        TextView safety = text(
            "Não escolha Roblox VNG/Vietnã. A própria EasyFun alerta que entrar nessa versão pode transferir permanentemente a conta para o servidor VNG. Escolha a versão global.",
            13, WHITE, false
        );
        safety.setLineSpacing(dp(4), 1f);
        safetyCard.addView(safety, spaced(-1, -2, 0));

        TextView privacy = text(
            "LOGIN E SEGURANÇA\n" +
            "O botão abre easyfun.gg no navegador. Confira se o endereço é exatamente easyfun.gg antes de entrar. Não compartilhe códigos de verificação e não reutilize a senha de outros serviços.",
            13, MUTED, false
        );
        privacy.setLineSpacing(dp(4), 1f);
        page.addView(privacy, spaced(-1, -2, 17));

        TextView limitation = text(
            "Este APK é um iniciador independente. Ele não contém servidores de nuvem e não remove os limites da EasyFun. O serviço externo é necessário para executar o Roblox.",
            12, MUTED, false
        );
        limitation.setPadding(dp(12), dp(12), dp(12), dp(12));
        limitation.setBackground(rounded(Color.rgb(17, 25, 34), 12,
            Color.rgb(39, 54, 68)));
        limitation.setLineSpacing(dp(3), 1f);
        page.addView(limitation, spaced(-1, -2, 0));
    }

    private void confirmAndOpen() {
        new AlertDialog.Builder(this)
            .setTitle("Abrir Roblox na EasyFun")
            .setMessage(
                "O jogo será executado nos servidores da EasyFun, não no seu celular. " +
                "A regra informada pela plataforma é até 6 horas grátis por dia com login " +
                "ou 30 minutos como visitante.\n\n" +
                "Na página, escolha Roblox global. Não escolha Roblox VNG/Vietnã."
            )
            .setNegativeButton("VOLTAR", null)
            .setPositiveButton("CONTINUAR", (dialog, which) -> openUrl(ROBLOX_URL))
            .show();
    }

    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(Intent.createChooser(intent, "Abrir no navegador"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Nenhum navegador foi encontrado.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Não foi possível abrir o serviço.", Toast.LENGTH_LONG).show();
        }
    }
}
