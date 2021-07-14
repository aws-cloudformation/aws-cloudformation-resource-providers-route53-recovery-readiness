package software.amazon.route53recoveryreadiness.cell;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import software.amazon.awssdk.services.route53recoveryreadiness.model.CreateCellRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.DeleteCellRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetCellRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.GetCellResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.ListCellsRequest;
import software.amazon.awssdk.services.route53recoveryreadiness.model.ListCellsResponse;
import software.amazon.awssdk.services.route53recoveryreadiness.model.UpdateCellRequest;

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
  static CreateCellRequest translateToCreateRequest(final ResourceModel model) {
    return CreateCellRequest.builder()
            .cellName(model.getCellName())
            .cells(model.getCells())
            .build();
  }

  /**
   * Request to read a resource
   * @param model resource model
   * @return awsRequest the aws service request to describe a resource
   */
  static GetCellRequest translateToReadRequest(final ResourceModel model) {
    return GetCellRequest.builder()
            .cellName(model.getCellName())
            .build();
  }

  /**
   * Translates resource object from sdk into a resource model
   * @param response the aws service describe resource response
   * @return model resource model
   */
  static ResourceModel translateFromReadResponse(final GetCellResponse response) {
    return ResourceModel.builder()
        .cellName(response.cellName())
            .cellArn(response.cellArn())
            .cells(response.cells())
            .parentReadinessScopes(response.parentReadinessScopes())
        .build();
  }

  /**
   * Request to delete a resource
   * @param model resource model
   * @return awsRequest the aws service request to delete a resource
   */
  static DeleteCellRequest translateToDeleteRequest(final ResourceModel model) {
    return DeleteCellRequest.builder()
            .cellName(model.getCellName())
            .build();
  }

  /**
   * Request to update properties of a previously created resource
   * @param model resource model
   * @return awsRequest the aws service request to modify a resource
   */
  static UpdateCellRequest translateToUpdateRequest(final ResourceModel model) {
    return UpdateCellRequest.builder()
            .cellName(model.getCellName())
            .cells(model.getCells())
            .build();
  }

  /**
   * Request to list resources
   * @param nextToken token passed to the aws service list resources request
   * @return awsRequest the aws service request to list resources within aws account
   */
  static ListCellsRequest translateToListRequest(final String nextToken) {
    return ListCellsRequest.builder()
            .nextToken(nextToken)
            .build();
  }

  /**
   * Translates resource objects from sdk into a resource model (primary identifier only)
   * @param response the aws service describe resource response
   * @return list of resource models
   */
  static List<ResourceModel> translateFromListRequest(final ListCellsResponse response) {
    return streamOfOrEmpty(response.cells())
        .map(resource -> ResourceModel.builder()
            .cellName(resource.cellName())
            .build())
        .collect(Collectors.toList());
  }

  private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
    return Optional.ofNullable(collection)
        .map(Collection::stream)
        .orElseGet(Stream::empty);
  }
}
