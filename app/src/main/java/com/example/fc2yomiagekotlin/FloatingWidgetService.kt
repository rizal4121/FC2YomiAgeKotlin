package com.example.fc2yomiagekotlin

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import com.squareup.picasso.Picasso
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.exp

class FloatingWidgetService : Service(), View.OnClickListener , View.OnTouchListener{
    private lateinit var mWindowManager: WindowManager
    private lateinit var mFloatingWidgetView: View
    private lateinit var collapsedView: View
    private lateinit var expandedView: View
    private lateinit var remove_image_view: ImageView
    private var szWindow = Point()
    private lateinit var removeFloatingWidgetView: View
    private var x_init_cord = 0
    private var y_init_cord = 0
    private var x_init_margin = 0
    private var y_init_margin = 0
    private var isLeft = true
    private lateinit var imgview : ImageView
    private lateinit var imgview2 : ImageView
    var urlprofile = ""
    private var userimageprofile : String = "image.txt"

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val ctx : Context = this.applicationContext
        try {
            val fileInputStream = ctx.openFileInput(userimageprofile)

            val fileData = readFromFileInputStream(fileInputStream)

            if (fileData.length > 0) {
                urlprofile = fileData
            }
            else {
            }
        } catch (ex: FileNotFoundException) {
            Log.e("JSON", ex.message, ex)
        }

        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManagerDefaultDisplay
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        addRemoveView(inflater)
        addFloatingWidgetView(inflater)
        implementClickListeners()
        implementTouchListenerToFloatingWidgetView()
    }
    override fun onTouch(v: View, event: MotionEvent?): Boolean {
        return true
    }

    private fun addRemoveView(inflater: LayoutInflater): View? {
        removeFloatingWidgetView = inflater.inflate(R.layout.remove_floating_widget_layout, null)
        val layoutFlag = if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }
        else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        val paramRemove = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )

        paramRemove.gravity = Gravity.TOP or Gravity.LEFT
        removeFloatingWidgetView!!.visibility = View.GONE
        remove_image_view = removeFloatingWidgetView!!.findViewById<View>(R.id.remove_img) as ImageView
        mWindowManager!!.addView(removeFloatingWidgetView, paramRemove)
        return remove_image_view
    }

    private fun addFloatingWidgetView(inflater: LayoutInflater) {
        mFloatingWidgetView = inflater.inflate(R.layout.floating_widget_layout, null)
        val layoutFlag = if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }
        else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = 500
        params.y = 100

        mWindowManager!!.addView(mFloatingWidgetView, params)
        collapsedView = mFloatingWidgetView!!.findViewById<View>(R.id.collapse_view)
        imgview = mFloatingWidgetView!!.findViewById<ImageView>(R.id.collapsed_iv)
        Picasso.get().load(urlprofile).fit().into(imgview)
        expandedView = mFloatingWidgetView!!.findViewById<View>(R.id.expanded_container)
        imgview2 = mFloatingWidgetView!!.findViewById<ImageView>(R.id.floating_widget_image_view)
        Picasso.get().load(urlprofile).fit().into(imgview2)
    }

    private val windowManagerDefaultDisplay: Unit
        get() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) mWindowManager!!.defaultDisplay.getSize(
                szWindow
            )
            else {
                val w = mWindowManager!!.defaultDisplay.width
                val h = mWindowManager!!.defaultDisplay.height
                szWindow[w] = h
            }
        }

    private fun implementTouchListenerToFloatingWidgetView() {
        mFloatingWidgetView!!.findViewById<View>(R.id.root_container)
            .setOnTouchListener(object : OnTouchListener {
                var time_start: Long = 0
                var time_end: Long = 0
                var isLongClick: Boolean = false
                var inBounded: Boolean = false
                var remove_img_width: Int = 0
                var remove_img_height: Int = 0
                var handler_longClick: Handler = Handler()
                var runnable_longClick: Runnable = Runnable {
                    isLongClick = true
                    removeFloatingWidgetView!!.visibility = View.VISIBLE
                    onFloatingWidgetLongClick()
                }

                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    val layoutParams =
                        mFloatingWidgetView!!.layoutParams as WindowManager.LayoutParams

                    val x_cord = event.rawX.toInt()
                    val y_cord = event.rawY.toInt()
                    val x_cord_Destination: Int
                    var y_cord_Destination: Int

                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            time_start = System.currentTimeMillis()

                            handler_longClick.postDelayed(runnable_longClick, 600)

                            remove_img_width = remove_image_view!!.layoutParams.width
                            remove_img_height = remove_image_view!!.layoutParams.height

                            x_init_cord = x_cord
                            y_init_cord = y_cord
                            x_init_margin = layoutParams.x
                            y_init_margin = layoutParams.y

                            return true
                        }

                        MotionEvent.ACTION_UP -> {
                            isLongClick = false
                            removeFloatingWidgetView!!.visibility = View.GONE
                            remove_image_view!!.layoutParams.height = remove_img_height
                            remove_image_view!!.layoutParams.width = remove_img_width
                            handler_longClick.removeCallbacks(runnable_longClick)
                            if (inBounded) {
                                stopSelf()
                                inBounded = false
                            }
                            val x_diff = x_cord - x_init_cord
                            val y_diff = y_cord - y_init_cord

                            if (abs(x_diff.toDouble()) < 5 && abs(y_diff.toDouble()) < 5) {
                                time_end = System.currentTimeMillis()


                                if ((time_end - time_start) < 300) onFloatingWidgetClick()
                            }

                            y_cord_Destination = y_init_margin + y_diff

                            val barHeight: Int = getstatusBarHeight
                            if (y_cord_Destination < 0) {
                                y_cord_Destination = 0
                            } else if (y_cord_Destination + (mFloatingWidgetView!!.height + barHeight) > szWindow.y) {
                                y_cord_Destination =
                                    szWindow.y - (mFloatingWidgetView!!.height + barHeight)
                            }

                            layoutParams.y = y_cord_Destination

                            inBounded = false
                            resetPosition(x_cord)

                            return true
                        }

                        MotionEvent.ACTION_MOVE -> {
                            val x_diff_move = x_cord - x_init_cord
                            val y_diff_move = y_cord - y_init_cord
                            x_cord_Destination = x_init_margin + x_diff_move
                            y_cord_Destination = y_init_margin + y_diff_move

                            if (isLongClick) {
                                val x_bound_left = szWindow.x / 2 - (remove_img_width * 1.5).toInt()
                                val x_bound_right =
                                    szWindow.x / 2 + (remove_img_width * 1.5).toInt()
                                val y_bound_top = szWindow.y - (remove_img_height * 1.5).toInt()

                                if ((x_cord >= x_bound_left && x_cord <= x_bound_right) && y_cord >= y_bound_top) {
                                    inBounded = true
                                    val x_cord_remove =
                                        ((szWindow.x - (remove_img_height * 1.5)) / 2).toInt()
                                    val y_cord_remove: Int =
                                        (szWindow.y - ((remove_img_width * 1.5) + getstatusBarHeight)).toInt()

                                    if (remove_image_view!!.layoutParams.height == remove_img_height) {
                                        remove_image_view!!.layoutParams.height =
                                            (remove_img_height * 1.5).toInt()
                                        remove_image_view!!.layoutParams.width =
                                            (remove_img_width * 1.5).toInt()
                                        val param_remove =
                                            removeFloatingWidgetView!!.layoutParams as WindowManager.LayoutParams

                                        param_remove.x = x_cord_remove
                                        param_remove.y = y_cord_remove

                                        mWindowManager!!.updateViewLayout(
                                            removeFloatingWidgetView,
                                            param_remove
                                        )
                                    }
                                    layoutParams.x =
                                        (x_cord_remove + (abs((removeFloatingWidgetView!!.width - mFloatingWidgetView!!.width).toDouble())) / 2).toInt()
                                    layoutParams.y =
                                        (y_cord_remove + (abs((removeFloatingWidgetView!!.height - mFloatingWidgetView!!.height).toDouble())) / 2).toInt()

                                    mWindowManager!!.updateViewLayout(
                                        mFloatingWidgetView,
                                        layoutParams
                                    )
                                } else {
                                    inBounded = false
                                    remove_image_view!!.layoutParams.height = remove_img_height
                                    remove_image_view!!.layoutParams.width = remove_img_width
                                    onFloatingWidgetClick()
                                }
                            }
                            layoutParams.x = x_cord_Destination
                            layoutParams.y = y_cord_Destination
                            mWindowManager!!.updateViewLayout(mFloatingWidgetView, layoutParams)
                            return true
                        }
                    }
                    return false
                }
            })
    }

    private fun implementClickListeners() {
        mFloatingWidgetView!!.findViewById<View>(R.id.close_floating_view).setOnClickListener(
            this
        )
        mFloatingWidgetView!!.findViewById<View>(R.id.close_expanded_view).setOnClickListener(
            this
        )
        mFloatingWidgetView!!.findViewById<View>(R.id.Tombol_STOP).setOnClickListener(
            this
        )
        mFloatingWidgetView!!.findViewById<View>(R.id.Tombol_START).setOnClickListener(
            this
        )
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.close_floating_view -> stopSelf()
            R.id.close_expanded_view -> {
                collapsedView!!.visibility = View.VISIBLE
                expandedView!!.visibility = View.GONE
            }

            R.id.Tombol_STOP -> {
                stopService(
                    Intent(
                        this@FloatingWidgetService,
                        MyService::class.java
                    )
                )
                Toast.makeText(applicationContext, "読み上げ終了しました", Toast.LENGTH_LONG).show()
            }

            R.id.Tombol_START -> startService(
                Intent(
                    this@FloatingWidgetService,
                    MyService::class.java
                )
            )
        }
    }

    private fun onFloatingWidgetLongClick() {
        val removeParams = removeFloatingWidgetView!!.layoutParams as WindowManager.LayoutParams
        val x_cord = (szWindow.x - removeFloatingWidgetView!!.width) / 2
        val y_cord = szWindow.y - (removeFloatingWidgetView!!.height + getstatusBarHeight)
        removeParams.x = x_cord
        removeParams.y = y_cord
        mWindowManager!!.updateViewLayout(removeFloatingWidgetView, removeParams)
    }

    private fun resetPosition(x_cord_now: Int) {
        if (x_cord_now <= szWindow.x / 2) {
            isLeft = true
            moveToLeft(x_cord_now)
        } else {
            isLeft = false
            moveToRight(x_cord_now)
        }
    }

    private fun moveToLeft(current_x_cord: Int) {
        val x = szWindow.x - current_x_cord

        object : CountDownTimer(500, 5) {
            var mParams: WindowManager.LayoutParams =
                mFloatingWidgetView!!.layoutParams as WindowManager.LayoutParams

            override fun onTick(t: Long) {
                val step = (500 - t) / 5

                mParams.x = 0 - (current_x_cord * current_x_cord * step).toInt()
                mWindowManager!!.updateViewLayout(mFloatingWidgetView, mParams)
            }

            override fun onFinish() {
                mParams.x = 0
                mWindowManager!!.updateViewLayout(mFloatingWidgetView, mParams)
            }
        }.start()
    }

    private fun moveToRight(current_x_cord: Int) {
        object : CountDownTimer(500, 5) {
            var mParams: WindowManager.LayoutParams =
                mFloatingWidgetView!!.layoutParams as WindowManager.LayoutParams

            override fun onTick(t: Long) {
                val step = (500 - t) / 5
                mParams.x =
                    (szWindow.x + (current_x_cord * current_x_cord * step) - mFloatingWidgetView!!.width).toInt()
                mWindowManager!!.updateViewLayout(mFloatingWidgetView, mParams)
            }

            override fun onFinish() {
                mParams.x = szWindow.x - mFloatingWidgetView!!.width
                mWindowManager!!.updateViewLayout(mFloatingWidgetView, mParams)
            }
        }.start()
    }


    private fun bounceValue(step: Long, scale: Long): Double {
        val value = scale * exp(-0.055 * step) * cos(0.08 * step)
        return value
    }

    private val isViewCollapsed: Boolean
        get() = mFloatingWidgetView == null || mFloatingWidgetView!!.findViewById<View>(R.id.collapse_view).visibility == View.VISIBLE

    private val getstatusBarHeight: Int
        get() = ceil((25 * applicationContext.resources.displayMetrics.density).toDouble())
            .toInt()

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        windowManagerDefaultDisplay
        val layoutParams = mFloatingWidgetView!!.layoutParams as WindowManager.LayoutParams

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (layoutParams.y + (mFloatingWidgetView!!.height + getstatusBarHeight) > szWindow.y) {
                layoutParams.y = szWindow.y - (mFloatingWidgetView!!.height + getstatusBarHeight)
                mWindowManager!!.updateViewLayout(mFloatingWidgetView, layoutParams)
            }

            if (layoutParams.x != 0 && layoutParams.x < szWindow.x) {
                resetPosition(szWindow.x)
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (layoutParams.x > szWindow.x) {
                resetPosition(szWindow.x)
            }
        }
    }

    private fun onFloatingWidgetClick() {
        if (isViewCollapsed) {
            collapsedView!!.visibility = View.GONE
            expandedView!!.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (mFloatingWidgetView != null) mWindowManager!!.removeView(mFloatingWidgetView)

        if (removeFloatingWidgetView != null) mWindowManager!!.removeView(removeFloatingWidgetView)
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