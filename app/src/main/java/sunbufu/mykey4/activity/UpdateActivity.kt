package sunbufu.mykey4.activity

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_update.*
import sunbufu.mykey4.R
import sunbufu.mykey4.model.Account
import android.content.ClipData
import android.text.TextUtils
import sunbufu.mykey4.MainApplication

/**
 * 修改界面
 */
class UpdateActivity : AppCompatActivity(), View.OnClickListener {
    var account: Account? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener(this)

        initData()
        initView()
    }

    private fun initData() {
        account = intent.getSerializableExtra("account") as Account?
    }

    private fun initView() {
        if (account != null) {
            nameText.setText(account!!.name)
            userNameText.setText(account!!.userName)
            passWordText.setText(account!!.passWord)
            detailText.setText(account!!.deatil)
        }
        saveBtn.setOnClickListener(this)
        userNameCopyBtn.setOnClickListener(this)
        passWordCopyBtn.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_update, menu)
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            exit()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete -> {
                if (account != null)
                    AlertDialog.Builder(this)
                            .setTitle("提示")
                            .setMessage("确定删除该条账号信息吗？")
                            .setPositiveButton("删除", { dialog, which -> exit(MainActivity.DELETE) })
                            .setNegativeButton("取消", { dialog, which -> dialog.dismiss() })
                            .show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            -1 -> exit()
            R.id.saveBtn -> {
                if (TextUtils.isEmpty(nameText.text)) {
                    MainApplication.instance.toast("[名称]不能为空")
                    return
                }
                exit()
            }
            R.id.userNameCopyBtn -> {
                var cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.primaryClip = ClipData.newPlainText("userName from MyKey4", userNameText.text)
                MainApplication.instance.toast("复制成功")
            }
            R.id.passWordCopyBtn -> {
                var cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.primaryClip = ClipData.newPlainText("passWord from MyKey4", passWordText.text)
                MainApplication.instance.toast("复制成功")
            }
        }
    }

    /**保存并退出*/
    fun exit(cmd: Int = MainActivity.NO_ACTION) {
        val intent = Intent()

        if (cmd == MainActivity.NO_ACTION) {
            intent.putExtra("cmd", if (account == null) MainActivity.INSERT else MainActivity.UPDATE)//判断是修改还是新建
        } else {
            intent.putExtra("cmd", cmd)
        }

        account = Account(
                if (account == null) -1 else account!!.id,
                nameText.text.toString(),
                userNameText.text.toString(),
                passWordText.text.toString(),
                detailText.text.toString())

        intent.putExtra("account", account)
        setResult(1001, intent)
        this.finish()
    }
}
