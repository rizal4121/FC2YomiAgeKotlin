package com.example.fc2yomiagekotlin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.ToggleButton
import androidx.activity.ComponentActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.*
import java.util.regex.Pattern

class MainActivity : ComponentActivity(),TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var userAlamatFilename: String = "JSON.txt"
    private var usercfg : String = "cfg.txt"
    private var userimageprofile : String = "image.txt"
    private var urlstring :String = ""
    private var cfgx: String = ""
    private var pit: Double = 0.0
    private var rat : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val ctx : Context = this.applicationContext
        tts = TextToSpeech(this, this)
        val editchannel = findViewById<EditText>(R.id.editText)
        val edittoken = findViewById<EditText>(R.id.editText2)

        try {
            val fileInputStream = ctx.openFileInput(userAlamatFilename)
            val fileData = readFromFileInputStream(fileInputStream)
            if (fileData.length > 0) {
                urlstring = fileData
                val regexStr = Pattern.compile("(?<=)\\w+(?=&t)")
                val matcher = regexStr.matcher(urlstring)
                var hasil = ""
                while (matcher.find()) {
                    hasil = matcher.group()
                    editchannel.setText(hasil)
                    editchannel.setSelection(hasil.length)
                }
                val regexStr2 = Pattern.compile("(?<=)\\w+(?=&l)")
                val matcher2 = regexStr2.matcher(urlstring)
                var hasil2 = ""
                while (matcher2.find()) {
                    hasil2 = matcher2.group()
                    edittoken.setText(hasil2)
                    edittoken.setSelection(hasil2.length)
                }
            }
            else {
            }
        } catch (ex: FileNotFoundException) {
            Log.e("JSON", ex.message, ex)
        }

        try {
            val seekBar = findViewById<SeekBar>(R.id.seekBar)
            val seekBar2 = findViewById<SeekBar>(R.id.seekBar2)
            val pitch_txt = findViewById<TextView>(R.id.pitch_ongko)
            val rate_txt = findViewById<TextView>(R.id.rate_ongko)
            val fileInputStream = ctx.openFileInput(usercfg)
            val fileData = readFromFileInputStream(fileInputStream)
            if (fileData.length > 0) {
                cfgx = fileData
                val regexStr = Pattern.compile("(?<=)..\\w+(?=&r)")
                val matcher = regexStr.matcher(cfgx)
                var hasil = ""
                while (matcher.find()) {
                    hasil = matcher.group()
                    pit=hasil.toDouble()
                    if (hasil.toDouble() == 0.1) {seekBar.setProgress(0)
                        pitch_txt.setText("0.1")}
                    if (hasil.toDouble() == 0.2) {seekBar.setProgress(1)
                        pitch_txt.setText("0.2")}
                    if (hasil.toDouble() == 0.3) {seekBar.setProgress(2)
                        pitch_txt.setText("0.3")}
                    if (hasil.toDouble() == 0.4) {seekBar.setProgress(3)
                        pitch_txt.setText("0.4")}
                    if (hasil.toDouble() == 0.5) {seekBar.setProgress(4)
                        pitch_txt.setText("0.5")}
                    if (hasil.toDouble() == 0.6) {seekBar.setProgress(5)
                        pitch_txt.setText("0.6")}
                    if (hasil.toDouble() == 0.7) {seekBar.setProgress(6)
                        pitch_txt.setText("0.7")}
                    if (hasil.toDouble() == 0.8) {seekBar.setProgress(7)
                        pitch_txt.setText("0.8")}
                    if (hasil.toDouble() == 0.9) {seekBar.setProgress(8)
                        pitch_txt.setText("0.9")}
                    if (hasil.toDouble() == 1.0) {seekBar.setProgress(9)
                        pitch_txt.setText("1.0")}
                    if (hasil.toDouble() == 1.1) {seekBar.setProgress(10)
                        pitch_txt.setText("1.1")}
                    if (hasil.toDouble() == 1.2) {seekBar.setProgress(11)
                        pitch_txt.setText("1.2")}
                    if (hasil.toDouble() == 1.3) {seekBar.setProgress(12)
                        pitch_txt.setText("1.3")}
                    if (hasil.toDouble() == 1.4) {seekBar.setProgress(13)
                        pitch_txt.setText("1.4")}
                    if (hasil.toDouble() == 1.5) {seekBar.setProgress(14)
                        pitch_txt.setText("1.5")}
                    if (hasil.toDouble() == 1.6) {seekBar.setProgress(15)
                        pitch_txt.setText("1.6")}
                    if (hasil.toDouble() == 1.7) {seekBar.setProgress(16)
                        pitch_txt.setText("1.7")}
                    if (hasil.toDouble() == 1.8) {seekBar.setProgress(17)
                        pitch_txt.setText("1.8")}
                    if (hasil.toDouble() == 1.9) {seekBar.setProgress(18)
                        pitch_txt.setText("1.9")}
                    if (hasil.toDouble() == 2.0) {seekBar.setProgress(19)
                        pitch_txt.setText("2.0")}
                }
                val regexStr2 = Pattern.compile("(?<=)..\\w+(?=&e)")
                val matcher2 = regexStr2.matcher(cfgx)
                var hasil2 = ""
                while (matcher2.find()) {
                    hasil2 = matcher2.group()
                    rat=hasil2.toDouble()
                    if (hasil2.toDouble() == 0.1) {seekBar2.setProgress(0)
                        rate_txt.setText("0.1")}
                    if (hasil2.toDouble() == 0.2) {seekBar2.setProgress(1)
                        rate_txt.setText("0.2")}
                    if (hasil2.toDouble() == 0.3) {seekBar2.setProgress(2)
                        rate_txt.setText("0.3")}
                    if (hasil2.toDouble() == 0.4) {seekBar2.setProgress(3)
                        rate_txt.setText("0.4")}
                    if (hasil2.toDouble() == 0.5) {seekBar2.setProgress(4)
                        rate_txt.setText("0.5")}
                    if (hasil2.toDouble() == 0.6) {seekBar2.setProgress(5)
                        rate_txt.setText("0.6")}
                    if (hasil2.toDouble() == 0.7) {seekBar2.setProgress(6)
                        rate_txt.setText("0.7")}
                    if (hasil2.toDouble() == 0.8) {seekBar2.setProgress(7)
                        rate_txt.setText("0.8")}
                    if (hasil2.toDouble() == 0.9) {seekBar2.setProgress(8)
                        rate_txt.setText("0.9")}
                    if (hasil2.toDouble() == 1.0) {seekBar2.setProgress(9)
                        rate_txt.setText("1.0")}
                    if (hasil2.toDouble() == 1.1) {seekBar2.setProgress(10)
                        rate_txt.setText("1.1")}
                    if (hasil2.toDouble() == 1.2) {seekBar2.setProgress(11)
                        rate_txt.setText("1.2")}
                    if (hasil2.toDouble() == 1.3) {seekBar2.setProgress(12)
                        rate_txt.setText("1.3")}
                    if (hasil2.toDouble() == 1.4) {seekBar2.setProgress(13)
                        rate_txt.setText("1.4")}
                    if (hasil2.toDouble() == 1.5) {seekBar2.setProgress(14)
                        rate_txt.setText("1.5")}
                    if (hasil2.toDouble() == 1.6) {seekBar2.setProgress(15)
                        rate_txt.setText("1.6")}
                    if (hasil2.toDouble() == 1.7) {seekBar2.setProgress(16)
                        rate_txt.setText("1.7")}
                    if (hasil2.toDouble() == 1.8) {seekBar2.setProgress(17)
                        rate_txt.setText("1.8")}
                    if (hasil2.toDouble() == 1.9) {seekBar2.setProgress(18)
                        rate_txt.setText("1.9")}
                    if (hasil2.toDouble() == 2.0) {seekBar2.setProgress(19)
                        rate_txt.setText("2.0")}
                }
            }
            else {
            }
        } catch (ex: FileNotFoundException) {
            Log.e("JSON", ex.message, ex)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onInit(i: Int) {
        if (i == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.JAPANESE)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language not supported!")
            } else {
                Log.e("TTS","supported!")
            }
        }

        val editchannel = findViewById<EditText>(R.id.editText)
        val edittoken = findViewById<EditText>(R.id.editText2)
        val tombolkonek = findViewById<ToggleButton>(R.id.tombol_login)
        val preview = findViewById<Button>(R.id.preview)
        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        val seekBar2 = findViewById<SeekBar>(R.id.seekBar2)
        val pitch_txt = findViewById<TextView>(R.id.pitch_ongko)
        val rate_txt = findViewById<TextView>(R.id.rate_ongko)
        val pitch_text = findViewById<EditText>(R.id.pitch_text)
        val rate_text = findViewById<EditText>(R.id.rate_text2)
        var pitch : Float = 0.0F
        var rate : Float = 0.0F

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            var progressChangedValue: Int = 1
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (progressChangedValue == 0) {
                    pitch = 0.1f
                    pitch_text.setText("0.1")
                    pitch_txt.setText("0.1")
                }
                if (progressChangedValue == 1) {
                    pitch = 0.2f
                    pitch_text.setText("0.2")
                    pitch_txt.setText("0.2")
                }
                if (progressChangedValue == 2) {
                    pitch = 0.3f
                    pitch_text.setText("0.3")
                    pitch_txt.setText("0.3")
                }
                if (progressChangedValue == 3) {
                    pitch = 0.4f
                    pitch_text.setText("0.4")
                    pitch_txt.setText("0.4")
                }
                if (progressChangedValue == 4) {
                    pitch = 0.5f
                    pitch_text.setText("0.5")
                    pitch_txt.setText("0.5")
                }
                if (progressChangedValue == 5) {
                    pitch = 0.6f
                    pitch_text.setText("0.6")
                    pitch_txt.setText("0.6")
                }
                if (progressChangedValue == 6) {
                    pitch = 0.7f
                    pitch_text.setText("0.7")
                    pitch_txt.setText("0.7")
                }
                if (progressChangedValue == 7) {
                    pitch = 0.8f
                    pitch_text.setText("0.8")
                    pitch_txt.setText("0.8")
                }
                if (progressChangedValue == 8) {
                    pitch = 0.9f
                    pitch_text.setText("0.9")
                    pitch_txt.setText("0.9")
                }
                if (progressChangedValue == 9) {
                    pitch = 1.0f
                    pitch_text.setText("1.0")
                    pitch_txt.setText("1.0")
                }
                if (progressChangedValue == 10) {
                    pitch = 1.1f
                    pitch_text.setText("1.1")
                    pitch_txt.setText("1.1")
                }
                if (progressChangedValue == 11) {
                    pitch = 1.2f
                    pitch_text.setText("1.2")
                    pitch_txt.setText("1.2")
                }
                if (progressChangedValue == 12) {
                    pitch = 1.3f
                    pitch_text.setText("1.3")
                    pitch_txt.setText("1.3")
                }
                if (progressChangedValue == 13) {
                    pitch = 1.4f
                    pitch_text.setText("1.4")
                    pitch_txt.setText("1.4")
                }
                if (progressChangedValue == 14) {
                    pitch = 1.5f
                    pitch_text.setText("1.5")
                    pitch_txt.setText("1.5")
                }
                if (progressChangedValue == 15) {
                    pitch = 1.6f
                    pitch_text.setText("1.6")
                    pitch_txt.setText("1.6")
                }
                if (progressChangedValue == 16) {
                    pitch = 1.7f
                    pitch_text.setText("1.7")
                    pitch_txt.setText("1.7")
                }
                if (progressChangedValue == 17) {
                    pitch = 1.8f
                    pitch_text.setText("1.8")
                    pitch_txt.setText("1.8")
                }
                if (progressChangedValue == 18) {
                    pitch = 1.9f
                    pitch_text.setText("1.9")
                    pitch_txt.setText("1.9")
                }
                if (progressChangedValue == 19) {
                    pitch = 2.0f
                    pitch_text.setText("2.0")
                    pitch_txt.setText("2.0")
                }
            }
        })
        seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            var progressChangedValue: Int = 1
            override fun onProgressChanged(seekBar2: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
            }

            override fun onStartTrackingTouch(seekBar2: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar2: SeekBar) {
                if (progressChangedValue == 0) {
                    rate = 0.1f
                    rate_text.setText("0.1")
                    rate_txt.text = "0.1"
                }
                if (progressChangedValue == 1) {
                    rate = 0.2f
                    rate_text.setText("0.2")
                    rate_txt.text = "0.2"
                }
                if (progressChangedValue == 2) {
                    rate = 0.3f
                    rate_text.setText("0.3")
                    rate_txt.text = "0.3"
                }
                if (progressChangedValue == 3) {
                    rate = 0.4f
                    rate_text.setText("0.4")
                    rate_txt.text = "0.4"
                }
                if (progressChangedValue == 4) {
                    rate = 0.5f
                    rate_text.setText("0.5")
                    rate_txt.text = "0.5"
                }
                if (progressChangedValue == 5) {
                    rate = 0.6f
                    rate_text.setText("0.6")
                    rate_txt.text = "0.6"
                }
                if (progressChangedValue == 6) {
                    rate = 0.7f
                    rate_text.setText("0.7")
                    rate_txt.text = "0.7"
                }
                if (progressChangedValue == 7) {
                    rate = 0.8f
                    rate_text.setText("0.8")
                    rate_txt.text = "0.8"
                }
                if (progressChangedValue == 8) {
                    rate = 0.9f
                    rate_text.setText("0.9")
                    rate_txt.text = "0.9"
                }
                if (progressChangedValue == 9) {
                    rate = 1.0f
                    rate_text.setText("1.0")
                    rate_txt.text = "1.0"
                }
                if (progressChangedValue == 10) {
                    rate = 1.1f
                    rate_text.setText("1.1")
                    rate_txt.text = "1.1"
                }
                if (progressChangedValue == 11) {
                    rate = 1.2f
                    rate_text.setText("1.2")
                    rate_txt.text = "1.2"
                }
                if (progressChangedValue == 12) {
                    rate = 1.3f
                    rate_text.setText("1.3")
                    rate_txt.text = "1.3"
                }
                if (progressChangedValue == 13) {
                    rate = 1.4f
                    rate_text.setText("1.4")
                    rate_txt.text = "1.4"
                }
                if (progressChangedValue == 14) {
                    rate = 1.5f
                    rate_text.setText("1.5")
                    rate_txt.text = "1.5"
                }
                if (progressChangedValue == 15) {
                    rate = 1.6f
                    rate_text.setText("1.6")
                    rate_txt.text = "1.6"
                }
                if (progressChangedValue == 16) {
                    rate = 1.7f
                    rate_text.setText("1.7")
                    rate_txt.text = "1.7"
                }
                if (progressChangedValue == 17) {
                    rate = 1.8f
                    rate_text.setText("1.8")
                    rate_txt.text = "1.8"
                }
                if (progressChangedValue == 18) {
                    rate = 1.9f
                    rate_text.setText("1.9")
                    rate_txt.text = "1.9"
                }
                if (progressChangedValue == 19) {
                    rate = 2.0f
                    rate_text.setText("2.0")
                    rate_txt.text = "2.0"
                }
            }
        })
        preview.setOnClickListener {
            val ctx : Context = this.applicationContext
            val pitchx = pitch.toString().toFloat()
            val ratex = rate.toString().toFloat()
            tts!!.setPitch(pitchx)
            tts!!.setSpeechRate(ratex)
            val woco = "これはテストです。ユーザーめい"
            tts!!.speak(woco, TextToSpeech.QUEUE_FLUSH, null,null)
            val cfg = "pitch=" + pitch.toString() + "&rate="+ rate.toString()+"&end"

            try {
                val fileOutputStream: FileOutputStream = ctx.openFileOutput(usercfg, MODE_PRIVATE)
                writeDataToFile(fileOutputStream, cfg)
            } catch (ex: FileNotFoundException) {
                Log.e("TAG_WRITE_READ_FILE2", ex.message, ex)
            }
        }

        tombolkonek.setOnCheckedChangeListener() { _, isChecked ->
            if (isChecked)
            {
                urlstring="https://live.fc2.com/api/getChannelComment.php?channel_id=" + editchannel.getText().toString() + "&token=" + edittoken.getText().toString() + "&last_comment_index=-1"
                val ctx : Context = this.applicationContext
                try {
                    val fileOutputStream = ctx.openFileOutput(userAlamatFilename, MODE_PRIVATE)
                    writeDataToFile(fileOutputStream, urlstring)
                } catch (ex: FileNotFoundException) {
                    Log.e("JSON", ex.message, ex)
                }
                var urlprofile = "https://live.fc2.com/api/memberApi.php?streamid="+ editchannel.text.toString() +"&channel=1&profile=1"
                val queue = Volley.newRequestQueue(this)
                val rekues = StringRequest(
                    Request.Method.GET, urlprofile,
                    Response.Listener { response ->
                        val datax = response.toString()
                        var jsonobjek = JSONObject(datax)
                        var profdata = jsonobjek.getJSONObject("data")
                        var getprofdata = profdata.getJSONObject("profile_data")
                        var getchandata = profdata.getJSONObject("channel_data")
                        var getimgurl = getchandata.getString("image")
                        try {
                            val fileOutputStream: FileOutputStream = ctx.openFileOutput(userimageprofile, MODE_PRIVATE)
                            writeDataToFile(fileOutputStream, getimgurl)
                        } catch (ex: FileNotFoundException) {
                            Log.e("TAG_WRITE_READ_FILE3", ex.message, ex)
                        }
                    },Response.ErrorListener {
                    })
                queue.add(rekues)

                Thread.sleep(2000)
                val intent= Intent(this, MyService::class.java)
                startService(intent)
            }
            else {
                tts!!.speak("読み上げ終了しました",TextToSpeech.QUEUE_FLUSH,null,null)
                val intent= Intent(this, MyService::class.java)
                stopService(intent)
            }
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
    private fun writeDataToFile(fileOutputStream: FileOutputStream, data: String) {
        try {
            val outputStreamWriter = OutputStreamWriter(fileOutputStream)
            val bufferedWriter = BufferedWriter(outputStreamWriter)
            bufferedWriter.write(data)
            bufferedWriter.flush()
            bufferedWriter.close()
            outputStreamWriter.close()
        } catch (ex: FileNotFoundException) {
            Log.e("TAG_WRITE_READ_FILE", ex.message, ex)
        } catch (ex: IOException) {
            Log.e("TAG_WRITE_READ_FILE", ex.message, ex)
        }
    }
}

