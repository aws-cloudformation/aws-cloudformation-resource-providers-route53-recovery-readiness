# AWS::Route53RecoveryReadiness::Cell

The API Schema for AWS Route53 Recovery Readiness Cells.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::Route53RecoveryReadiness::Cell",
    "Properties" : {
        "<a href="#cellname" title="CellName">CellName</a>" : <i>String</i>,
        "<a href="#cells" title="Cells">Cells</a>" : <i>[ String, ... ]</i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::Route53RecoveryReadiness::Cell
Properties:
    <a href="#cellname" title="CellName">CellName</a>: <i>String</i>
    <a href="#cells" title="Cells">Cells</a>: <i>
      - String</i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
</pre>

## Properties

#### CellName

The name of the cell to create.

_Required_: Yes

_Type_: String

_Maximum_: <code>64</code>

_Pattern_: <code>[a-zA-Z0-9_]+</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### Cells

A list of cell Amazon Resource Names (ARNs) contained within this cell, for use in nested cells. For example, Availability Zones within specific Regions.

_Required_: No

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

A collection of tags associated with a resource

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the CellName.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### CellArn

The Amazon Resource Name (ARN) of the cell.

#### ParentReadinessScopes

The readiness scope for the cell, which can be a cell Amazon Resource Name (ARN) or a recovery group ARN. This is a list but currently can have only one element.

