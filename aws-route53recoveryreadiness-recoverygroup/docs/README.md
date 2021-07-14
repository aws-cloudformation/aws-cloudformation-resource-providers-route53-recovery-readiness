# AWS::Route53RecoveryReadiness::RecoveryGroup

AWS Route53 Recovery Readiness Recovery Group Schema and API specifications.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::Route53RecoveryReadiness::RecoveryGroup",
    "Properties" : {
        "<a href="#recoverygroupname" title="RecoveryGroupName">RecoveryGroupName</a>" : <i>String</i>,
        "<a href="#cells" title="Cells">Cells</a>" : <i>[ String, ... ]</i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::Route53RecoveryReadiness::RecoveryGroup
Properties:
    <a href="#recoverygroupname" title="RecoveryGroupName">RecoveryGroupName</a>: <i>String</i>
    <a href="#cells" title="Cells">Cells</a>: <i>
      - String</i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
</pre>

## Properties

#### RecoveryGroupName

The name of the recovery group to create.

_Required_: Yes

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>64</code>

_Pattern_: <code>[a-zA-Z0-9_]+</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### Cells

A list of the cell Amazon Resource Names (ARNs) in the recovery group.

_Required_: No

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

A collection of tags associated with a resource.

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the RecoveryGroupName.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### RecoveryGroupArn

A collection of tags associated with a resource.

