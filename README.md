# JalebiTPA

TPA plugin for **Jalebi SMP** (Paper 1.21+, Java 21).

## Commands
| Command | What it does |
|---|---|
| `/tpa <player>` | Opens the confirm GUI; on send, you teleport to them if they accept |
| `/tpahere <player>` | Opens the confirm GUI; on send, they teleport to you if they accept |
| `/tpaccept` | Accept the request sent to you |
| `/tpacancel` | Deny the request sent to you (alias `/tpdeny`) |

After accepting, both players see a small-caps action bar countdown
`ᴛᴇʟᴇᴘᴏʀᴛɪɴɢ ɪɴ 5 ꜱᴇᴄᴏɴᴅꜱ` (number in `#00a2ff`) and the teleport happens after 5 seconds.
Requests expire after 60 seconds.

## GUI
Red pane = cancel · Grass/Netherrack/End stone = target's dimension · Player head = target ·
Feather = kills & deaths · Lime pane = send.

## Build
```
mvn package
```
The jar is in `target/JalebiTPA.jar`. Drop it into `plugins/`.

## GitHub
```
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/<you>/JalebiTPA.git
git push -u origin main
```
GitHub Actions builds the jar on every push (Actions tab → Artifacts).
Push a tag like `v1.0.0` to publish it as a Release.
