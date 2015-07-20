from pykc import Turtle

t = Turtle('Marie')
t.forward(10)
t.turnRight(2)
#print(t.toJson())
t.upload('127.0.0.1:8888', 'Spacdog')
