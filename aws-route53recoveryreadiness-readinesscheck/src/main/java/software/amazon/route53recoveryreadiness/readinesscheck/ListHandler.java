package software.amazon.route53recoveryreadiness.readinesscheck;

import software.amazon.awssdk.services.route53recoveryreadiness.Route53RecoveryReadinessClient;
import software.amazon.awssdk.services.route53recoveryreadiness.model.ListReadinessChecksRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.ListReadinessChecksResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ListHandler extends BaseHandlerStd {

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<Route53RecoveryReadinessClient> proxyClient,
            final Logger logger) {

        final ListReadinessChecksRequest listRequest = Translator.translateToListRequest(request.getNextToken());

        ListReadinessChecksResponse listResponse = proxy.injectCredentialsAndInvokeV2(listRequest, ClientBuilder.getClient()::listReadinessChecks);

        String nextToken = listResponse.nextToken();

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModels(Translator.translateFromListRequest(listResponse))
            .nextToken(nextToken)
            .status(OperationStatus.SUCCESS)
            .build();
    }
}
