package software.amazon.route53recoveryreadiness.readinesscheck;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.route53recoveryreadiness.Route53RecoveryReadinessClient;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetReadinessCheckRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetReadinessCheckResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.Route53RecoveryReadinessException;
import software.amazon.awssdk.services.route53recoveryreadiness.model.UpdateReadinessCheckRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.UpdateReadinessCheckResponse;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnNotUpdatableException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class UpdateHandler extends BaseHandlerStd {
    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<Route53RecoveryReadinessClient> proxyClient,
        final Logger logger) {

        this.logger = logger;

        final ResourceModel model = request.getDesiredResourceState();
        final ResourceModel previousModel = request.getPreviousResourceState();

        //check if resource exists
        GetReadinessCheckRequest getRequest = GetReadinessCheckRequest.builder().readinessCheckName(request.getDesiredResourceState().getReadinessCheckName()).build();
        preExistenceCheck(getRequest, proxyClient);

        //Make sure not updating readOnly properties
        if (previousModel != null) {
            if (!model.getReadinessCheckArn().equals(previousModel.getReadinessCheckArn())) {
                throw new CfnNotUpdatableException(ResourceModel.TYPE_NAME, request.getDesiredResourceState().getReadinessCheckName());
            }
            if (!model.getReadinessCheckName().equals(previousModel.getReadinessCheckName())) {
                throw new CfnNotUpdatableException(ResourceModel.TYPE_NAME, request.getDesiredResourceState().getReadinessCheckName());
            }
        }

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(progress ->
                    proxy.initiate("AWS-Route53RecoveryReadiness-ReadinessCheck::Update", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                        .translateToServiceRequest(Translator::translateToUpdateRequest)
                        .makeServiceCall(this::updateReadinessCheck)
                        .progress())

            .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private GetReadinessCheckResponse preExistenceCheck(
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

    private UpdateReadinessCheckResponse updateReadinessCheck(
            UpdateReadinessCheckRequest updateRequest,
            ProxyClient<Route53RecoveryReadinessClient> proxyClient
    ) {
        UpdateReadinessCheckResponse updateResponse;

        try {
            updateResponse = proxyClient.injectCredentialsAndInvokeV2(updateRequest, proxyClient.client()::updateReadinessCheck);
        } catch (Route53RecoveryReadinessException e) {
            if (e.statusCode() == 404) {
                throw new CfnNotFoundException(ResourceModel.TYPE_NAME,
                    updateRequest.readinessCheckName(), e);
            } else
                throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        } catch (AwsServiceException e) {
            throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        }

        logger.log(String.format("%s has successfully been updated.", ResourceModel.TYPE_NAME));

        return updateResponse;

    }

}
