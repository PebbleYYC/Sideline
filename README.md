# Sideline

A clean, native Android scores app for NHL, MLB, NBA and IIHF — built on
theScore's public data API, with no ads, betting, or tracking. Jetpack
Compose + Retrofit.

## Getting the APK (no Android Studio needed)

This repo builds itself on GitHub Actions. On every push to `main`:

1. Go to the **Actions** tab → open the latest "Build APK" run.
2. Download the **`sideline-debug-apk`** artifact (a zip containing
   `app-debug.apk`), or grab it from the **Releases** page under the
   `latest` tag.
3. Copy the APK to your phone and install it (allow "install unknown
   apps"). It's a debug build, signed with the standard debug key.

To trigger a build without pushing: Actions tab → "Build APK" → **Run
workflow**.

## Design

Apple-Sports-inspired: true-black background, team-tinted score cards,
large bold numerals, the losing team dimmed so the result reads at a
glance, favorites highlighted in orange. Two-weight typography, one
accent.

- Favorites are set in `ScoresScreen.kt` (`FAVORITES` — team
  abbreviations). Wire this to real preferences later.
- Colors/typography live in `ui/theme/`.

## Data — theScore API

Requests go to `https://api.thescore.com/{league}/events`. See
`data/api/`:

- `TheScoreApi.kt` — the Retrofit interface and base URL.
- `Dtos.kt` — response models. **Verify these against a live response
  before trusting them.** Fetch `https://api.thescore.com/nhl/events` in
  a browser and confirm the field names; theScore renames fields
  occasionally and some are league-specific. Everything is nullable, so a
  mismatch degrades gracefully instead of crashing — but you'll see empty
  data until the mapping in `ScoresRepository.kt` matches reality.
- League slugs are in `data/model/UiModels.kt`. `nhl`, `mlb`, `nba` are
  stable; **IIHF has no single slug** — theScore files those under
  tournament slugs (e.g. `wjc` for World Juniors). Set `League.IIHF`'s
  slug to whatever tournament is live.

### Note on the API

This is theScore's private backend, not an official/sanctioned API. It
works well and has for years, but it can change or block access without
notice, and the sports data underneath is licensed. This project is for
personal use — don't redistribute it publicly or present it as theScore.

## Stack

- Kotlin, Jetpack Compose (Material 3)
- Retrofit + Moshi + OkHttp
- Coil (team logos, loaded from theScore's logo URLs with a colored-circle
  fallback)
- ViewModel + StateFlow + coroutines
- minSdk 26, targetSdk 34
