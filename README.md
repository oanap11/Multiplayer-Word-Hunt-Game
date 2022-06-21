# Multiplayer-Word-Hunt-Game
Multiplayer client-server game developed with Java.
The program uses an authorative server approach: the game state and logic are maintained on the server. The client provides the user input to the game, 
but the server determines the response to the input and instructs all the clients how to react. Only invited players (players on the server's list) can play the game.


Word Hunt is a timed word game for any number of players. Each player receives the same 6x6 grid of letters.
The object of the game is to find as many words as you can in a given amount of time (I set it to 1 minute). Longer words are worth more points.
The game uses a dictionary to check for valid words. I uploaded it along with the classes.
The provided dictionary file was also used for the game Words with Friends. 
You will need to copy dictionary.txt to the bin folder of your Java work folder for the game to work. (or do something else to make it work, I don't know)

Start the server, add the names of the invited players, start the clients, connect to localhost and enjoy the game.

![game](https://user-images.githubusercontent.com/91391485/174847404-05f9e614-a61d-49a6-9c90-3751eebd13fa.png)

You can learn to build your own internet-based games with this book which I highly recommend: http://www.godtlandsoftware.com/DIYJava/DIYMultiplayerJava.html
