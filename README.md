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