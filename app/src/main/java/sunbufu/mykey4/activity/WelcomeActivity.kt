package sunbufu.mykey4.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import sunbufu.mykey4.MainApplication
import sunbufu.mykey4.R
import sunbufu.mykey4.dialog.FingerprintDialog

/**
 * 欢迎页面
 */
class WelcomeActivity : AppCompatActivity() {

    companion object {
        /**跳转到main*/
        const val JUMP_TO_MAIN_TYPE = 0
        /**结束*/
        const val FINISHE_THIS_TYPE = 1
    }

    var type: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val extra = intent.getSerializableExtra("type")
        if (extra != null)
            type = extra as Int

        val fingerprintDialog = FingerprintDialog(this, "登录", "请验证已有指纹")
        fingerprintDialog.show()
        fingerprintDialog.setPostiveBtn("退出") { System.exit(0) }
        fingerprintDialog.authSuccess = { end() }
        fingerprintDialog.authFail = { MainApplication.instance.toast("认证失败") }
    }

    fun end() {
        if (JUMP_TO_MAIN_TYPE == type)
            startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}