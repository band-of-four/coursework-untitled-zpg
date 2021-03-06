# Gameplay Ideas

Preliminary notes on the gameplay aspects of a (yet untitled) Zero-Player Game
coursework project. 

We drew inspiration for the game concept from [Godville](https://en.wikipedia.org/wiki/Godville).
The setting is loosely based on J.K. Rowling's Harry Potter series, although
we allowed ourselves to freely incorporate references to other media as well.

# Overview

## Concept

When using the term zero-player game, we refer to a specific definition
where the involvement of a human player is not _necessary_ for the game
characters to develop. The [role of the player](#players-role) is therefore
to _speed up_ the progress, not enable it in the first place.

The game experience is more comparable to brief interactions
with a news feed rather than conventional drawn-out game sessions.
The interface is text-based, and [the character's diary](#diary) plays
an important role in keeping the player up to date with ongoing developments.

The player's interest is constantly piqued by new gameplay [mechanics
being unlocked](#game-progression) and further possibilities being hinted at.
On top of that, the exact kind of [controls](#players-controls) available
to the player changes from time to time.

## Setting

The game takes place within a high-school building with a large but finite number
of classrooms. The building is full of aggressive creatures who guard the halls
and show an apparent dislike for passersby. Classrooms are the only places safe
from monsters, but students who don't dare to step outside of one quickly get
expelled: after all, their classes are spread all over the building, and they've
got attendance to keep up.

## Player's Role

The player takes up a role of a _ghost_, a supernatural entity that is
assigned to every student who enrolls in the school. Ghosts can be likened
to guardian angels, although their motives may be less than pure — and that
is up to the player.

Try as it might, a ghost can never physically control its student.
This is where owls come to the rescue...

## Player's Controls

Inside tiny cages scattered around the building, countless owls are
kept in captivity by monsters of the hallways. Students free them out of the
goodness of their hearts, not awaiting anything in return — perhaps
they simply empathize with how bleak life in captivity is.

Owls, being creatures of dignity, always repay the favor to the student's
ghost. Some of them are, for example, natural healers, and the ghost may take
advantage of that to save the student from a particularly nasty monster.

An owl only helps once, so the ghost must be careful not to spend the favors
on gratuitous whims.

## Game Progression

Every student has to get through 7 years of tough subjects, strict teachers,
and dangerous creatures of the hallways... What's more, advancing to the
next year takes a little more than just remaining alive.

Students get better at beating the monsters when moving from classroom
to classroom by discovering new spells and finding information about specific
creatures from _books_ available in the school library. The library is very
[strict about who can touch its catalog](#acquiring-books), though.

Curious students also take notice of the mysterious [passages guarded by
menacing beasts](#gating), and hope to someday get strong enough to
fight their way through.

# Gating

> [Remember, players are going to assume that when something becomes available, that it's meant to be used now.](https://www.gamasutra.com/blogs/JoshBycer/20160628/275930/Examining_Gating_in_Game_Design.php)

The school is split into areas, each covering a range of classrooms (e.g.
numbers from 101 to 120 is where every student begins their journey).
Creatures are bound to classroom numbers, as are subjects and clubs,
which ensures that students only face monsters they're able to handle.

To level up (advance to the next study year), a student gets a pass for
all subjects on their study plan — a chance of getting one during a class
increases exponentially with the number of classes attended for the subject.
When all subjects are passed, the student pays a visit to the academic dean,
who transfers them.

Advancing to the next year bumps HPs, allowing the student to win a fight
against the monster gating their next area.

## 0th Level

The first area is like a tutorial. The student goes to a class once, gets a pass,
fights a monster, and gets advanced to the first (actual) level.

This level requires constant player attention because it demonstrates
the cruical mechanics. Perhaps we could delay student creation (specifying
the name, the gender, and the personal value)?

### The Diary

First, we introduce the player to the _diary_, where the student writes down
major events that happen to them. It serves the purpose of keeping the player
up with the game progression, which happens mostly without their attention.

### Movement

Students move from one classroom to another, going inside with a certain chance
(none for a class they don't need to attend, low for a [student club](#student-clubs),
high for a class they do need to attend).

### Owls

The player must use an owl least once during the "tutorial" stage. The
monster the student fights is likely too strong to be defeated without
a special boost (healing?), which gives us an opportunity to
introduce the player to the concept of getting and spending owls.

## 1st Level

This is the first _actual_ area of a game that calls itself zero-player.
The student is actually able to progress on their own — albeit rather
slowly without the player's help.

### Books

Monsters begin dropping book permissions as loot.

### Spells

Students read books to learn spells.
Spells are stats — there's an attack spell, a defense spell, and so on.

## 2nd Level

Students begin visiting clubs as well as studying.
Club activities award artefacts.

## 3rd Level

Some of the books now teach students to domesticate creatures.

# Death.
Lets talk about death.

If student lose the fight opponent monster naturally kills him (or no, discuss it later).
In this section will be mentioned ideas about handling this sad situation

## Strategy 1: Just do it like dudes done in Godville
Student dies -> "Holy owl" appears in inventory -> Player must use it to revive his character

Before this student disabled and nothing happens with him.

## Strategy 2: Groundhog Day
After death student lose all (or some) his stats and start the game from the beginning.
"Holy owls" spawns as any other owls and need to disable losing stats.

Cruel way.

## Strategy 3: They said we had too little of characters interaction
After defeating students body lies near the room he was defeated.
However, he is still breathing. But he need some help to get back to
educational process. So, he revive as another student enter this room.

Friendship is Magic!

## Strategy 4: Strange way
New creature - Death Itself! Yay!

After defeating student meet this charming creature and to revive
he need to defeat it or trade some of his stuff.

Or all dead students joins special quest for Death Itself. This
sidequest can bring more fun to game

## Notes about hp restoration
We can add one more type of room - infirmary, when student get there his hp restores.
And he can spawn there after revive

# Examples

## Some examples of owls that student can use during the fight stage

- Healing owl: restores a certain number of students hp's
- Power owl: increase the power of student attack spell during this fight stage
- Feather shield: increase the power of student defence spell during this fight stage
- Bronze owl: slightly increase power of student luck spell during this stage (not only fight)
- Silver owl: moderately increase power of student luck spell during this stage (rare)
- Golden owl: significantly increase power of student luck spell during this stage (very rare)
- Platinum owl: maximum possible power of student luck spell during this stage (super-mega rare)
- Pet-keeper: increase the power of student pet (if he have one) during this fight stage
- Killer owl: immidiately kills the opponent creature (sets it's hp to 0) (very rare)
- Bad omen: makes student immidiately quit current stage and move to the next room (not sure about the fight stage) (rare)
- Shibari owl: binds opponent creature so it can't attack student during this fight stage (named after Japanese binding art)
- Bloody owl: student pays half of his hp's but wins the fight immidiately (rare)
- Chaotic evil owl: damages opponent or student (not sure about this one)
- Time-freezing owl: makes current stage proceed slowly (can we do this?)
- Time-speeding owl: makes current stage proceed faster
- Master of creatures: opponent creature becomes student pet (very rare) (not sure about this one)

Maybe some owls can be a fuse of other owls, for example: Bloody owl + Shibary owl + Bad omen = Killer owl
