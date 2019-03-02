package sunbufu.mykey4.model

import android.text.TextUtils
import org.litepal.annotation.Column
import org.litepal.crud.DataSupport
import java.io.Serializable
import java.text.Collator
import java.util.*

class Account(id: Int = 0, name: String = "", userName: String = "", passWord: String = "", deatil: String = "") : Serializable, DataSupport(), Comparable<Account> {
    @Column(unique = true, defaultValue = "unknown")
    var id = id
    /**名称(这个账号)*/
    var name = name
    /**账号*/
    var userName = userName
    /**密码*/
    var passWord = passWord
    /**描述*/
    var deatil = deatil

    /**生成标题*/
    fun getTitle(): String {
        return name + (if (TextUtils.isEmpty(deatil)) "" else "($deatil)")
    }

    override fun compareTo(other: Account): Int {
        val collator = Collator.getInstance(Locale.CHINA)
        var value = collator.compare(name, other.name)
        if (value == 0) {
            value = collator.compare(deatil, other.deatil)
        }
        return value
    }

    override fun toString(): String {
        return "Account(id=$id, name='$name', userName='$userName', passWord='$passWord', deatil='$deatil')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Account

        if (id != other.id) return false
        if (name != other.name) return false
        if (userName != other.userName) return false
        if (passWord != other.passWord) return false
        if (deatil != other.deatil) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }

    fun copy(newAccount: Account) {
        this.name = newAccount.name
        this.userName = newAccount.userName
        this.passWord = newAccount.passWord
        this.deatil = newAccount.deatil
    }

}
