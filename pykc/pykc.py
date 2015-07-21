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
	def __init__(self, name):
		self.name=name
		self.commands=[]
	def forward(self, numBlocks):
		self.commands.append(Command('forward', {'dist' : numBlocks}))
	def turnRight(self, numDegrees):
		self.commands.append(Command('turnRight', {'degrees' : numDegrees}))
	# TODO: Other turtle methods!

	def toJson(self):
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

