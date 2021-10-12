package software.amazon.route53recoveryreadiness.readinesscheck;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.route53recoveryreadiness.Route53RecoveryReadinessClient;
import software.amazon.awssdk.services.route53recoveryreadiness.model.CreateReadinessCheckRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.CreateReadinessCheckResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetReadinessCheckRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetReadinessCheckResponse;
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

        if (request.getDesiredResourceState().getReadinessCheckArn() != null) {
            throw new CfnInvalidRequestException(request.getDesiredResourceState().toString());
        }

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
            .then(progress -> {
                // Check to see if ReadinessCheck exists
                final ProgressEvent<ResourceModel, CallbackContext> progressEvent =
                 proxy.initiate("AWS-Route53RecoveryReadiness-ReadinessCheck::Create::ReadinessCheckPreExistanceCheck", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                    .translateToServiceRequest(Translator::translateToReadRequest)
                    .makeServiceCall(this::readinessCheckPreExistenceCheck)
                    .progress();
                progressEvent.setCallbackDelaySeconds(0);
                    return progressEvent;
            })
            .then(progress -> {
                // Check to see if ResourceSet exists
                final ProgressEvent<ResourceModel, CallbackContext> progressEvent =
                 proxy.initiate("AWS-Route53RecoveryReadiness-ReadinessCheck::Create::ResourceSetPreExistanceCheck", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                    .translateToServiceRequest(Translator::translateToReadResourceSetRequest)
                    .makeServiceCall(this::resourceSetPreExistanceCheck)
                    .progress();
                progressEvent.setCallbackDelaySeconds(0);
                    return progressEvent;
            })
            .then(progress ->
                proxy.initiate("AWS-Route53RecoveryReadiness-ReadinessCheck::Create", proxyClient,progress.getResourceModel(), progress.getCallbackContext())
                    .translateToServiceRequest(Translator::translateToCreateRequest)
                    .makeServiceCall(this::createReadinessCheck)
                    .stabilize((awsRequest, awsResponse, client, model, context) -> isStabilized(model, client))
                    .progress()
                )
            .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private GetResourceSetResponse resourceSetPreExistanceCheck(
        GetResourceSetRequest getRequest,
        ProxyClient<Route53RecoveryReadinessClient> proxyClient
    ) {
        try {
            proxyClient.injectCredentialsAndInvokeV2(getRequest, proxyClient.client()::getResourceSet);
        } catch (Route53RecoveryReadinessException e) {
            if (e.statusCode() == 404) {
                throw new CfnNotFoundException(ResourceModel.TYPE_NAME,
                    getRequest.resourceSetName(), e);
            } else
                throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        } catch (AwsServiceException e) {
            throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        }
        logger.log(String.format("%s successfully read.", ResourceModel.TYPE_NAME));

        // Don't care about response
        return null;
    }

    private CreateReadinessCheckResponse createReadinessCheck(
            CreateReadinessCheckRequest createRequest,
            ProxyClient<Route53RecoveryReadinessClient> proxyClient
    ) {

        CreateReadinessCheckResponse createResponse;

        try {
            createResponse = proxyClient.injectCredentialsAndInvokeV2(createRequest, proxyClient.client()::createReadinessCheck);
        } catch (final Route53RecoveryReadinessException e) {
            if (e.statusCode() == 409)
                throw new CfnAlreadyExistsException(ResourceModel.TYPE_NAME, createRequest.readinessCheckName(), e);
            else
                throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        } catch (AwsServiceException e) {
            throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        }
        logger.log(String.format("%s successfully created.", ResourceModel.TYPE_NAME));

        return createResponse;
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

    private boolean isStabilized(
            ResourceModel model,
            ProxyClient<Route53RecoveryReadinessClient> proxyClient
    ) {
        String readinessCheckName = model.getReadinessCheckName();
        try {
            getReadinessCheck(GetReadinessCheckRequest.builder()
                    .readinessCheckName(readinessCheckName).build(), proxyClient);
        } catch (CfnNotFoundException e) {
            logger.log(String.format("%s has not yet stabilized.", ResourceModel.TYPE_NAME));
            return false;
        }

        return true;
    }

    private GetReadinessCheckResponse readinessCheckPreExistenceCheck(
            GetReadinessCheckRequest getRequest,
            ProxyClient<Route53RecoveryReadinessClient> proxyClient
    ) {
        
        GetReadinessCheckResponse getResponse = null;

        try {
            getResponse = proxyClient.injectCredentialsAndInvokeV2(getRequest, proxyClient.client()::getReadinessCheck);
        } catch (Route53RecoveryReadinessException e) {
            // expected
            if (e.statusCode() == 404)
                logger.log("Resource Not found found");
            else {
                throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
            }
        } catch (AwsServiceException e) {
            throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        }

        logger.log(String.format("%s has successfully been read.", ResourceModel.TYPE_NAME));

        return getResponse;
    }
}
