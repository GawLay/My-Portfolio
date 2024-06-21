package com.kyrie.myportfolio.lint_checks

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

class MyIssueRegistry : IssueRegistry() {
    override val issues: List<Issue>
        get() = listOf()

    override val api: Int
        get() = CURRENT_API
    override val minApi: Int
        get() = 8
}