package com.company.io;

import com.company.interfaces.IStoreFactory;
import com.google.protobuf.Message;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by kobiruskin on 7/26/17.
 */
public interface ObjectStoreFactory extends IStoreFactory<ObjectStoreFactory.ObjectStore> {

  interface ObjectStore {

    String put(String namespace, String key, byte[] bytes) throws IOException;

    String put(String namespace, String key, Message message) throws IOException;

    String move(String namespace, String sourceBucket, String sourceKey,
                String key) throws IOException;

    Long getObjectSize(String bucket, String key);

    <T extends Message> Optional<T> get(String namespace, String key, Function<byte[],
      Optional<T>> callable) throws IOException;

    InputStream getAsStream(String namespace, String key) throws IOException;

    byte[] getAsBytes(String namespace, String key) throws IOException;

    byte[] getAsBytes(String key) throws IOException;

    void scanKeys(String namespace, String regex, Consumer<String> consumer)
            throws IOException;

    void scanObject(String namespace, String regex, Consumer<byte[]> consumer)
            throws IOException;
  }
}
