package software.amazon.route53recoveryreadiness.recoverygroup;

import java.util.Objects;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.route53recoveryreadiness.Route53RecoveryReadinessClient;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetRecoveryGroupRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetRecoveryGroupResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.Route53RecoveryReadinessException;
import software.amazon.awssdk.services.route53recoveryreadiness.model.UpdateRecoveryGroupRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.UpdateRecoveryGroupResponse;
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

        if (previousModel != null) {
            //Name needed to find Recovery Group to update, but is not itself update-able
            if (!previousModel.getRecoveryGroupName().equals(model.getRecoveryGroupName())) {
                throw new CfnNotUpdatableException(ResourceModel.TYPE_NAME, request.getDesiredResourceState().getRecoveryGroupName());
            }
            if (previousModel.getRecoveryGroupArn() != null && model.getRecoveryGroupArn() != null) {
                if (!model.getRecoveryGroupArn().equals(previousModel.getRecoveryGroupArn())) {
                    throw new CfnNotUpdatableException(ResourceModel.TYPE_NAME, request.getDesiredResourceState().getRecoveryGroupArn());
                }
            }
        }

        //check if resource exists
        GetRecoveryGroupRequest getRequest = GetRecoveryGroupRequest.builder().recoveryGroupName(request.getDesiredResourceState().getRecoveryGroupName()).build();
        preExistenceCheck(getRequest, proxyClient);

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
            .then(progress ->
                proxy.initiate("AWS-Route53RecoveryReadiness-RecoveryGroup::Update", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                    .translateToServiceRequest(Translator::translateToUpdateRequest)
                    .makeServiceCall(this::updateRecoveryGroup)
                    .progress())

            .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private UpdateRecoveryGroupResponse updateRecoveryGroup(
            final UpdateRecoveryGroupRequest request,
            final ProxyClient<Route53RecoveryReadinessClient> proxyClient
    ) {
        UpdateRecoveryGroupResponse response = null;

        try {
            response = proxyClient.injectCredentialsAndInvokeV2(request, proxyClient.client()::updateRecoveryGroup);
        } catch (final Route53RecoveryReadinessException e) {
            if (e.statusCode() == 404) {
                throw new CfnNotFoundException(ResourceModel.TYPE_NAME,
                        String.format("%s not found in update request", request.recoveryGroupName()), e);
            }
        } catch (final AwsServiceException e) {
            throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        }

        logger.log(String.format("%s successfully updated.", ResourceModel.TYPE_NAME));
        return response;
    }

    private GetRecoveryGroupResponse preExistenceCheck(
            final GetRecoveryGroupRequest awsRequest,
            final ProxyClient<Route53RecoveryReadinessClient> proxyClient ) {

        GetRecoveryGroupResponse response;

        try {
            response = proxyClient.injectCredentialsAndInvokeV2(awsRequest, proxyClient.client()::getRecoveryGroup);
        } catch (final Route53RecoveryReadinessException e) {
            if (e.statusCode() == 404) {
                throw new CfnNotFoundException(ResourceModel.TYPE_NAME,
                        awsRequest.recoveryGroupName(), e);
            } else
                throw new CfnGeneralServiceException(awsRequest.recoveryGroupName(), e);
        } catch (final AwsServiceException e) {
            throw new CfnGeneralServiceException(awsRequest.recoveryGroupName(), e);
        }

        logger.log(String.format("%s has successfully been read.", ResourceModel.TYPE_NAME));

        return response;
    }
}
