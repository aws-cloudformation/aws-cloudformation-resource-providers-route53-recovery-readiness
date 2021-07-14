package software.amazon.route53recoveryreadiness.resourceset;

import software.amazon.awssdk.services.route53recoveryreadiness.model.CreateResourceSetRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.DeleteResourceSetRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetResourceSetRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetResourceSetResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.ListResourceSetsRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.ListResourceSetsResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.UpdateResourceSetRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is a centralized placeholder for
 *  - api request construction
 *  - object translation to/from aws sdk
 *  - resource model construction for read/list handlers
 */

public class Translator {

  /**
   * Request to create a resource
   * @param model resource model
   * @return awsRequest the aws service request to create a resource
   */
  static CreateResourceSetRequest translateToCreateRequest(final ResourceModel model) {
    return CreateResourceSetRequest.builder()
            .resourceSetName(model.getResourceSetName())
            .resourceSetType(model.getResourceSetType())
            .resources(primedResourceListBuilder(model.getResources()))
            .build();
  }

  private static List<software.amazon.awssdk.services.route53recoveryreadiness.model.Resource> primedResourceListBuilder(List<Resource> modelResources) {
    List<software.amazon.awssdk.services.route53recoveryreadiness.model.Resource> primedResources = new ArrayList<>();

    for(Resource resource : modelResources) {
      primedResources.add(primedResourceBuilder(resource));
    }

    return primedResources;
  }

  private static software.amazon.awssdk.services.route53recoveryreadiness.model.Resource primedResourceBuilder(Resource modelResource) {

    String resourceArn = modelResource.getResourceArn();
    List<String> readinessScopes = modelResource.getReadinessScopes() != null  ? modelResource.getReadinessScopes() : null;
    software.amazon.awssdk.services.route53recoveryreadiness.model.DNSTargetResource dnsTargetResource = modelResource.getDnsTargetResource() != null ?
            primedDNSTargetBuilder(modelResource.getDnsTargetResource()) : null;

    if (resourceArn != null){
      return software.amazon.awssdk.services.route53recoveryreadiness.model.Resource.builder()
              .resourceArn(resourceArn)
              .readinessScopes(readinessScopes)
              .build();
    } else {
      return software.amazon.awssdk.services.route53recoveryreadiness.model.Resource.builder()
              .readinessScopes(readinessScopes)
              .dnsTargetResource(dnsTargetResource)
              .build();
    }
  }

  private static software.amazon.awssdk.services.route53recoveryreadiness.model.DNSTargetResource primedDNSTargetBuilder(software.amazon.route53recoveryreadiness.resourceset.DNSTargetResource dnsTarget) {
      if (dnsTarget.getTargetResource() != null) {
        return software.amazon.awssdk.services.route53recoveryreadiness.model.DNSTargetResource.builder()
                .domainName(dnsTarget.getDomainName())
                .hostedZoneArn(dnsTarget.getHostedZoneArn())
                .recordSetId(dnsTarget.getRecordSetId())
                .recordType(dnsTarget.getRecordType())
                .targetResource(primedTargetResourceBuilder(dnsTarget.getTargetResource()))
                .build();
      } else {
        return software.amazon.awssdk.services.route53recoveryreadiness.model.DNSTargetResource.builder()
                .domainName(dnsTarget.getDomainName())
                .hostedZoneArn(dnsTarget.getHostedZoneArn())
                .recordSetId(dnsTarget.getRecordSetId())
                .recordType(dnsTarget.getRecordType())
                .build();
      }
  }

  private static software.amazon.awssdk.services.route53recoveryreadiness.model.TargetResource primedTargetResourceBuilder(TargetResource targetResource) {
    if (targetResource.getR53Resource() != null) {
      return software.amazon.awssdk.services.route53recoveryreadiness.model.TargetResource.builder()
              .r53Resource(software.amazon.awssdk.services.route53recoveryreadiness.model.R53ResourceRecord.builder()
                      .recordSetId(targetResource.getR53Resource().getRecordSetId())
                      .domainName(targetResource.getR53Resource().getDomainName())
                      .build())
              .build();
    } else {
      return software.amazon.awssdk.services.route53recoveryreadiness.model.TargetResource.builder()
              .nlbResource(software.amazon.awssdk.services.route53recoveryreadiness.model.NLBResource.builder()
                      .arn(targetResource.getNLBResource().getArn())
                      .build())
              .build();
    }
  }

  /**
   * Request to read a resource
   * @param model resource model
   * @return awsRequest the aws service request to describe a resource
   */
  static GetResourceSetRequest translateToReadRequest(final ResourceModel model) {
    return GetResourceSetRequest.builder()
            .resourceSetName(model.getResourceSetName())
            .build();
  }

  /**
   * Translates resource object from sdk into a resource model
   * @param response the aws service describe resource response
   * @return model resource model
   */
  static ResourceModel translateFromReadResponse(final GetResourceSetResponse response) {
    return ResourceModel.builder()
        .resourceSetName(response.resourceSetName())
            .resourceSetArn(response.resourceSetArn())
            .resourceSetType(response.resourceSetType())
            .resources(buildModelResources(response.resources()))
        .build();
  }


  private static List<Resource> buildModelResources(
          List<software.amazon.awssdk.services.route53recoveryreadiness.model.Resource> primedResources
  ) {
    List<Resource> resources = new ArrayList<>();

    for(software.amazon.awssdk.services.route53recoveryreadiness.model.Resource resource : primedResources) {
      resources.add(modelResourceBuilder(resource));
    }

    return resources;
  }

  private static Resource modelResourceBuilder(software.amazon.awssdk.services.route53recoveryreadiness.model.Resource resource) {
    String resourceArn = resource.resourceArn();
    List<String> readinessScopes = resource.readinessScopes();
    String componentId = resource.componentId() != null ? resource.componentId() : null;
    software.amazon.route53recoveryreadiness.resourceset.DNSTargetResource dnsResource =
            resource.dnsTargetResource() != null ? modelDnsBuilder(resource.dnsTargetResource()) : null;

    if (dnsResource != null) {
      return Resource.builder()
              .readinessScopes(readinessScopes)
              .componentId(componentId)
              .dnsTargetResource(dnsResource)
              .build();
    }
    return Resource.builder()
            .resourceArn(resourceArn)
            .readinessScopes(readinessScopes)
            .build();
  }

  private static software.amazon.route53recoveryreadiness.resourceset.DNSTargetResource modelDnsBuilder(software.amazon.awssdk.services.route53recoveryreadiness.model.DNSTargetResource dnsResource) {
    TargetResource targetResource =
            dnsResource.targetResource() != null ?
                    modelTargetBuilder(dnsResource.targetResource()) :
                    null;

    return software.amazon.route53recoveryreadiness.resourceset.DNSTargetResource.builder()
            .recordType(dnsResource.recordType())
            .recordSetId(dnsResource.recordSetId())
            .hostedZoneArn(dnsResource.hostedZoneArn())
            .domainName(dnsResource.domainName())
            .targetResource(targetResource)
            .build();
  }

  private static TargetResource modelTargetBuilder(software.amazon.awssdk.services.route53recoveryreadiness.model.TargetResource targetResource) {

    if (targetResource.r53Resource() != null) {
      return TargetResource.builder()
              .r53Resource(modelR53TargetBuilder(targetResource.r53Resource()))
              .build();
    } else {
      return TargetResource.builder()
              .nLBResource(modelNlbTargetBuilder(targetResource.nlbResource()))
              .build();
    }
  }

  private static software.amazon.route53recoveryreadiness.resourceset.R53ResourceRecord modelR53TargetBuilder(
          software.amazon.awssdk.services.route53recoveryreadiness.model.R53ResourceRecord r53Resource) {
    return software.amazon.route53recoveryreadiness.resourceset.R53ResourceRecord.builder()
            .recordSetId(r53Resource.recordSetId())
            .domainName(r53Resource.domainName())
            .build();
  }

  private static software.amazon.route53recoveryreadiness.resourceset.NLBResource modelNlbTargetBuilder(
          software.amazon.awssdk.services.route53recoveryreadiness.model.NLBResource nlbResource
  ) {
    return software.amazon.route53recoveryreadiness.resourceset.NLBResource.builder()
            .arn(nlbResource.arn())
            .build();
  }

  /**
   * Request to delete a resource
   * @param model resource model
   * @return awsRequest the aws service request to delete a resource
   */
  static DeleteResourceSetRequest translateToDeleteRequest(final ResourceModel model) {
    return DeleteResourceSetRequest.builder()
            .resourceSetName(model.getResourceSetName())
            .build();
  }

  /**
   * Request to update properties of a previously created resource
   * @param model resource model
   * @return awsRequest the aws service request to modify a resource
   */
  static UpdateResourceSetRequest translateToUpdateRequest(final ResourceModel model) {
    return UpdateResourceSetRequest.builder()
            .resourceSetName(model.getResourceSetName())
            .resourceSetType(model.getResourceSetType())
            .resources(primedResourceListBuilder(model.getResources()))
            .build();
  }

  /**
   * Request to list resources
   * @param nextToken token passed to the aws service list resources request
   * @return awsRequest the aws service request to list resources within aws account
   */
  static ListResourceSetsRequest translateToListRequest(final String nextToken) {
    return ListResourceSetsRequest.builder()
            .nextToken(nextToken)
            .build();
  }

  /**
   * Translates resource objects from sdk into a resource model (primary identifier only)
   * @param response the aws service describe resource response
   * @return list of resource models
   */
  static List<ResourceModel> translateFromListRequest(final ListResourceSetsResponse response) {
    return streamOfOrEmpty(response.resourceSets())
        .map(resource -> ResourceModel.builder()
            .resourceSetName(resource.resourceSetName())
                .resourceSetArn(resource.resourceSetArn())
                .resources(buildModelResources(resource.resources()))
            .build())
        .collect(Collectors.toList());
  }

  private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
    return Optional.ofNullable(collection)
        .map(Collection::stream)
        .orElseGet(Stream::empty);
  }
}
