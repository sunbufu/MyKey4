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

    /**类型：0结束后跳转到MainActivity，1直接finish*/
    var type: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        var extra = intent.getSerializableExtra("type")
        if (extra != null)
            type = extra as Int

        var fignerprintDialog = FingerprintDialog(this, "登录", "请验证已有指纹")
        fignerprintDialog.show()
        fignerprintDialog.setPostiveBtn("退出", { System.exit(0) })
        fignerprintDialog.authSuccess = { end() }
        fignerprintDialog.authFail = { MainApplication.instance.toast("认证失败") }
    }

    fun end() {
        if (type == 0)
            startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}