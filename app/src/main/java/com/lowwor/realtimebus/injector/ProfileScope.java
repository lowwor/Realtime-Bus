package com.lowwor.realtimebus.injector;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by lowworker on 2015/9/13.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ProfileScope {}
