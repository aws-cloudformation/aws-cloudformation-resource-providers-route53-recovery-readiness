package software.amazon.route53recoveryreadiness.readinesscheck;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.route53recoveryreadiness.Route53RecoveryReadinessClient;
import software.amazon.awssdk.services.route53recoveryreadiness.model.DeleteReadinessCheckRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.DeleteReadinessCheckResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.Route53RecoveryReadinessException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class DeleteHandler extends BaseHandlerStd {
    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<Route53RecoveryReadinessClient> proxyClient,
        final Logger logger) {

        this.logger = logger;

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
            .then(progress ->
                proxy.initiate("AWS-Route53RecoveryReadiness-ReadinessCheck::Delete", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                    .translateToServiceRequest(Translator::translateToDeleteRequest)
                    .makeServiceCall(this::deleteReadinessCheck)
                    // .stabilize((awsRequest, awsResponse, client, model, context) -> isStabilized(model, client))
                    .progress(0)
            )
            .then(progress -> ProgressEvent.defaultSuccessHandler(null));
    }

    private DeleteReadinessCheckResponse deleteReadinessCheck(
            DeleteReadinessCheckRequest deleteRequest,
            ProxyClient<Route53RecoveryReadinessClient> proxyClient
    ) {
        DeleteReadinessCheckResponse deleteResponse;

        try {
            deleteResponse = proxyClient.injectCredentialsAndInvokeV2(deleteRequest, proxyClient.client()::deleteReadinessCheck);
        } catch (Route53RecoveryReadinessException e) {
            if (e.statusCode() == 404) {
                throw new CfnNotFoundException(ResourceModel.TYPE_NAME,
                    deleteRequest.readinessCheckName(), e);
            } else
                throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        } catch (AwsServiceException e) {
            throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        }
        logger.log(String.format("%s delete requested.", ResourceModel.TYPE_NAME));
        return deleteResponse;

    }


}
