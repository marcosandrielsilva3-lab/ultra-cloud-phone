package com.floatinggamewindow.pro

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*

class MainActivity : Activity() {
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(26), dp(20), dp(18))
            setBackgroundColor(Color.rgb(15, 18, 27))
        }
        root.addView(TextView(this).apply {
            text = "ROBLOX FLOATING WINDOW"
            textSize = 22f
            setTextColor(Color.WHITE)
        })
        root.addView(TextView(this).apply {
            text = "Este app tenta abrir o Roblox usando a janela livre nativa do Android/Samsung. O próprio sistema precisa aceitar esse modo; nenhum app de terceiros consegue colocar a tela do Roblox dentro de uma janela falsa."
            textSize = 14f
            setTextColor(Color.LTGRAY)
            setPadding(0, dp(12), 0, dp(18))
        })
        root.addView(Button(this).apply {
            text = "Abrir Roblox em janela pequena"
            setOnClickListener { openRobloxWindow() }
        })
        root.addView(Button(this).apply {
            text = "Como ativar janela pop-up no Samsung"
            setOnClickListener { showSamsungHelp() }
        })
        root.addView(Button(this).apply {
            text = "Permissão de sobreposição (opcional)"
            setOnClickListener {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
                } else Toast.makeText(this@MainActivity, "Permissão já concedida", Toast.LENGTH_SHORT).show()
            }
        })
        root.addView(TextView(this).apply {
            text = "Se o aparelho não oferecer janelas livres/pop-up para o Roblox, o Android abrirá o jogo em tela cheia. Nesse caso, este app não consegue forçar o modo flutuante."
            textSize = 13f
            setTextColor(Color.rgb(255, 205, 120))
            setPadding(0, dp(18), 0, 0)
        })
        setContentView(root)
    }

    private fun openRobloxWindow() {
        val launch = packageManager.getLaunchIntentForPackage("com.roblox.client")
        if (launch == null) {
            Toast.makeText(this, "Roblox não está instalado. Instale pela Play Store primeiro.", Toast.LENGTH_LONG).show()
            return
        }
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val dm = resources.displayMetrics
            val width = (dm.widthPixels * 0.72f).toInt().coerceAtLeast(dp(260))
            val height = (dm.heightPixels * 0.62f).toInt().coerceAtLeast(dp(360))
            val left = (dm.widthPixels - width) / 2
            val top = (dm.heightPixels - height) / 4
            val options = ActivityOptions.makeBasic()
            options.launchBounds = Rect(left, top, left + width, top + height)
            try {
                startActivity(launch, options.toBundle())
            } catch (_: Exception) {
                startActivity(launch)
            }
        } else {
            startActivity(launch)
        }
    }

    private fun showSamsungHelp() {
        AlertDialog.Builder(this)
            .setTitle("Janela pop-up no Samsung")
            .setMessage("Se o Roblox abrir em tela cheia: abra a tela de Aplicativos recentes, toque no ícone do Roblox acima do cartão e procure 'Abrir na exibição pop-up' (se essa opção existir no seu modelo/versão). Depois você poderá mover e redimensionar a janela pelos controles do sistema. Alguns modelos Galaxy A03s/versões One UI Core não oferecem essa opção.")
            .setPositiveButton("Entendi", null)
            .show()
    }
}
