# Android Project 4 - Flixster (Trending People)

Submitted by: **Joshua Akeredolu**

**Flixster** is a browsing app that allows users to browse trending people from The Movie DB, view their details, and explore each person's filmography with posters, roles, and plot summaries.

Time spent: **8** hours spent in total

## Required Features
- [x] **Choose any endpoint on The MovieDB API except `now_playing`**
  - Chosen Endpoint: `trending/person/{time_window}`
- [x] **Make a request to your chosen endpoint and implement a RecyclerView to display all entries**
- [x] **Use Glide to load and display at least one image per entry**
- [x] **Click on an entry to view specific details about that entry using Intents**

## Optional Features
- [x] **Add another API call and RecyclerView that lets the user interact with different data.**
  - The detail screen calls `person/{person_id}/movie_credits` and shows the person's filmography (cast + crew, most popular first) in a horizontal RecyclerView. Tapping a movie opens a dialog with its year, role, and overview.
- [x] **Add rounded corners to the images using the Glide transformations**
  - `CenterCrop()` + `RoundedCorners()` on every image; the placeholder/error drawables are rounded to match.
- [x] **Implement a shared element transition when user clicks into the details of a movie**
  - The person's photo animates from the list into the detail screen (`ActivityOptionsCompat.makeSceneTransitionAnimation` with a per-person `transitionName`). The transition is postponed until Glide has loaded the photo.

## Additional Features
- [x] Day / Week toggle for the `time_window` (defaults to Week, survives rotation)
- [x] Loading spinners, plus error and empty states with a Retry button (list) and inline messages (Known For / Filmography)
- [x] Detail screen also shows department, popularity, and the "known for" titles with posters, ratings, and overviews
- [x] Portrait list / landscape 2-column grid, edge-to-edge insets handled on both screens
- [x] TMDB API key kept out of source control (`local.properties` → `BuildConfig`); all UI text in `strings.xml`
- [x] Unit tests for JSON parsing and the model helpers (`TmdbModelsTest`)

## Video Walkthrough
<img src='walkthrough.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with LiceCap

## Notes
- TMDB currently returns an empty `known_for` array for every result of `trending/person`, so the list shows "No known titles yet." for everyone. Adding the second API call (`movie_credits`) on the detail screen made sure there is always real filmography data to show.
- Trending people can have no `profile_path` and credits can have blank characters or dates, so the models treat those fields as nullable and fall back to placeholder images and text.
- For the shared element transition, the detail photo loads asynchronously, so the enter transition is postponed until Glide reports the image (or error drawable) is ready. Otherwise the animation would run into an empty ImageView.

## Setup
Add your TMDB (v3) API key to `local.properties` (this file is git-ignored):

```
TMDB_API_KEY=your_key_here
```

Then open the project folder (the one containing `settings.gradle`) in Android Studio, let Gradle sync, and run the `app` configuration.

## License
Copyright 2026 Joshua Akeredolu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
