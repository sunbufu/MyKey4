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
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import org.litepal.crud.DataSupport
import sunbufu.mykey4.MainApplication
import sunbufu.mykey4.R
import sunbufu.mykey4.adapter.RecycleAdapter
import sunbufu.mykey4.dialog.ImportExportDialog
import sunbufu.mykey4.model.Account
import sunbufu.mykey4.util.AESUtil
import sunbufu.mykotlin.util.LogUtils
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object {
        val NOACTION = -1
        val INSERT = 1
        val DELETE = 2
        val UPDATE = 3
    }

    var exitTime = 0L
    lateinit var accounts: MutableList<Account>
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
        Collections.sort(accounts)
        LogUtils.e(accounts)
        adapter = RecycleAdapter(this, accounts)
    }

    var toolbarTimes = 0

    private fun initView() {
        fab.setOnClickListener { update() }
        recyclerView.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        recyclerView.adapter = adapter
        adapter.clickCallback = { update(it) }
        adapter.longClickCallback = {
            var account = accounts[it]
            android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("确定删除[${account.genTitle()}]账号信息吗？")
                    .setPositiveButton("删除", { dialog, which ->
                        deleteAccount(account)
                        adapter.notifyDataSetChanged()
                    })
                    .setNegativeButton("取消", { dialog, which -> dialog.dismiss() })
                    .show()
        }
        toolbar.setOnClickListener {
            if (toolbarTimes++ >= 10) {
                toolbarTimes = 0
                MainApplication.instance.toast("当前共有${accounts.size}条记录")
            }
        }
    }

    fun update(id: Int = -1) {
        val intent = Intent()
        if (id != -1)
            intent.putExtra("account", accounts[id])
        intent.setClass(this, UpdateActivity::class.java)
        startActivityForResult(intent, 1000)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_export -> {
                var gson = Gson()
                val type = object : TypeToken<List<Account>>() {}.type;
                var str = gson.toJson(accounts, type)
                ImportExportDialog(this, AESUtil.encrypt(str), ImportExportDialog.EXPORT, { dialog, string ->
                    var cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.primaryClip = ClipData.newPlainText("passWord from MyKey4", string)
                    MainApplication.instance.toast("复制完成")
                }).show()
                return true
            }
            R.id.action_import -> {
                var gson = Gson()
                val type = object : TypeToken<List<Account>>() {}.type;
                ImportExportDialog(this, "", ImportExportDialog.IMPORT, { dialog, string ->
                    try {
                        var importAccounts: List<Account> = gson.fromJson(AESUtil.decrypt(string), type)
                        for (account in importAccounts)
                            Account(account.id, account.name, account.userName, account.passWord, account.deatil).save()
                        accounts.addAll(importAccounts)
                        Collections.sort(accounts)
                        adapter.notifyDataSetChanged()
                        MainApplication.instance.toast("导入成功")
                        dialog.dismiss()
                    } catch (e: Exception) {
                        MainApplication.instance.toast("导入失败，请检查文本复制是否完整")
                    }
                }).show()
                return true
            }
            R.id.action_about -> {
                AlertDialog.Builder(this)
                        .setTitle("MyKey4")
                        .setMessage("非常感谢您下载体验 MyKey4 。 \nMyKey4是由本人独立开发完成的一款用于存储用户密码的软件。 \n该软件没有联网等任何多余权限，可放心使用。 \n如有问题，欢迎随时联系我：sunyoubufu@qq.com")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1000 -> {//updateActivity的回调
                var cmd = data.getIntExtra("cmd", NOACTION)
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
                            var oldAccount: Account? = findOldAccount(account.id)
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
    fun deleteAccount(account: Account) {
        var oldAccount: Account? = findOldAccount(account.id)
        if (oldAccount != null) {
            oldAccount.delete()
            accounts.remove(oldAccount)
        }
    }

    /**根据ID在accounts内查找*/
    fun findOldAccount(id: Int = -1): Account? {
        var oldAccount: Account? = null;//accounts.map { if (it.id == account.id) it else null }
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
            if (System.currentTimeMillis() - exitTime > 2000) {
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
