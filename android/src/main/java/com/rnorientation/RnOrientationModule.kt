package com.rnorientation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.util.Log
import android.view.Surface
import android.view.WindowManager
import com.facebook.common.logging.FLog
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.common.ReactConstants


class RnOrientationModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext), LifecycleEventListener {
  val mReceiver: BroadcastReceiver? = null

  private companion object {
    const val PROJECT_NAME = "RnOrientation"
    const val LANDSCAPE = "LANDSCAPE"
    const val PORTRAIT = "PORTRAIT"
    const val UNKNOWN = "UNKNOWN"
  }

  override fun getName() = PROJECT_NAME

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  fun multiply(a: Double, b: Double, promise: Promise) {
    promise.resolve(a * b)
  }

  private fun getCurrentOrientation(): String {
    val display =
      (reactApplicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    return when (display.rotation) {
      Surface.ROTATION_0 -> PORTRAIT
      Surface.ROTATION_90 -> LANDSCAPE
      Surface.ROTATION_180 -> PORTRAIT
      Surface.ROTATION_270 -> LANDSCAPE
      else -> UNKNOWN
    }
  }

  override fun getConstants(): Map<String, Any?> {
    val constants = HashMap<String, Any?>()
    val orientation = getCurrentOrientation()
    if (orientation === UNKNOWN) {
      constants["initialOrientation"] = null
    } else {
      constants["initialOrientation"] = orientation
    }
    return constants
  }
  @ReactMethod
  fun getOrientation(callback: Callback) {
    val orientation = getCurrentOrientation()
    Log.d("TAG", orientation);
    callback.invoke(orientation)
  }

  override fun onHostResume() {
    val activity = currentActivity ?: return
    if (activity == null) {
      FLog.e(ReactConstants.TAG, "no activity to register receiver");
      return;
    }
    activity.registerReceiver(mReceiver, IntentFilter("onConfigurationChanged"));
  }

  override fun onHostPause() {
    val activity = currentActivity ?: return
    try {
      activity.unregisterReceiver(mReceiver)
    } catch (e: IllegalArgumentException) {
      FLog.e(ReactConstants.TAG, "receiver already unregistered", e)
    }
  }

  override fun onHostDestroy() {
    TODO("Not yet implemented")
  }
}
