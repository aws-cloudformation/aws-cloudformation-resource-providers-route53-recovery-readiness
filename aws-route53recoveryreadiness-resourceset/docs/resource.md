# AWS::Route53RecoveryReadiness::ResourceSet Resource

The resource element of a ResourceSet

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#resourcearn" title="ResourceArn">ResourceArn</a>" : <i>String</i>,
    "<a href="#componentid" title="ComponentId">ComponentId</a>" : <i>String</i>,
    "<a href="#dnstargetresource" title="DnsTargetResource">DnsTargetResource</a>" : <i><a href="dnstargetresource.md">DNSTargetResource</a></i>,
    "<a href="#readinessscopes" title="ReadinessScopes">ReadinessScopes</a>" : <i>[ String, ... ]</i>
}
</pre>

### YAML

<pre>
<a href="#resourcearn" title="ResourceArn">ResourceArn</a>: <i>String</i>
<a href="#componentid" title="ComponentId">ComponentId</a>: <i>String</i>
<a href="#dnstargetresource" title="DnsTargetResource">DnsTargetResource</a>: <i><a href="dnstargetresource.md">DNSTargetResource</a></i>
<a href="#readinessscopes" title="ReadinessScopes">ReadinessScopes</a>: <i>
      - String</i>
</pre>

## Properties

#### ResourceArn

The Amazon Resource Name (ARN) of the AWS resource.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ComponentId

The component identifier of the resource, generated when DNS target resource is used.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### DnsTargetResource

A component for DNS/routing control readiness checks.

_Required_: No

_Type_: <a href="dnstargetresource.md">DNSTargetResource</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ReadinessScopes

A list of recovery group Amazon Resource Names (ARNs) and cell ARNs that this resource is contained within.

_Required_: No

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

