package software.amazon.route53recoveryreadiness.resourceset;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.route53recoveryreadiness.Route53RecoveryReadinessClient;
import software.amazon.awssdk.services.route53recoveryreadiness.model.DeleteResourceSetRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.DeleteResourceSetResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetResourceSetRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetResourceSetResponse;
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
                        proxy.initiate(
                                "AWS-Route53RecoveryReadiness-ResourceSet::Delete::PreExistenceCheck", proxyClient, progress.getResourceModel(), progress.getCallbackContext()
                        )
                                .translateToServiceRequest(Translator::translateToReadRequest)
                                .makeServiceCall(this::preExistenceCheck)
                                .progress()
                )
                .then(progress ->
                    proxy.initiate("AWS-Route53RecoveryReadiness-ResourceSet::Delete", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                        .translateToServiceRequest(Translator::translateToDeleteRequest)
                        .makeServiceCall(this::deleteResourceSet)
                        .progress()
                )
                .then(progress -> ProgressEvent.defaultSuccessHandler(null));
    }

    private DeleteResourceSetResponse deleteResourceSet(
            DeleteResourceSetRequest deleteRequest,
            ProxyClient<Route53RecoveryReadinessClient> proxyClient
    ) {
        DeleteResourceSetResponse deleteResponse;

        try {
            deleteResponse = proxyClient.injectCredentialsAndInvokeV2(deleteRequest, proxyClient.client()::deleteResourceSet);
        } catch (Route53RecoveryReadinessException e) {
            if (e.statusCode() == 404) {
                throw new CfnNotFoundException(ResourceModel.TYPE_NAME,
                        deleteRequest.resourceSetName(), e);
            } else
                throw new CfnGeneralServiceException(deleteRequest.resourceSetName(), e);
        } catch (AwsServiceException e) {
            throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        }

        logger.log(String.format("%s successfully deleted.", ResourceModel.TYPE_NAME));

        return deleteResponse;
    }

    private GetResourceSetResponse preExistenceCheck(
            GetResourceSetRequest getRequest,
            ProxyClient<Route53RecoveryReadinessClient> proxyClient
    ) {
        GetResourceSetResponse getResponse;

        try {
            getResponse = proxyClient.injectCredentialsAndInvokeV2(getRequest, proxyClient.client()::getResourceSet);
        } catch (final Route53RecoveryReadinessException e) {
            if (e.statusCode() == 404) {
                throw new CfnNotFoundException(ResourceModel.TYPE_NAME,
                        getRequest.resourceSetName(), e);
            } else
                throw new CfnGeneralServiceException(getRequest.resourceSetName(), e);
        } catch (final AwsServiceException e) {
            throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        }

        logger.log(String.format("%s has successfully been read.", ResourceModel.TYPE_NAME));
        return getResponse;
    }

}
