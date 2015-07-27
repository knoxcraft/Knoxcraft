from pykc import Turtle, BlockType as bt

t = Turtle('Francois')
t.forward(10)
t.turnRight(2)
t.setBlock(bt.air)
#print(t.toJson())
t.upload('127.0.0.1:8888', 'Spacdog')
