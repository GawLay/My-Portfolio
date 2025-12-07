package com.kyrie.myportfolio.lintChecks

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.XmlContext
import org.w3c.dom.Attr
import org.w3c.dom.Element

class SnakeCaseDetector : Detector(), Detector.XmlScanner {
    companion object {
        val ISSUE =
            Issue.create(
                id = "SnakeCaseNaming",
                briefDescription = "View IDs must use camelCase or snake_case with view-type prefix",
                explanation =
                    """
                    View IDs must start with a view-type prefix (e.g., `tv_` for TextView, `btn_` for Button) 
                    followed by a name in camelCase (e.g., `tv_nextClick`) or snake_case (e.g., `tv_next_click).
                    """.trimIndent(),
                category = Category.USABILITY,
                priority = 6,
                severity = Severity.ERROR,
                implementation =
                    Implementation(
                        SnakeCaseDetector::class.java,
                        Scope.RESOURCE_FILE_SCOPE,
                    ),
            )

        private val VIEW_PREFIXES =
            mapOf(
                "TextView" to "tv_",
                "Button" to "btn_",
                "ImageView" to "iv_",
                "EditText" to "et_",
                "LinearLayout" to "ll_",
                "ConstraintLayout" to "cl_",
                "RecyclerView" to "rc_",
                "View" to "view_",
                "CircularRevealFrameLayout" to "cf_",
                "FloatingActionButton" to "fab_",
                "include" to "include_",
                "BottomNavigationView" to "btm_nav_",
                "Space" to "space_",
                "ViewPager2" to "vp_",
                "SwitchMaterial" to "switch_",
                "LottieAnimationView" to "lottie_",
                "ImageButton" to "imgBtn_",
                "PdfRendererView" to "pdfRendererView_",
                "MyProFolioWebView" to "webView_",
                "item" to "menu_",
                "FrameLayout" to "fl_",
                "MaterialToolbar" to "toolbar_",
                "CircularRevealFrameLayout" to "cr_",
                "include" to "include_",
                "MaterialCardView" to "cv_",
            )

        // Regex for camelCase (e.g., nextClick) or snake_case (e.g., next_click)
        private val NAME_REGEX = Regex("^[a-z][a-z0-9]*([A-Z][a-z0-9]*)*$|^[a-z][a-z0-9]*(_[a-z0-9]+)*$")
    }

    override fun getApplicableAttributes(): Collection<String> {
        return listOf("id")
    }

    override fun visitAttribute(
        context: XmlContext,
        attribute: Attr,
    ) {
        val idValue = attribute.value
        if (!idValue.startsWith("@+id/")) return

        val idName = idValue.removePrefix("@+id/")
        val element = attribute.ownerElement
        val viewType = getViewType(element)
        val expectedPrefix = VIEW_PREFIXES[viewType] ?: "view_"

        // Check for missing prefix
        if (!idName.startsWith(expectedPrefix)) {
            reportIssue(
                context,
                attribute,
                "Missing prefix",
                "ID '$idName' for $viewType should start with '$expectedPrefix' (e.g., ${expectedPrefix}nextClick or ${expectedPrefix}next_click)",
            )
            return
        }

        // Check the name part after prefix
        val namePart = idName.removePrefix(expectedPrefix)
        if (namePart.isEmpty()) {
            reportIssue(
                context,
                attribute,
                "Empty name",
                "ID needs a meaningful name after '$expectedPrefix' (e.g., ${expectedPrefix}nextClick or ${expectedPrefix}next_click)",
            )
            return
        }

        if (!NAME_REGEX.matches(namePart)) {
            reportIssue(
                context,
                attribute,
                "Invalid format",
                "ID '$idName' for $viewType should use camelCase (e.g., ${expectedPrefix}nextClick) or snake_case (e.g., ${expectedPrefix}next_click) after prefix",
            )
        }
    }

    private fun getViewType(element: Element): String {
        val tagName = element.tagName
        return when {
            tagName.contains(".") -> tagName.substringAfterLast(".")
            else -> tagName
        }
    }

    private fun reportIssue(
        context: XmlContext,
        attribute: Attr,
        summary: String,
        details: String,
    ) {
        context.report(
            issue = ISSUE,
            location = context.getValueLocation(attribute),
            message = "$summary for ${attribute.ownerElement.tagName}. $details",
        )
    }
}
