package software.amazon.route53recoveryreadiness.recoverygroup;

import java.util.List;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.route53recoveryreadiness.Route53RecoveryReadinessClient;
import software.amazon.awssdk.services.route53recoveryreadiness.model.CreateRecoveryGroupRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.CreateRecoveryGroupResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetCellRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetCellResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetRecoveryGroupRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetRecoveryGroupResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.Route53RecoveryReadinessException;
import software.amazon.cloudformation.exceptions.CfnAlreadyExistsException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;


public class CreateHandler extends BaseHandlerStd {
    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<Route53RecoveryReadinessClient> proxyClient,
        final Logger logger) {

        this.logger = logger;

        // Make sure the user isn't trying to assign values to readOnly properties
        if (request.getDesiredResourceState().getRecoveryGroupArn() != null) {
            throw new CfnInvalidRequestException(request.getDesiredResourceState().toString());
        }

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                // Check to see if RecoveryGroup already exists
                .then(progress -> {
                            final ProgressEvent<ResourceModel, CallbackContext> progressEvent = proxy.initiate("AWS-Route53RecoveryReadiness-RecoveryGroup::Create::PreExistenceCheck", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                                    .translateToServiceRequest(Translator::translateToReadRequest)
                                    .makeServiceCall(this::getRecoveryGroup)
                                    .progress(0);
                            // Set callback delay to 0 so that CloudFormation will execute the next step immediately.
                            progressEvent.setCallbackDelaySeconds(0);
                            return progress;
                        }
                )
                // Then make sure the cells exist
                .then(progress -> {
                    final ProgressEvent<ResourceModel, CallbackContext> progressEvent = proxy.initiate("AWS-Route53RecoveryReadiness-RecoveryGroup::Create::CheckForCellExistence", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                            .translateToServiceRequest(Translator::translateToReadRequest)
                            .makeServiceCall((awsRequest, client) -> validateCells(request.getDesiredResourceState(), proxyClient))
                            .progress(0);
                    progressEvent.setCallbackDelaySeconds(0);
                    return progressEvent;
                })
                .then(progress -> {
                    final ProgressEvent<ResourceModel, CallbackContext> progressEvent = proxy.initiate("AWS-Route53RecoveryReadiness-RecoveryGroup::Create", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                            .translateToServiceRequest(Translator::translateToCreateRequest)
                            .makeServiceCall(this::createRecoveryGroup)
                            .progress();
                    progressEvent.setCallbackDelaySeconds(0);
                    return progressEvent;
                })
                .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private CreateRecoveryGroupResponse createRecoveryGroup(
            CreateRecoveryGroupRequest request,
            ProxyClient<Route53RecoveryReadinessClient> proxyClient
    ) {
        CreateRecoveryGroupResponse response;

        try {
            response = proxyClient.injectCredentialsAndInvokeV2(request, proxyClient.client()::createRecoveryGroup);
        } catch (Route53RecoveryReadinessException e) {
            if (e.statusCode() == 409)
                throw new CfnAlreadyExistsException(ResourceModel.TYPE_NAME, request.recoveryGroupName(), e);
            else
                throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        } catch (AwsServiceException e) {
            throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        }

        logger.log(String.format("%s successfully created.", ResourceModel.TYPE_NAME));
        return response;
    }

    /**
     * Just validates that each cell exists, if it does, we do not need the response, otherwise
     * throw exception.
     */
    private GetRecoveryGroupResponse validateCells(
            ResourceModel model,
            ProxyClient<Route53RecoveryReadinessClient> proxyClient) {
        List<String> cells = model.getCells();

        if (cells != null && cells.size() > 0) {
            for (String cellArn : cells) {
                String [] cell = cellArn.split("/");
                getCell(GetCellRequest.builder().cellName(cell[1]).build(), proxyClient);
                logger.log(String.format("%s exists", cell[1]));
            }
        }
        //We don't care about the response
        return null;
    }

    // We throw a Conflict Exception when a resource already exists, but our service is not idempotent, so doing this step as a precaution
    private GetRecoveryGroupResponse getRecoveryGroup(
            GetRecoveryGroupRequest request,
            ProxyClient<Route53RecoveryReadinessClient> proxyClient
    ) {
        GetRecoveryGroupResponse response = null;

        try {
            response = proxyClient.injectCredentialsAndInvokeV2(request, proxyClient.client()::getRecoveryGroup);
        } catch (final Route53RecoveryReadinessException e) {
            if (e.statusCode() == 404)
                logger.log("Resource Not found found");
            else {
                throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
            }
        } catch (final AwsServiceException e) {
            throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        }

        logger.log(String.format("%s has successfully been read.", ResourceModel.TYPE_NAME));

        // Should always be null
        return response;
    }

    // Pre-existence check for Cells
    private GetCellResponse getCell(
            GetCellRequest request,
            ProxyClient<Route53RecoveryReadinessClient> proxyClient
    ) {
        GetCellResponse response;

        try {
            response = proxyClient.injectCredentialsAndInvokeV2(request, proxyClient.client()::getCell);
        } catch (final Route53RecoveryReadinessException e) {
            if (e.statusCode() == 404) {
                throw new CfnNotFoundException(ResourceModel.TYPE_NAME,
                        request.cellName(), e);
            } else
                throw new CfnGeneralServiceException(request.cellName(), e);
        } catch (final AwsServiceException e) {
            throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        }
        logger.log(String.format("%s has successfully been read.", request.cellName()));

        return response;
    }
}
