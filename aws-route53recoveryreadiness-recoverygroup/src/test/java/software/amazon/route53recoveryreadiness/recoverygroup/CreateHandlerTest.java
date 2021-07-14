package software.amazon.route53recoveryreadiness.recoverygroup;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.services.route53recoveryreadiness.Route53RecoveryReadinessClient;
import software.amazon.awssdk.services.route53recoveryreadiness.model.CreateRecoveryGroupResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetCellResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetRecoveryGroupResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.Route53RecoveryReadinessException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<Route53RecoveryReadinessClient> proxyClient;

    @Mock
    Route53RecoveryReadinessClient sdkClient;

    @BeforeEach
    public void setup() {
        proxy = spy(new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis()));
        sdkClient = mock(Route53RecoveryReadinessClient.class);
        proxyClient = MOCK_PROXY(proxy, sdkClient);
    }

    @AfterEach
    public void tear_down() {
        verify(sdkClient, atLeastOnce()).serviceName();
        verifyNoMoreInteractions(sdkClient);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final CreateHandler handler = new CreateHandler();

        List<String> cells = new ArrayList<>();
        cells.add("arn::acellArn/myCell");
        cells.add("arn::aCellArn/myCellArn2");

        final ResourceModel model = ResourceModel.builder()
                .recoveryGroupName("MyRecoveryGroup")
                .cells(cells)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final GetCellResponse myCellResponse = GetCellResponse.builder()
                .cellName("MyCell")
                .cellArn("arn::acellArn/myCell")
                .build();

        final GetCellResponse cellyMcCellFaceResponse = GetCellResponse.builder()
                .cellName("CellyMcCellface")
                .cellArn("arn::aCellArn/myCellArn2")
                .build();

        final CreateRecoveryGroupResponse myRecoveryGroupResponse = CreateRecoveryGroupResponse.builder()
                .recoveryGroupName("MyRecoveryGroup")
                .recoveryGroupArn("recoveryGroupArn")
                .cells(cells)
                .build();

        final GetRecoveryGroupResponse finalRecoveryGroupResponse = GetRecoveryGroupResponse.builder()
                .recoveryGroupName("MyRecoveryGroup")
                .recoveryGroupArn("myRecoveryGroupArn")
                .cells(cells)
                .build();

        doThrow(Route53RecoveryReadinessException.builder().statusCode(404).build())
                .doReturn(myCellResponse)
                .doReturn(cellyMcCellFaceResponse)
                .doReturn(myRecoveryGroupResponse)
                .doReturn(finalRecoveryGroupResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.any(),
                        ArgumentMatchers.any()
                );

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getRecoveryGroupName()).isEqualTo(request.getDesiredResourceState().getRecoveryGroupName());
        assertThat(response.getResourceModel().getRecoveryGroupArn()).isNotNull();
        assertThat(response.getResourceModel().getCells().size()).isEqualTo(2);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_NoCells() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .recoveryGroupName("MyRecoveryGroup")
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final CreateRecoveryGroupResponse createRecoveryGroupResponse = CreateRecoveryGroupResponse.builder()
                .recoveryGroupName("MyRecoveryGroup")
                .recoveryGroupArn("recoveryGroupArn")
                .build();

        final GetRecoveryGroupResponse getRecoveryGroupResponse = GetRecoveryGroupResponse.builder()
                .recoveryGroupName("MyRecoveryGroup")
                .recoveryGroupArn("recoveryGroupArn")
                .build();

        doThrow(Route53RecoveryReadinessException.builder().statusCode(404).build())
                .doReturn(createRecoveryGroupResponse)
                .doReturn(getRecoveryGroupResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.any(),
                        ArgumentMatchers.any()
                );

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getRecoveryGroupName()).isEqualTo(request.getDesiredResourceState().getRecoveryGroupName());
        assertThat(response.getResourceModel().getRecoveryGroupArn()).isNotNull();
        assertThat(response.getResourceModel().getCells().size()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

    }

    @Test
    public void handleRequest_CellsNotFoundError() {
        final CreateHandler handler = new CreateHandler();

        List<String> cells = new ArrayList<>();
        cells.add("arn::blaaaah/cell2");

        final ResourceModel model = ResourceModel.builder()
                .recoveryGroupName("MyRecoveryGroup")
                .cells(cells)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        doThrow(Route53RecoveryReadinessException.builder().statusCode(404).build())
                .doThrow(Route53RecoveryReadinessException.builder().statusCode(404).build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.any(),
                        ArgumentMatchers.any()
                );

        assertThrows(CfnNotFoundException.class, () -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger));

        // This test never sends a request, so we have to call serviceName() ourselves otherwise tear_down() will fail
        sdkClient.serviceName();
    }

    @Test
    public void handleRequest_AssignArnReadOnlyError() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .recoveryGroupName("MyRecoveryGroup")
                .recoveryGroupArn("Should-be-read-only")
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnInvalidRequestException.class, () -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger));

        // This test never sends a request, so we have to call serviceName() ourselves otherwise tear_down() will fail
        sdkClient.serviceName();
    }
}
