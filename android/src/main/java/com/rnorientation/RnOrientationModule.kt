package com.rnorientation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.hardware.SensorManager
import android.util.Log
import android.view.OrientationEventListener
import android.view.Surface
import android.view.WindowManager
import android.widget.Toast
import com.facebook.common.logging.FLog
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.WritableMap
import com.facebook.react.common.ReactConstants
import com.facebook.react.modules.core.DeviceEventManagerModule


class RnOrientationModule(val reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext), LifecycleEventListener {
  private var lastOrientationValue = ""
  private var isLocked = false
  private var mOrientationListener: OrientationListener? = null
  private var mTestOrientationListener: OrientationEventListener? = null

  private companion object {
    const val PROJECT_NAME = "RnOrientation"
    const val LANDSCAPE = "LANDSCAPE"
    const val PORTRAIT = "PORTRAIT"
    const val UNKNOWN = "UNKNOWN"
    const val ORIENTATION_DID_UPDATE = "orientationDidUpdate"
  }

  private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      val newConfig: Configuration? = intent.getParcelableExtra("newConfig")
      Log.d("receiver", newConfig?.orientation.toString())
      val orientationValue = if (newConfig?.orientation == 1) "PORTRAIT" else "LANDSCAPE"
      val params: WritableMap = Arguments.createMap()
      params.putString("eventProperty", orientationValue)
      if (reactContext.hasActiveCatalystInstance()) {
        reactContext
          .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
          .emit("orientationDidChange", params)
      }
    }
  }

  init {
    Log.i("RnInit", "Init");
    reactContext.addLifecycleEventListener(this)
    detectOrientationListener()
    testDetectOrientationListener()
  }

  override fun getName() = PROJECT_NAME

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  fun multiply(a: Double, b: Double, promise: Promise) {
    promise.resolve(a * b)
  }

  @ReactMethod
  fun sendEvent() {
    val params = Arguments.createMap() // add here the data you want to send
    params.putString("sessionId", "DummyId") // <- example
    sendEventJsLand(reactContext, "onSessionConnect", params)
  }

  @ReactMethod
  fun enableScreenOrientation() {
    val activity = currentActivity ?: return
    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    isLocked = false
    forceSendChangeOrientation()
  }

  private fun forceSendChangeOrientation() {
    lastOrientationValue = getCurrentOrientation()
    val params = Arguments.createMap()
    params.putString("orientation", lastOrientationValue)
    if (reactContext.hasActiveCatalystInstance()) {
      sendEventJsLand(reactContext, ORIENTATION_DID_UPDATE, params)
    }
  }
  private fun showToast(msg: String?) {
    Toast.makeText(reactApplicationContext, msg, Toast.LENGTH_SHORT).show()
  }

  private fun testDetectOrientationListener() {
    Log.i("RnTestDetect", "TestDetect");
    mTestOrientationListener = object : OrientationEventListener(reactContext, SensorManager.SENSOR_DELAY_UI) {
      override fun onOrientationChanged(orientation: Int) {
        showToast("Orientation Received Test : $orientation")
        forceSendChangeOrientation()
      }
    }
  }

  private fun detectOrientationListener() {
    mOrientationListener = OrientationListener(reactContext, SensorManager.SENSOR_DELAY_UI)
    mOrientationListener!!.setOrientationListener(object : OrientationListener.OrientationReceiveListener {
      override fun onOrientationReceived(orientation: Int) {
        showToast("Orientation Received : $orientation")
        forceSendChangeOrientation()
      }
    })
  }

  private fun sendEventJsLand(mContext: ReactContext, eventName: String, params: WritableMap?) {
    mContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(eventName, params)
  }

  private fun getCurrentOrientation(): String {
    val display =
      (reactContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
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
    Log.i("RnResume", "Resume");
    val activity = currentActivity ?: return
    if (activity == null) {
      FLog.e(ReactConstants.TAG, "no activity to register receiver");
      return;
    }
    activity.registerReceiver(mReceiver, IntentFilter("onConfigurationChanged"));
  }

  override fun onHostPause() {
    Log.i("RnPause", "Pause");
    val activity = currentActivity ?: return
    try {
      activity.unregisterReceiver(mReceiver)
    } catch (e: IllegalArgumentException) {
      FLog.e(ReactConstants.TAG, "receiver already unregistered", e)
    }
  }

  override fun onHostDestroy() {
    Log.i("RnDestroy", "Destroy");
    TODO("Not yet implemented")
  }

  @ReactMethod
  fun addListener(eventName: String?) {
    // Keep: Required for RN built in Event Emitter Calls.
  }

  @ReactMethod
  fun removeListeners(count: Int) {
    // Keep: Required for RN built in Event Emitter Calls.
  }

}
