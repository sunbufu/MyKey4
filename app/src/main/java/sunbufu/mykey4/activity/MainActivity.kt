package sunbufu.mykey4.activity

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import org.litepal.crud.DataSupport
import sunbufu.mykey4.MainApplication
import sunbufu.mykey4.R
import sunbufu.mykey4.adapter.RecycleAdapter
import sunbufu.mykey4.dialog.ImportExportDialog
import sunbufu.mykey4.model.Account
import sunbufu.mykotlin.util.LogUtils
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object {
        /**无变化*/
        const val NO_ACTION = -1
        /**插入*/
        const val INSERT = 1
        /**删除*/
        const val DELETE = 2
        /**更新*/
        const val UPDATE = 3
    }

    private var exitTime = 0L
    private lateinit var accounts: MutableList<Account>
    lateinit var adapter: RecycleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initData()
        initView()
    }

    private fun initData() {
        accounts = DataSupport.findAll(Account::class.java)
        accounts.sort()
        adapter = RecycleAdapter(this, accounts)
    }

    var toolbarTimes = 0

    private fun initView() {
        fab.setOnClickListener { update() }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.clickCallback = { _: Int, account: Account -> update(account) }
        adapter.longClickCallback = { _: Int, account: Account ->
            android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("确定删除[${account.getTitle()}]账号信息吗？")
                    .setPositiveButton("删除") { dialog, which ->
                        deleteAccount(account)
                        adapter.notifyDataSetChanged()
                    }
                    .setNegativeButton("取消") { dialog, which -> dialog.dismiss() }
                    .show()
        }
        //过滤输入框
        filterEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(sequence: CharSequence, i: Int, i1: Int, i2: Int) {
                adapter.filter.filter(sequence.toString())
            }

            override fun beforeTextChanged(sequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {}
        })

        toolbar.setOnClickListener {
            if (toolbarTimes++ >= 10) {
                toolbarTimes = 0
                MainApplication.instance.toast("当前共有${accounts.size}条记录")
            }
        }
    }

    /**
     * 跳转到更新页面
     */
    fun update(account: Account? = null) {
        val intent = Intent()
        if (account != null)
            intent.putExtra("account", account)
        intent.setClass(this, UpdateActivity::class.java)
        startActivityForResult(intent, 1000)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                setFilterEditTextVisibility(View.VISIBLE != filterEditText.visibility)
                return true
            }
            R.id.action_export -> {
                val str = Gson().toJson(accounts, object : TypeToken<List<Account>>() {}.type)
                ImportExportDialog(this, str, ImportExportDialog.EXPORT) { dialog, string ->
                    val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.primaryClip = ClipData.newPlainText("account from MyKey4", string)
                    MainApplication.instance.toast("复制完成")
                }.show()
                return true
            }
            R.id.action_import -> {
                ImportExportDialog(this, "", ImportExportDialog.IMPORT) { dialog, string ->
                    try {
                        val importAccounts: List<Account> = Gson().fromJson(string, object : TypeToken<List<Account>>() {}.type)
                        for (account in importAccounts)
                            Account(account.id, account.name, account.userName, account.passWord, account.deatil).save()
                        accounts.addAll(importAccounts)
                        Collections.sort(accounts)
                        adapter.notifyDataSetChanged()
                        MainApplication.instance.toast("导入成功")
                        dialog.dismiss()
                    } catch (e: Exception) {
                        LogUtils.e(e)
                        MainApplication.instance.toast("导入失败，请检查文本复制是否完整")
                    }
                }.show()
                return true
            }
            R.id.action_about -> {
                AlertDialog.Builder(this)
                        .setTitle("MyKey4")
                        .setMessage("非常感谢您下载体验 MyKey4 。 \nMyKey4是由本人独立开发完成的一款用于存储用户密码的软件。 \n该软件没有联网等任何多余权限，可放心使用。 \n如有问题，欢迎随时联系我：sunyoubufu@qq.com, 也欢迎 star。")
                        .setNegativeButton("github") { dialogInterface, i ->
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/sunbufu/MyKey4")))
                        }
                        .setPositiveButton("联系我") { dialogInterface, i ->
                            val data = Intent(Intent.ACTION_SENDTO)
                            data.data = Uri.parse("mailto:sunyoubufu@qq.com")
                            data.putExtra(Intent.EXTRA_SUBJECT, "应用[ MyKey4 ]反馈")
                            startActivity(data)
                        }.show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**设置搜索框是否可见*/
    private fun setFilterEditTextVisibility(visible: Boolean) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (visible) {
            filterEditText.visibility = View.VISIBLE
            filterEditText.requestFocus()
            imm.showSoftInput(filterEditText, 0)
        } else {
            filterEditText.setText("")
            filterEditText.visibility = View.GONE
            imm.hideSoftInputFromWindow(filterEditText.windowToken, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1000 -> {//updateActivity的回调
                val cmd = data.getIntExtra("cmd", NO_ACTION)
                val account = data.getSerializableExtra("account") as Account
                when (cmd) {
                    INSERT -> {
                        if (!TextUtils.isEmpty(account.name)) {
                            account.save()
                            accounts.add(account)
                        }
                    }
                    UPDATE -> {
                        if (!TextUtils.isEmpty(account.name) && account.id != -1) {
                            val oldAccount: Account? = findOldAccount(account.id)
                            if (oldAccount != null && !oldAccount.equals(account)) {
                                oldAccount.copy(account)
                                oldAccount.save()
                            }
                        }
                    }
                    DELETE -> {
                        deleteAccount(account)
                    }
                }
                Collections.sort(accounts)
                adapter.notifyDataSetChanged()
            }
        }
    }

    /**删除account*/
    private fun deleteAccount(account: Account) {
        val oldAccount: Account? = findOldAccount(account.id)
        if (oldAccount != null) {
            oldAccount.delete()
            accounts.remove(oldAccount)
        }
    }

    /**根据ID在accounts内查找*/
    private fun findOldAccount(id: Int = -1): Account? {
        var oldAccount: Account? = null//accounts.map { if (it.id == account.id) it else null }
        if (id != -1)
            for (temp in accounts) {
                if (temp.id == id)
                    oldAccount = temp
            }
        return oldAccount
    }

    /**连续按返回键两次，退出程序*/
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (View.VISIBLE == filterEditText.visibility) {
                setFilterEditTextVisibility(false)
            } else if (System.currentTimeMillis() - exitTime > 2000) {
                MainApplication.instance.toast("再按一次退出程序")
                exitTime = System.currentTimeMillis()
            } else {
                System.exit(0)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
