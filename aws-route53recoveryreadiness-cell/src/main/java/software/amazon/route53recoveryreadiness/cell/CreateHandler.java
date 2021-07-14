package software.amazon.route53recoveryreadiness.cell;

import java.util.List;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.route53recoveryreadiness.Route53RecoveryReadinessClient;
import software.amazon.awssdk.services.route53recoveryreadiness.model.CreateCellRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.CreateCellResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetCellRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetCellResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.Route53RecoveryReadinessException;
import software.amazon.cloudformation.exceptions.CfnAlreadyExistsException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
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

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
            .then(progress ->
                proxy.initiate("AWS-Route53RecoveryReadiness-Cell::Create::PreExistenceCheckCell", proxyClient,progress.getResourceModel(), progress.getCallbackContext())
                    .translateToServiceRequest(Translator::translateToReadRequest)
                    .makeServiceCall(this::preExistenceCheckCell)
                    .progress(0)
                )
            .then(progress ->
                proxy.initiate("AWS-Route53RecoveryReadiness-Cell::Create::PreExistenceValidateCells", proxyClient,progress.getResourceModel(), progress.getCallbackContext())
                    .translateToServiceRequest(Translator::translateToCreateRequest)
                    .makeServiceCall(this::validateCells)
                    .progress(0)
                )
            .then(progress ->
                proxy.initiate("AWS-Route53RecoveryReadiness-Cell::Create", proxyClient,progress.getResourceModel(), progress.getCallbackContext())
                    .translateToServiceRequest(Translator::translateToCreateRequest)
                    .makeServiceCall(this::createCell)
                    .progress(0)
                )
            .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private GetCellResponse preExistenceCheckCell(
            final GetCellRequest awsRequest,
            final ProxyClient<Route53RecoveryReadinessClient> proxyClient ) {

        GetCellResponse response;

        try {
            response = proxyClient.injectCredentialsAndInvokeV2(awsRequest, proxyClient.client()::getCell);
        } catch (Route53RecoveryReadinessException e) {
            if (e.statusCode() == 404) {
                //expected not to find
                logger.log(String.format("%s not found.", awsRequest.cellName()));
            } else if (e.statusCode() == 409) {
                throw new CfnAlreadyExistsException(ResourceModel.TYPE_NAME, awsRequest.cellName());
            } else
                throw new CfnGeneralServiceException( ResourceModel.TYPE_NAME, e);
        } catch ( AwsServiceException e) {
            throw new CfnGeneralServiceException( ResourceModel.TYPE_NAME, e);
        }
        return null;
    }

    private CreateCellResponse createCell(
            final CreateCellRequest awsRequest,
            final ProxyClient<Route53RecoveryReadinessClient> proxyClient
    ) {
        CreateCellResponse createResponse;

        try {
            createResponse = proxyClient.injectCredentialsAndInvokeV2(awsRequest, proxyClient.client()::createCell);
        } catch (Route53RecoveryReadinessException e) {
            if (e.statusCode() == 409) {
                throw new CfnAlreadyExistsException(ResourceModel.TYPE_NAME, awsRequest.cellName());
            } else {
                throw new CfnGeneralServiceException( ResourceModel.TYPE_NAME, e);
            }
        } catch (AwsServiceException e) {
            throw new CfnGeneralServiceException( ResourceModel.TYPE_NAME, e);
        }

        logger.log(String.format("%s successfully created.", ResourceModel.TYPE_NAME));
        return createResponse;
    }

    private GetCellResponse getValidationCell(
            final GetCellRequest awsRequest,
            final ProxyClient<Route53RecoveryReadinessClient> proxyClient ) {

        GetCellResponse response;

        try {
            response = proxyClient.injectCredentialsAndInvokeV2(awsRequest, proxyClient.client()::getCell);
        } catch (Route53RecoveryReadinessException e) {
            if (e.statusCode() == 404) {
                throw new CfnNotFoundException(ResourceModel.TYPE_NAME,
                        awsRequest.cellName(), e);
            } else
                throw new CfnGeneralServiceException(awsRequest.cellName(), e);
        } catch (final AwsServiceException e) {
            throw new CfnGeneralServiceException(awsRequest.cellName(), e);
        }

        logger.log(String.format("%s has successfully been read.", awsRequest.cellName()));

        return response;
    }

    private GetCellResponse validateCells(
            final CreateCellRequest awsRequest,
            ProxyClient<Route53RecoveryReadinessClient> proxyClient
    ) {
        List<String> cells = awsRequest.cells();
        GetCellResponse response = null;

        if (cells != null && cells.size() > 0) {
            for(String cellArn : cells) {
                String [] cell = cellArn.split("/");
                response = getValidationCell(GetCellRequest.builder().cellName(cell[1]).build(), proxyClient);
                logger.log(String.format("%s exists", cell[1]));
            }
        }
        return response;
    }
}
