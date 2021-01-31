# CommandCountdown
## Commands
### Main command /commandcountdown
Aliases: cc, cl
### Subcommands
#### `/cc info` - view command counter data
Two forms: For the player - `/cc info` For others - `/cc info <player_name>`
#### `/cc setlimit <count> <player_name> <command> args...` - set limit for player
Set a limit `count` for `command args...` for a player `player_name`
#### `/cc reload` - reload config
Reload config from disk
#### `/cc reset` - remove limits
Two forms:
- Remove EVERY limit on a player - `/cc reset <player_name>`
  - Limits for `player_name` will revert to defaults in config 
- For individual limits - `/cc reset <player_name> <rule>`
  - Tab-complete shows current rules available on `player_name`
#### `/cc setdefault <count> <command> args...` - set a default limit
Set a default limit `count` for `command args...`
#### `/cc resetdefault <rule>` - remove a default limit
Remove default `rule`
- Tab-complete shows current rules from config

## Config
#### Settings
```yaml
# Should arguments be treated with case sensitivity? This should stay false in most cases
args-case-sensitive: false

# Should we manually process commands for players and keep original count on failures?
## This feature prevents counts from incrementing when a command fails for a player.
## It uses the boolean result of Command#execute, which might not return false in all
## types of "failure" (no permissions, etc). If you want EVERY potential command execution
## to be counted against the player, set this to false.
keep-count-on-failure: true

# Should limits with fewer args than given also match?
# ### Warning: false not fully implemented at the moment.###
## This setting decides whether not the counters and commands run match like the following:
## -rule: cmd='auctionhouse:ah' args:[sell]
## -cmd1: /ah sell 10 5 # true = match
## -cmd2: /ah sell 10 5 # false = won't match
match-all-args: true
```
#### Default limit syntax
```yaml
default-limits:
# Rich example
  "auctionhouse:ah":
    args: ["sell", "*"]
    limit: 5
    uses:
      playerid: 0 # 0 = count
# Description: matches any alias of 'ah' from plugin 'auctionhouse'
#  with two args: 'test', and a wildcard match for the second
# Allow 5 executions of the command
#  'uses' map stores player uses; no need to create this by hand

# Simple example
  sethome:
    args: []
    limit: 1
# Description: Allow only one execution of the first match for
#  command 'sethome' with arg match according to match-all-args.
```

## Licensing
_Variably licensed under the terms of the GPLv3.0 and LGPLv3.0._
##### LGPLv3.0 components:
- [All event classes](https://github.com/ms5984/CommandCountdown/blob/master/src/main/java/com/github/ms5984/commission/commandcountdown/events/)
- [CommandCountdownAPI](https://github.com/ms5984/CommandCountdown/blob/master/src/main/java/com/github/ms5984/commission/commandcountdown/api/CommandCountdownAPI.java)
- [CommandCounter](https://github.com/ms5984/CommandCountdown/blob/master/src/main/java/com/github/ms5984/commission/commandcountdown/api/CommandCounter.java)
- [Counter.java](https://github.com/ms5984/CommandCountdown/blob/master/src/main/java/com/github/ms5984/commission/commandcountdown/model/Counter.java)
- [NullCommand.java](https://github.com/ms5984/CommandCountdown/blob/master/src/main/java/com/github/ms5984/commission/commandcountdown/model/Nullcommand.java)
##### _All other components subject to GPLv3.0._