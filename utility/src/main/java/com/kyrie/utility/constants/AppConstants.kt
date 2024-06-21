package com.kyrie.utility.constants


enum class PermissionCodes(val code: Int){
    STORAGE_PERMISSION_CODE(1000)
}
enum class SharedElementsNames {
    IV_PROFILE_SHARED,
    TV_POSITION_SHARED,
    JOB_CARD_SHARED
}

enum class FancyToastTypes(val value: Int) {
    SUCCESS(1),
    WARNING(2),
    ERROR(3),
    INFO(4),
    DEFAULT(5),
    CONFUSING(6)
}
enum class PlayStoreUrl(val url:String){
    MARKET_DETAIL("play.google.com/store/apps/details?id="),
    MARKET_DEV_DETAIL("play.google.com/store/apps/dev?id=")
}

enum class BundleKeys(val key:String){
    ABOUT_ME_BITMAP_KEY(":aboutMeBitmapProfile")
}
enum class ExpIntentKeys(val key: String) {
    DOCUMENT_ID(":documentID"),
    ADAPTER_POS(":adapterPosition")
}

enum class TemplateIntentKey(val key: String) {
    PDF_PREVIEW_URL(":pdfPreviewUrl"),
    PDF_FILE_NAME(":pdfFileName")
}

enum class WebViewIntentKey(val key: String) {
    URL_KEY(":urlKey"),
    URL_TYPE("urlType")
}
enum class PackageName(val packageName:String){
    LINKEDIN_PACKAGE_NAME("com.linkedin.android"),
}

enum class MyAddressAssociate(val associate: String) {
    LINKEDIN_URL("https://www.linkedin.com/in/phyoaungzaw/"),
    MY_GMAIL("phyoaz14@gmail.com"),
    GMAIL_TYPE("text/html"),
    GMAIL_PACKAGE("com.google.android.gm"),
    GMAIL_CHOOSE_TYPE("Send mail"),
    GMAIL_WEB_COMPOSE_BOX("https://mail.google.com/mail/u/0/#compose")
}

enum class FirebasePDFStrings(val value: String) {
    PDF_URL_KEY("url"),
    PDF_FILE_NAME_KEY("filename"),
    PDF_FILE_URI_KEY("fileURI")
}

enum class NotificationChannelName(val value: String) {
    PDF_DOWNLOAD_CHANNEL("PDF DOWNLOAD CHANNEL"),
    PDF_DOWNLOAD_CHANNEL_DESC("Channel for download notifications"),
    PDF_DOWNLOAD_CHANNEL_ID("pdf_download_channel_id")
}

enum class NotificationNotifyId(val id:Int){
    PDF_NOTIFICATION_NOTIFY_ID(1)
}

enum class PDFPreviewGoogleDocUrl(val url: String){
    //url= linktoyourpdf.pdf

    PDF_PREVIEW_GOOGLE_DOC_URL("https://docs.google.com/gview?embedded=true&url=")
}