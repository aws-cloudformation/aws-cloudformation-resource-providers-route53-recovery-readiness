package software.amazon.route53recoveryreadiness.resourceset;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.route53recoveryreadiness.Route53RecoveryReadinessClient;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetCellRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetRecoveryGroupRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetResourceSetRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetResourceSetResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.Route53RecoveryReadinessException;
import software.amazon.awssdk.services.route53recoveryreadiness.model.UpdateResourceSetRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.UpdateResourceSetResponse;
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

        // Make sure the user isn't trying to assign values to readOnly properties

        if (previousModel != null) {
            //Name needed to find Recovery Group to update, but is not itself update-able
            if(!Objects.equals(previousModel.getResourceSetName(), model.getResourceSetName())) {
                throw new CfnNotUpdatableException(ResourceModel.TYPE_NAME, request.getDesiredResourceState().getResourceSetName());
            }
            if (model.getResourceSetArn() != null) {
                if(!previousModel.getResourceSetArn().equals(model.getResourceSetArn())) {
                    throw new CfnNotUpdatableException(ResourceModel.TYPE_NAME, request.getDesiredResourceState().getResourceSetName());
                }
            }
        }

        GetResourceSetRequest getRequest = GetResourceSetRequest.builder().resourceSetName(model.getResourceSetName()).build();
        preExistenceCheck(getRequest, proxyClient);

        List<String> readinessScopes = new ArrayList<>();
        for(Resource resource : model.getResources()) {
            List<String> resourceScopes = resource.getReadinessScopes();
            if (resourceScopes != null && resourceScopes.size() != 0) {
                readinessScopes.addAll(resource.getReadinessScopes());
            }
        }

        //validate readiness scope existence
        validateScopes(readinessScopes, proxyClient);

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
            .then(progress ->
                proxy.initiate("AWS-Route53RecoveryReadiness-ResourceSet::Update", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                    .translateToServiceRequest(Translator::translateToUpdateRequest)
                    .makeServiceCall(this::updateResourceSet)
                    .progress())

            .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private GetResourceSetResponse preExistenceCheck(
            final GetResourceSetRequest awsRequest,
            final ProxyClient<Route53RecoveryReadinessClient> proxyClient ) {

        GetResourceSetResponse response;

        try {
            response = proxyClient.injectCredentialsAndInvokeV2(awsRequest, proxyClient.client()::getResourceSet);
        } catch (final Route53RecoveryReadinessException e) {
            if (e.statusCode() == 404) {
                throw new CfnNotFoundException(ResourceModel.TYPE_NAME,
                        awsRequest.resourceSetName(), e);
            } else
                throw new CfnGeneralServiceException(awsRequest.resourceSetName(), e);
        } catch (final AwsServiceException e) {
            throw new CfnGeneralServiceException(awsRequest.resourceSetName(), e);
        }

        logger.log(String.format("%s has successfully been read.", ResourceModel.TYPE_NAME));

        return response;
    }

    UpdateResourceSetResponse updateResourceSet(
            UpdateResourceSetRequest updateRequest,
            ProxyClient<Route53RecoveryReadinessClient> proxyClient
    ) {
        UpdateResourceSetResponse updateResponse;

        try {
            updateResponse = proxyClient.injectCredentialsAndInvokeV2(updateRequest, proxyClient.client()::updateResourceSet);
        } catch (Route53RecoveryReadinessException e) {
            if (e.statusCode() == 404) {
                throw new CfnNotFoundException(ResourceModel.TYPE_NAME,
                        updateRequest.resourceSetName(), e);
            } else
                throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        } catch (AwsServiceException e) {
            throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        }

        logger.log(String.format("%s has successfully been updated.", ResourceModel.TYPE_NAME));
        return updateResponse;
    }

    private void validateScopes(
            List<String> readinessScopes,
            ProxyClient<Route53RecoveryReadinessClient> proxyClient
    ) {
        if (readinessScopes.size() != 0) {
            List<String> cellNames = new ArrayList<>();
            List<String> recoveryGroupNames = new ArrayList<>();

            for (String scope : readinessScopes) {
                if (scope.contains("cell")) {
                    String cellName = scope.split("/")[1];
                    cellNames.add(cellName);
                } else {
                    String recoveryGroupName = scope.split("/")[1];
                    recoveryGroupNames.add(recoveryGroupName);
                }
            }

            if (cellNames.size() != 0) {
                for (String cellName : cellNames) {
                    GetCellRequest cellRequest = GetCellRequest.builder().cellName(cellName).build();
                    getCell(cellRequest, proxyClient);
                }
            }

            if (recoveryGroupNames.size() != 0) {
                for (String rgName : recoveryGroupNames) {
                    GetRecoveryGroupRequest rgRequest = GetRecoveryGroupRequest.builder().recoveryGroupName(rgName).build();
                    getRecoveryGroup(rgRequest, proxyClient);
                }
            }
        }
    }

    private void getCell(
            GetCellRequest awsRequest,
            ProxyClient<Route53RecoveryReadinessClient> proxyClient
    ) {

        try {
            proxyClient.injectCredentialsAndInvokeV2(awsRequest, proxyClient.client()::getCell);
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
    }

    private void getRecoveryGroup(
            final GetRecoveryGroupRequest request,
            final ProxyClient<Route53RecoveryReadinessClient> proxyClient
    ) {

        try {
            proxyClient.injectCredentialsAndInvokeV2(request, proxyClient.client()::getRecoveryGroup);
        } catch (final Route53RecoveryReadinessException e) {
            if (e.statusCode() == 404) {
                throw new CfnNotFoundException(ResourceModel.TYPE_NAME,
                        request.recoveryGroupName(), e);
            } else
                throw new CfnGeneralServiceException(request.recoveryGroupName(), e);
        } catch (final AwsServiceException e) {
            throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        }
        logger.log(String.format("%s has successfully been read.", ResourceModel.TYPE_NAME));

    }
}
