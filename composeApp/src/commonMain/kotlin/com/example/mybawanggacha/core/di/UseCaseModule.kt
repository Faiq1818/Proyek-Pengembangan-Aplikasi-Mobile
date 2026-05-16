package com.example.mybawanggacha.core.di

import com.example.mybawanggacha.domain.usecase.DeleteNoteUseCase
import com.example.mybawanggacha.domain.usecase.GenerateIdeasUseCase
import com.example.mybawanggacha.domain.usecase.GetAllNotesUseCase
import com.example.mybawanggacha.domain.usecase.ImproveWritingUseCase
import com.example.mybawanggacha.domain.usecase.SaveNoteUseCase
import com.example.mybawanggacha.domain.usecase.SearchNotesUseCase
import com.example.mybawanggacha.domain.usecase.SummarizeNoteUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val useCaseModule = module {
    singleOf(::GetAllNotesUseCase)
    singleOf(::SearchNotesUseCase)
    singleOf(::SaveNoteUseCase)
    singleOf(::DeleteNoteUseCase)
    singleOf(::SummarizeNoteUseCase)
    singleOf(::ImproveWritingUseCase)
    singleOf(::GenerateIdeasUseCase)
}
