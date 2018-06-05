# zmq-fatal-error
Small project for reproduce JeroMQ fatal error

Use net.overc.zmq.ReproduceFatalException for reproduce. Exception happens 1 time from 3 runs:

`Exception in thread "pool-2-thread-1" java.lang.NullPointerException
	at zmq.pipe.Pipe.isDelimiter(Pipe.java:504)
	at zmq.pipe.Pipe.checkRead(Pipe.java:193)
	at zmq.socket.FQ.hasIn(FQ.java:146)
	at zmq.socket.pipeline.Pull.xhasIn(Pull.java:52)
	at zmq.SocketBase.hasIn(SocketBase.java:872)
	at zmq.SocketBase.getSocketOpt(SocketBase.java:260)
	at zmq.poll.PollItem.readyOps(PollItem.java:121)
	at zmq.ZMQ.poll(ZMQ.java:724)
	at org.zeromq.ZMQ$Poller.poll(ZMQ.java:3832)
	at net.overc.zmq.server.BiDirectionMessageServer.lambda$0(BiDirectionMessageServer.java:78)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
	at java.lang.Thread.run(Thread.java:745)`
	
After an error occurred, all existing connections stop working (Event DISCONNECTED received for all connections). New connections couldn't be established.

Another exception that can be caught by this example (but rarely):

`Exception in thread "pool-1-thread-1" java.lang.IndexOutOfBoundsException: Index: 1, Size: 1
	at java.util.ArrayList.rangeCheck(ArrayList.java:653)
	at java.util.ArrayList.get(ArrayList.java:429)
	at zmq.socket.LB.sendpipe(LB.java:116)
	at zmq.socket.pipeline.Push.xsend(Push.java:50)
	at zmq.SocketBase.send(SocketBase.java:712)
	at org.zeromq.ZMQ$Socket.send(ZMQ.java:3237)
	at org.zeromq.ZMQ$Socket.send(ZMQ.java:3187)
	at net.overc.zmq.server.BaseMessageServer.send(BaseMessageServer.java:24)
	at net.overc.zmq.server.EventServer.send(EventServer.java:38)
	at net.overc.zmq.server.ServerRunner.lambda$1(ServerRunner.java:52)
	at java.util.concurrent.ConcurrentHashMap$KeySetView.forEach(ConcurrentHashMap.java:4649)
	at java.util.Collections$SetFromMap.forEach(Collections.java:5476)
	at net.overc.zmq.server.ServerRunner.lambda$0(ServerRunner.java:51)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
	at java.lang.Thread.run(Thread.java:745)`