// Generated by Dagger (https://google.github.io/dagger).
package com.kyberswap.android.data.mapper;

import dagger.internal.Factory;

public final class HeaderMapper_Factory implements Factory<HeaderMapper> {
  private static final HeaderMapper_Factory INSTANCE = new HeaderMapper_Factory();

  @Override
  public HeaderMapper get() {
    return provideInstance();
  }

  public static HeaderMapper provideInstance() {
    return new HeaderMapper();
  }

  public static HeaderMapper_Factory create() {
    return INSTANCE;
  }

  public static HeaderMapper newHeaderMapper() {
    return new HeaderMapper();
  }
}
