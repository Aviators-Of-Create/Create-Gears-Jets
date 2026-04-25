# Contributing

## Before You Start

- Use Java `21`.
- Target Minecraft `1.21.1` and the dependency versions defined in `gradle.properties`.
- Open an issue before starting large gameplay or balance changes so the direction is clear.

## Local Setup

```powershell
./gradlew.bat build
./gradlew.bat runClient
```

On macOS or Linux, use `./gradlew`.

## Project Expectations

- Keep changes focused and easy to review.
- Follow the existing package structure under `src/main/java/dev/aviatorsofcreate/gearsandjets`.
- Update assets, tags, localization, and loot tables when new content requires them.
- If behavior changes, include a short explanation in the pull request describing player-facing impact.

## Pull Requests

- Reference the related issue when one exists.
- Use clear commit messages and a concise PR description.
- Make sure `./gradlew build` passes before opening the PR.
- Include screenshots or short videos for visible content changes when practical.

## Reporting Bugs

When filing a bug, include:

- Minecraft version
- NeoForge version
- Create version
- Sable version
- Steps to reproduce
- Expected result
- Actual result
