package sunbufu.mykey4.util

import android.text.TextUtils
import com.tozny.crypto.android.AesCbcWithIntegrity

object AESUtil {
    val password = ""//你自己的秘密
    var keys: AesCbcWithIntegrity.SecretKeys? = null

    /**
     * 加密
     */
    fun encrypt(string: String): String {
        if (TextUtils.isEmpty(string))
            return ""
        if (keys == null)
            keys = AesCbcWithIntegrity.generateKeyFromPassword(password, password)
        return AesCbcWithIntegrity.encrypt(string, keys).toString()
    }

    /**
     * 解密
     */
    fun decrypt(string: String): String {
        if (TextUtils.isEmpty(string))
            return ""
        if (keys == null)
            keys = AesCbcWithIntegrity.generateKeyFromPassword(password, password)
        return AesCbcWithIntegrity.decryptString(AesCbcWithIntegrity.CipherTextIvMac(string), keys)
    }

}
