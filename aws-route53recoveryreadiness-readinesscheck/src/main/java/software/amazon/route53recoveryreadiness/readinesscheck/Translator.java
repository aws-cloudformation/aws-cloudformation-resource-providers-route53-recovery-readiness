package software.amazon.route53recoveryreadiness.readinesscheck;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import software.amazon.awssdk.services.route53recoveryreadiness.model.CreateReadinessCheckRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.DeleteReadinessCheckRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetReadinessCheckRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetReadinessCheckResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetResourceSetRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.ListReadinessChecksRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.ListReadinessChecksResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.UpdateReadinessCheckRequest;

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
  static CreateReadinessCheckRequest translateToCreateRequest(final ResourceModel model) {
    return CreateReadinessCheckRequest.builder()
            .readinessCheckName(model.getReadinessCheckName())
            .resourceSetName(model.getResourceSetName())
            .build();
  }

  /**
   * Request to read a resource
   * @param model resource model
   * @return awsRequest the aws service request to describe a resource
   */
  static GetReadinessCheckRequest translateToReadRequest(final ResourceModel model) {
    return GetReadinessCheckRequest.builder()
            .readinessCheckName(model.getReadinessCheckName())
            .build();
  }


   /**
   * Request to read a resource
   * @param model resource model
   * @return awsRequest the aws service request to describe a resource
   */
  static GetResourceSetRequest translateToReadResourceSetRequest(final ResourceModel model) {
    return GetResourceSetRequest.builder()
            .resourceSetName(model.getResourceSetName())
            .build();
  }


  /**
   * Translates resource object from sdk into a resource model
   * @param response the aws service describe resource response
   * @return model resource model
   */
  static ResourceModel translateFromReadResponse(final GetReadinessCheckResponse response) {
    return ResourceModel.builder()
        .readinessCheckName(response.readinessCheckName())
            .readinessCheckArn(response.readinessCheckArn())
            .resourceSetName(response.resourceSet())
        .build();
  }

  /**
   * Request to delete a resource
   * @param model resource model
   * @return awsRequest the aws service request to delete a resource
   */
  static DeleteReadinessCheckRequest translateToDeleteRequest(final ResourceModel model) {
    return DeleteReadinessCheckRequest.builder()
            .readinessCheckName(model.getReadinessCheckName())
            .build();
  }

  /**
   * Request to update properties of a previously created resource
   * @param model resource model
   * @return awsRequest the aws service request to modify a resource
   */
  static UpdateReadinessCheckRequest translateToUpdateRequest(final ResourceModel model) {
    return UpdateReadinessCheckRequest.builder()
            .readinessCheckName(model.getReadinessCheckName())
            .resourceSetName(model.getResourceSetName())
            .build();
  }

  /**
   * Request to list resources
   * @param nextToken token passed to the aws service list resources request
   * @return awsRequest the aws service request to list resources within aws account
   */
  static ListReadinessChecksRequest translateToListRequest(final String nextToken) {
    return ListReadinessChecksRequest.builder()
            .nextToken(nextToken)
            .build();
  }

  /**
   * Translates resource objects from sdk into a resource model (primary identifier only)
   * @param response the aws service describe resource response
   * @return list of resource models
   */
  static List<ResourceModel> translateFromListRequest(final ListReadinessChecksResponse response) {
    return streamOfOrEmpty(response.readinessChecks())
        .map(resource -> ResourceModel.builder()
            .readinessCheckName(resource.readinessCheckName())
                .resourceSetName(resource.resourceSet())
                .readinessCheckArn(resource.readinessCheckArn())
            .build())
        .collect(Collectors.toList());
  }

  private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
    return Optional.ofNullable(collection)
        .map(Collection::stream)
        .orElseGet(Stream::empty);
  }
}
