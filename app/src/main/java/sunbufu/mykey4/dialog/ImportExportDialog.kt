package sunbufu.mykey4.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import kotlinx.android.synthetic.main.dialog_import_export.*
import sunbufu.mykey4.R


/**
 * 导入导出弹出框
 */
class ImportExportDialog(context: Context, var text: String = "", var type: Int = IMPORT, var callback: (ImportExportDialog, String) -> Unit) : Dialog(context) {

    companion object {
        const val IMPORT = 0
        const val EXPORT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_import_export)

        textView.setText(text)
        if (type == IMPORT) {
            postiveBtn.text = "导入"
            msgText.text = "请粘贴文本并导入"
        } else if (type == EXPORT) {
            postiveBtn.text = "复制"
            textView.isFocusable = false
            msgText.text = "请复制并保存上面的文本"
        }
        postiveBtn.setOnClickListener { callback(this, textView.text.toString()) }
    }
}