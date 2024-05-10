package com.rnorientation

import android.view.OrientationEventListener
import com.facebook.react.bridge.ReactApplicationContext

class OrientationListener(mContext: ReactApplicationContext, sensorManager: Int) :
  OrientationEventListener(mContext, sensorManager) {
  private var orientationListener: OrientationReceiveListener? = null

  companion object {
    const val ORIENTATION_PORTRAIT: Int = 0
    const val ORIENTATION_LANDSCAPE: Int = 1
    const val ORIENTATION_PORTRAIT_REVERSE: Int = 2
    const val ORIENTATION_LANDSCAPE_REVERSE: Int = 3
  }

  fun setOrientationListener(orientationListener: OrientationReceiveListener) {
    this.orientationListener = orientationListener
  }

  override fun enable() {
    super.enable()
  }

  override fun disable() {
    super.enable()
  }
  override fun onOrientationChanged(orientation: Int) {
    if (orientation < 0) {
      return  // Flip screen, Not take account
    }
    val curOrientation = if (orientation <= 45) {
      ORIENTATION_PORTRAIT
    } else if (orientation <= 135) {
      ORIENTATION_LANDSCAPE_REVERSE
    } else if (orientation <= 225) {
      ORIENTATION_PORTRAIT_REVERSE
    } else if (orientation <= 315) {
      ORIENTATION_LANDSCAPE
    } else {
      ORIENTATION_PORTRAIT
    }
    if (orientationListener != null) {
      orientationListener!!.onOrientationReceived(curOrientation)
    }
  }

  interface OrientationReceiveListener {
    fun onOrientationReceived(orientation: Int)
  }
}
