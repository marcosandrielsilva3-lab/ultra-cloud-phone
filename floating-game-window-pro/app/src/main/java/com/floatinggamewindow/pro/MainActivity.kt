package com.floatinggamewindow.pro
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
class MainActivity : Activity() {
 private lateinit var list: LinearLayout
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  val root=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(20,26,20,16); setBackgroundColor(Color.rgb(15,18,27)) }
  root.addView(TextView(this).apply { text="FLOATING GAME WINDOW PRO"; textSize=22f; setTextColor(Color.WHITE) })
  root.addView(TextView(this).apply { text="Controles flutuantes para seus jogos. O Android não permite incorporar livremente a tela de outro app em uma janela criada por terceiros."; textSize=14f; setTextColor(Color.LTGRAY); setPadding(0,12,0,16) })
  root.addView(Button(this).apply { text="Conceder permissão de sobreposição"; setOnClickListener { if(!Settings.canDrawOverlays(this@MainActivity)) startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:$packageName"))) else Toast.makeText(this@MainActivity,"Permissão já concedida",Toast.LENGTH_SHORT).show() } })
  root.addView(Button(this).apply { text="Mostrar controles flutuantes"; setOnClickListener { if(!Settings.canDrawOverlays(this@MainActivity)) Toast.makeText(this@MainActivity,"Ative primeiro a permissão",Toast.LENGTH_LONG).show() else { startService(Intent(this@MainActivity,FloatingControlsService::class.java)); moveTaskToBack(true) } } })
  root.addView(TextView(this).apply { text="Jogos e aplicativos instalados"; textSize=18f; setTextColor(Color.WHITE); setPadding(0,18,0,8) })
  list=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL }
  root.addView(list,LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,1f))
  setContentView(root); loadApps()
 }
 private fun loadApps() {
  list.removeAllViews()
  val i=Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
  packageManager.queryIntentActivities(i,0).filter { it.activityInfo.packageName!=packageName }.sortedBy { it.loadLabel(packageManager).toString().lowercase() }.forEach { info ->
   val row=LinearLayout(this).apply { gravity=Gravity.CENTER_VERTICAL }
   row.addView(TextView(this).apply { text=info.loadLabel(packageManager).toString(); textSize=15f; setTextColor(Color.WHITE) },LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1f))
   row.addView(Button(this).apply { text="Abrir"; setOnClickListener {
    val launch=packageManager.getLaunchIntentForPackage(info.activityInfo.packageName)
    if(launch!=null) { launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(launch); if(Settings.canDrawOverlays(this@MainActivity)) startService(Intent(this@MainActivity,FloatingControlsService::class.java)) }
    else Toast.makeText(this@MainActivity,"Não foi possível iniciar",Toast.LENGTH_SHORT).show()
   } })
   list.addView(row)
  }
 }
}
