# OrangeCast: Android Podcast Player

<img src="https://github.com/tech1ee/OrangeCast/assets/32034127/843b248f-5c62-4ea9-9e1a-cbbb08794268" alt="discover" width="200"/>
<img src="https://github.com/tech1ee/OrangeCast/assets/32034127/a45cf352-1a35-4e29-a2c5-e4f1a4d8b555" alt="details" width="200"/>

## Key Features

- **Top Rated Podcasts:** Discover an expansive selection of top-rated podcasts fetched from the API.
- **Podcast Details and Episodes:** Explore in-depth podcast information and access their episode feeds.
- **Audio Playback:** Enjoy uninterrupted podcast listening with audio playback functionality.
- **Podcast Subscription:** Stay up-to-date with your preferred podcasts by subscribing to them.
- **User-Friendly Interface:** Intuitive and elegant UI built using Jetpack Compose for a delightful user experience.
- **Offline Listening:** Download podcast episodes for offline listening convenience.
- **Background Playback:** Continue playing podcasts while using other apps.
- **Custom Playlists:** Create and manage personalized playlists to curate your listening queue.
- **Dynamic Themes:** Support for both light and dark themes to suit your visual preferences.

## Technologies Utilized

- **Kotlin:** The primary programming language for Android app development, known for its conciseness and safety features.
- **Jetpack Compose:** A revolutionary UI toolkit that facilitates dynamic UI creation with a declarative syntax.
- **Coroutines:** A powerful framework for handling asynchronous tasks and managing concurrency.
- **Flow:** A reactive programming library used to manage data streams and asynchronous operations.
- **Multi-modules:** Modular project architecture for enhanced organization, modularity, and maintainability.
- **Room:** A robust SQLite database library for efficient data storage and retrieval.
- **Retrofit:** A type-safe HTTP client for seamless network communication and API interactions.

## Project Structure

The project adheres to a modular architecture to ensure clear separation of concerns. Core modules include:

- `app`: The primary Android application module containing UI components and presentation logic.
- `data`: Responsible for data management, including API interactions and database operations.
- `domain`: Houses the core business logic and defines data models.
- `common`: Holds shared utility classes, extensions, and resources.
- `player`: Manages audio playback and related controls.
- `podcasts`: Module responsible for handling the display and management of podcast lists.
- `details`: Module focused on displaying detailed information about a selected podcast.
- `discover`: Module for discovering new podcasts and trending content.
- `genres`: Module for categorizing podcasts based on different genres.
- `library`: Module for managing subscribed and downloaded podcasts.
- `search`: Module for searching podcasts and episodes.
