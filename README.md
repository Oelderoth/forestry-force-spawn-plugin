# Forestry Force Spawn Helper
A plugin for helping keep track of trees across multiple worlds when force spawning Forestry events.

## Features
- Easily keep track trees, their remaining time, and their worlds
- Quick hop to tracked worlds
- Highlight when a tree has been cut long enough to start force spawning
- Customizable highlights for in-progress and ready-to-cut trees
- Configure which tree types are tracked
- Configure what order tracked trees are displayed in
- (Optional) Automatically un-track trees if not harvested within a configurable time

## How to use the plugin
After you start cutting an eligible, tracked type of tree the plugin will highlight the tree after 4 game-ticks have passed.
At this point you can log out or hop worlds and the plugin will keep track of the tree, world, and remaining time before you can cut it odwn.

Once the timer has completed fully, you can hop back to the world manually (or double-click on the world number on the timer), and cut down the highlighted tree.

You can also right-click on a timer to hop to the corresponding world, or to delete a specific timer.

## Force spawning mechanics

To start force spawning, you'll want to start cutting a tree that is able to spawn Forestry events. This includes most trees and locations, but excludes the following (more details can be found at [the OSRS Wiki](https://oldschool.runescape.wiki/w/Forestry#Spawn_Mechanics)):
- Regular or Burnt trees
- Hollow trees
- Player-Grown trees
- Blisterwood tree
- Redwood trees
- Trees in the Woodcutting Guild

After at least one skill roll (4 game ticks spent cutting the tree), log out or hop worlds *without* stopping cutting the tree. 
If done correctly, the tree will not register that the player has stopped cutting it. As a result, the [despawn timer](https://oldschool.runescape.wiki/w/Forestry#Core_Woodcutting_changes) for the tree will continue progressing even though no players are cutting the tree.
If you stop cutting the tree before changing worlds, the timer will reset and will _not_ continue progressing.

Once the tree's despawn timer has completed, hop back to the original world and cut the tree again. 
If done correctly, the tree should despawn after a harvesting single log and have a chance to spawn a forestry event. 
Higher tiered trees (such as Yew and Magic) have longer despawn timers but also higher chances to spawn events.

### Downsides of force spawning events
Force spawning events on high-level trees can get comparable spawn rates to using a forestry event CC, without requiring multiple people or risking being ineligible for an event (since you will always be present near the tree when an event spawns). 
But, since the tree has a "ghost" player cutting it, forestry events that scale based on the number of players cutting the tree may be more difficult than they would otherwise be.

I've found that some events are difficult or impossible to fully complete solo while force spawning, including:
- Struggling Sapling - Unlucky mulch spawns or taking too long to find the right combo can make it hard to complete in time
- Beehives - Hives will take 2x the normal amount of logs 
- Friendly Ent - This one seems impossible to fully prune all 5 ents within the provided time window