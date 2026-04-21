# BodyHealth SkriptAddon — AI-Generated Documentation

Exposes the [BodyHealth](https://modrinth.com/plugin/bodyhealth) API to [Skript](https://modrinth.com/plugin/skript).

**Requirements:** BodyHealth 4.0.0+, Skript 2.15+, Paper 1.21+

---

## Types

### `bodypart`
Represents a body part of a player.

| Skript name | BodyPart enum |
|---|---|
| `head` | `HEAD` |
| `torso` | `TORSO` |
| `left arm` | `ARM_LEFT` |
| `right arm` | `ARM_RIGHT` |
| `left leg` | `LEG_LEFT` |
| `right leg` | `LEG_RIGHT` |
| `left foot` | `FOOT_LEFT` |
| `right foot` | `FOOT_RIGHT` |

### `bodypartstate`
Represents the condition of a body part, from healthiest to most damaged.

| Skript name | BodyPartState enum |
|---|---|
| `full` | `FULL` |
| `nearly full` | `NEARLYFULL` |
| `intermediate` | `INTERMEDIATE` |
| `damaged` | `DAMAGED` |
| `broken` | `BROKEN` |

---

## Effects

### Heal Player
Heals one or all body parts of a player.

```vb
fully heal %players%
heal %players% by %number%

fully heal %bodypart% of %players%
heal %bodypart% of %players% by %number%
```

> The `by %number%` variants take an integer amount. For percentage-based control, use [Set Body Health](#set-body-health).

**Examples:**
```vb
fully heal player
heal player by 50
fully heal head of player
heal left arm of player by 25
```

---

### Damage Player Directly
Applies raw damage to one or all body parts, bypassing the plugin's damage configuration.

```vb
directly damage %players% by %number%
directly damage %bodypart% of %players% by %number%
```

**Examples:**
```vb
directly damage player by 10
directly damage torso of player by 35.5
```

---

### Damage Player With Config
Applies damage to one or all body parts using BodyHealth's configured multipliers for the given damage cause.

```vb
damage %players% by %number% with [damage] cause %damagecause%
damage %bodypart% of %players% by %number% with [damage] cause %damagecause%
```

**Examples:**
```vb
damage player by 10 with damage cause fall
damage torso of player by 20 with cause fire
```

---

### Set Body Health
Sets the health of one or all body parts to a specific percentage (0–100).

```vb
set [the] body health of %players% to %number%
set [the] body health of %bodypart% of %players% to %number%
```

**Examples:**
```vb
set body health of player to 75
set body health of head of player to 10
```

---

### Validate Effects
Checks and removes any invalid leftover BodyHealth effects from a player.

```vb
validate [body health] effects (of|for) %players%
```

**Examples:**
```vb
validate body health effects of player
validate effects for all players
```

---

## Expressions

### Body Part Health
The current health percentage (0–100) of a specific body part.  
**Changeable:** supports `set`, `add` (heals), `remove` (damages directly).

```vb
[the] [body] health of %bodypart% of %player%
```

**Examples:**
```vb
set {_hp} to body health of head of player
set body health of head of player to 75
add 20 to body health of left arm of player
remove 15 from body health of right leg of player
```

---

### Body Part State
The current `bodypartstate` of a specific body part.  
Read-only.

```vb
[the] [body part] state of %bodypart% of %player%
```

**Examples:**
```vb
set {_state} to body part state of head of player
if body part state of left leg of player is broken:
```

---

### Max Body Part Health
The maximum health value for a body part as calculated by BodyHealth.  
Read-only.

```vb
[the] max[imum] [body part] health of %bodypart% of %player%
```

**Examples:**
```vb
set {_max} to maximum body part health of torso of player
if body health of torso of player < maximum body part health of torso of player:
```

---

## Conditions

### Body Health Is Enabled
Checks whether the BodyHealth system is active in a world or at a player's location.

```vb
body health is enabled (in|for) %world%
body health is (not enabled|disabled) (in|for) %world%

body health is enabled (in|for) %player%
body health is (not enabled|disabled) (in|for) %player%
```

**Examples:**
```vb
if body health is enabled for player:
if body health is disabled in world "mining_world":
```

---

### Can Player Jump / Walk / Sprint
Checks whether a player is currently able to perform the movement action.

```vb
%players% can jump
%players% can't jump

%players% can walk
%players% can't walk

%players% can sprint
%players% can't sprint
```

**Examples:**
```vb
if player can't jump:
    send "Your legs are too damaged to jump!"
```

---

### Can Player Interact
Checks whether a player is currently able to interact with a specific hand.

```vb
%players% can interact with [their] (main hand|primary hand)
%players% can interact with [their] (off hand|secondary hand)
%players% (can't|cannot) interact with [their] (main hand|primary hand)
%players% (can't|cannot) interact with [their] (off hand|secondary hand)
```

**Examples:**
```vb
if player can't interact with main hand:
    cancel event
```

---

### Is Vanished
Checks whether a player is vanished via PremiumVanish or SuperVanish.

```vb
%players% is vanished
%players% isn't vanished
```

**Examples:**
```vb
if player is vanished:
    stop
```

---

## Events

### Body Part Health Change
Fired whenever the health percentage of any body part of any player changes.  
**Cancellable:** yes — `cancel event` will prevent the health change.

```vb
[on] body part health change [of %-bodypart%]
```

| Event value | Type | Description |
|---|---|---|
| `event-player` | player | The affected player |
| `event-bodypart` | bodypart | The body part that changed |
| `event-number` | number | The **new** health value (percent) — settable |
| `past event-number` | number | The **old** health value (percent) |

> Setting `event-number` inside the event modifies the incoming health value before it is applied.

**Examples:**
```vb
on body part health change:
    if event-bodypart is head:
        if event-number <= 20:
            send "&cCritical head damage!" to event-player

on body part health change of torso:
    # Cap torso damage — never let it drop below 10%
    if event-number < 10:
        set event-number to 10
```

---

### Body Part State Change
Fired whenever any body part of any player transitions to a new `bodypartstate`.  
**Not cancellable.**

> Both states may be `null` when a player joins, leaves, or BodyHealth is reloaded. Always check with `is set` before using them.

```vb
[on] body part state change [of %-bodypart%]
```

| Event value | Type | Description |
|---|---|---|
| `event-player` | player | The affected player |
| `event-bodypart` | bodypart | The body part whose state changed |
| `event-bodypartstate` | bodypartstate | The **new** state |
| `past event-bodypartstate` | bodypartstate | The **old** state |

**Examples:**
```vb
on body part state change:
    set {_new} to event-bodypartstate
    if {_new} is set:
        if event-bodypartstate is broken:
            send "&4Your %event-bodypart% is broken!" to event-player

on body part state change of left leg:
    set {_new} to event-bodypartstate
    set {_old} to past event-bodypartstate
    if {_new} is set:
        if {_old} is set:
            send "Left leg: %{_old}% -> %{_new}%" to event-player
```
