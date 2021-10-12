package software.amazon.route53recoveryreadiness.cell;

import software.amazon.awssdk.services.route53recoveryreadiness.model.ListCellsRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.ListCellsResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ListHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ListCellsRequest listRequest = Translator.translateToListRequest(request.getNextToken());

        ListCellsResponse listResponse = proxy.injectCredentialsAndInvokeV2(listRequest, ClientBuilder.getClient()::listCells);

        String nextToken = listResponse.nextToken();

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModels(Translator.translateFromListRequest(listResponse))
            .nextToken(nextToken)
            .status(OperationStatus.SUCCESS)
            .build();
    }
}
