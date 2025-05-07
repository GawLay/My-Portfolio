package com.kyrie.utility.utility

import android.app.Activity
import android.app.Activity.OVERRIDE_TRANSITION_CLOSE
import android.app.Activity.OVERRIDE_TRANSITION_OPEN
import android.app.ActivityOptions
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.net.toUri
import com.kyrie.utility.constants.MyAddressAssociate
import com.kyrie.utility.constants.PackageName
import android.util.Pair as UtilPair


inline fun <reified T> Context.startIntent() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}

inline fun <reified T> Activity.startIntentWithPendingTransition(enterAnim: Int, exitAnim: Int) {
    val intent = Intent(this, T::class.java)
    startActivity(intent)


}

inline fun <reified T> Context.startIntentWithData(noinline extras: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    intent.extras()
    startActivity(intent)
}

inline fun <reified T> Context.getIntentWithData(noinline extras: Intent.() -> Unit = {}): Intent {
    val intent = Intent(this, T::class.java)
    intent.extras()
    return intent
}

inline fun <reified T> Context.startRevealIntent(noinline extras: Intent.() -> Unit = {}) {

    val intent = Intent(this, T::class.java)
    intent.extras()
    startActivity(intent)
}

inline fun <reified T> Activity.startOptionsRevealIntent(noinline extras: Intent.() -> Unit = {}) {

    val intent = Intent(this, T::class.java)
    intent.extras()
    startActivity(
        intent,
        ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
    )
}

inline fun <reified T> Activity.makeSharedSceneTransition(
    requestCode: Int,
    vararg pairs: UtilPair<View?, String?>
) {
    val intent = Intent(this, T::class.java)
    val options = ActivityOptions.makeSceneTransitionAnimation(
        this,
        *pairs
    )
    startActivityForResult(
        intent, requestCode,
        options.toBundle()
    )
}

inline fun <reified T> Activity.makeSharedSceneTransitionWithData(
    requestCode: Int,
    vararg pairs: UtilPair<View?, String?>,
    noinline extras: Intent.() -> Unit = {}
) {
    val intent = Intent(this, T::class.java)
    val options = ActivityOptions.makeSceneTransitionAnimation(
        this,
        *pairs
    )
    intent.extras()
    startActivityForResult(
        intent, requestCode,
        options.toBundle()
    )
}

inline fun <reified T> Activity.makeSharedSceneTransitionWithDataResult(
    requestCode: Int,
    vararg pairs: UtilPair<View?, String?>,
    noinline extras: Intent.() -> Unit = {}
) {
    val intent = Intent(this, T::class.java)
    val options = ActivityOptions.makeSceneTransitionAnimation(
        this,
        *pairs
    )
    intent.extras()
    startActivityForResult(
        intent, requestCode,
        options.toBundle()
    )
}

fun Context.startActionViewIntent(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

fun Context.startPlayStore(url: String, fallback: () -> Unit) {
    try {
        val intent = Intent(
            Intent.ACTION_VIEW,
            url.toUri()
        )
        intent.setPackage("com.android.vending")
        startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        fallback.invoke()
    }

}

fun Activity.startLinkedIn(fallback: () -> Unit) {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = MyAddressAssociate.LINKEDIN_URL.associate.toUri()
        intent.setPackage(PackageName.LINKEDIN_PACKAGE_NAME.packageName)
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        fallback.invoke()
    }
}

fun Activity.startGmail() {
    try {
        val intent = Intent(Intent.ACTION_SEND)
        val recipients = arrayOf(MyAddressAssociate.MY_GMAIL.associate)
        intent.putExtra(Intent.EXTRA_EMAIL, recipients)
        intent.type = MyAddressAssociate.GMAIL_TYPE.associate
        intent.setPackage(MyAddressAssociate.GMAIL_PACKAGE.associate)
        startActivity(Intent.createChooser(intent, MyAddressAssociate.GMAIL_CHOOSE_TYPE.associate))
    } catch (e: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                MyAddressAssociate.GMAIL_WEB_COMPOSE_BOX.associate.toUri()
            )
        )
    }
}

fun Context.callPhone(phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null))
    startActivity(intent)
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

fun Activity.overridePendingTransitionExt(
    overrideTransitionClose: Boolean = false,
    enterAnim: Int,
    exitAnim: Int,
) {
    if (SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        overrideActivityTransition(
            if (overrideTransitionClose) OVERRIDE_TRANSITION_CLOSE else OVERRIDE_TRANSITION_OPEN,
            enterAnim,
            exitAnim
        )
    } else {
        @Suppress("DEPRECATION")
        overridePendingTransition(enterAnim, exitAnim)
    }
}

