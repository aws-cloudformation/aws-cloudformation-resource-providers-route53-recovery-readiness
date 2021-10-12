package software.amazon.route53recoveryreadiness.readinesscheck;

import software.amazon.awssdk.auth.credentials.internal.CredentialSourceType;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.route53recoveryreadiness.Route53RecoveryReadinessClient;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetReadinessCheckRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetReadinessCheckResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.Route53RecoveryReadinessException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ReadHandler extends BaseHandlerStd {
    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<Route53RecoveryReadinessClient> proxyClient,
        final Logger logger) {

        this.logger = logger;

        ResourceModel model = request.getDesiredResourceState();

        if (model.getReadinessCheckName() == null) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, "NoReadinessCheck");
        }

        return proxy.initiate("AWS-Route53RecoveryReadiness-ReadinessCheck::Read", proxyClient, request.getDesiredResourceState(), callbackContext)
            .translateToServiceRequest(Translator::translateToReadRequest)
            .makeServiceCall(this::getReadinessCheck)
            .done(awsResponse -> ProgressEvent.defaultSuccessHandler(Translator.translateFromReadResponse(awsResponse)));
    }

    private GetReadinessCheckResponse getReadinessCheck(
            GetReadinessCheckRequest getRequest,
            ProxyClient<Route53RecoveryReadinessClient> proxyClient
    ) {
        GetReadinessCheckResponse getResponse;

        try {
            getResponse = proxyClient.injectCredentialsAndInvokeV2(getRequest, proxyClient.client()::getReadinessCheck);
        } catch (Route53RecoveryReadinessException e) {
            if (e.statusCode() == 404) {
                throw new CfnNotFoundException(ResourceModel.TYPE_NAME,
                    getRequest.readinessCheckName(), e);
            } else
                throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        } catch (AwsServiceException e) {
            throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        }

        logger.log(String.format("%s has successfully been read.", ResourceModel.TYPE_NAME));

        return getResponse;
    }
}
