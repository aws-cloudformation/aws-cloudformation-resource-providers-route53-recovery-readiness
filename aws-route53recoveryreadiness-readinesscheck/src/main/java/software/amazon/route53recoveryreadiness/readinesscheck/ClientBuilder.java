package software.amazon.route53recoveryreadiness.readinesscheck;

import java.net.URI;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.route53recoveryreadiness.Route53RecoveryReadinessClient;
import software.amazon.cloudformation.LambdaWrapper;

public class ClientBuilder {

  public static Route53RecoveryReadinessClient getClient() {
  
    final URI prodUri = URI.create("https://route53-recovery-readiness.us-west-2.amazonaws.com/");

    return Route53RecoveryReadinessClient.builder()
              .endpointOverride(prodUri)
              .region(Region.US_WEST_2)
              .httpClient(LambdaWrapper.HTTP_CLIENT)
              .build();
  }
}
