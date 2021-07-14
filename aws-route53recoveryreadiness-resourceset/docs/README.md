# AWS::Route53RecoveryReadiness::ResourceSet

Schema for the AWS Route53 Recovery Readiness ResourceSet Resource and API.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::Route53RecoveryReadiness::ResourceSet",
    "Properties" : {
        "<a href="#resourcesetname" title="ResourceSetName">ResourceSetName</a>" : <i>String</i>,
        "<a href="#resources" title="Resources">Resources</a>" : <i>[ <a href="resource.md">Resource</a>, ... ]</i>,
        "<a href="#resourcesettype" title="ResourceSetType">ResourceSetType</a>" : <i>String</i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::Route53RecoveryReadiness::ResourceSet
Properties:
    <a href="#resourcesetname" title="ResourceSetName">ResourceSetName</a>: <i>String</i>
    <a href="#resources" title="Resources">Resources</a>: <i>
      - <a href="resource.md">Resource</a></i>
    <a href="#resourcesettype" title="ResourceSetType">ResourceSetType</a>: <i>String</i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
</pre>

## Properties

#### ResourceSetName

The name of the resource set to create.

_Required_: Yes

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### Resources

A list of resource objects in the resource set.

_Required_: Yes

_Type_: List of <a href="resource.md">Resource</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ResourceSetType

The resource type of the resources in the resource set. Enter one of the following values for resource type: 

AWS: :AutoScaling: :AutoScalingGroup, AWS: :CloudWatch: :Alarm, AWS: :EC2: :CustomerGateway, AWS: :DynamoDB: :Table, AWS: :EC2: :Volume, AWS: :ElasticLoadBalancing: :LoadBalancer, AWS: :ElasticLoadBalancingV2: :LoadBalancer, AWS: :MSK: :Cluster, AWS: :RDS: :DBCluster, AWS: :Route53: :HealthCheck, AWS: :SQS: :Queue, AWS: :SNS: :Topic, AWS: :SNS: :Subscription, AWS: :EC2: :VPC, AWS: :EC2: :VPNConnection, AWS: :EC2: :VPNGateway, AWS::Route53RecoveryReadiness::DNSTargetResource

_Required_: Yes

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### Tags

A tag to associate with the parameters for a resource set.

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the ResourceSetName.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### ResourceSetArn

The Amazon Resource Name (ARN) of the resource set.

#### ComponentId

Returns the <code>ComponentId</code> value.

