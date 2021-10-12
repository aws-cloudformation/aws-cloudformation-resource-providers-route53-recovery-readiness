package software.amazon.route53recoveryreadiness.resourceset;

import software.amazon.awssdk.services.route53recoveryreadiness.model.ListResourceSetsResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.Resource;
import software.amazon.awssdk.services.route53recoveryreadiness.model.ResourceSetOutput;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private Logger logger;

    @BeforeEach
    public void setup() {
        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final ListHandler handler = new ListHandler();

        final ResourceModel model = ResourceModel.builder()
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        List<ResourceSetOutput> resourceSets = new ArrayList<>();

        List<Resource> resources = new ArrayList<>();

        resources.add(
                Resource.builder()
                .readinessScopes(Collections.singletonList("blah blah"))
                .resourceArn("thisIsAResourceArn")
                .build());

        resources.add(
                Resource.builder()
                .readinessScopes(Collections.singletonList("scope1"))
                .resourceArn("WoooResourceArn")
                .build());

        resourceSets.add(ResourceSetOutput.builder()
                .resourceSetName("MyFavoriteResourceSet")
                .resourceSetArn("resourceSetArn2")
                .resourceSetType("ProbablyDDBTables")
                .resources(resources)
                .build());

        final ListResourceSetsResponse listResponse = ListResourceSetsResponse.builder()
                .nextToken("nextToken")
                .resourceSets(resourceSets)
                .build();

        doReturn(listResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.any(),
                        ArgumentMatchers.any()
                );


        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, new CallbackContext(), logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNotNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
}
