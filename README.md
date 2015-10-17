# Q Example

A simple implementation of the Q library.

## What is Q?

It's a small library with a weird name that solves a problem that really didn't
need to be solved. It's also kind of cool.

## For real though

It simplifies the process of linking together playback from different sources or
file formats where multiple players are be required.

#### An example

My audio player expects a file name, but I also want to play mp3 streams off the
internet. I have another player that does that, but trying to get them to play
nice together is giving me a headache. With Q, as long as I have a basic
knowledge of the player's state, I can just add the players to the Q and supply a
generic list of tracks with URIs (that at least one of the players understands)
and the Q will handle the rest.

## Why does this matter?

It doesn't that much. There's a few inherent drawbacks (using the same player for
all tracks would make seamless playback much easier for instance) and it's not
necessarily that difficult to implement anyways. I thought it would be a cool
project and now it exists.

## I think it's awesome! Anything I can do to help?

Heck yeah there is. Check out the
[open source code](https://github.com/lubecjac/Q) and fork the project. Create a
feature branch, and whenever you think your code is ready, I'll check it out.
If everything passes inspection, it'll be merged into the master branch. You can
find a bit more info on the actual project page, so definitely go there if
you're interested.


## Thanks for checking it out!

I hope someone finds it useful.