# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

TinySettings is a PaperMC plugin for Minecraft 1.21 (`api-version: '1.21'`), targeting Java 21. The project is in its initial scaffold — `TinySettings.java` has empty `onEnable`/`onDisable` and no commands, listeners, or config yet.

## Build

```bash
mvn clean package
```

Produces a shaded JAR in `target/`. The default goal is `clean package`, so plain `mvn` works too. Drop the JAR into a Paper server's `plugins/` directory to test.

The `paper-api` dependency is `provided` — it's supplied by the server at runtime and must not be bundled. Any new runtime deps that should be bundled rely on `maven-shade-plugin` (already configured, no relocations set).

Resource filtering is enabled, so `${project.version}` etc. in `plugin.yml` are substituted from `pom.xml` at build time.

## Architecture

Standard Bukkit/Paper plugin layout:

- `src/main/java/me/pondhalf/project/tinySettings/TinySettings.java` — `JavaPlugin` entry point declared as `main:` in `plugin.yml`. Register commands, listeners, and config loading from `onEnable`.
- `src/main/resources/plugin.yml` — Paper plugin descriptor. Add `commands:`, `permissions:`, and `depend:`/`softdepend:` here as features are added.

When adding commands or events, keep the `me.pondhalf.project.tinySettings` package as the root and update `plugin.yml` accordingly — `plugin.yml` is the source of truth for what the server loads, not annotations.
