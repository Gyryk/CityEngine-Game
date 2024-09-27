# Trampoline Bounce Brawl

## [Video](https://www.canva.com/design/DAGCrE-5W8k/fEt5bFSp6-7tkoCQ6nHi2g/watch?utm_content=DAGCrE-5W8k&utm_campaign=designshare&utm_medium=link&utm_source=editor)

A short game made by Girik for the Java coursework. You play as a ball bouncing around, using collectible weapons
and power-ups to kill the enemies. <br><br>
<b> STORY MODE: </b> Once the area is clear you can progress to the next level, with your new unlocks and upgrades. <br>
<b> ARCADE MODE: </b> See how high you can get your score, with new weapons unlocking as you progress. Limited Ammo.
Regular Enemies. <br>
<b> SURVIVAL MODE: </b> Fight endless waves of enemies, with the difficulty increasing as you progress. All weapons +
Unlimited Ammo. Regular + Special Enemies. <br>
<b> CUSTOM MODE: </b> Create your own levels, save them as a file and load them for play in arcade mode. Modify the
rules for guns and enemy spawning to your liking. <br>

### CONTROLS:

A - Move Left <br>
D - Move Right <br>
Shift - Boost/Ram <br>
Space - Jump/Vault <br>
Ctrl - Ground Pound <br>
Mouse - Aim <br>
LMB - Shoot <br>
RMB - Sniper Mode (IF Sniper Equipped) <br>
1:5 - Change Active Weapon <br>

## Milestone 1

Created a basic Player with horizontal movement using keyboard buttons <br>
Added dashing to the player, which also allows you to ram into enemies and damage them; but you can only do it once
every 5 seconds <br>
Added a vault-like mechanic that allows the player to boost upwards when near a wall/platform <br>
Created some basic enemies that shoot the player when in range and bounce around more and more violently over time <br>
Added a basic weapon system that allows the player to shoot the enemies with a small variety of weapons that are
collected over time <br>
Added inventory system that allows the player to switch between weapons collected <br>
Added simple shooting capability to both player and enemies using a laser with varying attributes based on gun
equipped <br>
Added a sniper that allows player to shoot any enemy on the entire map however limits their vision <br>
Added a health system for the player and damage indicators for the enemies <br>
Created trampolines that boost player and enemies upwards as an extension of platforms <br>
Created an update class that implements StepListener with velocity-capping for dynamic objects and other frame-based
calculations <br>
Made platforms that inherit from StaticBody class <br>
Made enemies show their health in the middle of the body <br>
Made separate classes for inputs that implement methods from parent class <br>
Added a basic UI that shows the player's health and inventory graphically as well as how soon the player can dash
again <br>
Made the collectibles bob up and down using a sine wave to make them more noticeable <br>
Added an enemy spawner for arcade mode that randomly chooses the enemy type and spawns them in a random position within
the confines of the play area <br>
Added a basic score system for player to measure performance <br>
Added sprites to the weapons as collectibles and in the inventory <br>
Added an animated gif as the background for the playable area of the game <br>
Added relevant sprites for the platforms and the trampoline <br>

## Milestone 2

Added the ability to ground pound while away from ground so that the player can quickly bounce back off the ground <br>
Created a main menu so that player can choose between the various game modes at start <br>
Used inheritance and packages to organize the code better for scalability and readability <br>
Added a score writer that writes the player's score to a file so that it can be displayed on the scoreboard <br>
Created a scoreboard that displays the top 20 highest scores in arcade mode with their names as well as longest survived
in Survival Mode <br>
Added projectiles that replace the old shooting mechanism with bullet dynamic bodies <br>
Added spread to the shotgun and spawned 3 pellets every time you shoot <br>
Limited the number of enemies that can spawn in arcade mode so that the game doesn't become too difficult <br>
Added a basic ammo system in arcade mode with different guns using up different amounts so that you have to be strategic
with your shots <br>
Made the enemies drop ammo or health pick-ups when you kill them in arcade mode to keep game going <br>
Added a script that takes spritesheets and converts them into an array of images used for animation with the engine <br>
Added an ammo bar that shows you how much ammo you have left in a similar format to health <br>
Added the ability to write level data to a JSON file so that you can create your own levels <br>
Added the ability to load level data from a JSON file so that you can play your own levels <br>
Added an audio player to the game that allows for both background music and sound effects <br>
Made changes to player movement to make it more fluid. Simplified code and reduced redundancies. Fixed code for
cursor <br>
Added a class that adds player's time survived to a file so that it can be displayed on the scoreboard <br>
Added animations for when you shoot the gun and a gun sprite that shows up instead of the line for the player <br>
Gave the sniper the ability to shoot when not scoped, however with much higher inaccuracy <br>
Vocally produced and added sound effects to most of the user interaction in the game <br>
Added a survival mode with a bigger stronger player in a bigger arena and wave based enemies from either side <br>
Made it so that the player can only no scope with the sniper in survival mode, added brutes that damage the player and
die on impact <br>
Added a boss fight at the end of story mode and survival mode that has a lot of health and shoots bullets or spawns
enemies based on which state he is in <br>
Gave the player the ability to modify arcade mode game settings to tune the difficulty to their liking <br>
Added a custom mode that allows you to create your own levels to play in arcade mode with a custom settings file <br>
Added 3 more platform types, a mini platform, a rotator, and a vanisher that add more variety to the levels <br>
Added background music and more art assets to the game to make it more visually appealing <br>
Gave the special platforms a random offset to make them more unique <br>