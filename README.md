# Hidato Game

Implementació d'escriptori de l'**Hidato**, el trencaclosques numèric de camins, escrita en Java 21 amb interfície gràfica Swing. El jugador omple un tauler amb nombres consecutius des de l'`1` fins al valor màxim, de manera que cada parell de nombres consecutius quedi en cel·les adjacents.

El joc admet tres topologies de tauler (quadrats, triangles, hexàgons), dues regles d'adjacència, generació aleatòria de trencaclosques, un solucionador per backtracking, mode competitiu amb rànquing global, pistes, partides desades i repositoris d'hidatos gestionats per l'usuari.

> Projecte desenvolupat per a l'assignatura **PROP** (Projectes de Programació, quadrimestre de primavera 2026, UPC-FIB). Grup 12, subgrup 12.4.

## Taula de continguts

1. [Característiques](#característiques)
2. [Requisits](#requisits)
3. [Inici ràpid](#inici-ràpid)
4. [Com jugar](#com-jugar)
5. [Format de fitxer d'Hidato](#format-de-fitxer-dhidato)
6. [Dificultat i puntuació](#dificultat-i-puntuació)
7. [Arquitectura](#arquitectura)
8. [Algorismes](#algorismes)
9. [Persistència](#persistència)
10. [Estructura del repositori](#estructura-del-repositori)
11. [Proves](#proves)
12. [Documentació](#documentació)
13. [Limitacions conegudes](#limitacions-conegudes)
14. [Equip](#equip)

## Característiques

| Àrea | Què fa |
|------|--------|
| Topologies | Quadrats (`Q`), triangles (`T`), hexàgons (`H`) |
| Adjacència | Només costats (`C`) o costats + vèrtexs (`CA`). Els hexàgons només admeten costats. |
| Generació | Trencaclosques aleatoris resolubles a partir de topologia, adjacència, dificultat i nombre de cel·les (de 3 a 200) |
| Resolució | Solucionador automàtic amb comprovació de resolubilitat i informe del temps de CPU |
| Validació | Detecta trencaclosques irresolubles i solucions incorrectes de l'usuari |
| Joc interactiu | Tauler que s'omple fent clic, selector de nombre, cronòmetre amb pausa/represa, pistes |
| Mode Casual | Carrega un hidato desat, reprèn una partida desada o comença'n un d'aleatori |
| Mode Ranked | Partida aleatòria per dificultat. Una solució correcta suma puntuació al rànquing global. |
| Importar / crear | Enganxa un hidato en format CSV o carrega un fitxer `.csv` |
| Repositoris | Crea i elimina llistes personals d'hidatos. La llista compartida `all` és de només lectura. |
| Desar / carregar | Desa una partida (tauler, temps transcorregut, pistes usades) i reprèn-la més tard |
| Rànquing | Els 100 millors usuaris per puntuació total en mode competitiu |
| Usuaris | Registre i inici de sessió. Cada usuari té la seva puntuació i les seves partides desades. |

## Requisits

- **JDK 21**
- Terminal Linux o macOS (el script `gradlew` inclòs s'usa per compilar)
- Cap instal·lació manual de dependències. Gradle descarrega automàticament:
  - Gson 2.8.9
  - Jackson Databind 2.17.0
  - JUnit 4.13.1 i Mockito 4.11.0 (només per a proves)

## Inici ràpid

```bash
# 1. Extreu el projecte
mkdir ~/hidatogame && tar -xzf ~/Downloads/<filename>.tar.gz -C ~/hidatogame

# 2. Compila i executa
cd ~/hidatogame/FONTS
./gradlew build && ./gradlew run
```

S'obre una finestra d'inici de sessió. Registra un usuari i inicia sessió.

### Jar precompilat

```bash
cd EXE
java -jar hidatoGame.jar
```

Executa'l des del directori `EXE`. L'aplicació llegeix i escriu la carpeta relativa `data/` (vegeu [Persistència](#persistència)). Si el jar falla al teu sistema, usa l'ordre de Gradle anterior.

## Com jugar

Després d'iniciar sessió, el menú principal ofereix sis modes i el tancament de sessió.

| Opció del menú | Finalitat |
|----------------|-----------|
| **Play Casual** | Load Hidato (d'una llista), Load Match (partida desada) o Start Solving (hidato aleatori nou per dificultat) |
| **Play Ranked** | Hidato aleatori per dificultat. Si el resols correctament, sumes puntuació. |
| **Generate Hidato** | Tria topologia, adjacència, dificultat i nombre de cel·les, i juga el resultat |
| **Create/Import Hidato** | Enganxa text CSV o selecciona un fitxer, i prem *Play Hidato* |
| **View Ranking** | Els 100 millors usuaris per puntuació |
| **Consult Lists** | Gestiona repositoris d'hidatos i juga-hi |

### Controls dins la partida

- **Start / Pause**: controla el cronòmetre. El tauler s'amaga mentre és en pausa.
- **Selector de nombre (`<` `>`)**: tria el nombre que rebrà la pròxima cel·la buida clicada.
- **Clic en una cel·la buida**: hi col·loca el nombre seleccionat. **Clic en una cel·la plena**: l'esborra.
- **Hint**: revela una cel·la correcta. Les cel·les de pista són cian i no es poden editar. Les pistes són il·limitades però redueixen la puntuació competitiva.
- **Machine Solve**: comprova la resolubilitat, resol el tauler i mostra el temps de CPU. Després no s'atorga puntuació.
- **Save & Exit**: desa la partida. Una partida competitiva desada així ja no pot atorgar puntuació.
- **Back**: torna al menú.

Quan totes les cel·les del camí estan plenes, el joc indica si la solució és correcta. Si és incorrecta, el temporitzador continua i pots corregir-la.

### Comportament del temporitzador

- Comença automàticament: *Load Match* i *Start Solving* (Casual i Ranked).
- Espera el botó Start: *Load Hidato*, *Consult Lists*, *Generate Hidato*, *Create/Import Hidato*.

## Format de fitxer d'Hidato

CSV, separat per comes.

**La línia 1** és la capçalera: `tipus de cel·la, adjacència, files, columnes`

| Camp | Valors |
|------|--------|
| Tipus de cel·la | `Q` quadrat, `T` triangle, `H` hexàgon |
| Adjacència | `C` costats, `CA` costats + vèrtexs |
| Files / columnes | Enters |

**Les `files` línies següents** contenen els valors de les cel·les:

| Símbol | Significat |
|--------|------------|
| enter | Nombre fix |
| `#` | Cel·la fora de l'àrea de l'hidato |
| `*` | Forat (cel·la inaccessible) |
| `?` | Cel·la buida per omplir |

Exemple (quadrats, costats + vèrtexs, 5x5):

```csv
Q,CA,5,5
#,#,1,#,#
#,?,*,?,#
8,?,?,?,3
#,?,11,*,#
#,#,?,#,#
```

Convencions de disposició:

- **Quadrats**: matriu simple.
- **Hexàgons**: hexàgons verticals. Les files parelles es desplacen la mida de l'apotema.
- **Triangles**: les files senars comencen amb la base a baix, les parelles amb la base a dalt. Afegeix files tot `#` quan calgui.

L'importador rebutja fitxers mal formats, per exemple una discrepància de dimensions com una graella 5x5 declarada que conté dades 3x4.

## Dificultat i puntuació

La dificultat fixa la fracció de cel·les del camí que amaga el generador. El primer nombre (`1`) sempre es manté.

| Dificultat | Cel·les buides | Puntuació inicial | Penalització per pista | Penalització per temps |
|------------|----------------|-------------------|------------------------|------------------------|
| Easy | aprox. 25% | 1000 | 100 per pista | 1 per segon |
| Medium | aprox. 50% | 2000 | 200 per pista | 2 per segon |
| Hard | aprox. 75% | 3000 | 300 per pista | 3 per segon |

`puntuació = puntuació inicial - pistes x penalització per pista - segons x penalització per temps`

Exemple: Medium, resolt en 01:15 sense pistes dona `2000 - 0 - 75 x 2 = 1850`.

Només s'atorga puntuació en una partida competitiva jugada completament i resolta correctament.

## Arquitectura

Arquitectura de tres capes. Cada capa només parla amb la de sota a través d'un controlador.

```
presentation  (vistes Swing + PresentationController)
      |
   domain     (DomainController, model, algorismes)
      |
 persistence  (interfície DataManager + implementacions JSON)
```

### Presentació (`presentation`)

- `Main`: punt d'entrada.
- `PresentationController`: pont entre les vistes i el domini.
- Vistes: `ViewLogin`, `ViewMenu`, `ViewPlayCasual`, `ViewGenerateHidato`, `ViewCreateHidato`, `ViewHidatoSolving`, `ViewConsultList`, `ViewRanking`.
- `HidatoBoard`: component propi que dibuixa taulers de quadrats, triangles i hexàgons.

### Domini (`domain`)

- `domain.controller`
  - `DomainController`: façana singleton per a la capa de presentació.
  - `HidatoController`, `UserController`: lògica d'hidatos i d'usuaris.
- `domain.model`
  - `Hidato` (abstracta) amb `Square`, `Triangle`, `Hexagon`: cadascuna defineix la seva pròpia geometria d'adjacència.
  - `Cell`, `User`, `GameSession`.
- `domain.enumeration`: `Adjacency` (`C`, `CA`), `Difficulty` (`EASY`, `MEDIUM`, `HARD`).
- `domain.controller.solvingAlgorithm`: `SolvingAlgorithm` (abstracta) i `Backtraquing`.
- `domain.controller.generationAlgorithm`: `GenerationAlgorithm` (abstracta) i `BasicGenerator`.

### Persistència (`persistence`)

- `DataManager<T, ID>` i `DataListManager`: interfícies d'emmagatzematge.
- `JsonManager`: magatzem JSON genèric.
- `JsonHidatoManager`, `JsonUserManager`, `JsonGameSessionManager`: magatzems concrets.

### Extensibilitat

Els solucionadors i generadors estan darrere de classes abstractes (`SolvingAlgorithm`, `GenerationAlgorithm`), i l'emmagatzematge darrere d'interfícies. Un algorisme nou o un backend d'emmagatzematge nou és una subclasse o implementació nova. Les classes existents no canvien.

Les topologies noves segueixen el mateix patró: estendre `Hidato` i implementar `getAdjacents`.

## Algorismes

### Solucionador (`Backtraquing`)

1. L'hidato es converteix en un graf. Cada cel·la que no és forat és un node, i les arestes surten de la topologia i la regla d'adjacència.
2. Els valors prefixats es col·loquen en un vector de camí. El solucionador rebutja el trencaclosques d'entrada quan:
   - un valor supera el nombre de cel·les del camí,
   - un valor apareix dues vegades,
   - falta el primer o l'últim valor,
   - un node té més veïns dels que permet la topologia,
   - dos valors prefixats consecutius no són adjacents.
3. El backtracking recursiu omple cada posició buida del camí a partir dels veïns del nombre anterior, i desfà l'elecció davant un carreró sense sortida.
4. `hasSolution`, `isSolution` i `solve` comparteixen aquest model. `solve` retorna un `GameSession` amb el tauler resolt i el temps de CPU, o `null` si és irresoluble.

### Generador (`BasicGenerator`)

1. Comença en una cel·la aleatòria amb valor `1`.
2. Col·loca `2, 3, ...` en un veí lliure aleatori. El tauler creix quan el recorregut surt de la matriu actual.
3. Reintenta fins a 30 vegades i conserva el millor intent si no s'assoleix la longitud completa.
4. `makeSolvable` amaga un conjunt aleatori de valors (mai l'`1` ni l'últim valor) segons la dificultat.

Els hidatos generats són sempre resolubles per construcció. El generador accepta de 3 a 200 cel·les.

## Persistència

Totes les dades són JSON o CSV dins una carpeta `data/`, relativa al directori de treball.

| Ruta | Contingut |
|------|-----------|
| `data/users/<username>.json` | Nom d'usuari, contrasenya, puntuació total, partides desades |
| `data/hidato/all/<uuid>.csv` | Hidato en el format CSV anterior |
| `data/hidato/lists/<list>.json` | Repositoris d'hidatos (vectors d'UUID d'hidatos). `all.json` és la llista compartida. |
| `data/gamesession/all/<uuid>.json` | Partides desades |

La carpeta `data/` es crea en la primera execució. Executa l'aplicació sempre des del mateix directori per conservar les dades.

## Estructura del repositori

```
.
├── FONTS/               Codi font i compilació Gradle
│   ├── build.gradle
│   ├── settings.gradle
│   ├── gradlew, gradlew.bat
│   └── src/
│       ├── presentation/
│       ├── domain/
│       └── persistence/
├── EXE/                 Jar executable, dades d'exemple i jocs de prova
│   ├── hidatoGame.jar
│   ├── data/
│   └── test_sets/
├── DOCS/                Manual d'usuari, classes per membre, documentació de proves
├── javadoc/             Documentació de l'API generada (obre javadoc/index.html)
├── equip.txt            Membres de l'equip
└── index.txt            Índex de directoris
```

## Proves

Els jocs de prova manuals són a `EXE/test_sets/`, una carpeta per cada prova de `DOCS/test_sets.pdf`.

| Carpeta | Cobreix |
|---------|---------|
| `T2.1` / `T2.2` | Hidatos resolubles / irresolubles per al solucionador automàtic |
| `T3-T5` | Validació i joc interactiu, totes les topologies i adjacències |
| `T6` | Control del temps (sessions i hidatos desats) |
| `T7` | Rànquing, diversos usuaris |
| `T8.1`, `T8.5` | Crear, desar i carregar sessions de joc |
| `T9.1` a `T9.3` | Mode Ranked, easy / medium / hard |
| `T9.4` a `T9.6` | Mode Casual, easy / medium / hard |
| `T9.7` / `T9.8` | Pistes en un tauler gairebé buit / completat incorrectament |

Per reproduir una prova, carrega els fitxers amb **Create/Import Hidato** (hidatos) o copia'ls a `data/` (sessions i usuaris). Cada carpeta té un `description.txt`.

`build.gradle` està configurat per a JUnit 4 i Mockito, amb `../EXE` com a directori de codi de proves. Executa amb:

```bash
cd FONTS
./gradlew test
```

## Documentació

- `DOCS/user_manual.pdf`: guia d'usuari completa amb captures i missatges d'error.
- `DOCS/classes_per_member.pdf`: qui va implementar cada classe.
- `DOCS/test_sets.pdf`: pla de proves i resultats.
- `javadoc/`: documentació de l'API de tots els paquets. Obre `javadoc/index.html`.

## Limitacions conegudes

- Les contrasenyes es desen en text pla a `data/users/*.json`. No hi reutilitzis una contrasenya real.
- El solucionador és backtracking simple, sense heurístiques de poda. Els taulers grans i poc omplerts poden trigar molt.
- El generador construeix el camí amb un recorregut aleatori i pot quedar-se curt respecte al nombre de cel·les demanat. Conserva el millor intent de 30.
- Els taulers d'hexàgons només admeten adjacència per costats.
- No hi ha comprovació de solució única. Un trencaclosques generat té almenys una solució, no exactament una.
- Les rutes de dades són relatives al directori de treball. Si inicies l'aplicació des d'una altra carpeta, es crea un `data/` nou.
- El jar precompilat està documentat per a sistemes basats en Linux.

## Equip

Grup 12, subgrup 12.4.

| Nom | Contacte |
|-----|----------|
| Mohamed Aghzaf El Hachimi | mohamed.aghzaf@estudiantat.upc.edu |
| Félix Cifuentes Salamí | felix.cifuentes@estudiantat.upc.edu |
| Rafel Miquel Mitjans | rafel.miquel@estudiantat.upc.edu |
| Joan Pere Ollers Rigo | joan.pere.ollers@estudiantat.upc.edu |
