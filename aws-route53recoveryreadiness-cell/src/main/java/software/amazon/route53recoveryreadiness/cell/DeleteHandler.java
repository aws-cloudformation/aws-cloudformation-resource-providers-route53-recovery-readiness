package software.amazon.route53recoveryreadiness.cell;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.route53recoveryreadiness.Route53RecoveryReadinessClient;
import software.amazon.awssdk.services.route53recoveryreadiness.model.DeleteCellRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.DeleteCellResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetCellRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetCellResponse;
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
                        proxy.initiate("AWS-Route53RecoveryReadiness-Cell::Delete::PreExistenceCheck", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                                .translateToServiceRequest(Translator::translateToReadRequest)
                                .makeServiceCall(this::preExistenceCheck)
                                .progress()
                )
            .then(progress ->
                proxy.initiate("AWS-Route53RecoveryReadiness-Cell::Delete", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                    .translateToServiceRequest(Translator::translateToDeleteRequest)
                    .makeServiceCall(this::deleteCell)
                    .progress()
            )
            .then(progress -> ProgressEvent.defaultSuccessHandler(null));
    }

    private DeleteCellResponse deleteCell(
            final DeleteCellRequest awsRequest,
            final ProxyClient<Route53RecoveryReadinessClient> proxyClient
    ) {
        DeleteCellResponse awsResponse;
        try {
            awsResponse = proxyClient.injectCredentialsAndInvokeV2(awsRequest, proxyClient.client()::deleteCell);
        } catch (final Route53RecoveryReadinessException e) {
            if (e.statusCode() == 404) {
                throw new CfnNotFoundException(ResourceModel.TYPE_NAME,
                        awsRequest.cellName(), e);
            } else
                throw new CfnGeneralServiceException(awsRequest.cellName(), e);
        } catch (final AwsServiceException e) {
            throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        }

        return awsResponse;
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
