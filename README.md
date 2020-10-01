# Battleships-Game

## Правила на играта

- Играта се играе от двама играчи.
-	Всеки играч разполага с игрално поле, което се състои от 10x10 клетки. Редовете са обозначени с буквите от A до J, а колоните са номерирани с числата от 1 до 10.
-	Всеки играч има на полето си:
    -	1 кораб, състоящ се от 5 клетки;
    -	2 кораба, състоящи се от 4 клетки;
    -	3 кораба, състоящи се от 3 клетки;
    -	4 кораба, състоящи се от 2 клетки;
-	В началото на играта, всеки играч разполага корабите си на полето, като те могат да са само в права линия (хоризонтално или вертикално)
-	Целта на всеки играч е да уцели корабите на противника си, като играчите се редуват и всеки има право на един изтрел на ход.
    -	Играчът на ход подава координатите на клетката, по която стреля, и като отговор получава индикация, дали е уцелил или не, и ако е уцелил, дали корабът е потопен.
    -	За да е потопен даден кораб, трябва да са уцелени всичките му клетки.
-	Играта приключва, когато някой от играчите остане без кораби.

## Game Server

Функционалности:
-	Създаване на игра
-	Извеждане на списък с всички игри, активни в момента, с информация дали играта е започнала и броя на играчите в нея.
-	Присъединяване към игра (всяка игра трябва да има уникален идентификатор), ако има свободно място. (ако тази игра не съществува, избираме или да я създадем, или да се присъединим към случйна игра)
-	Присъединяване към случайна игра, в която има място.
-	Запазване на състоянието на играта, в която сме в момента.
-	Извеждане на всички запазени игри, в които сме участвали.
-	Възстановяване на запазена игра и присъединяване към нея.
-	Изтриване на запазена игра.

## Gameplay

- Създаване на игра
```bash
$ java client.java

# Извеждане на възможните команди
Choose a command: 
  username <username> - to set username
  list-users - to list all online users 
  create-game <game-name> - to create a game 
  join-game <game-name> - to join game <game-name> 
  join-random-game - to join a random game 
  list-games - to list all games
  start-game - to start the game if you are the creator of the room
  list-saved-games - to list your saved games
  load-game <game-name> - to load a saved game
  delete-saved-game <game-name> - to delete a saved game
  disconnect - to disconnect

> username mimdim
Username set to mimdim
> create-game my-game
Game my-game has been created.
```

-	Присъединяване към игра

```bash
$ java client.java

# извеждане на възможните команди

> username darlik
Username set to darlik

> list-games
my-game -> creator: mimdim | status: pending | [0/2]

> join-game my-game
You joined game my-game
One player in the game room: 
Player 1: mimdim
Player 2: 
```

- Въвеждане на кораби
```bash
$ java client.java

# извеждане на възможните команди
# присъединяване към игра

> start-game
Lets play!
   1 2 3 4 5 6 7 8 9 10
A |_|_|_|_|_|_|_|_|_|_|
B |_|_|_|_|_|_|_|_|_|_|
C |_|_|_|_|_|_|_|_|_|_|		Legend:
D |_|_|_|_|_|_|_|_|_|_|		* - ship field
E |_|_|_|_|_|_|_|_|_|_|		X - hit ship field
F |_|_|_|_|_|_|_|_|_|_|		O - hit empty field
G |_|_|_|_|_|_|_|_|_|_|
H |_|_|_|_|_|_|_|_|_|_|
I |_|_|_|_|_|_|_|_|_|_|
J |_|_|_|_|_|_|_|_|_|_|
Please select your ships! Write first and last cell of the ship. (e.g. A1 A5)
5 cells long ships: 
#1: A1 A5
#2: B1 B4
#3: C1 C4
#4: D1 D3
...
```

- Въвеждане на ход
```bash
$ java client.java

# извеждане на възможните команди
# присъединяване към игра
# стартиране на игра

Please select hit position!
D9
	  YOUR BOARD
   1 2 3 4 5 6 7 8 9 10
A |*|*|*|*|*|_|_|_|_|_|
B |*|*|*|*|_|_|_|_|_|_|
C |*|*|*|*|_|_|_|_|_|_|		Legend:
D |*|*|*|_|_|_|_|_|_|_|		* - ship field
E |*|*|*|_|_|_|_|_|_|_|		X - hit ship field
F |*|*|*|_|_|_|_|_|_|_|		O - hit empty field
G |*|*|_|_|_|_|_|_|_|_|
H |*|*|_|_|_|_|_|_|_|_|
I |*|*|_|_|_|_|_|_|_|_|
J |*|*|_|_|_|_|_|_|_|_|

	  ENEMY BOARD
   1 2 3 4 5 6 7 8 9 10
A |_|_|_|_|_|_|_|_|_|_|
B |_|_|_|_|_|_|_|_|_|_|
C |_|_|_|_|_|_|_|_|_|_|
D |_|_|_|_|_|_|_|_|O|_|
E |_|_|_|_|_|_|_|_|_|_|
F |_|_|_|_|_|_|_|_|_|_|
G |_|_|_|_|_|_|_|_|_|_|
H |_|_|_|_|_|_|_|_|_|_|
I |_|_|_|_|_|_|_|_|_|_|
J |_|_|_|_|_|_|_|_|_|_|
darlik's turn.
```

- Запазване на игра
```bash
$ java client.java

# извеждане на възможните команди
# присъединяване към игра
# стартиране на игра

> save-game
Game "my-game" saved successfully.
> list-games
my-game -> creator: mimdim | status: saved | [2/2]
> list-saved-games
my-game
> load-game my-game
mimdim's turn.
```
