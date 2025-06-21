# The Daily

An Android application for AI-powered conversational experiences with customizable virtual characters.

## Overview

The Daily is a sophisticated AI companion app that enables users to create and interact with personalized AI characters through realistic chat conversations. Built with modern Android development practices using Jetpack Compose and Kotlin, the app provides an immersive messaging experience with deep character customization and personality-driven interactions.

## Key Features

- **AI-Powered Conversations**: Engage in natural, context-aware conversations with AI characters
- **Character Customization**: Create detailed character profiles with unique personalities, interests, and relationship dynamics
- **Relationship System**: Develop relationships with characters through an advanced affection and personality system
- **Configurable AI Backend**: Support for OpenAI-compatible APIs with customizable model settings
- **Modern UI**: Clean, Material 3 design with a messaging-app interface
- **Persistent Chat History**: All conversations are saved locally with full message management
- **Background Notifications**: Receive proactive messages from characters even when the app is closed

## Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Repository pattern
- **Database**: Room (SQLite)
- **Networking**: Retrofit with OkHttp
- **Dependency Injection**: Manual DI
- **Background Processing**: WorkManager
- **Local Storage**: DataStore Preferences

## Project Structure

The app follows clean architecture principles with clear separation of concerns:

- `ui/` - Compose UI screens and components
- `viewmodel/` - ViewModels for UI state management
- `data/` - Data models, database entities, and DAOs
- `data/repository/` - Repository implementations
- `data/network/` - API client and network models
- `utils/` - Utility classes and extensions

## Development Status

This project is currently in active development. Core chat functionality and character management features are implemented, with ongoing work on advanced AI features and life simulation capabilities.

## License

This project is proprietary software. All rights reserved. 