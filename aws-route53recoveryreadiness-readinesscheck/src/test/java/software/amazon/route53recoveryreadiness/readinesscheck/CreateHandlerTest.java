package software.amazon.route53recoveryreadiness.readinesscheck;

import software.amazon.awssdk.services.route53recoveryreadiness.Route53RecoveryReadinessClient;
import software.amazon.awssdk.services.route53recoveryreadiness.model.CreateReadinessCheckResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetReadinessCheckResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.Route53RecoveryReadinessException;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.time.Duration;

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

        final ResourceModel model = ResourceModel.builder()
                .readinessCheckName("MyFavoriteReadinessCheck")
                .resourceSetName("MyResourceSet")
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final CreateReadinessCheckResponse createResponse = CreateReadinessCheckResponse.builder()
                .readinessCheckName("MyFavoriteReadinessCheck")
                .readinessCheckArn("readinessCheckArn")
                .resourceSet("MyResourceSet")
                .build();

        final GetReadinessCheckResponse getResponse = GetReadinessCheckResponse.builder()
                .readinessCheckName("MyFavoriteReadinessCheck")
                .resourceSet("MyResourceSet")
                .readinessCheckArn("readinessCheckArn")
                .build();

        doThrow(Route53RecoveryReadinessException.builder().statusCode(404).build())
                .doReturn(null, createResponse, getResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.any(),
                        ArgumentMatchers.any()
                );

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getReadinessCheckName())
                .isEqualTo(request.getDesiredResourceState().getReadinessCheckName());
        assertThat(response.getResourceModel().getResourceSetName())
                .isEqualTo(request.getDesiredResourceState().getResourceSetName());
        assertThat(response.getResourceModel().getReadinessCheckArn()).isNotNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

}