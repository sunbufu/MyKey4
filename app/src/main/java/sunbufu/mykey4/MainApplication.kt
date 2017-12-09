package sunbufu.mykey4

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
        activityLifecycleCallbacks.runOnBackgroundLongTime = {
            //跳转到认证界面
            if (activityLifecycleCallbacks.frontActivities.size > 0) {
                var frontActivity: Activity = activityLifecycleCallbacks.frontActivities[0]
                var intent = Intent(frontActivity, WelcomeActivity::class.java)
                intent.putExtra("type", 1)
                startActivity(intent)
            }
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