package sunbufu.mykey4.dialog

import android.app.Dialog
import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.dialog_fingerprint.*
import sunbufu.mykey4.R
import sunbufu.mykey4.util.FingerprintHelper


/**
 * 指纹弹出框
 */
class FingerprintDialog(context: Context?, title: String = "Sign in", content: String = "Confirm fingerprint to continue") : Dialog(context) {

    var title: String = title
    var content: String = content

    private var postiveListener: () -> Unit = {}
    private var negativeListener: () -> Unit = {}

    lateinit var fingerprintHelper: FingerprintHelper

    /**认证成功*/
    var authSuccess: () -> Unit = {}
    /**认证失败*/
    var authFail: () -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_fingerprint)

        if (title != null) setTitleText(title!!)
        if (content != null) setContentText(content!!)

        postiveBtn.setOnClickListener { postiveListener() }
        negativeBtn.setOnClickListener { negativeListener() }

        var fingerprintManager: FingerprintManager = context.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
        fingerprintHelper = FingerprintHelper(fingerprintManager, imageView, msgText)
        fingerprintHelper.authSuccess = {
            dismiss()
            authSuccess()
        }
        fingerprintHelper.authFail = { authFail() }
        fingerprintHelper.startListening(null)
        setCancelable(false)//不允许返回按钮和空白区域点击的响应
    }

    override fun dismiss() {
        super.dismiss()
        fingerprintHelper.cancelListening()
    }

    fun setTitleText(titleStr: String) {
        titleText.text = titleStr
    }

    fun setContentText(contentStr: String) {
        contentText.text = contentStr
    }

    fun setPostiveBtn(text: String, postiveListener: () -> Unit) {
        postiveBtn.visibility = View.VISIBLE
        postiveBtn.setText(text)
        this.postiveListener = postiveListener
    }

    fun setNegativeBtn(text: String, negativeListener: () -> Unit) {
        negativeBtn.visibility = View.VISIBLE
        negativeBtn.setText(text)
        this.negativeListener = negativeListener
    }

}
