package com.lowwor.realtimebus.data.local;

import com.lowwor.realtimebus.BuildConfig;
import com.lowwor.realtimebus.BusRobolectricTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.observers.TestObserver;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created by lowworker on 2016/5/19 0019.
 */
@RunWith(BusRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, packageName = "com.lowwor.realtimebus")
public class PreferencesHelperTest {
    final PreferencesHelper mPreferencesHelper = new PreferencesHelper(RuntimeEnvironment.application);

    @Before
    public void setUp() {
        mPreferencesHelper.clear();
    }

    @Test
    public void getStartFromFirst_whenEmpty() throws Exception {
        assertThat(mPreferencesHelper.getIsStartFromFirst()).isEqualTo(true);
    }

    @Test
    public void putAndGetStartFromFirst() throws Exception {
        mPreferencesHelper.saveStartFromFirst(true);
        assertThat(mPreferencesHelper.getIsStartFromFirst()).isEqualTo(true);
    }


    @Test
    public void getAutoCompleteAsObservable() throws Exception {

        TestObserver<List<String>> testObserver = mPreferencesHelper.getAutoCompleteAsObservable().test();
        // default 3A
        testObserver.assertValues(Collections.singletonList("3A"));

        // add 4A
        mPreferencesHelper.saveAutoCompleteItem("4A");
        testObserver.assertValues(Collections.singletonList("3A"), Arrays.asList("3A", "4A"));

        // add 10A
        mPreferencesHelper.saveAutoCompleteItem("10A");
        testObserver.assertValues(Collections.singletonList("3A"), Arrays.asList("3A", "4A"), Arrays.asList("3A", "4A","10A"));
    }

    @Test
    public void getAutoCompleteAsObservable_whenEmpty_default3A() throws Exception {

        // default 3A
        TestObserver<List<String>> testObserver = mPreferencesHelper.getAutoCompleteAsObservable().test();
        // null is not allow in RxJava2
        testObserver.assertValues(Collections.singletonList("3A"));

    }

    @Test
    public void saveAutoCompleteAsObservable_removeDuplicate() throws Exception {

        // default 3A
        TestObserver<List<String>> testObserver = mPreferencesHelper.getAutoCompleteAsObservable().test();
        testObserver.assertValues(Collections.singletonList("3A"));

        // add 10A
        mPreferencesHelper.saveAutoCompleteItem("10A");
        mPreferencesHelper.saveAutoCompleteItem("10A");
        mPreferencesHelper.saveAutoCompleteItem("10A");
        mPreferencesHelper.saveAutoCompleteItem("10A");

        testObserver.assertValues(Collections.singletonList("3A"), Arrays.asList("3A","10A"));

    }


    @Test
    public void putAndGetLastQueryLine() throws Exception {
        mPreferencesHelper.saveLastQueryLine("10");
        assertThat(mPreferencesHelper.getLastQueryLine()).isEqualTo("10");
    }

    @Test
    public void getLastQueryLine_whenEmpty() throws Exception {
        assertThat(mPreferencesHelper.getLastQueryLine()).isEqualTo("3A");
    }

    @Test
    public void getShowNotification_whenEmpty() throws Exception {
        assertThat(mPreferencesHelper.getShowNotification()).isEqualTo(true);
    }

    @Test
    public void getTrackBackground_whenEmpty() throws Exception {
        assertThat(mPreferencesHelper.getTrackBackground()).isEqualTo(true);
    }

    @Test
    public void getShowPopupNotification_whenEmpty() throws Exception {
        assertThat(mPreferencesHelper.getShowPopupNotification()).isEqualTo(true);
    }

    @Test
    public void testGetAutoRefreshInterval() throws Exception {
        assertThat(mPreferencesHelper.getAutoRefreshInterval()).isEqualTo(3);

    }
}