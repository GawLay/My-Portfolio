package com.kyrie.myportfolio.lintChecks

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.Issue

class MyIssueRegistry : IssueRegistry() {
    override val issues: List<Issue>
        get() =
            listOf(
                SnakeCaseDetector.ISSUE,
            )
}
