/*
 *
 *  * Copyright 2020 New Relic Corporation. All rights reserved.
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.newrelic.api.agent.security;

import com.newrelic.api.agent.security.instrumentation.helpers.LowSeverityHelper;
import com.newrelic.api.agent.security.schema.AbstractOperation;
import com.newrelic.api.agent.security.schema.SecurityMetaData;
import com.newrelic.api.agent.security.schema.policy.AgentPolicy;
import com.newrelic.api.agent.security.utils.logging.LogLevel;

import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

/**
 * Provides NoOps for API objects to avoid returning <code>null</code>. Do not call these objects directly.
 */
class NoOpAgent implements SecurityAgent {
    private static final SecurityAgent INSTANCE = new NoOpAgent();
    public static final String EMPTY = "";

    public static SecurityAgent getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean refreshState(URL agentJarURL, Instrumentation instrumentation) {
        return true;
    }

    @Override
    public boolean deactivateSecurity() {
        return true;
    }

    @Override
    public void registerOperation(AbstractOperation operation) {
    }

    @Override
    public void registerExitEvent(AbstractOperation operation) {}

    @Override
    public boolean isSecurityActive() {
        return false;
    }

    @Override
    public AgentPolicy getCurrentPolicy() {
        return new AgentPolicy();
    }

    @Override
    public SecurityMetaData getSecurityMetaData() {
        return null;
    }

    @Override
    public String getAgentUUID() {
        return EMPTY;
    }

    @Override
    public String getAgentTempDir() {
        return EMPTY;
    }

    @Override
    public Instrumentation getInstrumentation() {
        return null;
    }

    @Override
    public boolean isLowPriorityInstrumentationEnabled() {
        return false;
    }

    @Override
    public void setServerInfo(String key, String value) {}

    @Override
    public String getServerInfo(String key) {
        return null;
    }

    @Override
    public void setApplicationConnectionConfig(int port, String scheme) {
    }

    @Override
    public String getApplicationConnectionConfig(int port) {
        return null;
    }

    @Override
    public Map<Integer, String> getApplicationConnectionConfig() {
        return Collections.emptyMap();
    }

    @Override
    public void log(LogLevel logLevel, String event, Throwable throwableEvent, String logSourceClassName) {

    }

    @Override
    public void log(LogLevel logLevel, String event, String logSourceClassName) {

    }

    @Override
    public void reportIncident(LogLevel logLevel, String event, Throwable exception, String caller) {

    }

    @Override
    public void retransformUninstrumentedClass(Class<?> classToRetransform) {}

    @Override
    public String decryptAndVerify(String encryptedData, String hashVerifier) {
        return null;
    }


}
