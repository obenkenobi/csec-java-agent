package spray.client;

import com.newrelic.agent.security.instrumentation.spray.client.SprayUtils;
import com.newrelic.api.agent.security.NewRelicSecurity;
import com.newrelic.api.agent.security.instrumentation.helpers.GenericHelper;
import com.newrelic.api.agent.security.schema.AbstractOperation;
import com.newrelic.api.agent.security.schema.SecurityMetaData;
import com.newrelic.api.agent.security.schema.exceptions.NewRelicSecurityException;
import com.newrelic.api.agent.security.schema.operation.SSRFOperation;
import com.newrelic.api.agent.security.utils.logging.LogLevel;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import scala.concurrent.Future;
import spray.http.HttpRequest;
import spray.http.HttpResponse;

import java.net.URI;

@Weave(type = MatchType.Interface, originalName = "spray.client.pipelining$$anonfun$sendReceive$1")
public class SendReceive_Instrumentation {

    public final Future<HttpResponse> apply(HttpRequest request) {
        boolean isLockAcquired = acquireLockIfPossible();
        AbstractOperation operation = null;
        // Preprocess Phase
        if (isLockAcquired) {
            operation = preprocessSecurityHook(request);
        }

        Future<HttpResponse> returnCode;
        try {
            returnCode = Weaver.callOriginal();
        } finally {
            if (isLockAcquired) {
                releaseLock();
            }
        }
        registerExitOperation(isLockAcquired, operation);
        return returnCode;
    }

    private void releaseLock() {
        try {
            GenericHelper.releaseLock(SprayUtils.getNrSecCustomAttribName());
        } catch (Throwable ignored) {
        }
    }

    private boolean acquireLockIfPossible() {
        try {
            return GenericHelper.acquireLockIfPossible(SprayUtils.getNrSecCustomAttribName());
        } catch (Throwable ignored) {
        }
        return false;
    }

    private AbstractOperation preprocessSecurityHook(HttpRequest httpRequest) {
        try {
            SecurityMetaData securityMetaData = NewRelicSecurity.getAgent().getSecurityMetaData();
            if (!NewRelicSecurity.isHookProcessingActive() || securityMetaData.getRequest().isEmpty()) {
                return null;
            }

            // Generate required URL
            URI methodURI = null;
            String uri = null;
            try {
                methodURI = new URI(httpRequest.uri().toString());
                uri = methodURI.toString();
                if (methodURI == null) {
                    return null;
                }
            } catch (Exception ignored){
                NewRelicSecurity.getAgent().log(LogLevel.WARNING, String.format(GenericHelper.URI_EXCEPTION_MESSAGE, SprayUtils.SPRAY_CLIENT, ignored.getMessage()), ignored, this.getClass().getName());
                return null;
            }
            return new SSRFOperation(uri, this.getClass().getName(), SprayUtils.METHOD_SEND_RECEIVE);
        } catch (Throwable e) {
            if (e instanceof NewRelicSecurityException) {
                NewRelicSecurity.getAgent().log(LogLevel.WARNING, String.format(GenericHelper.SECURITY_EXCEPTION_MESSAGE, SprayUtils.SPRAY_CLIENT, e.getMessage()), e, this.getClass().getName());
                throw e;
            }
            NewRelicSecurity.getAgent().log(LogLevel.SEVERE, String.format(GenericHelper.REGISTER_OPERATION_EXCEPTION_MESSAGE, SprayUtils.SPRAY_CLIENT, e.getMessage()), e, this.getClass().getName());
            NewRelicSecurity.getAgent().reportIncident(LogLevel.SEVERE, String.format(GenericHelper.REGISTER_OPERATION_EXCEPTION_MESSAGE, SprayUtils.SPRAY_CLIENT, e.getMessage()), e, this.getClass().getName());
        }
        return null;
    }
    private void registerExitOperation(boolean isProcessingAllowed, AbstractOperation operation) {
        try {
            if (operation == null || !isProcessingAllowed || !NewRelicSecurity.isHookProcessingActive() || NewRelicSecurity.getAgent().getSecurityMetaData().getRequest().isEmpty()
            ) {
                return;
            }
            NewRelicSecurity.getAgent().registerExitEvent(operation);
        } catch (Throwable e) {
            NewRelicSecurity.getAgent().log(LogLevel.FINEST, String.format(GenericHelper.EXIT_OPERATION_EXCEPTION_MESSAGE, SprayUtils.SPRAY_CLIENT, e.getMessage()), e, this.getClass().getName());
        }
    }
}

