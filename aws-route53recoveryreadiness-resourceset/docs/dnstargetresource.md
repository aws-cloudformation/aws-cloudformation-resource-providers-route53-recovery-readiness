# AWS::Route53RecoveryReadiness::ResourceSet DNSTargetResource

A component for DNS/routing control readiness checks.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#domainname" title="DomainName">DomainName</a>" : <i>String</i>,
    "<a href="#recordsetid" title="RecordSetId">RecordSetId</a>" : <i>String</i>,
    "<a href="#hostedzonearn" title="HostedZoneArn">HostedZoneArn</a>" : <i>String</i>,
    "<a href="#recordtype" title="RecordType">RecordType</a>" : <i>String</i>,
    "<a href="#targetresource" title="TargetResource">TargetResource</a>" : <i><a href="targetresource.md">TargetResource</a></i>
}
</pre>

### YAML

<pre>
<a href="#domainname" title="DomainName">DomainName</a>: <i>String</i>
<a href="#recordsetid" title="RecordSetId">RecordSetId</a>: <i>String</i>
<a href="#hostedzonearn" title="HostedZoneArn">HostedZoneArn</a>: <i>String</i>
<a href="#recordtype" title="RecordType">RecordType</a>: <i>String</i>
<a href="#targetresource" title="TargetResource">TargetResource</a>: <i><a href="targetresource.md">TargetResource</a></i>
</pre>

## Properties

#### DomainName

The domain name that acts as an ingress point to a portion of the customer application.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### RecordSetId

The Route 53 record set ID that will uniquely identify a DNS record, given a name and a type.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### HostedZoneArn

The hosted zone Amazon Resource Name (ARN) that contains the DNS record with the provided name of the target resource.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### RecordType

The type of DNS record of the target resource.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### TargetResource

The target resource that the Route 53 record points to.

_Required_: No

_Type_: <a href="targetresource.md">TargetResource</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

