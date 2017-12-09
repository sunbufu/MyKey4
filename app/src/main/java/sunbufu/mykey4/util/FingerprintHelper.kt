package sunbufu.mykey4.util

import android.hardware.fingerprint.FingerprintManager
import android.hardware.fingerprint.FingerprintManager.AuthenticationCallback
import android.os.CancellationSignal
import android.widget.ImageView
import android.widget.TextView
import sunbufu.mykey4.R
import sunbufu.mykotlin.util.LogUtils

/**
 *  指纹识别工具类
 */
class FingerprintHelper(fingerprintManager: FingerprintManager, icon: ImageView, text: TextView) {

    val ERROR_TIMEOUT_MILLIS = 1600L
    val SUCCESS_DELAY_MILLIS = 1300L

    var fingerprintManager: FingerprintManager = fingerprintManager
    var authenticationCallback: AuthenticationCallback
    var resetTextRunnable: Runnable

    var icon: ImageView = icon
    var text: TextView = text
    var cancellationSignal: CancellationSignal? = null

    var authSuccess = {}
    var authFail = {}

    init {
        resetTextRunnable = object : Runnable {
            override fun run() {
                text.setTextColor(text.resources.getColor(R.color.hint_color, null))
                text.text = text.resources.getText(R.string.fingerprint_hint)
                icon.setImageResource(R.drawable.ic_fp_40px)
            }
        }
        authenticationCallback = object : AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                LogUtils.e("onAuthenticationError(errorCode:$errorCode, errString:$errString)")
                icon.postDelayed(object : Runnable {
                    override fun run() {
                        authFail()
                    }
                }, ERROR_TIMEOUT_MILLIS)
            }

            override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) {
                LogUtils.e("onAuthenticationHelp(helpCode:$helpCode, helpString:$helpString)")
            }

            override fun onAuthenticationFailed() {
                showError(icon.resources.getString(R.string.fingerprint_not_recognized))
            }

            override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
                text.removeCallbacks(resetTextRunnable)
                icon.setImageResource(R.drawable.ic_fingerprint_success)
                text.setTextColor(text.resources.getColor(R.color.success_color, null))
                text.text = text.resources.getText(R.string.fingerprint_success)
                icon.postDelayed(object : Runnable {
                    override fun run() {
                        authSuccess()
                    }
                }, SUCCESS_DELAY_MILLIS)
            }
        }
    }

    /**是否可以使用指纹识别*/
    fun isFingerprintAuthAvailable() = fingerprintManager.isHardwareDetected && fingerprintManager.hasEnrolledFingerprints()

    /**开始监听指纹传感器*/
    fun startListening(cryptoObject: FingerprintManager.CryptoObject?) {
        if (!isFingerprintAuthAvailable()) return
        cancellationSignal = CancellationSignal()
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, authenticationCallback, null)
    }

    /**结束监听指纹传感器*/
    fun cancelListening() = cancellationSignal?.cancel()

    /**显示错误信息*/
    fun showError(error: CharSequence) {
        icon.setImageResource(R.drawable.ic_fingerprint_error)
        text.text = error
        text.setTextColor(text.resources.getColor(R.color.warning_color, null))
        text.removeCallbacks(resetTextRunnable)
        text.postDelayed(resetTextRunnable, ERROR_TIMEOUT_MILLIS)
    }

}