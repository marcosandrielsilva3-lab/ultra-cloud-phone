package com.floatinggamewindow.pro
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.*
class FloatingControlsService : Service() {
 private lateinit var wm:WindowManager
 private var panel:LinearLayout?=null
 private var bubble:TextView?=null
 private lateinit var pp:WindowManager.LayoutParams
 private lateinit var bp:WindowManager.LayoutParams
 private var w=260; private var h=150
 override fun onCreate(){ super.onCreate(); wm=getSystemService(WINDOW_SERVICE) as WindowManager; showPanel() }
 private fun dp(v:Int)=(v*resources.displayMetrics.density).toInt()
 private fun type()=if(Build.VERSION.SDK_INT>=26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE
 private fun showPanel(){
  if(panel!=null)return
  val root=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setBackgroundColor(Color.rgb(28,34,48)); setPadding(dp(7),dp(5),dp(7),dp(5)) }
  val bar=LinearLayout(this).apply { gravity=Gravity.CENTER_VERTICAL }
  bar.addView(TextView(this).apply { text="FGW PRO • arraste"; setTextColor(Color.WHITE); textSize=12f },LinearLayout.LayoutParams(0,dp(38),1f))
  bar.addView(Button(this).apply { text="—"; setOnClickListener{minimize()} })
  bar.addView(Button(this).apply { text="×"; setOnClickListener{stopSelf()} })
  bar.setOnTouchListener(DragPanel()); root.addView(bar)
  root.addView(TextView(this).apply { text="O jogo permanece no app original. O painel não altera o tamanho do jogo."; setTextColor(Color.LTGRAY); textSize=11f })
  val actions=LinearLayout(this).apply { gravity=Gravity.CENTER }
  actions.addView(Button(this).apply { text="−"; setOnClickListener{w=(w-35).coerceAtLeast(170);h=(h-20).coerceAtLeast(95);resize()} })
  actions.addView(Button(this).apply { text="+"; setOnClickListener{w=(w+35).coerceAtMost(340);h=(h+20).coerceAtMost(240);resize()} })
  actions.addView(Button(this).apply { text="Ocultar"; setOnClickListener{minimize()} })
  root.addView(actions)
  panel=root; pp=WindowManager.LayoutParams(dp(w),dp(h),type(),WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,PixelFormat.TRANSLUCENT).apply{gravity=Gravity.TOP or Gravity.START;x=dp(16);y=dp(120)}
  wm.addView(root,pp)
 }
 private fun resize(){panel?.let{pp.width=dp(w);pp.height=dp(h);wm.updateViewLayout(it,pp)}}
 private fun minimize(){
  panel?.let{wm.removeView(it)};panel=null
  if(bubble==null){
   bubble=TextView(this).apply{text="FGW";textSize=13f;setTextColor(Color.WHITE);setBackgroundColor(Color.rgb(35,110,220));gravity=Gravity.CENTER;setPadding(dp(12),dp(10),dp(12),dp(10));setOnClickListener{restore()};setOnTouchListener(DragBubble())}
   bp=WindowManager.LayoutParams(dp(58),dp(48),type(),WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,PixelFormat.TRANSLUCENT).apply{gravity=Gravity.TOP or Gravity.START;x=dp(12);y=dp(220)}
   wm.addView(bubble,bp)
  }
 }
 private fun restore(){bubble?.let{wm.removeView(it)};bubble=null;showPanel()}
 private inner class DragPanel:View.OnTouchListener{
  var sx=0;var sy=0;var ox=0;var oy=0
  override fun onTouch(v:View,e:MotionEvent):Boolean{when(e.action){MotionEvent.ACTION_DOWN->{sx=e.rawX.toInt();sy=e.rawY.toInt();ox=pp.x;oy=pp.y;return true};MotionEvent.ACTION_MOVE->{pp.x=ox+e.rawX.toInt()-sx;pp.y=oy+e.rawY.toInt()-sy;panel?.let{wm.updateViewLayout(it,pp)};return true}};return false}
 }
 private inner class DragBubble:View.OnTouchListener{
  var sx=0;var sy=0;var ox=0;var oy=0
  override fun onTouch(v:View,e:MotionEvent):Boolean{when(e.action){MotionEvent.ACTION_DOWN->{sx=e.rawX.toInt();sy=e.rawY.toInt();ox=bp.x;oy=bp.y;return true};MotionEvent.ACTION_MOVE->{bp.x=ox+e.rawX.toInt()-sx;bp.y=oy+e.rawY.toInt()-sy;bubble?.let{wm.updateViewLayout(it,bp)};return true};MotionEvent.ACTION_UP->{if(kotlin.math.abs(e.rawX.toInt()-sx)<12&&kotlin.math.abs(e.rawY.toInt()-sy)<12)restore();return true}};return false}
 }
 override fun onDestroy(){panel?.let{try{wm.removeView(it)}catch(_:Exception){}};bubble?.let{try{wm.removeView(it)}catch(_:Exception){}};super.onDestroy()}
 override fun onBind(intent:Intent?):IBinder?=null
}
