# NeoAssist

A client-side survival utility mod for **Minecraft 1.21.1 / NeoForge**, featuring a draggable
ClickGUI, per-module keybinds, JSON config persistence, and a large set of survival-focused modules.

## Quick start

- **Open the ClickGUI:** press `Right Shift` (press again or `Esc` to close).
- **Bind a module:** expand a module (right-click its name) and click the **Bind** row, then press a key.
- **Toggle a module:** left-click its name in the GUI, or press its bound key in game.
- Settings, toggles, and keybinds are auto-saved to `.minecraft/neoassist/config.json`.

## ClickGUI UX

- Drag a category panel by its header; right-click the header to collapse it.
- Left-click a module to toggle, right-click to expand its settings.
- Use **Show in HUD** on expanded modules to hide noisy modules from the ArrayList.
- Setting types: toggle, slider, mode dropdown, RGBA color picker, key bind, and a **block selector**.
- **Block ESP** targets are chosen entirely in the GUI via a searchable block list
  (open `World > BlockESP > Blocks > Edit`); use **Add shown** to bulk-add search results.

## Modules

| Category | Modules |
|----------|---------|
| Combat   | KillAura, AutoTotem, AutoClicker, AntiKnockback, Criticals, Reach, BowAimbot |
| Player   | AutoEat, AutoTool, AutoArmor, ChestStealer, FastPlace, AutoRespawn, AutoFish, NoFall |
| Movement | AutoSprint, Step, Sneak, Spider, AutoWalk, HighJump, Flight, Jesus, NoSlow |
| World    | BlockESP (in-GUI block picker), Nuker, Scaffold, AutoReplant |
| Render   | Fullbright, Zoom, HUD (ArrayList / coords / direction / ping / FPS), Tracers, EntityESP, NoHurtCam, Chams, StorageESP, Trajectories, NameTags |
| Misc     | AntiAFK, AutoReconnect, Timer, FakeLag |

## Compatibility

Render features use NeoForge events (`RenderLevelStageEvent` / `RenderGuiEvent`) rather than
rendering mixins, so they coexist with Sodium/Iris/Embeddium. Only NoSlow, Timer, NameTags and
FakeLag use mixins; each is non-destructive (`@ModifyConstant` / `@Redirect` / `@Inject`), targets a
stable vanilla class, has `defaultRequire = 0`, and is a complete no-op while its module is disabled.

## Building

Requires JDK 21.

```bash
./gradlew build
```

The built mod jar is produced at `build/libs/neoassist-<version>.jar`. Drop it into your
NeoForge 1.21.1 `mods` folder.

To launch a dev client:

```bash
./gradlew runClient
```
