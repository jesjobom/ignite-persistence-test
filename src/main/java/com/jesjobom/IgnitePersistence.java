package com.jesjobom;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteSystemProperties;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;

import javax.cache.Cache;
import java.util.*;
import java.util.stream.Collectors;

public class IgnitePersistence {

    private static final String PERSISTENT_REGION_NAME = "persistence-data-region";

    private static final String STORAGE_LOCATION = "/tmp/ignite-test";

    private static IgniteCache<String, Set<Model>> store;

    private static Ignite ignite;

    IgnitePersistence() {
        System.setProperty(IgniteSystemProperties.IGNITE_NO_SHUTDOWN_HOOK, "true");

        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();

        //Region for persistent (in disk) data
        DataRegionConfiguration persistence = new DataRegionConfiguration().setPersistenceEnabled(true)
                .setInitialSize(64 * 1024 * 1024)
                .setMaxSize(128 * 1024 * 1024).setName(PERSISTENT_REGION_NAME);

        DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration().setDefaultDataRegionConfiguration(persistence);

        //Persistence location config
        dataStorageConfiguration.setWalPath(STORAGE_LOCATION + "/wal")
                .setWalArchivePath(STORAGE_LOCATION + "/wal-arc")
                .setStoragePath(STORAGE_LOCATION + "/store");

        igniteConfiguration.setDataStorageConfiguration(dataStorageConfiguration);

        ignite = Ignition.getOrStart(igniteConfiguration);
        ignite.cluster().active(true);

        store = ignite.getOrCreateCache(new CacheConfiguration<String, Set<Model>>()
                .setDataRegionName(PERSISTENT_REGION_NAME)
                .setCacheMode(CacheMode.REPLICATED)
                .setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
                .setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_SYNC)
                .setName("store")
                .setIndexedTypes(String.class, Set.class)
        );
    }

    void close() {
        ignite.close();
    }

    void add(String key, String value) {
        remove(key, value);

        Set<Model> values;
        if(store.containsKey(key)) {
            values = store.get(key);
        } else {
            values = new HashSet<>();
        }

        values.add(new Model(value));
        store.put(key, values);
    }

    void remove(String key, String value) {
        if(store.containsKey(key)) {
            Set<Model> values  = store.get(key);
            List<Model> remove = values.stream()
                    .filter(s -> value.equals(s.getValue()))
                    .collect(Collectors.toList());

            if(!remove.isEmpty()) {
                values.removeAll(remove);
                store.put(key, values);
            }
        }
    }

    List<Model> list() {
        List<Model> values = new ArrayList<>();
        Iterator<Cache.Entry<String, Set<Model>>> iterator = store.iterator();

        while(iterator.hasNext()) {
            Cache.Entry<String, Set<Model>> entry = iterator.next();
            values.addAll(entry.getValue());
        }

        return values;
    }
}
