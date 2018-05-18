package com.company.interfaces;

import java.io.IOException;

/**
 * Created by kobiruskin on 8/1/17.
 */
public interface IStoreFactory<T> {

  T getInstance(String account) throws IOException;
}
