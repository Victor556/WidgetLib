package com.putao.ptx.ptui

import android.content.Context
import android.media.AudioManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar

import com.putao.ptx.widget.PTPopWindow



object PTPopupWindowSample{

    var listener :View.OnClickListener = View.OnClickListener {
        view ->
            when(view.id){
                R.id.iv_volume -> PopupWindowSample(view.context).showAudioPopup(view)
                R.id.iv_volume1 -> PopupWindowSample(view.context).showAudioPopDown(view)
                R.id.button1 -> {
                    PTPopWindow.PopupWindowBuilder(view.context)
                            .setView(LayoutInflater.from(view.context).inflate(R.layout.pop_layout1, null))
                            .setAnimationStyle(R.style.CustomPopWindowStyle)
                            .create()
                            .showAsDropDown(view)
                }
            }
    }
    fun genPTPopupWindow(context: Context): View? = generateView(context,R.layout.layout_popwindow_test).also {
        it?.findViewById(R.id.iv_volume)?.setOnClickListener(listener)
        it?.findViewById(R.id.iv_volume1)?.setOnClickListener(listener)
        it?.findViewById(R.id.button1)?.setOnClickListener(listener)
        it?.layoutParams = ViewGroup.LayoutParams(1400, ViewGroup.LayoutParams.MATCH_PARENT)
    }



    class PopupWindowSample(context: Context) {
        private var popWindow: PTPopWindow? = null
        private var audioManager: AudioManager? = null
        private var contentView: View? = null
        private var seekbar: SeekBar? = null

        init {
            if (popWindow == null) {
                audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                contentView = LayoutInflater.from(context).inflate(R.layout.layout_ptpopwindow, null)
                seekbar = contentView!!.findViewById(R.id.audio_setting) as SeekBar
                seekbar!!.max = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                popWindow = PTPopWindow.PopupWindowBuilder(context)
                        .setView(contentView)
                        .setFocusable(true)
                        .setOutsideTouchable(true)
                        .create()
                changeListener()
            }
        }


        fun showAudioPopup(view: View) {
            if (popWindow != null) {
                popWindow!!.showAsUp(view)
                seekbar!!.progress = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
            }
        }

        fun showAudioPopDown(view: View) {
            if (popWindow != null) {
                popWindow!!.showAsDropDown(view)
                seekbar!!.progress = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
            }

        }
        fun dissmissAudioPopup() {
            if (popWindow != null) {
                popWindow!!.dissmiss()
            }
        }

        /**
         * 滑动事件处理
         */
        private fun changeListener() {
            seekbar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_PLAY_SOUND)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    seekBar.postDelayed({ dissmissAudioPopup() }, 2000)

                }
            })
        }
    }

}

