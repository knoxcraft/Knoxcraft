from pykc import Turtle, BlockType as bt

t = Turtle('francois')

t.setBlock(bt.BlueWool)
size=6
for j in range(size):
	for i in range(size):
		t.blockPlace(True)
		t.forward(size)
		t.blockPlace(False)
		t.right(1)
		t.backward(size)
	t.blockPlace(False)
	t.left(size)
	t.up(1)
#print(t.toJson())
t.upload('127.0.0.1:8888', 'Spacdog')
