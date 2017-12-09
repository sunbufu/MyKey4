package sunbufu.mykey4.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import sunbufu.mykey4.MainApplication
import sunbufu.mykey4.R
import sunbufu.mykey4.model.Account

class RecycleAdapter(context: Context, list: List<Account>) : RecyclerView.Adapter<RecycleAdapter.AccountHolder>() {
    var context = context
    var list = list

    var clickCallback: (Int) -> Unit = { MainApplication.instance.toast("click $it") }
    var longClickCallback: (Int) -> Unit = { id: Int -> MainApplication.instance.toast("long $id") }

    override fun onBindViewHolder(accountHolder: AccountHolder, id: Int) {
        accountHolder.id = id
        var account = list[id]
        var str = account.genTitle()//if (TextUtils.isEmpty(account.deatil)) account.name else "${account.name}(${account.deatil})"
        accountHolder.nameText.text = str
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup?, id: Int): AccountHolder {
        var view: View = LayoutInflater.from(context).inflate(R.layout.item_recycler, viewGroup, false)
        var holder = AccountHolder(view, id)
        return holder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class AccountHolder(view: View, id: Int) : RecyclerView.ViewHolder(view) {
        var id: Int = id
        var nameText = view.findViewById<TextView>(R.id.nameText)

        init {
            view.setOnClickListener { clickCallback(this.id) }
            view.setOnLongClickListener { onLongClick() }
        }

        fun onLongClick(): Boolean {
            longClickCallback(id)
            return true;
        }
    }
}