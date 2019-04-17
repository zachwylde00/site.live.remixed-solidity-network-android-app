// Generated by Dagger (https://google.github.io/dagger).
package com.kyberswap.android.presentation.helper;

import android.support.v4.app.FragmentManager;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class FragmentHelper_Factory implements Factory<FragmentHelper> {
  private final Provider<FragmentManager> fragmentManagerProvider;

  public FragmentHelper_Factory(Provider<FragmentManager> fragmentManagerProvider) {
    this.fragmentManagerProvider = fragmentManagerProvider;
  }

  @Override
  public FragmentHelper get() {
    return provideInstance(fragmentManagerProvider);
  }

  public static FragmentHelper provideInstance(Provider<FragmentManager> fragmentManagerProvider) {
    return new FragmentHelper(fragmentManagerProvider.get());
  }

  public static FragmentHelper_Factory create(Provider<FragmentManager> fragmentManagerProvider) {
    return new FragmentHelper_Factory(fragmentManagerProvider);
  }

  public static FragmentHelper newFragmentHelper(FragmentManager fragmentManager) {
    return new FragmentHelper(fragmentManager);
  }
}
