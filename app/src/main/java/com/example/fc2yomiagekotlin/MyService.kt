package com.example.fc2yomiagekotlin

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.util.Locale
import java.util.Timer
import java.util.regex.Pattern
import kotlin.concurrent.timerTask

 class MyService : Service(), TextToSpeech.OnInitListener {
     private var tts: TextToSpeech? = null
     private var timestampx: String = ""
     private var hashx: String = ""
     private var lci = 0
     private var wesmoco: Int = 0
     private var mytimer= Timer()
     private var chan: String= ""
     private var tok: String= ""
     private var userAlamatFilename: String = "JSON.txt"
     private var urlstring :String = ""
     private var usercfg: String = "cfg.txt"
     private var cfgx: String = ""

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        tts = TextToSpeech(this,this)
        val ctx : Context = this.applicationContext
        try {
            val fileInputStream = ctx.openFileInput(userAlamatFilename)
            val fileData = readFromFileInputStream(fileInputStream)
            if (fileData.length > 0) {
                urlstring = fileData
                val regexStr = Pattern.compile("(?<=)\\w+(?=&t)")
                val matcher = regexStr.matcher(urlstring)
                while (matcher.find()) {
                    chan = matcher.group()
                }
                val regexStr2 = Pattern.compile("(?<=)\\w+(?=&l)")
                val matcher2 = regexStr2.matcher(urlstring)
                while (matcher2.find()) {
                    tok = matcher2.group()
                }
            } else {
            }
        } catch (ex: FileNotFoundException) {
            Log.e("JSON", ex.message, ex)
        }
    }

    override fun onInit(i: Int) {
        if (i == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.JAPANESE)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language not supported!")
            } else {
                Log.e("TTS","supported!")
            }
        }

       val intent= Intent(this, FloatingWidgetService::class.java)
       startService(intent)
        tts!!.speak("読み上げ起動しました",TextToSpeech.QUEUE_ADD,null,null)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val ctx : Context = this.applicationContext
        try {
            val fileInputStream = ctx.openFileInput(usercfg)
            val fileData = readFromFileInputStream(fileInputStream)
            if (fileData.length > 0) {
                cfgx = fileData
                val regexStr = Pattern.compile("(?<=)..\\w+(?=&r)")
                val matcher = regexStr.matcher(cfgx)
                var hasil = ""
                while (matcher.find()) {
                    hasil = matcher.group()
                    tts!!.setPitch(hasil.toFloat())
                }
                val regexStr2 = Pattern.compile("(?<=)..\\w+(?=&e)")
                val matcher2 = regexStr2.matcher(cfgx)
                var hasil2 = ""
                while (matcher2.find()) {
                    hasil2 = matcher2.group()
                    tts!!.setSpeechRate(hasil2.toFloat())
                }
            }
            else {
            }
        } catch (ex: FileNotFoundException) {
            Log.e("JSON", ex.message, ex)
        }
        startJSON()
        return START_STICKY // If the service is killed, it will be automatically restarted
    }

    override fun onDestroy() {
        super.onDestroy()
        mytimer.cancel()
        if (tts != null)
        {
            tts!!.stop()
            tts!!.shutdown()
        }
    }

     private fun startJSON() {
         val urlkomen  =
            "https://live.fc2.com/api/getChannelComment.php?channel_id=$chan&token=$tok&last_comment_index=-1"
        val queue = MySingleton.getInstance(this.applicationContext).requestQueue
        val rekues = StringRequest(
            Request.Method.GET,  urlkomen,
            { response ->
                val jsonstatus = response.toString()
                val jsonstatusobjek = JSONObject(jsonstatus)
                val status: Int = jsonstatusobjek.getInt("status")
                if(status == 12) {
                    Log.e("JSON","CHANNEL_ID TIDAK AKTIF")
                }
                else if(status == 11) {
                    Log.e("JSON","TOKEN SALAH")
                }
                else if(status == 13) {
                    Log.e("JSON","DURUNG NONTON CHANNEL")
                }
                else if (status == 10) {
                    Log.e("JSON","Parameter ERROR")
                    Toast.makeText(this, "パラメーターエラーです", Toast.LENGTH_SHORT).show();
                }
                else if(status == 0 ) {
                    val last_comment_index: Int = jsonstatusobjek.getInt("last_comment_index")
                    if (status == 0 && last_comment_index == 1) {
                        Log.e("JSON","durung ono sing komen")
                    }
                    else {
                        // Log.e("JSON","wes ono sing komen")
                        try{
                            val datax = response.toString()
                            val jsonobjek = JSONObject(datax)
                            val komen = jsonobjek.getJSONArray("comments")
                            val komenterakhir = komen.getJSONObject(komen.length()-1)
                            val timestampxxx = komenterakhir.getString("timestamp")
                            val hashxxx = komenterakhir.getString("hash")
                            if (lci == last_comment_index)
                            {
                                //  Log.e("JSON","sama")
                            }
                            else {
                                lci = last_comment_index
                                var a: Int=0
                                //  Log.e("JSON","berubah")
                                if (wesmoco == 0) ///////  <<<<<------------   komen pertama
                                {
                                    val komenarray = komen.getJSONObject(komen.length()-1)
                                    val aran = komenarray.getString("user_name")
                                    val anon = komenarray.getString("anonymous")
                                    if (komenarray.has("system_comment"))
                                    {
                                        Log.e("JSON","HADIAH1")
                                        val system_comment = komenarray.getJSONObject("system_comment")
                                        val tip = system_comment.getString("tip_amount")
                                        if (system_comment.has("gift_id")) {
                                            val type2 = system_comment.getString("gift_id")
                                            gift(type2,aran)
                                        }
                                        else {
                                            cling()
                                            tts!!.speak(aran+"さんが、"+tip+"ぷと"+"ちっぷしました",TextToSpeech.QUEUE_ADD,null,null)
                                        }
                                    }
                                    else {
                                        val komentar = komenarray.getString("comment")
                                        Log.i("JSON",komentar)
                                        if (anon.equals("1")) {
                                            tts!!.speak(komentar,TextToSpeech.QUEUE_ADD,null,null)
                                        }
                                        else {
                                            tts!!.speak(komentar,TextToSpeech.QUEUE_ADD,null,null)
                                            tts!!.speak(aran + "さん",TextToSpeech.QUEUE_ADD,null,null)
                                        }
                                    }
                                    wesmoco = 1
                                }

                                for(i in 0..komen.length()-1){
                                    val komenarray = komen.getJSONObject(i)
                                    val timestampxx = komenarray.getString("timestamp")
                                    val hashxx = komenarray.getString("hash")
                                    if (timestampxx.equals(timestampx) && hashxx.equals(hashx))
                                    {
                                        a = i +1
                                        for (i in a..komen.length()-1){
                                            val komenarray = komen.getJSONObject(i)
                                            val aran = komenarray.getString("user_name")
                                            val anon = komenarray.getString("anonymous")
                                            if (komenarray.has("system_comment"))
                                            {
                                                val system_comment = komenarray.getJSONObject("system_comment")
                                                val tip = system_comment.getString("tip_amount")
                                                if (system_comment.has("gift_id")) {
                                                    val type2 = system_comment.getString("gift_id")
                                                    gift(type2,aran)
                                                }
                                                else {
                                                    cling()
                                                    tts!!.speak(aran+"さんが、"+tip+"ぷと"+"ちっぷしました",TextToSpeech.QUEUE_ADD,null,null)
                                                }
                                            }
                                            else {
                                                val komentar = komenarray.getString("comment")
                                                Log.i("JSON",komentar)
                                                if (anon.equals("1")) {
                                                    tts!!.speak(komentar,TextToSpeech.QUEUE_ADD,null,null)
                                                }
                                                else {
                                                    tts!!.speak(komentar,TextToSpeech.QUEUE_ADD,null,null)
                                                    tts!!.speak(aran + "さん",TextToSpeech.QUEUE_ADD,null,null)
                                                }
                                            }
                                        }
                                    }
                                }
                                hashx = hashxxx
                                timestampx = timestampxxx
                            }
                        }
                        catch (e: Exception){
                        }
                    }
                }
            }, {
            })

        mytimer.schedule(
            timerTask({
                queue.add(rekues)
            }),
            0L,
            1000L
        )
    }
     private fun cling() {
             var mp = MediaPlayer.create(applicationContext, R.raw.smb_coin)
             try
             {
                if (mp.isPlaying) {
                    mp.stop()
                    mp.release()
                    mp = MediaPlayer.create(applicationContext, R.raw.smb_coin)
            }
                     mp.start()
            }
    catch (e: Exception) {
        e.printStackTrace()
        }
        }
     private fun gift (gift: String, aran: String) {
         if(gift.equals("1")) {
             tts!!.speak("ハート。１０ぷと。",TextToSpeech.QUEUE_ADD,null,null)
             cling()
             tts!!.speak(aran+"さん、ギフトありがとうございました。",TextToSpeech.QUEUE_ADD,null,null)
         }
         if(gift.equals("5")) {
             tts!!.speak("キャンディ。１０ぷと。",TextToSpeech.QUEUE_ADD,null,null)
             cling()
             tts!!.speak(aran+"さん、ギフトありがとうございました。",TextToSpeech.QUEUE_ADD,null,null)
         }
         if(gift.equals("7")) {
             tts!!.speak("花火。１０００ぷと。",TextToSpeech.QUEUE_ADD,null,null)
             cling()
             tts!!.speak(aran+"さん、ギフトありがとうございました。",TextToSpeech.QUEUE_ADD,null,null)
         }
         if(gift.equals("8")) {
             tts!!.speak("キッス。１００ぷと。",TextToSpeech.QUEUE_ADD,null,null)
             cling()
             tts!!.speak(aran+"さん、ギフトありがとうございました。",TextToSpeech.QUEUE_ADD,null,null)
         }
         if(gift.equals("13")) {
             tts!!.speak("シャンパン。１００００ぷと",TextToSpeech.QUEUE_ADD,null,null)
             cling()
             tts!!.speak(aran+"さん、ギフトありがとうございました。",TextToSpeech.QUEUE_ADD,null,null)
         }
         if(gift.equals("0")) {
             tts!!.speak("風船",TextToSpeech.QUEUE_ADD,null,null)
         }
         if(gift.equals("2")) {
             tts!!.speak("ダイヤ",TextToSpeech.QUEUE_ADD,null,null)
         }
         if(gift.equals("3")) {
             tts!!.speak("ダイヤ",TextToSpeech.QUEUE_ADD,null,null)
         }
         if(gift.equals("4")) {
             tts!!.speak("ニンジャ",TextToSpeech.QUEUE_ADD,null,null)
         }
         if(gift.equals("6")) {
             tts!!.speak("クラッカー ",TextToSpeech.QUEUE_ADD,null,null)
         }
         if(gift.equals("9")) {
             tts!!.speak("いいね",TextToSpeech.QUEUE_ADD,null,null)
         }
         if(gift.equals("10")) {
             tts!!.speak("車",TextToSpeech.QUEUE_ADD,null,null)
         }
         if(gift.equals("11")) {
             tts!!.speak("さかな ",TextToSpeech.QUEUE_ADD,null,null)
         }
         if(gift.equals("12")) {
             tts!!.speak("UFO",TextToSpeech.QUEUE_ADD,null,null)
         }
     }
     private fun readFromFileInputStream(fileInputStream: FileInputStream?): String {
         val retBuf = StringBuffer()
         try {
             if (fileInputStream != null) {
                 val inputStreamReader = InputStreamReader(fileInputStream)
                 val bufferedReader = BufferedReader(inputStreamReader)
                 var lineData = bufferedReader.readLine()
                 while (lineData != null) {
                     retBuf.append(lineData)
                     lineData = bufferedReader.readLine()
                 }
             }
         } catch (ex: IOException) {
             Log.e("TAG_WRITE_READ_FILE", ex.message, ex)
         } finally {
             return retBuf.toString()
         }
     }
}