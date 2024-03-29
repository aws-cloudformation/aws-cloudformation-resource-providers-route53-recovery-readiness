package software.amazon.route53recoveryreadiness.recoverygroup;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import software.amazon.awssdk.services.route53recoveryreadiness.model.CreateRecoveryGroupRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.DeleteRecoveryGroupRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetRecoveryGroupRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetRecoveryGroupResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.ListRecoveryGroupsRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.ListRecoveryGroupsResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.UpdateRecoveryGroupRequest;

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
  static CreateRecoveryGroupRequest translateToCreateRequest(final ResourceModel model) {
    return CreateRecoveryGroupRequest.builder()
            .recoveryGroupName(model.getRecoveryGroupName())
            .cells(model.getCells())
            .build();
  }

  /**
   * Request to read a resource
   * @param model resource model
   * @return awsRequest the aws service request to describe a resource
   */
  static GetRecoveryGroupRequest translateToReadRequest(final ResourceModel model) {
    return GetRecoveryGroupRequest.builder()
            .recoveryGroupName(model.getRecoveryGroupName())
            .build();
  }

  /**
   * Translates resource object from sdk into a resource model
   * @param response the aws service describe resource response
   * @return model resource model
   */
  static ResourceModel translateFromReadResponse(final GetRecoveryGroupResponse response) {
    return  ResourceModel.builder()
            .recoveryGroupName(response.recoveryGroupName())
            .recoveryGroupArn(response.recoveryGroupArn())
            .cells(response.cells())
            .build();
  }

  /**
   * Request to delete a resource
   * @param model resource model
   * @return awsRequest the aws service request to delete a resource
   */
  static DeleteRecoveryGroupRequest translateToDeleteRequest(final ResourceModel model) {
    return DeleteRecoveryGroupRequest.builder()
            .recoveryGroupName(model.getRecoveryGroupName())
            .build();
  }

  /**
   * Request to update properties of a previously created resource
   * @param model resource model
   * @return awsRequest the aws service request to modify a resource
   */
  static UpdateRecoveryGroupRequest translateToUpdateRequest(final ResourceModel model) {
    return UpdateRecoveryGroupRequest.builder()
            .recoveryGroupName(model.getRecoveryGroupName())
            .cells(model.getCells())
            .build();
  }

  /**
   * Request to list resources
   * @param nextToken token passed to the aws service list resources request
   * @return awsRequest the aws service request to list resources within aws account
   */
  static ListRecoveryGroupsRequest translateToListRequest(final String nextToken) {
    return ListRecoveryGroupsRequest.builder()
            .nextToken(nextToken)
            .build();
  }

  /**
   * Translates resource objects from sdk into a resource model (primary identifier only)
   * @param response the aws service describe resource response
   * @return list of resource models
   */
  static List<ResourceModel> translateFromListRequest(final ListRecoveryGroupsResponse response) {
    return streamOfOrEmpty(response.recoveryGroups())
        .map(resource -> ResourceModel.builder()
            .recoveryGroupName(resource.recoveryGroupName())
                .recoveryGroupArn(resource.recoveryGroupArn())
                .cells(resource.cells())
            .build())
        .collect(Collectors.toList());
  }

  private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
    return Optional.ofNullable(collection)
        .map(Collection::stream)
        .orElseGet(Stream::empty);
  }
}
