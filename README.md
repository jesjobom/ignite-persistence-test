# Ignite Persistence Test
(... to show a bug...)

I am trying to persist a `Set<Model>` for each `String` key.
So I'd have a `IgniteCache<String, Set<Model>>`. 

If I am running it locally always using the same storage, there's no problem.
But using Docker (or Kubernetes) and a mounted volume for this storage, I always have an error on restart.

```
javax.cache.CacheException: class org.apache.ignite.IgniteCheckedException: Cannot find metadata for object with compact footer: 2097659979
```

Without the `Set`, using persisting the `Model` individually, it also works.

#### 1. Clone Project
`$ git clone https://github.com/jesjobom/ignite-persistence-test.git`


#### 2. Build Docker Image
`$ docker build -t ignite-persistence-test ignite-persistence-test/`


#### 3. Run for the first time mounting the Ignite persistence folder
```
$ docker run --rm -v /tmp/ignite-test:/tmp/ignite-test ignite-persistence-test

	===============> INITIALIZING IGNITE 1
Aug 16, 2019 5:52:38 PM java.util.logging.LogManager$RootLogger log
WARNING: Failed to resolve default logging config file: config/java.util.logging.properties
[17:52:38]    __________  ________________ 
[17:52:38]   /  _/ ___/ |/ /  _/_  __/ __/ 
[17:52:38]  _/ // (7 7    // /  / / / _/   
[17:52:38] /___/\___/_/|_/___/ /_/ /___/  
[17:52:38] 
[17:52:38] ver. 2.7.5#20190603-sha1:be4f2a15
[17:52:38] 2018 Copyright(C) Apache Software Foundation
[17:52:38] 
[17:52:38] Ignite documentation: http://ignite.apache.org
[17:52:38] 

...

	===============> ADDING OBJECT 1
	===============> LISTING OBJECTS 1: 1
	===============> CLOSING IGNITE 1
[17:52:47] Ignite node stopped OK [uptime=00:00:05.913]
	===============> INITIALIZING IGNITE 2
[17:52:48]    __________  ________________ 
[17:52:48]   /  _/ ___/ |/ /  _/_  __/ __/ 
[17:52:48]  _/ // (7 7    // /  / / / _/   
[17:52:48] /___/\___/_/|_/___/ /_/ /___/  
[17:52:48] 
[17:52:48] ver. 2.7.5#20190603-sha1:be4f2a15
[17:52:48] 2018 Copyright(C) Apache Software Foundation
[17:52:48] 
[17:52:48] Ignite documentation: http://ignite.apache.org
[17:52:48] 

...

	===============> LISTING OBJECTS 2: 1
	===============> CLOSING IGNITE 2
[17:52:53] Ignite node stopped OK [uptime=00:00:02.925]

```


#### 4. Run again with the same mounted folder
```
$ docker run --rm -v /tmp/ignite-test:/tmp/ignite-test ignite-persistence-test

	===============> INITIALIZING IGNITE 1
Aug 16, 2019 5:52:58 PM java.util.logging.LogManager$RootLogger log
WARNING: Failed to resolve default logging config file: config/java.util.logging.properties
[17:52:58]    __________  ________________ 
[17:52:58]   /  _/ ___/ |/ /  _/_  __/ __/ 
[17:52:58]  _/ // (7 7    // /  / / / _/   
[17:52:58] /___/\___/_/|_/___/ /_/ /___/  
[17:52:58] 
[17:52:58] ver. 2.7.5#20190603-sha1:be4f2a15
[17:52:58] 2018 Copyright(C) Apache Software Foundation
[17:52:58] 

...

	===============> ADDING OBJECT 1
Exception in thread "main" javax.cache.CacheException: class org.apache.ignite.IgniteCheckedException: Cannot find metadata for object with compact footer: 2097659979
	at org.apache.ignite.internal.processors.cache.GridCacheUtils.convertToCacheException(GridCacheUtils.java:1337)
	at org.apache.ignite.internal.processors.cache.IgniteCacheProxyImpl.cacheException(IgniteCacheProxyImpl.java:1758)
	at org.apache.ignite.internal.processors.cache.IgniteCacheProxyImpl.get(IgniteCacheProxyImpl.java:931)
	at org.apache.ignite.internal.processors.cache.GatewayProtectedCacheProxy.get(GatewayProtectedCacheProxy.java:640)
	at com.jesjobom.IgnitePersistence.remove(IgnitePersistence.java:81)
	at com.jesjobom.IgnitePersistence.add(IgnitePersistence.java:66)
	at com.jesjobom.Main.main(Main.java:17)
Caused by: class org.apache.ignite.IgniteCheckedException: Cannot find metadata for object with compact footer: 2097659979
	at org.apache.ignite.internal.util.IgniteUtils.cast(IgniteUtils.java:7429)
	at org.apache.ignite.internal.util.future.GridFutureAdapter.resolve(GridFutureAdapter.java:261)
	at org.apache.ignite.internal.util.future.GridFutureAdapter.get0(GridFutureAdapter.java:172)
	at org.apache.ignite.internal.util.future.GridFutureAdapter.get(GridFutureAdapter.java:141)
	at org.apache.ignite.internal.processors.cache.GridCacheAdapter.get0(GridCacheAdapter.java:4723)
	at org.apache.ignite.internal.processors.cache.GridCacheAdapter.get(GridCacheAdapter.java:4697)
	at org.apache.ignite.internal.processors.cache.GridCacheAdapter.get(GridCacheAdapter.java:1415)
	at org.apache.ignite.internal.processors.cache.IgniteCacheProxyImpl.get(IgniteCacheProxyImpl.java:928)
	... 4 more
Caused by: class org.apache.ignite.binary.BinaryObjectException: Cannot find metadata for object with compact footer: 2097659979
	at org.apache.ignite.internal.binary.BinaryReaderExImpl.getOrCreateSchema(BinaryReaderExImpl.java:2012)
	at org.apache.ignite.internal.binary.BinaryReaderExImpl.<init>(BinaryReaderExImpl.java:286)
	at org.apache.ignite.internal.binary.BinaryReaderExImpl.<init>(BinaryReaderExImpl.java:185)
	at org.apache.ignite.internal.binary.BinaryObjectImpl.reader(BinaryObjectImpl.java:832)
	at org.apache.ignite.internal.binary.BinaryObjectImpl.deserializeValue(BinaryObjectImpl.java:795)
	at org.apache.ignite.internal.binary.BinaryObjectImpl.value(BinaryObjectImpl.java:143)
	at org.apache.ignite.internal.processors.cache.CacheObjectUtils.unwrapBinary(CacheObjectUtils.java:177)
	at org.apache.ignite.internal.processors.cache.CacheObjectUtils.unwrapKnownCollection(CacheObjectUtils.java:93)
	at org.apache.ignite.internal.processors.cache.CacheObjectUtils.unwrapBinary(CacheObjectUtils.java:181)
	at org.apache.ignite.internal.processors.cache.CacheObjectUtils.unwrapBinaryIfNeeded(CacheObjectUtils.java:67)
	at org.apache.ignite.internal.processors.cache.CacheObjectContext.unwrapBinaryIfNeeded(CacheObjectContext.java:125)
	at org.apache.ignite.internal.processors.cache.GridCacheContext.unwrapBinaryIfNeeded(GridCacheContext.java:1773)
	at org.apache.ignite.internal.processors.cache.GridCacheContext.unwrapBinaryIfNeeded(GridCacheContext.java:1761)
	at org.apache.ignite.internal.processors.cache.distributed.dht.GridPartitionedSingleGetFuture.setResult(GridPartitionedSingleGetFuture.java:695)
	at org.apache.ignite.internal.processors.cache.distributed.dht.GridPartitionedSingleGetFuture.localGet(GridPartitionedSingleGetFuture.java:477)
	at org.apache.ignite.internal.processors.cache.distributed.dht.GridPartitionedSingleGetFuture.mapKeyToNode(GridPartitionedSingleGetFuture.java:354)
	at org.apache.ignite.internal.processors.cache.distributed.dht.GridPartitionedSingleGetFuture.map(GridPartitionedSingleGetFuture.java:225)
	at org.apache.ignite.internal.processors.cache.distributed.dht.GridPartitionedSingleGetFuture.init(GridPartitionedSingleGetFuture.java:217)
	at org.apache.ignite.internal.processors.cache.distributed.dht.colocated.GridDhtColocatedCache.getAsync(GridDhtColocatedCache.java:272)
	at org.apache.ignite.internal.processors.cache.GridCacheAdapter.get0(GridCacheAdapter.java:4716)
	... 7 more
^C
```
