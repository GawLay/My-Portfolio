package com.kyrie.utility.annotation

/*change to lint check class after lint check rule implemented*/
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class DoNotImplementDirectly(
    val message: String =
        "This class should not be implemented directly except for classes starting with 'Base"
)