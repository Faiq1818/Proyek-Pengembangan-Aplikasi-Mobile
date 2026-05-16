package com.example.mybawanggacha.core.di

import com.example.mybawanggacha.presentation.screens.addnote.AddNoteViewModel
import com.example.mybawanggacha.presentation.screens.ai.AIAssistantViewModel
import com.example.mybawanggacha.presentation.screens.anime.AnimeDetailViewModel
import com.example.mybawanggacha.presentation.screens.anime.AnimeHomeViewModel
import com.example.mybawanggacha.presentation.screens.anime.AnimeListViewModel
import com.example.mybawanggacha.presentation.screens.detail.NoteDetailViewModel
import com.example.mybawanggacha.presentation.screens.home.HomeViewModel
import com.example.mybawanggacha.presentation.screens.library.LibraryEntryEditorViewModel
import com.example.mybawanggacha.presentation.screens.library.LibraryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::AddNoteViewModel)
    viewModelOf(::NoteDetailViewModel)
    viewModelOf(::AIAssistantViewModel)
    viewModelOf(::AnimeDetailViewModel)
    viewModelOf(::AnimeHomeViewModel)
    viewModelOf(::AnimeListViewModel)
    viewModelOf(::LibraryViewModel)
    viewModelOf(::LibraryEntryEditorViewModel)
}
