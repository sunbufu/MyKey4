package sunbufu.mykey4

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.widget.Toast
import org.litepal.LitePal
import sunbufu.mykey4.activity.WelcomeActivity
import sunbufu.mykey4.util.FrontBackgroundActivityLifecycleCallbacks


class MainApplication : Application() {
    companion object {
        lateinit var instance: MainApplication
    }

    var activityLifecycleCallbacks = FrontBackgroundActivityLifecycleCallbacks()
    var toast: Toast? = null

    init {
        instance = this
        closeAndroidPDialog()
        activityLifecycleCallbacks.runOnBackgroundLongTime = {
            //跳转到认证界面
            if (activityLifecycleCallbacks.frontActivities.size > 0) {
                val frontActivity: Activity = activityLifecycleCallbacks.frontActivities[0]
                val intent = Intent(frontActivity, WelcomeActivity::class.java)
                intent.putExtra("type", WelcomeActivity.FINISHE_THIS_TYPE)
                startActivity(intent)
            }
        }
    }

    /**屏蔽提示*/
    @SuppressLint("PrivateApi")
    private fun closeAndroidPDialog() {
        try {
            val aClass = Class.forName("android.content.pm.PackageParser\$Package")
            val declaredConstructor = aClass.getDeclaredConstructor(String::class.java)
            declaredConstructor.isAccessible = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val cls = Class.forName("android.app.ActivityThread")
            val declaredMethod = cls.getDeclaredMethod("currentActivityThread")
            declaredMethod.isAccessible = true
            val activityThread = declaredMethod.invoke(null)
            val mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown")
            mHiddenApiWarningShown.isAccessible = true
            mHiddenApiWarningShown.setBoolean(activityThread, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        LitePal.initialize(this)
    }

    /**弹出提示框*/
    fun toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
        if (toast == null) {
            toast = Toast.makeText(instance, text, duration)
        } else {
            toast!!.setText(text)
            toast!!.duration = duration
        }
        toast!!.show()
    }

}