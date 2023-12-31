package redis;

import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
//import io.lettuce.core.codec.StringCodec;
//#import io.lettuce.core.masterreplica.MasterReplica;
//#import io.lettuce.core.masterreplica.StatefulRedisMasterReplicaConnection;

public class SentinelRedisConnectorNew {
	// Definisco la PWD
	char[] password = "7cBEcwf6mV36Rx3S".toCharArray();
	
	// Definisco la URL di connessione
	RedisURI redisUri = RedisURI.Builder.sentinel("sentinel1", 26379)
			.withSentinel("sentinel2", 26379)
			.withSentinel("sentinel3", 26379)
            .withSentinelMasterId("redis-cache")
            .withPassword(password)
            .build();	
	// Creo il client passandogli lal URL di connessione
	RedisClient client = RedisClient.create(redisUri);
	
	// Credo la connessione a Redis
	StatefulRedisConnection<String, String> connection = client.connect();
	
	// Predispongo l'oggetto per richiamare i comandi su Redis
	RedisCommands<String, String> syncCommands = connection.sync();
	
	// Scrivo una chiave in cache
	String prova = syncCommands.set("keytest", "prova");
	
	// Definisco il metodo che recupera lo stato del master e interroga la chiave di test
    public void getMaster() {
    	String info_replication = syncCommands.info("replication");
    	String getKey = syncCommands.get("keytest");
        System.out.println("Master: " + info_replication);
        System.out.println("----------------------------------");
        System.out.println("Il valore della chiave memorizzata in cache è: " + getKey);
    }
        
    // Eseguo il metodo
    public static void main(String[] args) {
    	System.out.println("Connecting to Redis using Redis Sentinel");
    	SentinelRedisConnectorNew sentinelRedisConnectorNew = new SentinelRedisConnectorNew();
    	sentinelRedisConnectorNew.getMaster();
    }
}
