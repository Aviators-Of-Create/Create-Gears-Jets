# Create: Gears and Jets

[![Build](https://img.shields.io/github/actions/workflow/status/Aviators-Of-Create/Create-Gears-Jets/build.yml?branch=master&label=build)](https://github.com/Aviators-Of-Create/Create-Gears-Jets/actions/workflows/build.yml)
![Version](https://img.shields.io/badge/version-1.0.0-blue)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-3C8527)
![NeoForge](https://img.shields.io/badge/NeoForge-21.1.225-orange)
[![License](https://img.shields.io/badge/license-code%20MIT%20%7C%20assets%20ARR-lightgrey)](LICENSE.txt)

<div align="center">
    <a href="https://github.com/ryanhcode/sable">
        <img alt="powered by sable" src="https://raw.githubusercontent.com/Creators-of-Aeronautics/Simulated-Project/refs/heads/main/images/sable_512h.png" width="250">
    </a>
</div>

Create: Gears and Jets is a NeoForge add-on for Minecraft 1.21.1 that expands Create-based aircraft builds with early jet-engine parts and an airplane seat.

## What the Project Does

The mod currently adds:

- `Simple Intake`
- `Simple Combustion Chamber`
- `Simple Exhaust`
- `Airplane Seat`

Together, the intake, combustion chamber, and exhaust form a simple jet-engine multiblock. The combustion chamber stores fluid, reacts to redstone strength, updates its machine state (`off`, `idling`, `running`), and exposes thrust data through Sable's propeller actor interfaces. The airplane seat is tagged as a Create seat and supports riding on moving contraptions.

## Compatibility

- Minecraft `1.21.1`
- Java `21`
- NeoForge `21.1.225+`
- Create `6.0.9+`
- Sable `1.0.4+`
- Recommended for packs built around Create: Aeronautics

## Getting Started

### Install the Mod

1. Install NeoForge for Minecraft `1.21.1`.
2. Add the required dependencies to your `mods` folder:
   - Create
   - Sable
3. Add the `Create: Gears and Jets` jar.
4. For the aircraft-focused experience this add-on is designed around, also include your Create: Aeronautics setup.

### In-Game Quick Start

1. Place a `Simple Combustion Chamber` facing the direction you want the engine to push.
2. Attach a `Simple Intake` to the front of the chamber.
3. Attach a `Simple Exhaust` to the back of the chamber.
4. Fill the engine with fuel through the combustion chamber.
5. Apply a redstone signal:
   - signal `1-3`: idling
   - signal `4-15`: running
6. Use the `Airplane Seat` anywhere you need a Create-compatible seat on an aircraft or contraption.

## Project Layout

- `src/main/java/dev/aviatorsofcreate/gearsandjets`: mod entrypoints, registries, config, and gameplay logic
- `src/main/java/dev/aviatorsofcreate/gearsandjets/content`: blocks, block entities, items, and interfaces
- `src/main/resources`: assets, language files, tags, loot tables, and mod metadata templates
- `.github/workflows/build.yml`: CI build pipeline

## Getting Help

- Report bugs or request features through [GitHub Issues](https://github.com/Aviators-Of-Create/Create-Gears-Jets/issues).
- Review the source in `src/main/java/dev/aviatorsofcreate/gearsandjets` for implementation details.

When opening an issue, include the Minecraft, NeoForge, Create, and Sable versions you tested with, plus clear reproduction steps.

## License

Source code and documentation are available under MIT, while bundled visual and artistic assets are All Rights Reserved unless stated otherwise. See LICENSE.