# Multiplayer-Word-Hunt-Game
Word Hunt is a timed word game for any number of players. Each player receives the same 6x6 grid of letters.
The object of the game is to find as many words as you can in a given amount of time (I set it to 1 minute). Longer words are worth more points.\
The game uses a dictionary to check for valid words, which I uploaded along with the classes. 
You will need to copy dictionary.txt to the bin folder of your Java work folder for the game to work. 

# Additional information
This client-server program uses Java Sockets and TCP/IP and has an authorative server approach: the game state and logic are maintained on the server. 
The server program implements sockets and controls the game. Each player is represented by a software client - a GUI developed with Java Swing.\
Client role: provides the user input to the game.\
Server role: determines the response to the input and instructs the other clients how to react.\
Only invited players (players on the server's list) can join the game.

## The code for the project is organized as follows:
- Server-side connection and communication logic is handled by the WordHuntServer and Connection classes.
- Server-side game specific logic is handled by the Game and Player classes. Each Player object has one Connection object and each Game object has many Player objects.
- The Server creates a Game and starts many Connections. When a valid connection is made and a valid login is verified, the connection is added to a new Player and the Player is added to the Game.
- All client-side connection and communication is handled by the client's main window.
- All client-side user input is handled by the GamePanel class.
- User input is sent to the server, the server sends a response to one or more clients and then the client windows are updates as directed by the server.

If you're interested in reading the full documentation for the game, please contact me.

## Photos of the program
### Entering the names of the expected players and starting the server program
![startingServer](https://user-images.githubusercontent.com/91391485/188432410-c018846b-b204-40cd-b0a6-af349227476c.png)
### 2 players during the game
![playing](https://user-images.githubusercontent.com/91391485/189526912-9a9c6dc4-288a-426b-9953-0820b592226c.png)
### Game over - displaying a list of found words and corresponding points for each
![gameOver](https://user-images.githubusercontent.com/91391485/189527047-1e7587a9-448e-4fe2-b100-21f3efb2f26f.png)

In order to develop this game I used the book: [Do-It-Yourself Multiplayer Java Games](http://www.godtlandsoftware.com/DIYJava/DIYMultiplayerJava.html)\
I highly recommend it if you wish to learn how to build your own internet-based games.
 
