package software.amazon.route53recoveryreadiness.recoverygroup;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.services.route53recoveryreadiness.Route53RecoveryReadinessClient;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetRecoveryGroupResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.UpdateRecoveryGroupResponse;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest extends AbstractTestBase {

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
        final UpdateHandler handler = new UpdateHandler();

        List<String> cells = new ArrayList<>();
        cells.add("myCellArn");
        cells.add("myCellArn2");

        List<String> oldCellList = new ArrayList<>();
        oldCellList.add("myCell3Arn");

        final ResourceModel model = ResourceModel.builder()
                .recoveryGroupName("MyRecoveryGroup")
                .cells(cells)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final GetRecoveryGroupResponse getPreviousResponse = GetRecoveryGroupResponse.builder()
                .recoveryGroupName("MyRecoveryGroup")
                .recoveryGroupArn("recoveryGroupArn")
                .cells(oldCellList)
                .build();

        final UpdateRecoveryGroupResponse updateResponse = UpdateRecoveryGroupResponse.builder()
                .recoveryGroupName("MyRecoveryGroup")
                .recoveryGroupArn("recoveryGroupArn")
                .cells(cells)
                .build();

        final GetRecoveryGroupResponse getResponse = GetRecoveryGroupResponse.builder()
                .recoveryGroupName("MyRecoveryGroup")
                .recoveryGroupArn("recoveryGroupArn")
                .cells(cells)
                .build();

        doReturn(getPreviousResponse)
                .doReturn(updateResponse)
                .doReturn(getResponse)
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
        assertThat(response.getResourceModel().getCells()).isEqualTo(request.getDesiredResourceState().getCells());
        assertThat(response.getResourceModel().getRecoveryGroupArn()).isNotNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
}
