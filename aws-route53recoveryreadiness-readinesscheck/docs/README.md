# AWS::Route53RecoveryReadiness::ReadinessCheck

Aws Route53 Recovery Readiness Check Schema and API specification.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::Route53RecoveryReadiness::ReadinessCheck",
    "Properties" : {
        "<a href="#resourcesetname" title="ResourceSetName">ResourceSetName</a>" : <i>String</i>,
        "<a href="#readinesscheckname" title="ReadinessCheckName">ReadinessCheckName</a>" : <i>String</i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::Route53RecoveryReadiness::ReadinessCheck
Properties:
    <a href="#resourcesetname" title="ResourceSetName">ResourceSetName</a>: <i>String</i>
    <a href="#readinesscheckname" title="ReadinessCheckName">ReadinessCheckName</a>: <i>String</i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
</pre>

## Properties

#### ResourceSetName

The name of the resource set to check.

_Required_: No

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>64</code>

_Pattern_: <code>[a-zA-Z0-9_]+</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ReadinessCheckName

Name of the ReadinessCheck to create.

_Required_: Yes

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>64</code>

_Pattern_: <code>[a-zA-Z0-9_]+</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### Tags

A collection of tags associated with a resource.

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the ReadinessCheckName.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### ReadinessCheckArn

The Amazon Resource Name (ARN) of the readiness check.

