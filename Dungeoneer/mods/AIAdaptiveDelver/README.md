AIAdaptiveDelver (Tech-compatible mod)

- Declares generator 'AI_ADAPTIVE_DELAUNAY' under `generator/AIAdaptive/info.dat`
- Provides spawn overlay in `data/spawn_overrides.json`
- Runtime knobs (via engine SpawnController): enemy_density_scale, item_density_scale, spawn_budget_factor
- Per-biome and per-roomTag scales/caps supported

Install:
- Copy AIAdaptiveDelver folder into `Dungeoneer/mods`

Select:
- New Game auto-selects the Delaunay-based generator when this mod is active

Dev:
- Editing `data/spawn_overrides.json` will be picked up on the next generated level
