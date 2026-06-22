# NeoAssist

A client-side survival utility mod for **Minecraft 1.21.1 / NeoForge**, featuring a draggable
ClickGUI, per-module keybinds, JSON config persistence, and a large set of survival-focused modules.

## Quick start

- **Open the ClickGUI:** press `Right Shift` (press again or `Esc` to close).
- **Bind a module:** expand a module (right-click its name) and click the **Bind** row, then press a key.
- **Toggle a module:** left-click its name in the GUI, or press its bound key in game.
- Settings and toggles are saved to `.minecraft/neoassist/config.json`.

## ClickGUI UX

- Drag a category panel by its header; right-click the header to collapse it.
- Left-click a module to toggle, right-click to expand its settings.
- Setting types: toggle, slider, mode dropdown, RGBA color picker, key bind, and a **block selector**.
- **Block ESP** targets are chosen entirely in the GUI via a searchable block list
  (open `World > BlockESP > Blocks > Edit`).

## Modules

| Category | Modules |
|----------|---------|
| Combat   | KillAura, AutoTotem, AutoClicker, AntiKnockback |
| Player   | AutoEat, AutoTool, AutoArmor, ChestStealer, FastPlace, AutoRespawn, AutoFish, NoFall |
| Movement | AutoSprint, Step, Sneak, Spider, AutoWalk, HighJump |
| World    | BlockESP (in-GUI block picker) |
| Render   | Fullbright, Zoom, HUD (ArrayList / coords / FPS), Tracers, EntityESP, NoHurtCam |
| Misc     | AntiAFK |

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
