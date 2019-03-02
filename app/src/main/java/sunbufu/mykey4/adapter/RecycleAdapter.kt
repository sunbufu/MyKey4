package sunbufu.mykey4.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import sunbufu.mykey4.MainApplication
import sunbufu.mykey4.R
import sunbufu.mykey4.model.Account

/**
 * 列表适配器
 */
class RecycleAdapter(private var context: Context, private var list: List<Account>) : RecyclerView.Adapter<RecycleAdapter.AccountHolder>(), Filterable {

    /**点击回调*/
    var clickCallback: (Int) -> Unit = { MainApplication.instance.toast("click $it") }
    /**长按回调*/
    var longClickCallback: (Int) -> Unit = { id: Int -> MainApplication.instance.toast("long $id") }

    override fun onBindViewHolder(accountHolder: AccountHolder, id: Int) {
        accountHolder.id = id
        accountHolder.nameText.text = list[id].getTitle()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup?, id: Int): AccountHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_recycler, viewGroup, false)
        return AccountHolder(view, id)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    var sourceList = list

    override fun getFilter(): Filter {
        return object : Filter() {
            //执行过滤操作
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    //没有过滤的内容，则使用源数据
                    list = sourceList
                } else {
                    list = ArrayList()
                    for (account in sourceList) {
                        if (account.name.contains(charSequence) || account.deatil.contains(charSequence)) {
                            (list as ArrayList<Account>).add(account)
                        }
                    }
                }

                val filterResults = Filter.FilterResults()
                filterResults.values = list
                return filterResults
            }

            //把过滤后的值返回出来
            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                list = filterResults.values as List<Account>
                notifyDataSetChanged()
            }
        }
    }

    inner class AccountHolder(view: View, var id: Int) : RecyclerView.ViewHolder(view) {
        var nameText = view.findViewById<TextView>(R.id.nameText)!!

        init {
            view.setOnClickListener { clickCallback(this.id) }
            view.setOnLongClickListener {
                longClickCallback(id)
                true
            }
        }
    }

}