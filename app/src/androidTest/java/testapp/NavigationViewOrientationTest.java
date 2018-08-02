package testapp;

import android.support.test.espresso.ViewAction;

import com.mapbox.services.android.navigation.testapp.test.TestNavigationActivity;

import org.junit.Test;

import testapp.activity.BaseNavigationActivityTest;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static testapp.action.OrientationChangeAction.orientationLandscape;
import static testapp.action.OrientationChangeAction.orientationPortrait;

public class NavigationViewOrientationTest extends BaseNavigationActivityTest {

  @Override
  protected Class getActivityClass() {
    return TestNavigationActivity.class;
  }

  @Test
  public void onOrientationChange_navigationContinuesRunning() {
    validateTestSetup();

    changeOrientation(orientationLandscape());
    changeOrientation(orientationPortrait());
  }

  private void changeOrientation(ViewAction newOrientation) {
    onView(isRoot()).perform(newOrientation);
  }
}
