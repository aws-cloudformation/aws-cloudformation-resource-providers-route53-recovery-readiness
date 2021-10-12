package software.amazon.route53recoveryreadiness.cell;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.route53recoveryreadiness.Route53RecoveryReadinessClient;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetCellRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetCellResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.Route53RecoveryReadinessException;
import software.amazon.awssdk.services.route53recoveryreadiness.model.UpdateCellRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.UpdateCellResponse;
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
        GetCellRequest getRequest = GetCellRequest.builder().cellName(request.getDesiredResourceState().getCellName()).build();
        preExistenceCheck(getRequest, proxyClient);

        //Make sure not updating readOnly properties
        if (previousModel != null) {
            if(model.getCellArn() != null && previousModel.getCellArn() != null) {
                if (!model.getCellArn().equals(previousModel.getCellArn())) {
                    throw new CfnNotUpdatableException(ResourceModel.TYPE_NAME, request.getDesiredResourceState().getCellName());
                }
            }
            if (!model.getCellName().equals(previousModel.getCellName())) {
                throw new CfnNotUpdatableException(ResourceModel.TYPE_NAME, request.getDesiredResourceState().getCellName());
            }
        }

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(progress ->
                proxy.initiate("AWS-Route53RecoveryReadiness-Cell::Update", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                    .translateToServiceRequest(Translator::translateToUpdateRequest)
                    .makeServiceCall(this::updateCell)
                    .progress())

            .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private UpdateCellResponse updateCell(
            final UpdateCellRequest updateRequest,
            final ProxyClient<Route53RecoveryReadinessClient> proxyClient
            ) {
        UpdateCellResponse updateResponse;

        try {
            updateResponse = proxyClient.injectCredentialsAndInvokeV2(updateRequest, proxyClient.client()::updateCell);
        } catch (final Route53RecoveryReadinessException e) {
            if (e.statusCode() == 404) {
                throw new CfnNotFoundException(ResourceModel.TYPE_NAME,
                        updateRequest.cellName(), e);
            } else
                throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        } catch (final AwsServiceException e) {
            throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        }

        logger.log(String.format("%s successfully updated.", ResourceModel.TYPE_NAME));
        return updateResponse;
    }

    private GetCellResponse preExistenceCheck(
            final GetCellRequest awsRequest,
            final ProxyClient<Route53RecoveryReadinessClient> proxyClient ) {

        GetCellResponse response;

        try {
            response = proxyClient.injectCredentialsAndInvokeV2(awsRequest, proxyClient.client()::getCell);
        } catch (final Route53RecoveryReadinessException e) {
            if (e.statusCode() == 404) {
                throw new CfnNotFoundException(ResourceModel.TYPE_NAME,
                        awsRequest.cellName(), e);
            } else
                throw new CfnGeneralServiceException(awsRequest.cellName(), e);
        } catch (final AwsServiceException e) {
            throw new CfnGeneralServiceException(awsRequest.cellName(), e);
        }

        logger.log(String.format("%s has successfully been read.", ResourceModel.TYPE_NAME));

        return response;
    }

}
