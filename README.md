# TreeShade

<p align="center">
  <img src="docs/treeshade-icon.png" alt="TreeShade icon: an oak tree with letters R and D enjoying the shade" width="192" />
</p>

Rider plugin that hides folders and files you do not need to see in the **File System** and **Solution** explorers, and keeps the remaining entries sorted clearly.

## Features

- Hide folders and files by exact name (per project)
- Applies to Rider’s File System explorer and Solution explorer
- Remaining entries are sorted **folders first**, then **files**, each group alphabetically (case-insensitive)
- Settings persist per project in `treeshade.xml`

## Default hide lists

**Folders:** `bin`, `obj`, `lib`, `.vscode`, `.idea`

**Files:** `.DS_Store`

## Configuration

Open **Settings → Tools → TreeShade**.

Enter one name per line for hidden folders and hidden files. Names match the entry name only (not a full path). After you apply, the project view refreshes.

## Requirements

- JetBrains Rider (build `262+`, developed against Rider 2026.2)
- JDK 21 for building from source

## Build

```bash
gradle buildPlugin
```

The installable package is written to:

```text
build/distributions/TreeShade-<version>.zip
```

## Install from disk

1. Build the plugin (or use an existing zip from `build/distributions/`).
2. In Rider: **Settings → Plugins → ⚙ → Install Plugin from Disk…**
3. Select the `TreeShade-*.zip` file and restart if prompted.

## License

See [LICENSE](LICENSE).
