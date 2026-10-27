# Hidato Game

A desktop implementation of **Hidato**, the numeric path puzzle, written in Java 21 with a Swing GUI. The player fills a board with consecutive numbers from `1` to the maximum value so that every pair of consecutive numbers sits on adjacent cells.

The game supports three board topologies (squares, triangles, hexagons), two adjacency rules, random puzzle generation, a backtracking solver, ranked play with a global leaderboard, hints, saved matches and user-managed puzzle repositories.

> Project developed for the **PROP** course (Projectes de Programació, spring 2026, UPC-FIB). Group 12, subgroup 12.4.

## Table of contents

1. [Features](#features)
2. [Requirements](#requirements)
3. [Quick start](#quick-start)
4. [How to play](#how-to-play)
5. [Hidato file format](#hidato-file-format)
6. [Difficulty and scoring](#difficulty-and-scoring)
7. [Architecture](#architecture)
8. [Algorithms](#algorithms)
9. [Persistence](#persistence)
10. [Repository layout](#repository-layout)
11. [Testing](#testing)
12. [Documentation](#documentation)
13. [Known limitations](#known-limitations)
14. [Team](#team)

## Features

| Area | What it does |
|------|--------------|
| Topologies | Squares (`Q`), triangles (`T`), hexagons (`H`) |
| Adjacency | Sides only (`C`) or sides + vertices (`CA`). Hexagons only support sides. |
| Generation | Random solvable puzzles from topology, adjacency, difficulty and number of cells (3 to 200) |
| Solving | Machine solver with solvability check and CPU time report |
| Validation | Detects unsolvable puzzles and incorrect user solutions |
| Interactive play | Click-to-fill board, number selector, pause/resume chronometer, hints |
| Casual mode | Load a stored hidato, resume a saved match, or start a random one |
| Ranked mode | Random match by difficulty. A correct solution adds score to the global ranking. |
| Import / create | Paste a hidato in CSV format or load a `.csv` file |
| Repositories | Create and delete personal lists of hidatos. The shared `all` list is read-only. |
| Save / load | Save a match (board, elapsed time, hints used) and resume it later |
| Ranking | Top 100 users by total ranked score |
| Users | Register and login. Each user has its own score and saved matches. |

## Requirements

- **JDK 21**
- Linux or macOS terminal (the bundled `gradlew` script is used for building)
- No manual dependency install. Gradle downloads these automatically:
  - Gson 2.8.9
  - Jackson Databind 2.17.0
  - JUnit 4.13.1 and Mockito 4.11.0 (test scope)

## Quick start

```bash
# 1. Extract the project
mkdir ~/hidatogame && tar -xzf ~/Downloads/<filename>.tar.gz -C ~/hidatogame

# 2. Build and run
cd ~/hidatogame/FONTS
./gradlew build && ./gradlew run
```

A login window opens. Register a user and log in.

### Prebuilt jar

```bash
cd EXE
java -jar hidatoGame.jar
```

Run it from the `EXE` directory. The app reads and writes the relative `data/` folder (see [Persistence](#persistence)). If the jar fails on your system, use the Gradle command above.

## How to play

After login, the main menu offers six modes plus logout.

| Menu option | Purpose |
|-------------|---------|
| **Play Casual** | Load Hidato (from a list), Load Match (saved game), or Start Solving (new random hidato by difficulty) |
| **Play Ranked** | Random hidato by difficulty. Solving it correctly adds to your score. |
| **Generate Hidato** | Choose topology, adjacency, difficulty and number of cells, then play the result |
| **Create/Import Hidato** | Paste CSV text or select a file, then press *Play Hidato* |
| **View Ranking** | Top 100 users by score |
| **Consult Lists** | Manage hidato repositories and play from them |

### In-game controls

- **Start / Pause**: controls the chronometer. The board is hidden while paused.
- **Number selector (`<` `>`)**: picks the number that the next clicked empty cell will receive.
- **Click an empty cell**: places the selected number. **Click a filled cell**: clears it.
- **Hint**: reveals one correct cell. Hint cells are cyan and cannot be edited. Hints are unlimited but reduce ranked score.
- **Machine Solve**: checks solvability, solves the board and shows CPU time. No score is awarded afterwards.
- **Save & Exit**: stores the match. A ranked match saved this way can no longer award score.
- **Back**: returns to the menu.

When every path cell is filled, the game reports whether the solution is correct. If it is wrong, the timer keeps running and you can fix it.

### Timer behaviour

- Starts automatically: *Load Match* and *Start Solving* (Casual and Ranked).
- Waits for the Start button: *Load Hidato*, *Consult Lists*, *Generate Hidato*, *Create/Import Hidato*.

## Hidato file format

CSV, comma separated.

**Line 1** is the header: `cell type, adjacency, rows, columns`

| Field | Values |
|-------|--------|
| Cell type | `Q` square, `T` triangle, `H` hexagon |
| Adjacency | `C` sides, `CA` sides + vertices |
| Rows / columns | Integers |

**Next `rows` lines** hold the cell values:

| Symbol | Meaning |
|--------|---------|
| integer | Fixed number |
| `#` | Cell outside the hidato area |
| `*` | Hole (inaccessible cell) |
| `?` | Empty cell to fill |

Example (squares, sides + vertices, 5x5):

```csv
Q,CA,5,5
#,#,1,#,#
#,?,*,?,#
8,?,?,?,3
#,?,11,*,#
#,#,?,#,#
```

Layout conventions:

- **Squares**: plain matrix.
- **Hexagons**: vertical hexagons. Even rows are shifted by the apothem.
- **Triangles**: odd rows start with the base down, even rows with the base up. Pad with all-`#` rows when needed.

The importer rejects malformed files, for example a dimension mismatch such as a declared 5x5 grid that contains 3x4 data.

## Difficulty and scoring

Difficulty sets the fraction of path cells hidden by the generator. The first number (`1`) is always kept.

| Difficulty | Empty cells | Start score | Hint penalty | Time penalty |
|------------|-------------|-------------|--------------|--------------|
| Easy | about 25% | 1000 | 100 per hint | 1 per second |
| Medium | about 50% | 2000 | 200 per hint | 2 per second |
| Hard | about 75% | 3000 | 300 per hint | 3 per second |

`score = start score - hints x hint penalty - seconds x time penalty`

Example: Medium, solved in 01:15 with no hints gives `2000 - 0 - 75 x 2 = 1850`.

Score is awarded only for a correct, fully played ranked match.

## Architecture

Three-layer architecture. Each layer talks only to the one below through a controller.

```
presentation  (Swing views + PresentationController)
      |
   domain     (DomainController, model, algorithms)
      |
 persistence  (DataManager interface + JSON implementations)
```

### Presentation (`presentation`)

- `Main`: entry point.
- `PresentationController`: bridges views and the domain.
- Views: `ViewLogin`, `ViewMenu`, `ViewPlayCasual`, `ViewGenerateHidato`, `ViewCreateHidato`, `ViewHidatoSolving`, `ViewConsultList`, `ViewRanking`.
- `HidatoBoard`: custom component that draws square, triangle and hexagon boards.

### Domain (`domain`)

- `domain.controller`
  - `DomainController`: singleton facade for the presentation layer.
  - `HidatoController`, `UserController`: hidato and user logic.
- `domain.model`
  - `Hidato` (abstract) with `Square`, `Triangle`, `Hexagon`: each defines its own adjacency geometry.
  - `Cell`, `User`, `GameSession`.
- `domain.enumeration`: `Adjacency` (`C`, `CA`), `Difficulty` (`EASY`, `MEDIUM`, `HARD`).
- `domain.controller.solvingAlgorithm`: `SolvingAlgorithm` (abstract) and `Backtraquing`.
- `domain.controller.generationAlgorithm`: `GenerationAlgorithm` (abstract) and `BasicGenerator`.

### Persistence (`persistence`)

- `DataManager<T, ID>` and `DataListManager`: storage interfaces.
- `JsonManager`: generic JSON store.
- `JsonHidatoManager`, `JsonUserManager`, `JsonGameSessionManager`: concrete stores.

### Extensibility

Solvers and generators sit behind abstract classes (`SolvingAlgorithm`, `GenerationAlgorithm`), and storage sits behind interfaces. A new algorithm or a new storage backend is a new subclass or implementation. Existing classes do not change.

New topologies follow the same pattern: extend `Hidato` and implement `getAdjacents`.

## Algorithms

### Solver (`Backtraquing`)

1. The hidato becomes a graph. Each non-hole cell is a node, and edges come from the topology and adjacency rule.
2. Pre-filled values are placed into a path array. The solver rejects the puzzle early when:
   - a value exceeds the number of path cells,
   - a value appears twice,
   - the first or last value is missing,
   - a node has more neighbours than the topology allows,
   - two consecutive pre-filled values are not adjacent.
3. Recursive backtracking fills each empty path position from the neighbours of the previous number, and undoes the choice on a dead end.
4. `hasSolution`, `isSolution` and `solve` share this model. `solve` returns a `GameSession` with the solved board and CPU time, or `null` if unsolvable.

### Generator (`BasicGenerator`)

1. Start at a random cell with value `1`.
2. Place `2, 3, ...` on a random free neighbour. The board grows when the walk leaves the current matrix.
3. Retry up to 30 times and keep the best attempt if the full length is not reached.
4. `makeSolvable` hides a random set of values (never `1` and never the last value) according to difficulty.

Generated hidatos are always solvable by construction. The generator accepts 3 to 200 cells.

## Persistence

All data is JSON or CSV under a `data/` folder, relative to the working directory.

| Path | Content |
|------|---------|
| `data/users/<username>.json` | Username, password, total score, saved game sessions |
| `data/hidato/all/<uuid>.csv` | Hidato in the CSV format above |
| `data/hidato/lists/<list>.json` | Hidato repositories (arrays of hidato UUIDs). `all.json` is the shared list. |
| `data/gamesession/all/<uuid>.json` | Saved matches |

The `data/` folder is created on first run. Run the app from the same directory each time to keep your data.

## Repository layout

```
.
├── FONTS/               Source code and Gradle build
│   ├── build.gradle
│   ├── settings.gradle
│   ├── gradlew, gradlew.bat
│   └── src/
│       ├── presentation/
│       ├── domain/
│       └── persistence/
├── EXE/                 Executable jar, sample data and test sets
│   ├── hidatoGame.jar
│   ├── data/
│   └── test_sets/
├── DOCS/                User manual, classes per member, test documentation
├── javadoc/             Generated API documentation (open javadoc/index.html)
├── equip.txt            Team members
└── index.txt            Directory index
```

## Testing

Manual test sets are in `EXE/test_sets/`, one folder per test of `DOCS/test_sets.pdf`.

| Folder | Covers |
|--------|--------|
| `T2.1` / `T2.2` | Solvable / unsolvable hidatos for the machine solver |
| `T3-T5` | Validation and interactive game, all topologies and adjacencies |
| `T6` | Timer control (saved sessions and hidatos) |
| `T7` | Ranking, multiple users |
| `T8.1`, `T8.5` | Create, save and load game sessions |
| `T9.1` to `T9.3` | Ranked mode, easy / medium / hard |
| `T9.4` to `T9.6` | Casual mode, easy / medium / hard |
| `T9.7` / `T9.8` | Hints on a nearly empty board / a wrongly completed board |

To reproduce a test, load the files through **Create/Import Hidato** (hidatos) or copy them into `data/` (sessions and users). Each folder has a `description.txt`.

`build.gradle` is configured for JUnit 4 and Mockito, with `../EXE` as the test source directory. Run with:

```bash
cd FONTS
./gradlew test
```

## Documentation

- `DOCS/user_manual.pdf`: full user guide with screenshots and error messages.
- `DOCS/classes_per_member.pdf`: who implemented each class.
- `DOCS/test_sets.pdf`: test plan and results.
- `javadoc/`: API documentation for all packages. Open `javadoc/index.html`.

## Known limitations

- Passwords are stored in plain text in `data/users/*.json`. Do not reuse a real password.
- The solver is plain backtracking with no pruning heuristics. Large, sparse boards can take long.
- The generator builds a path by random walk and can stop short of the requested cell count. It keeps the best attempt out of 30.
- Hexagon boards support sides adjacency only.
- Unique-solution checking is not implemented. A generated puzzle has at least one solution, not exactly one.
- Data paths are relative to the working directory. Starting the app from another folder creates a new `data/`.
- The prebuilt jar is documented for Linux-based systems.

## Team

Group 12, subgroup 12.4.

| Name | Contact |
|------|---------|
| Mohamed Aghzaf El Hachimi | mohamed.aghzaf@estudiantat.upc.edu |
| Félix Cifuentes Salamí | felix.cifuentes@estudiantat.upc.edu |
| Rafel Miquel Mitjans | rafel.miquel@estudiantat.upc.edu |
| Joan Pere Ollers Rigo | joan.pere.ollers@estudiantat.upc.edu |
