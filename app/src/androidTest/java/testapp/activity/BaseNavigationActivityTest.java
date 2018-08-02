package testapp.activity;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResourceTimeoutException;
import android.support.test.rule.ActivityTestRule;

import com.mapbox.services.android.navigation.testapp.R;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import testapp.action.WaitAction;
import testapp.utils.OnNavigationReadyIdlingResource;
import timber.log.Timber;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public abstract class BaseNavigationActivityTest {

  @Rule
  public ActivityTestRule<Activity> rule = new ActivityTestRule<>(getActivityClass());
  protected NavigationView navigationView;
  protected OnNavigationReadyIdlingResource idlingResource;

  @Before
  public void beforeTest() {
    try {
      Timber.e("@Before test: register idle resource");
      idlingResource = new OnNavigationReadyIdlingResource(rule.getActivity());
      IdlingRegistry.getInstance().register(idlingResource);
      checkViewIsDisplayed(R.id.navigationView);
      navigationView = idlingResource.getNavigationView();
    } catch (IdlingResourceTimeoutException idlingResourceTimeoutException) {
      Timber.e("Idling resource timed out. Couldn't not validate if navigation is ready.");
      throw new RuntimeException("Could not start test for " + getActivityClass().getSimpleName() + ".\n"
        + "The ViewHierarchy doesn't contain a view with resource id = R.id.navigationView or \n"
        + "the Activity doesn't contain an instance variable with a name equal to mapboxMap.\n"
        + "You can resolve this issue by adding the requirements above or\n add "
        + getActivityClass().getSimpleName() + " to the platform/android/scripts/exclude-activity-gen.json to blacklist"
        + " the Activity from being generated.\n");
    }
  }

  protected void validateTestSetup() {
    Assert.assertTrue("Device is not connected to the Internet.", isConnected(rule.getActivity()));
    checkViewIsDisplayed(R.id.navigationView);
  }

  protected NavigationView getNavigationView() {
    return navigationView;
  }

  protected abstract Class getActivityClass();

  protected void checkViewIsDisplayed(int id) {
    onView(withId(id)).check(matches(isDisplayed()));
  }

  protected void waitAction(long waitTime) {
    onView(withId(R.id.navigationView)).perform(new WaitAction(waitTime));
  }

  static boolean isConnected(Context context) {
    ConnectivityManager connectivityManager
      = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
  }

  @After
  public void afterTest() {
    Timber.e("@After test: unregister idle resource");
    IdlingRegistry.getInstance().unregister(idlingResource);
  }
}