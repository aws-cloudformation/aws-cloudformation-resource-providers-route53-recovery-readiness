package software.amazon.route53recoveryreadiness.resourceset;

import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.route53recoveryreadiness.Route53RecoveryReadinessClient;
import software.amazon.awssdk.services.route53recoveryreadiness.model.CreateResourceSetRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.CreateResourceSetResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetCellRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetRecoveryGroupRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetResourceSetRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetResourceSetResponse;
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
        if (request.getDesiredResourceState().getResourceSetArn() != null) {
            throw new CfnInvalidRequestException(request.getDesiredResourceState().toString());
        }

        List<String> readinessScopes = new ArrayList<>();
        for(Resource resource : request.getDesiredResourceState().getResources()) {
            List<String> resourceScopes = resource.getReadinessScopes();
            if (resourceScopes != null && resourceScopes.size() != 0) {
                readinessScopes.addAll(resource.getReadinessScopes());
            }
        }

        //validate readiness scope existence
        validateScopes(readinessScopes, proxyClient);

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
            .then(progress ->
                proxy.initiate("AWS-Route53RecoveryReadiness-ResourceSet::Create::PreExistenceCheck", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                    .translateToServiceRequest(Translator::translateToReadRequest)
                    .makeServiceCall(this::getResourceSet)
                    .progress()
            )
            .then(progress ->
                proxy.initiate("AWS-Route53RecoveryReadiness-ResourceSet::Create", proxyClient,progress.getResourceModel(), progress.getCallbackContext())
                    .translateToServiceRequest(Translator::translateToCreateRequest)
                    .makeServiceCall(this::createResource)
                    .progress()
                )
            .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    CreateResourceSetResponse createResource(
            CreateResourceSetRequest createRequest,
            ProxyClient<Route53RecoveryReadinessClient> proxyClient
    ) {
        CreateResourceSetResponse createResponse;

        try {
            createResponse = proxyClient.injectCredentialsAndInvokeV2(createRequest, proxyClient.client()::createResourceSet);
        } catch (Route53RecoveryReadinessException e) {
            if (e.statusCode() == 409) {
                throw new CfnAlreadyExistsException(ResourceModel.TYPE_NAME, createRequest.resourceSetName());
            } else {
                throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
            }
        } catch (AwsServiceException e) {
            throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        }

        logger.log(String.format("%s successfully created.", ResourceModel.TYPE_NAME));

        return createResponse;
    }

    GetResourceSetResponse getResourceSet(
            GetResourceSetRequest getRequest,
            ProxyClient<Route53RecoveryReadinessClient> proxyClient
    ) {
        GetResourceSetResponse getResponse = null;

        try {
            getResponse = proxyClient.injectCredentialsAndInvokeV2(getRequest, proxyClient.client()::getResourceSet);
        } catch (Route53RecoveryReadinessException e) {
            if (e.statusCode() == 404) {
                //expected not to find
                logger.log(String.format("%s not found.", getRequest.resourceSetName()));
            } else
                throw new CfnGeneralServiceException( ResourceModel.TYPE_NAME, e);
        } catch (AwsServiceException e) {
            throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        }

        logger.log(String.format("%s successfully read.", ResourceModel.TYPE_NAME));

        //expected to always be null if we get here
        return getResponse;
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
