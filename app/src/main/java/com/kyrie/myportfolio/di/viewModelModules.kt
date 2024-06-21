package com.kyrie.myportfolio.di

import com.kyrie.myportfolio.aboutMe.AboutMeViewModel
import com.kyrie.myportfolio.experience.detail.ExperienceDetailViewModel
import com.kyrie.myportfolio.setting.faq.FaqViewModel
import com.kyrie.myportfolio.shared.SharedViewModel
import com.kyrie.myportfolio.skill.SkillViewModel
import com.kyrie.myportfolio.templates.TemplateViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModules = module {
    viewModelOf(::AboutMeViewModel)
    viewModelOf(::SkillViewModel)
    viewModelOf(::SharedViewModel)
    viewModelOf(::ExperienceDetailViewModel)
    viewModelOf(::TemplateViewModel)
    viewModelOf(::FaqViewModel)
}