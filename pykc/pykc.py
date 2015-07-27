import http.client
import urllib
import time


VERSION='0.0.0.1'

'''
{"scriptname" : "Phillipe", 
	"commands" : [
		{"cmd" : "forward", "args" : {"dist" : 10}}, 
		{"cmd" : "turnRight", "args" : {"degrees" : 90}}, 
		{"cmd" : "forward", "args" : {"dist" : 20}}
	]
}
'''

class Command:
	def __init__(self, cmd, args):
		self.command=cmd
		self.args=args
	def __str__(self):
		argList=[]
		for k,v in self.args.items():
			if type(v) == str:
				v='"%s"' % v
			elif type(v) == int or type(v) == long:
				v='%d' % v
			argList.append('"%s" : %s' % (k, v))
		argStr=', '.join(argList)
		return '{"cmd" : "%s", "args" : {%s}}' % (self.command, argStr)

class Turtle:
	#TODO testing
	#TODO Add value support? (verify it atleast)
	#TODO Anything I missed
	def __init__(self, name):
		self.name=name
		self.commands=[]
	def forward(self, numBlocks):
		self.commands.append(Command('forward', {'dist' : numBlocks}))
	def backward(self, numBlocks):
		self.commands.append(Command('backward', {'dist' : numBlocks}))
	def up(self, numBlocks):
		self.commands.append(Command('up', {'dist' : numBlocks}))
	def down(self, numBlocks):
		self.commands.append(Command('down', {'dist' : numBlocks}))		
	def turnRight(self, numDegrees):
		self.commands.append(Command('turnRight', {'degrees' : numDegrees}))
	def turnLeft(self, numDegrees):
		self.commands.append(Command('turnLeft', {'degrees' : numDegrees}))
	def setPosition(self, position):
		self.commands.append(Command('setPosition', {'position' : position}))
	def setDirection(self, direction):
		self.commands.append(Command('setDirection', {'direction' : direction}))
	def blockPlace(self, place):
		self.commands.append(Command('blockPlace', {'dist' : place}))
	def setBlock(self, blockType):
		self.commands.append(Command('setBlock', {'type' : blockType}))

	def toJson(self):
		# future work: use the actual Python json library instead of rewriting it...
		cmdList=[]
		for cmd in self.commands:
			cmdList.append(str(cmd))
		cmdStr=', '.join(cmdList) + "\n"
		return \
'''{"scriptname" : "%s",
	"commands" : [
	%s]
}''' % (self.name, cmdStr)

	def upload(self, url, minecraftName):
		conn = http.client.HTTPConnection(url)
		json = self.toJson()
		params = {'jsontext': json, 
			# TODO: upload source
			'sourcetext': 'TODO: source text', 
			'language': 'python', 
			'playerName' : minecraftName, 
			'client' : 'pykc-%s' % VERSION}
		
		boundary='P8qZ0SA4n1v9T'+str(round(time.time() * 1000))
		# holy crap, building a multipart upload by hand is a huge pain in the arse
		# I could have saved a whole day if I had read the documentation more closely... Ugh.
		dataList=[]
		dataList.append('--'+boundary)
		for key, val in params.items():
			# Add boundary and header
			dataList.append('Content-Disposition: form-data; name="{0}"'.format(key))
			dataList.append('')
			dataList.append(val)
			dataList.append('--'+boundary)
		dataList[-1]+='--'
		dataList.append('')
		body = '\r\n'.join(dataList)
		#print(body)
		contentType = 'multipart/form-data; boundary={}'.format(boundary)
		headers = {"Content-Type" : contentType, "Accept" : "text/plain"}
		conn.request("POST", "/kctupload", body, headers)
		response = conn.getresponse()

		if response.status==200:
			print('Success!')
			print(response.status, response.reason)	
			print(response.read().decode("utf-8"))
		else:
			print('Failed to upload...')
			print(response.status, response.reason)	
			print(response.read().decode("utf-8"))


class BlockType:
	'''Sort of like an enum, but we literally want the int values and we don't
need any of the features we get from extending a Python Enum.
	'''
	air = 0
	stone = 1
	# TODO: rest of the constant values from CanaryMod's BlockType class

