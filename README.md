> !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!<br>
>  THIS MODULE IS A **WORK IN PROGRESS**.<br>DO __**NOT**__ USE IT ON PRODUCTION INSTANCES<br><br>
>         NOT ALL OF THE FEATURES DESCRIBED HAVE BEEN IMPLEMENTED!<br><br>
>                              No stable releases are available at this time.<br>
> !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!<br>


> Please visit [our discord](https://discord.gg/mTaHuK6BVa) for all updates and support!

# Description
StarGate-Mechanics is an official module that adds numerous additional mechanics and portal types to [the Stargate plugin](sgrewritten.org/bukkitsource).<br>

It consists of two parts (each of which may be toggled on or off):
**A Vanilla++ Mechanics System**
- In addition to the Expands upon the plugin's sign-based system to add new Although these flags are reasonably immersive, these These flags were not included in the main plugin as they considerably alter SG's established syntax.

## Features:
This is a work in progress: many features are [still being determined](https://github.com/stargate-rewritten/Stargate-Mechanics/issues)

StarGate-Mechanics consists of two parts (both of which may be toggled in whole or in part):
### A Vanilla++ Mechanics System
#### Total Interface Replacement (TIR) Component
> __**This component is not enabled by default!**__
- Sign-based syntax can often be a bit complicated; moreover, although signs are an element of part of gameplay, programming with them can still be somewhat jarring.
- This OPTIONAL component completely eliminates all sign-programming mechanics from StarGate, instead replacing them with Vanilla++ mechanics.
  - Candidate portal frames have an automatically generated name, which may be changed through the use of a nametag item.
  - For Fixed portals, destination selection occurs by right-clicking the origin portal with a compass loaded with (right-clicked on) the destination portal.
  - For Networked portals, network selection occurs by right-clicking the origin portal with either:
      - A compass clicked on a player (for a personal network), or an existing portal of the target network (for custom/default networks); OR
      - Subject to an additional permission, a renamed recovery compass (which will create the network if needed).
  - For Flags, specific items are associated with each portal type. Right-clicking on the origin portal with specific items will add/remove the applicable flag from the portal.
      - Alternatively, specific control blocks (for example a chiselled bookshelf) automatically force a certain flag's presence (in this case, the `L` flag)
#### Additional Interface Mechanics (AIM) Component
- Even with sign-based syntax, item-based mechanics can provide some additional quality-of-life improvements.
- This OPTIONAL component adds the following item usages:
  - Right-clicking an Echo Shard on a portal binds the echo shard to that portal; right-clicking an Echo Shard will then teleport the player to the portal was bound to, consuming the shard in the process.
  - Right-clicking a Bundle or Shulker Box full of certain items (dyes, glowstone, glowsquid ink, etc.) will customise the colour scheme of a sign, consuming the items in the process.
### Additional Gate Types
This system adds certain immersive behaviours that are 
  - The `C` flag, which will leave a portal open for a specified amount of time once activated.
    - This flag is incompatible with the `D` flag.
    - Within the TIR component, this flag is activated by right-clicking the gate with a Curse of Vanishing Enchanted Book.
      - The gate has one minute loaded to the `C` flag by default;
        - Right-clicking it with Clocks adds one minute per clock (consuming the items)
        - Right-clicking it with Daylight Sensors adds 20 minutes per sensor (consuming the items)
    - Outside of the TIR component, this flag follows the form `C[0d0h0m0s]` where d, h, m, and s respectively indicate days, hours, minutes, and seconds.
  - The `D` flag, which will destroy the portal after a certain period of time.
    - This flag is incompatible with the `C` flag.
    - Within the TIR component, this flag follows the exact same mechanics as the `C` flag, but with gunpowder instead of a Vanishing Book
    - Outside of the TIR component, this flag follows the form `D[0d0h0m0s]
  - The `E` flag, which replaces the activator with a redstone lamp (or another form of signal detection)?
    - This flag might get moved to the Core.
  - The `G` flag, which generates the portal at specified coordinates.
    - In TIR mode, right-click the portal with a map named with the coordinates.
    - Outside of TIR mode, the syntax is {x,y,z} typed in lieu of the destination portal's name, assuming a fixed portal (G gates MUST be fixed).
  - The `H` flag, previously part of the Core, simply means that the gate will not be listed on the network's list unless the accessor has a specific node.
    - In TIR mode, this flag is associated with a window pane.
  - The `L` flag, which uses a chiselled bookshelf as its control surface, situates the portal within a 6-portal one-way pseudo-network based on the shelf's contents.
    - To add a destination to the portal's destination list, right-click an empty book on the destination portal and add it to the chiselled bookshelf.
    - To remove a destination from the portal's list, remove that book from the bookshelf.
    - To select a destination, left-click a book within the chiselled bookshelf.
    - The item to select this flag in TIR mode is a lectern.
  - The `N` flag, previously part of the Core, simply hides the network from the sign.
    - In the TIR component, the associated item is a structure void (this was originally meant as an admin flag).
  - The `Q` flag, previously part of the Core, suppresses all messages associated with usage of a stargate (for example, "teleported").
    - In the TIR component, the associated item is a noteblock.
  - The `S` flag, previously part of the Core, shows all portals on the network, even hidden portals; and allows users to teleport to them, regardless of permission.
    - As this is an admin flag, in TIR mode, the associated item is light.
  - The `V` flag, which will hide a portal's sign interface.
    - In TIR mode, the associated item is tinted Glass
  - The `W` feature, which teleports players randomly within a specified radius of the gate.
    - Outside of TIR mode, the syntax is `W[Distance, in blocks]`
    - Within TIR mode, the item is a map; the zoom of the map determines the distance.
  
## Dependencies
[The most recent version of Stargate](https://www.spigotmc.org/resources/stargate.87978/)

# Permissions
### Nodes
```
sg.mechanics.node -- to be added later (description)
  sg.mechanics.node.subnode -- not implemented yet (description)
```
### Defaults
```
sg.mechanics.node -- op
```

## Instructions
Not yet available.

## Configuration
Not yet available.


# Changes
[Version 0.0.0]
 - Created repository
