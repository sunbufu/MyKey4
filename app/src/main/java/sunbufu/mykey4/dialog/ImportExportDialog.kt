package sunbufu.mykey4.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import kotlinx.android.synthetic.main.dialog_import_export.*
import sunbufu.mykey4.R


/**
 * 导入导出弹出框
 */
class ImportExportDialog(context: Context, text: String = "", type: Int = IMPORT, callback: (ImportExportDialog, String) -> Unit) : Dialog(context) {

    companion object {
        val IMPORT = 0
        val EXPORT = 1
    }

    /**模式*/
    var type: Int = type
    var text: String = text
    var callback: (ImportExportDialog, String) -> Unit = callback
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_import_export)

        textView.setText(text)
        if (type == IMPORT) {
            postiveBtn.setText("导入")
            msgText.setText("请粘贴文本并导入")
        } else if (type == EXPORT) {
            postiveBtn.setText("复制")
            textView.isFocusable = false
            msgText.setText("请复制并保存上面的文本")
        }
        postiveBtn.setOnClickListener { callback(this, textView.text.toString()) }
    }
}