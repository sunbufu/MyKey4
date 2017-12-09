package sunbufu.mykey4.util

import android.app.Activity
import android.app.Application
import android.os.Bundle
import sunbufu.mykey4.MainApplication

/**
 * 检测应用前后台切换
 */
class FrontBackgroundActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

    var frontActivities = ArrayList<Activity>()
    /**是否处于前台*/
    var isFront: Boolean = false
    /**进入后台时的时间戳*/
    var enterBackgroundTime = 0L
    /**后台运行时间*/
    var runOnBackgroundTime = 30 * 1000

    /**进入后台*/
    var enterBackgroundCallback: () -> Unit = { }
    /**进入前台*/
    var enterFrontCallback: () -> Unit = { }
    /**长时间处于后台*/
    var runOnBackgroundLongTime: () -> Unit = { }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityStarted(activity: Activity) {
        if (frontActivities.size <= 0) {//判断是不是有后台进入前台
            isFront = true
            enterFrontCallback()
        }
        frontActivities.add(activity)
        if (enterBackgroundTime > 0) {//判断处于后台的时间
            if ((System.currentTimeMillis() - enterBackgroundTime) > runOnBackgroundTime) {
                runOnBackgroundLongTime()
            }
            enterBackgroundTime = 0L
        }
    }

    override fun onActivityStopped(activity: Activity) {
        frontActivities.remove(activity)
        if (frontActivities.size <= 0) {
            isFront = false
            enterBackgroundTime = System.currentTimeMillis()
            enterBackgroundCallback()
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle?) {
    }

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
    }
}