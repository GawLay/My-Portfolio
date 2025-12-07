package com.kyrie.data.di

import com.kyrie.data.repository.skill.SkillsRepository
import com.kyrie.data.repository.skill.SkillsRepositoryImpl
import org.koin.dsl.module

val skillRepoModule =
    module {
        single<SkillsRepository> { SkillsRepositoryImpl() }
    }
